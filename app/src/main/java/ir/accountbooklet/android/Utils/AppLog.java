package ir.accountbooklet.android.Utils;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import ir.accountbooklet.android.BuildConfig;
import ir.accountbooklet.android.Constants;

public class AppLog {
  private static final String TAG = AppLog.class.getSimpleName();

  public static void i(@NonNull Class<?> o, String msg) {
    for (String str : messages(msg)) {
      Log.i(TAG + " " + o.getSimpleName(), str);
    }
  }

  public static void e(@NonNull Class<?> o, String msg) {
    for (String str : messages(msg)) {
      Log.e(TAG + " " + o.getSimpleName(), str);
    }
  }

  public static void e(@NonNull Class<?> o, Throwable t) {
    if (t == null) {
      return;
    }
    StackTraceElement[] elements = t.getStackTrace();
    StringBuilder builder = new StringBuilder();
    for (StackTraceElement element : elements) {
      builder.append("message ->").append(t.getMessage()).append("\n");
      builder.append("lineNumber ->").append(element.getLineNumber()).append("\n");
      builder.append("methodName ->").append(element.getMethodName()).append("\n");
      builder.append("className ->").append(element.getClassName()).append("\n");
//      builder.append("fileName ->").append(element.getFileName()).append("\n");
      builder.append("---").append("\n");
    }
    e(o, builder.toString());
  }

  public static void w(@NonNull Class<?> o, String msg) {
    for (String str : messages(msg)) {
      Log.w(TAG + " " + o.getSimpleName(), str);
    }
  }

  private static String[] messages(String msg) {
    if (!BuildConfig.DEBUG || TextUtils.isEmpty(msg)) {
      return new String[] {};
    }
    StringBuilder messages = new StringBuilder();
    int index = 0;
    if (msg.length() < 2000) {
      messages.append(msg);
    } else {
      do {
        messages.append(msg.substring(index, Math.min((index + 2000), msg.length())));
        messages.append(Constants.SPLIT);
        index += 2000;
      } while (index < msg.length() && index < 100000);
    }
    return messages.toString().split(Constants.SPLIT);
  }
}
