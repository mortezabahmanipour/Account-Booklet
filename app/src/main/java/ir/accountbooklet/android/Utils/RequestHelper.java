package ir.accountbooklet.android.Utils;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.RealResponseBody;
import okio.GzipSource;
import okio.Okio;

public class RequestHelper {

  private static volatile RequestHelper Instance;

  public static synchronized RequestHelper getInstance() {
    RequestHelper localInstance = Instance;
    if (localInstance == null) {
      synchronized (RequestHelper.class) {
        localInstance = Instance;
        if (localInstance == null) {
          Instance = localInstance = new RequestHelper();
        }
      }
    }
    return localInstance;
  }


  private static final int defaultTimeOut = 40;

  private AtomicInteger requestId = new AtomicInteger(1);
  private Map<Object, List<Integer>> listTags = new HashMap<>();
  private OkHttpClient okHttpClient;

  public interface RequestHelperListener {
    void onResponse(boolean isSuccessful, String response);
  }

  private RequestHelper() {
    okHttpClient = new OkHttpClient.Builder().connectTimeout(defaultTimeOut, TimeUnit.SECONDS).writeTimeout(defaultTimeOut, TimeUnit.SECONDS).readTimeout(defaultTimeOut, TimeUnit.SECONDS).build();
  }

  public int addToRequestQueue(Object tag, String url, Map<String, Object> params, RequestHelperListener listener) {
    return addToRequestQueue(tag, url, params, null, listener);
  }

  public int addToRequestQueue(final Object tag, final String url, final Map<String, Object> params, Map<String, String> headers, final RequestHelperListener listener) {
    if (TextUtils.isEmpty(url)) {
      if (listener != null) {
        AndroidUtilities.runOnUIThread(() -> listener.onResponse(false, "url is empty"));
      }
      return 0;
    }
    final int id = requestId.getAndIncrement();
    if (tag != null) {
      addIdToListTag(tag, id);
    }
    final Request.Builder builder = new Request.Builder().url(url).tag(id);
    final StringBuilder logBuilder = new StringBuilder();
    logBuilder.append(url);
    if (headers != null && !headers.isEmpty()) {
      Headers.Builder headersBuilder = new Headers.Builder();
      for (HashMap.Entry<String, String> entry : headers.entrySet()) {
        headersBuilder.add(entry.getKey(), entry.getValue());
      }
      builder.headers(headersBuilder.build());

      if (logBuilder.length() > 0) {
        logBuilder.append("\n");
      }
      logBuilder.append("headers ->\n");
      logBuilder.append(headers.toString());
    }
    if (params != null && !params.isEmpty()) {
      JSONObject postObject = new JSONObject();
      try {
        for (HashMap.Entry<String, Object> entry : params.entrySet()) {
          postObject.put(entry.getKey(), entry.getValue());
        }
      } catch (Exception e) {
        AppLog.e(RequestHelper.class, e.getMessage());
      }
      builder.post(RequestBody.create(postObject.toString(), MediaType.parse("application/json")));

      logBuilder.append("\nposts ->\n");
      logBuilder.append(postObject.toString());
    }
    okHttpClient.newCall(builder.build()).enqueue(new Callback() {

      @Override
      public void onResponse(@NonNull Call call, @NonNull Response res) throws IOException {
        removeIdFromListTag(id);
        final Response response = unzip(res);
        ResponseBody body = response.body();
        if (body != null) {
          AppLog.w(RequestHelper.class, logBuilder.toString());
          boolean isCanceled = call.isCanceled();
          final String str = body.string();
          if (response.isSuccessful()) {
            AppLog.i(RequestHelper.class, "isSuccessful ->true / isCanceled ->" + isCanceled + "\n" + str);
            if (!isCanceled && listener != null) {
              AndroidUtilities.runOnUIThread(() -> listener.onResponse(true, str));
            }
          } else {
            AppLog.e(RequestHelper.class, "isSuccessful ->false / isCanceled ->" + isCanceled + "\n" + str);
            if (!isCanceled && listener != null) {
              AndroidUtilities.runOnUIThread(() -> listener.onResponse(false, str));
            }
          }
        }
      }

      @Override
      public void onFailure(@NonNull Call call, @NonNull IOException e) {
        AppLog.w(RequestHelper.class, logBuilder.toString());
        boolean isCanceled = call.isCanceled();
        AppLog.e(RequestHelper.class, "isSuccessful ->false / isCanceled ->" + isCanceled + "\n" + e.getMessage());
        removeIdFromListTag(id);
        if (!isCanceled) {
          if (listener != null) {
            AndroidUtilities.runOnUIThread(() -> listener.onResponse(false, e.getMessage()));
          }
        }
      }
    });
    return id;
  }

  private void addIdToListTag(Object tag, Integer id) {
    if (tag == null) {
      return;
    }
    List<Integer> objects = listTags.get(tag);
    if (objects == null) {
      listTags.put(tag, objects = new ArrayList<>());
    }
    if (objects.contains(id)) {
      return;
    }
    objects.add(id);
  }

  private void removeTagFromListTag(Object tag) {
    if (tag == null) {
      return;
    }
    listTags.remove(tag);
  }

  private void removeIdFromListTag(Integer id) {
    for (Map.Entry<Object, List<Integer>> entry : listTags.entrySet()) {
      List<Integer> ides = entry.getValue();
      if (ides.contains(id)) {
        ides.remove(id);
        if (ides.isEmpty()) {
          listTags.remove(entry.getKey());
        }
        break;
      }
    }
  }

  public void cancelAll() {
    listTags.clear();
    List<Call> calls = new ArrayList<>();
    calls.addAll(okHttpClient.dispatcher().queuedCalls());
    calls.addAll(okHttpClient.dispatcher().runningCalls());
    if (calls.isEmpty()) {
      return;
    }
    for (Call call : calls) {
      call.cancel();
    }
  }

  public void cancel(Object... tags) {
    for (Object tag : tags) {
      List<Integer> ides = listTags.get(tag);
      if (ides != null) {
        for (Integer id : ides) {
          cancelCall(id);
        }
        removeTagFromListTag(tag);
      }
    }
  }

  public void cancelId(int id) {
    if (id <= 0) {
      return;
    }
    removeIdFromListTag(id);
    cancelCall(id);
  }

  public boolean hasRequest(Object tag) {
    List<Call> calls = new ArrayList<>();
    calls.addAll(okHttpClient.dispatcher().queuedCalls());
    calls.addAll(okHttpClient.dispatcher().runningCalls());
    if (!calls.isEmpty() && tag != null) {
      for (Call call : calls) {
        if (tag.equals(call.request().tag())) {
          return true;
        }
      }
    }
    return false;
  }

  private void cancelCall(Object tag) {
    List<Call> calls = new ArrayList<>();
    calls.addAll(okHttpClient.dispatcher().queuedCalls());
    calls.addAll(okHttpClient.dispatcher().runningCalls());
    if (calls.isEmpty() || tag == null) {
      return;
    }
    for (Call call : calls) {
      if (tag.equals(call.request().tag())) {
        call.cancel();
      }
    }
  }

  private Response unzip(@NonNull Response response) {
    ResponseBody body = response.body();
    if (body == null) {
      return response;
    }
    String str = response.headers().get("Content-Encoding");
    if (str != null && str.equals("gzip")) {
      GzipSource gzipSource = new GzipSource(body.source());
      MediaType type = body.contentType();
      response = response.newBuilder().headers(response.headers().newBuilder().build()).body(new RealResponseBody(type != null ? type.toString() : null, body.contentLength(), Okio.buffer(gzipSource))).build();
    }
    return response;
  }
}
