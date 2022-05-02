package ir.accountbooklet.android.Session;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import ir.accountbooklet.android.ApplicationLoader;
import ir.accountbooklet.android.BuildConfig;
import ir.accountbooklet.android.Utils.AndroidUtilities;

public class Session {

  private static volatile Session Instance;

  public static synchronized Session getInstance() {
    Session localInstance = Instance;
    if (localInstance == null) {
      synchronized (Session.class) {
        localInstance = Instance;
        if (localInstance == null) {
          Instance = localInstance = new Session();
        }
      }
    }
    return localInstance;
  }

  private static final String SHARED_PREFERENCE = "SharedPreference";
  private static final String NAME = "name";
  private static final String PASSWORD = "password_";
  private static final String FIRST_LOGIN = "first_login";
  private static final String TAPSELL_KEY = "tapsell_key";
  private static final String TAPSELL_BANNER_MAIN = "tapsell_banner_main";
  private static final String TAPSELL_BANNER_ACCOUNT = "tapsell_banner_account";
  private static final String ADIVERY_KEY = "adivery_key";
  private static final String ADIVERY_BANNER_MAIN = "adivery_banner_main";
  private static final String ADIVERY_BANNER_ACCOUNT = "adivery_banner_account";
  private static final String FINGERPRINT_LOGIN = "fingerprint_login";
  private SharedPreferences preferences;

  public Session() {
    preferences = ApplicationLoader.applicationContext.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE);
  }

  public void setName(String str) {
    preferences.edit().putString(NAME, AndroidUtilities.enBase64(str.trim())).apply();
  }

  public String getName() {
    return AndroidUtilities.deBase64(preferences.getString(NAME , ""));
  }

  public void setPassword(String str) {
    preferences.edit().putString(PASSWORD, AndroidUtilities.enBase64(str.trim())).apply();
  }

  public String getPassword() {
    return AndroidUtilities.deBase64(preferences.getString(PASSWORD , ""));
  }

  public void setFirstLogin(boolean b) {
    preferences.edit().putBoolean(FIRST_LOGIN, b).apply();
  }

  public boolean isFirstLogin() {
    return preferences.getBoolean(FIRST_LOGIN , false);
  }

  public void setFingerprintLogin(boolean b) {
    preferences.edit().putBoolean(FINGERPRINT_LOGIN, b).apply();
  }

  public boolean isFingerprintLogin() {
    return preferences.getBoolean(FINGERPRINT_LOGIN , false);
  }

//  public void setTapsellKey(String str) {
//    preferences.edit().putString(TAPSELL_KEY, str).apply();
//  }
//
//  public String getTapsellKey() {
//    return preferences.getString(TAPSELL_KEY, BuildConfig.TAPSELL_KEY);
//  }
//
//  public void setTapsellBannerMain(String str) {
//    preferences.edit().putString(TAPSELL_BANNER_MAIN, str).apply();
//  }
//
//  public String getTapsellBannerMain() {
//    return preferences.getString(TAPSELL_BANNER_MAIN, BuildConfig.TAPSELL_BANNER_MAIN);
//  }
//
//  public void setTapsellBannerAccount(String str) {
//    preferences.edit().putString(TAPSELL_BANNER_ACCOUNT, str).apply();
//  }
//
//  public String getTapsellBannerAccount() {
//    return preferences.getString(TAPSELL_BANNER_ACCOUNT, BuildConfig.TAPSELL_BANNER_ACCOUNT);
//  }

  public void setAdiveryKey(String str) {
    preferences.edit().putString(ADIVERY_KEY, str).apply();
  }

  public String getAdiveryKey() {
    return preferences.getString(ADIVERY_KEY, BuildConfig.ADIVERY_KEY);
  }

  public void setAdiveryBannerMain(String str) {
    preferences.edit().putString(ADIVERY_BANNER_MAIN, str).apply();
  }

  public String getAdiveryBannerMain() {
    return preferences.getString(ADIVERY_BANNER_MAIN, BuildConfig.ADIVERY_BANNER_MAIN);
  }

  public void setAdiveryBannerAccount(String str) {
    preferences.edit().putString(ADIVERY_BANNER_ACCOUNT, str).apply();
  }

  public String getAdiveryBannerAccount() {
    return preferences.getString(ADIVERY_BANNER_ACCOUNT, BuildConfig.ADIVERY_BANNER_ACCOUNT);
  }

  public String getString(String key, String def) {
    return preferences.getString(key, def);
  }

  public void setString(String key, String str) {
    preferences.edit().putString(key, str).apply();
  }

  public void setBoolean(String key, boolean value) {
    preferences.edit().putBoolean(key, value).apply();
  }

  public boolean isBoolean(String key, boolean def) {
    return preferences.getBoolean(key, def);
  }

  public boolean isActive() {
    return !TextUtils.isEmpty(getPassword());
  }
}
