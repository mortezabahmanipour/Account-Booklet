package ir.accountbooklet.android;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.telephony.TelephonyManager;

import ir.accountbooklet.android.Utils.AdiveryUtils;
import ir.accountbooklet.android.Utils.AppLog;
import ir.accountbooklet.android.Utils.MortinoUtils;

public class ApplicationLoader extends Application {
  public static volatile Context applicationContext;
  public static volatile Handler applicationHandler;

  @Override
  public void onCreate() {
    try {
      applicationContext = getApplicationContext();
    } catch (Throwable ignore) {

    }

    super.onCreate();

    if (applicationContext == null) {
      applicationContext = getApplicationContext();
    }

    applicationHandler = new Handler(applicationContext.getMainLooper());

    AdiveryUtils.getInstance().initialized(this);
    MortinoUtils.initialize();
  }

  @Override
  public void startActivity(Intent intent) {
    if (AdiveryUtils.getInstance().canStartActivity()) {
      super.startActivity(intent);
    }
  }

  public static boolean isRoaming() {
    try {
      ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationLoader.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
      if (netInfo != null) {
        return netInfo.isRoaming();
      }
    } catch (Exception e) {
      AppLog.e(ApplicationLoader.class, "isRoaming ->" + e.getMessage());
    }
    return false;
  }

  public static boolean isConnectedOrConnectingToWiFi() {
    try {
      ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationLoader.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo netInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
      NetworkInfo.State state = netInfo.getState();
      if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING || state == NetworkInfo.State.SUSPENDED) {
        return true;
      }
    } catch (Exception e) {
      AppLog.e(ApplicationLoader.class, "isConnectedOrConnectingToWiFi ->" + e.getMessage());
    }
    return false;
  }

  public static boolean isConnectedToWiFi() {
    try {
      ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationLoader.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo netInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
      if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
        return true;
      }
    } catch (Exception e) {
      AppLog.e(ApplicationLoader.class, "isConnectedToWiFi ->" + e.getMessage());
    }
    return false;
  }

  public static boolean isConnectionSlow() {
    try {
      ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationLoader.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
      if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
        switch (netInfo.getSubtype()) {
          case TelephonyManager.NETWORK_TYPE_1xRTT:
          case TelephonyManager.NETWORK_TYPE_CDMA:
          case TelephonyManager.NETWORK_TYPE_EDGE:
          case TelephonyManager.NETWORK_TYPE_GPRS:
          case TelephonyManager.NETWORK_TYPE_IDEN:
            return true;
        }
      }
    } catch (Exception e) {
      AppLog.e(ApplicationLoader.class, "isConnectionSlow ->" + e.getMessage());
    }
    return false;
  }

  public static boolean isNetworkOnline() {
    try {
      ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationLoader.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
      if (netInfo != null && (netInfo.isConnectedOrConnecting() || netInfo.isAvailable())) {
        return true;
      }

      netInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

      if (netInfo != null && netInfo.isConnectedOrConnecting()) {
        return true;
      } else {
        netInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
          return true;
        }
      }
    } catch (Exception e) {
      AppLog.e(ApplicationLoader.class, "isNetworkOnline ->" + e.getMessage());
      return true;
    }
    return false;
  }
}
