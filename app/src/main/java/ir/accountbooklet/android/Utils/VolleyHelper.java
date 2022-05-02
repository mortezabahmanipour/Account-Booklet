//package ir.accountbooklet.android.Utils;
//
//import android.text.TextUtils;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Cache;
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.NetworkResponse;
//import com.android.volley.ParseError;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.RetryPolicy;
//import com.android.volley.toolbox.HttpHeaderParser;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.Map;
//import java.util.zip.GZIPInputStream;
//
//import ir.accountbooklet.android.ApplicationLoader;
//
//public class VolleyHelper {
//
//  private static volatile VolleyHelper Instance;
//
//  public static synchronized VolleyHelper getInstance() {
//    VolleyHelper localInstance = Instance;
//    if (localInstance == null) {
//      synchronized (VolleyHelper.class) {
//        localInstance = Instance;
//        if (localInstance == null) {
//          Instance = localInstance = new VolleyHelper();
//        }
//      }
//    }
//    return localInstance;
//  }
//
//  private static final int DEFAULT_TIME_OUT = 25000;
//  private RequestQueue requestQueue;
//
//  public interface VolleyListener {
//    void onResponse(boolean isSucceed, String response);
//  }
//
//  private VolleyHelper() {
//    requestQueue = Volley.newRequestQueue(ApplicationLoader.applicationContext);
//  }
//
//  public void addToRequestQueue(String url, VolleyListener listener) {
//    addToRequestQueue(url, null, null, false,  listener);
//  }
//
//  public void addToRequestQueue(String url, Map<String, String> params, VolleyListener listener) {
//    addToRequestQueue(url, params, null, false,  listener);
//  }
//
//  public void addToRequestQueue(String url, Map<String, String> params, Map<String, String> headersParams, boolean stringResponse, VolleyListener listener) {
//    requestQueue.add(request(url, params, headersParams, stringResponse, listener));
//  }
//
//  private StringRequest request(final String url, final Map<String, String> params, final Map<String, String> headersParams, final boolean stringResponse, final VolleyListener listener) {
//    final int method = params != null ? Request.Method.POST : Request.Method.GET;
//
//    return new StringRequest(method, url, response -> {
//      listener.onResponse(true, response);
//      AppLog.i(VolleyHelper.class, ".\nurl ->" + url + "\n" + ("params ->" + (params != null ? params.toString() : "")) + "\nresponse -> " + response);
//    }, error -> {
//      listener.onResponse(false, error.getMessage());
//      AppLog.e(VolleyHelper.class, ".\nurl ->" + url + "\n" + ("params ->" + (params != null ? params.toString() : "")) + "\nerrorResponse -> " + error.getMessage());
//    }) {
//      @Override
//      protected Response<String> parseNetworkResponse(NetworkResponse response) {
//        return stringResponse ? networkResponse(response) : super.parseNetworkResponse(response);
//      }
//
//      @Override
//      public Map<String, String> getHeaders() throws AuthFailureError {
//        return headersParams != null && headersParams.size() > 0 ? headersParams : super.getHeaders();
//      }
//
//      @Override
//      protected Map<String, String> getParams() throws AuthFailureError {
//        return params != null && params.size() > 0 ? params : super.getParams();
//      }
//
//      @Override
//      public RetryPolicy getRetryPolicy() {
//        return new DefaultRetryPolicy(DEFAULT_TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//      }
//
//      @Override
//      public Object getTag() {
//        return TextUtils.isEmpty(url) ? VolleyHelper.this : url;
//      }
//
//      @Override
//      public void cancel() {
//        super.cancel();
//        AppLog.e(VolleyHelper.class, ".\nurl ->" + url + "\n" + ("params ->" + (params != null ? params.toString() : "")) + "\nerrorResponse -> request canceled");
//      }
//    };
//  }
//
//  public void cancel(Object...tag) {
//    if (tag == null) {
//      return;
//    }
//    for (Object ob : tag) {
//      if (ob != null) {
//        requestQueue.cancelAll(ob);
//      }
//    }
//  }
//
//  public void destroy() {
//    Cache cache = requestQueue.getCache();
//    if (cache != null) {
//      cache.clear();
//    }
//  }
//
//  private Response<String> networkResponse(NetworkResponse response) {
//    StringBuilder output = new StringBuilder();
//    try {
//      GZIPInputStream gStream = new GZIPInputStream(new ByteArrayInputStream(response.data));
//      InputStreamReader reader = new InputStreamReader(gStream);
//      BufferedReader in = new BufferedReader(reader, 16384);
//      String read;
//      while ((read = in.readLine()) != null) {
//        output.append(read).append("\n");
//      }
//      reader.close();
//      in.close();
//      gStream.close();
//    } catch (IOException e) {
//      return Response.error(new ParseError());
//    }
//    return Response.success(output.toString(), HttpHeaderParser.parseCacheHeaders(response));
//  }
//}
