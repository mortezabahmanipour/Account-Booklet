package ir.accountbooklet.android.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import ir.accountbooklet.android.API;
import ir.accountbooklet.android.ApplicationLoader;
import ir.accountbooklet.android.BuildConfig;
import ir.accountbooklet.android.Constants;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Session.Session;

public class MarketUtils {

  public static boolean isPublic() {
    return Session.getInstance().isBoolean("is_public", false);
  }

  public static String getMarketLinkShare(boolean haveMarket) {
    if (!haveMarket && isPublic()) {
      return API.BASE_MORTINO;
    }
    Map<String, String> list = new HashMap<>();
    list.put(Constants.ID_BAZAAR, "http://cafebazaar.ir/app/" + BuildConfig.APPLICATION_ID);
    list.put(Constants.ID_MAYKET, "https://myket.ir/app/" + BuildConfig.APPLICATION_ID);
    list.put(Constants.ID_IRANAPPS, "http://iranapps.ir/app/" + BuildConfig.APPLICATION_ID);
    list.put(Constants.ID_GOOGLE, "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
    list.put(Constants.ID_SAMSUNG, "https://apps.samsung.com/appquery/appDetail.as?appId=" + BuildConfig.APPLICATION_ID);
    final Map.Entry<String, String> shareLink = getItemWithMarketId(list);
    if (shareLink == null || TextUtils.isEmpty(shareLink.getValue())) {
      return "";
    }
    return shareLink.getValue();
  }

  public static void sendComment(Context context) {
    Map<String, String> list = new HashMap<>();
    list.put(Constants.ID_BAZAAR, "bazaar://details?id=" + BuildConfig.APPLICATION_ID);
    list.put(Constants.ID_MAYKET, "myket://comment?id=" + BuildConfig.APPLICATION_ID);
    list.put(Constants.ID_IRANAPPS, "iranapps://app/" + BuildConfig.APPLICATION_ID + " ?a=comment&r=5");
    list.put(Constants.ID_GOOGLE, "market://details?id=" + BuildConfig.APPLICATION_ID);
    list.put(Constants.ID_SAMSUNG, "");
    final Map.Entry<String, String> ratingLink = getItemWithMarketId(list);
    if (ratingLink == null || TextUtils.isEmpty(ratingLink.getValue())) {
      AndroidUtilities.toast(ApplicationLoader.applicationContext, R.string.err_rating);
      return;
    }
    Intent intent = new Intent(ratingLink.getKey().equals(Constants.ID_BAZAAR) ? Intent.ACTION_EDIT : Intent.ACTION_VIEW);
    intent.setData(Uri.parse(ratingLink.getValue()));
    intent.setPackage(ratingLink.getKey());
    AndroidUtilities.startActivity(context, intent);
  }

  private static Map.Entry<String, String> getItemWithMarketId(Map<String, String> list) {
    if (list != null && !list.isEmpty()) {
      if (AndroidUtilities.isPackageInstalled(BuildConfig.MARKET_APPLICATION_ID)) {
        return new AbstractMap.SimpleEntry<>(BuildConfig.MARKET_APPLICATION_ID, list.get(BuildConfig.MARKET_APPLICATION_ID));
      }
      for (Map.Entry<String, String> item : list.entrySet()) {
        if (AndroidUtilities.isPackageInstalled(item.getKey())) {
          return item;
        }
      }
      if (BuildConfig.MARKET_APPLICATION_ID.equals(Constants.ID_BAZAAR)) {
        AndroidUtilities.toast(ApplicationLoader.applicationContext,  R.string.install_bazaar);
      } else if (BuildConfig.MARKET_APPLICATION_ID.equals(Constants.ID_MAYKET)) {
        AndroidUtilities.toast(ApplicationLoader.applicationContext, R.string.install_my_ket);
      } else if (BuildConfig.MARKET_APPLICATION_ID.equals(Constants.ID_IRANAPPS)) {
        AndroidUtilities.toast(ApplicationLoader.applicationContext, R.string.install_iran_apps);
      } else if (BuildConfig.MARKET_APPLICATION_ID.equals(Constants.ID_GOOGLE)) {
        AndroidUtilities.toast(ApplicationLoader.applicationContext, R.string.install_google_play);
      } else if (BuildConfig.MARKET_APPLICATION_ID.equals(Constants.ID_SAMSUNG)) {
        AndroidUtilities.toast(ApplicationLoader.applicationContext, R.string.install_galaxy_samsung);
      }
    }
    return null;
  }
}
