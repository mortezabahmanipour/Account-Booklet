package ir.accountbooklet.android.Utils;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import ir.accountbooklet.android.ApplicationLoader;
import ir.accountbooklet.android.Customs.TextView;
import ir.accountbooklet.android.R;

public class AndroidUtilities {

  public static Random random = new Random();
  public static int statusBarHeight = 0;
  public static int navigationBarHeight = 0;
  public static int toolbarHeight = 0;
  public static float density = 1;
  public static Point displaySize = new Point();
  public static Typeface IRANSans_FaNum;
  private static boolean instanceCreated;
  private static boolean isFullScreen;

  public static void createInstance(Context context) {
    if (instanceCreated) {
      return;
    }

    try {
      Resources resources = context.getResources();

      //initialize statusBarHeight
      int status_bar_height = resources.getIdentifier("status_bar_height", "dimen", "android");
      if (status_bar_height > 0) {
        statusBarHeight = resources.getDimensionPixelSize(status_bar_height);
      }

      //initialize navigationBar
      int navigationBarResourceId = resources.getIdentifier("config_showNavigationBar", "bool", "android");
      boolean hasOnScreenNavBar = navigationBarResourceId > 0 && resources.getBoolean(navigationBarResourceId);
      navigationBarResourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
      if (hasOnScreenNavBar && navigationBarResourceId > 0) {
        navigationBarHeight = resources.getDimensionPixelSize(navigationBarResourceId);
      }

      //initialize toolbarHeight
      toolbarHeight = resources.getDimensionPixelSize(R.dimen.toolbar_height);

      //initialize displaySize
      DisplayMetrics displayMetrics = new DisplayMetrics();
      WindowManager windowManager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
      if (windowManager != null) windowManager.getDefaultDisplay().getMetrics(displayMetrics);
      displaySize.x = displayMetrics.widthPixels;
      displaySize.y = displayMetrics.heightPixels;

      //initialize density
      density = resources.getDisplayMetrics().density;

      //initialize typeFace
      IRANSans_FaNum = Typeface.createFromAsset(context.getAssets(), "fonts/IRANSans_FaNum.ttf");

      //Instance Created
      instanceCreated = true;
    } catch (Exception e) {
      AppLog.e(AndroidUtilities.class, "createInstance ->\n" + e.getMessage());
    }
  }

  public static void runOnUIThread(Runnable runnable) {
    runOnUIThread(runnable, 0);
  }

  public static void runOnUIThread(Runnable runnable, long delay) {
    if (delay == 0) {
      ApplicationLoader.applicationHandler.post(runnable);
    } else {
      ApplicationLoader.applicationHandler.postDelayed(runnable, delay);
    }
  }

  public static void cancelRunOnUIThread(Runnable runnable) {
    ApplicationLoader.applicationHandler.removeCallbacks(runnable);
  }

  public static int dp(float value) {
    if (value == 0) {
      return 0;
    }
    return (int) Math.ceil(density * value);
  }

  public static int dp2(float value) {
    if (value == 0) {
      return 0;
    }
    return (int) Math.floor(density * value);
  }

  public static float dpf(float value) {
    if (value == 0) {
      return 0;
    }
    return density * value;
  }

  public static float pix(float dp) {
    if (dp == 0) {
      return 0;
    }
    return dp / density;
  }

  public static int int_pix(float dp) {
    if (dp == 0) {
      return 0;
    }
    return (int)(dp / density);
  }

  public static int getHeightWithParent(int parent, int width, int height) {
    if (parent > width) {
      int sub = parent - width;
      float percent = sub * 100f / width;
      return height + (int) (percent / 100f * height);
    } else {
      int sub = width - parent;
      float percent = sub * 100f / width;
      return height - (int) (percent / 100f * height);
    }
  }

  public static boolean showKeyboard(View view) {
    if (view == null) {
      return false;
    }
    try {
      view.requestFocus();
      InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
      return inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    } catch (Exception e) {
      AppLog.e(AndroidUtilities.class, "showKeyboard ->" + e.getMessage());
    }
    return false;
  }

  public static void showSpeak(Fragment frg, int req) {
    Context context;
    if (frg == null || (context = frg.getContext()) == null) {
      return;
    }
    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "fa_IR");
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa_IR");
    intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.speak));
    if (intent.resolveActivity(context.getPackageManager()) != null) {
      frg.startActivityForResult(intent, req);
    }
  }

  public static void hideKeyboard(View view) {
    if (view == null) {
      return;
    }
    try {
      InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
      if (!imm.isActive()) {
        return;
      }
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    } catch (Exception e) {
      AppLog.e(AndroidUtilities.class, "hideKeyboard ->" + e.getMessage());
    }
  }

  public static void addMediaToGallery(String ...paths) {
    if (paths == null || paths.length <= 0) return;
    try {
      MediaScannerConnection.scanFile(ApplicationLoader.applicationContext, paths, null, null);
    } catch (Exception e) {
      AppLog.e(AndroidUtilities.class, "addMediaToGallery ->" + e.getMessage());
    }
  }

  public static void setVisibility(View view, int visibility) {
    if (view == null || view.getVisibility() == visibility) {
      return;
    }
    view.setVisibility(visibility);
  }

  public static void showActionWork(View view, String str, boolean top) {
    Context context = view.getContext();
    Toast toast = new Toast(context);
    Rect rect = new Rect();
    int xOffset = 0;
    int yOffset = 0;

    view.getGlobalVisibleRect(rect);

    if (top) {
      rect.top = rect.top + AndroidUtilities.dp(-120);
      rect.bottom = rect.bottom + AndroidUtilities.dp(-120);
    }

    try {
      TextView text = new TextView(context);
      text.setSelectorBuilder(new Theme.SelectorBuilder().selectable(false).color(ContextCompat.getColor(context, R.color.colorToast)).style(Theme.RECT).radii(AndroidUtilities.dp(2)));
      text.setPadding(AndroidUtilities.dp(13), AndroidUtilities.dp(5), AndroidUtilities.dp(13), AndroidUtilities.dp(5));
      text.setTextColor(0xFFFFFFFF);
      text.setText(str);
      text.setTextSize(12);

      int toastHufWidth = (int) (text.getPaint().measureText(str) + text.getPaddingLeft() + text.getPaddingRight()) / 2;

      int xOff = (rect.right - rect.width() / 2) - toastHufWidth;
      int padding = AndroidUtilities.dp(10);

      xOffset = Math.max(xOff, padding);
      xOffset = xOff + toastHufWidth * 2 > AndroidUtilities.displaySize.x - padding ? AndroidUtilities.displaySize.x - padding - toastHufWidth * 2 : xOffset;
      yOffset = rect.bottom - AndroidUtilities.statusBarHeight / 2;

      toast.setView(text);
      toast.setDuration(Toast.LENGTH_LONG);
      toast.setGravity(Gravity.TOP | 3, xOffset, yOffset);
      toast.show();
    } catch (Exception e) {
      toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
      toast.setGravity(Gravity.NO_GRAVITY, xOffset, yOffset);
      toast.show();
    }
  }

  public static void toast(Context context, @StringRes int res) {
    toast(context, context.getString(res));
  }

  public static void toast(Context context, String str) {
    try {
      Toast toast = new Toast(context);
      TextView text = new TextView(context);
      text.setSelectorBuilder(new Theme.SelectorBuilder().selectable(false).color(ContextCompat.getColor(context, R.color.colorToast)).style(Theme.RECT).radii(AndroidUtilities.dp(7)));
      text.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10));
      text.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
      text.setText(str);
      text.setTextSize(14);
      toast.setDuration(Toast.LENGTH_LONG);
      toast.setView(text);
      toast.show();
    } catch (Exception e) {
      Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }
  }

  public static void restApp() {
    Intent intent = ApplicationLoader.applicationContext.getPackageManager().getLaunchIntentForPackage(ApplicationLoader.applicationContext.getPackageName());
    if (intent == null) {
      return;
    }
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(ApplicationLoader.applicationContext, intent);
  }

  public static String enBase64(String str) {
    return Base64.encodeToString((TextUtils.isEmpty(str) ? "" : str).getBytes(), Base64.DEFAULT);
  }

  public static String deBase64(String str) {
    return new String(Base64.decode((TextUtils.isEmpty(str) ? "" : str), Base64.DEFAULT));
  }

  public static String enBase64List(String[] str) {
    if (str == null) {
      return "";
    }
    String s = Arrays.toString(str).replace(" ", "").replace("]", "").replace("[", "").replace(",", "((-m-))");
    return Base64.encodeToString((TextUtils.isEmpty(s) ? "" : s).getBytes(), Base64.DEFAULT);
  }

  public static String[] deBase64List(String str) {
    if (TextUtils.isEmpty(str)) {
      return null;
    }
    return deBase64(str).replace("((-m-))", ",").split(",");
  }

  @SuppressLint("DefaultLocale")
  public static String formatFileSize(long size, boolean removeZero) {
    if (size < 1024) {
      return String.format("%d B", size);
    } else if (size < 1024 * 1024) {
      float value = size / 1024.0f;
      if (removeZero && (value - (int) value) * 10 == 0) {
        return String.format("%d KB", (int) value);
      } else {
        return String.format("%.1f KB", value);
      }
    } else if (size < 1024 * 1024 * 1024) {
      float value = size / 1024.0f / 1024.0f;
      if (removeZero && (value - (int) value) * 10 == 0) {
        return String.format("%d MB", (int) value);
      } else {
        return String.format("%.1f MB", value);
      }
    } else {
      float value = size / 1024.0f / 1024.0f / 1024.0f;
      if (removeZero && (value - (int) value) * 10 == 0) {
        return String.format("%d GB", (int) value);
      } else {
        return String.format("%.1f GB", value);
      }
    }
  }

  public static String formatBrief(long value) {
    NavigableMap<Long, String> suffixes = new TreeMap<>();
    suffixes.put(1_000L, "k");
    suffixes.put(1_000_000L, "m");
    suffixes.put(1_000_000_000L, "g");
    suffixes.put(1_000_000_000_000L, "t");
    suffixes.put(1_000_000_000_000_000L, "p");
    suffixes.put(1_000_000_000_000_000_000L, "e");

    if (value == Long.MIN_VALUE) {
      return formatBrief(Long.MIN_VALUE + 1);
    }
    if (value < 0) {
      return "-" + formatBrief(-value);
    }
    if (value < 1000) {
      return Long.toString(value);
    }

    Map.Entry<Long, String> e = suffixes.floorEntry(value);
    Long divideBy = e.getKey();
    String suffix = e.getValue();

    long truncated = value / (divideBy / 10);
    boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10f);
    return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10f) + suffix;
  }

  public static void addToClipboard(CharSequence str) {
    try {
      ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE);
      ClipData clip = ClipData.newPlainText("label", str);
      clipboard.setPrimaryClip(clip);
    } catch (Exception e) {
      AppLog.e(AndroidUtilities.class, "addToClipboard ->" + e.getMessage());
    }
  }

  public static Bitmap fastblur(Bitmap source, float radius, float scale) {
    if (source == null) {
      return null;
    }

    int mRadius = (int) (radius >= 1 ? 25 : radius <= 0 ? 0 : radius * 25);
    int width = Math.round(source.getWidth() * scale);
    int height = Math.round(source.getHeight() * scale);

    source = Bitmap.createScaledBitmap(source, width, height, false);
    Bitmap bitmap = source.copy(source.getConfig(), true);

    int w = bitmap.getWidth();
    int h = bitmap.getHeight();

    int[] pix = new int[w * h];
    bitmap.getPixels(pix, 0, w, 0, 0, w, h);

    int wm = w - 1;
    int hm = h - 1;
    int wh = w * h;
    int div = mRadius + mRadius + 1;

    int[] r = new int[wh];
    int[] g = new int[wh];
    int[] b = new int[wh];
    int[] a = new int[wh];
    int rsum, gsum, bsum, asum, x, y, i, p, yp, yi, yw;
    int[] vmin = new int[Math.max(w, h)];

    int divsum = (div + 1) >> 1;
    divsum *= divsum;
    int[] dv = new int[256 * divsum];
    for (i = 0; i < 256 * divsum; i++) {
      dv[i] = (i / divsum);
    }

    yw = yi = 0;

    int[][] stack = new int[div][4];
    int stackpointer;
    int stackstart;
    int[] sir;
    int rbs;
    int r1 = mRadius + 1;
    int routsum, goutsum, boutsum, aoutsum;
    int rinsum, ginsum, binsum, ainsum;

    for (y = 0; y < h; y++) {
      rinsum = ginsum = binsum = ainsum = routsum = goutsum = boutsum = aoutsum = rsum = gsum = bsum = asum = 0;
      for (i = -mRadius; i <= mRadius; i++) {
        p = pix[yi + Math.min(wm, Math.max(i, 0))];
        sir = stack[i + mRadius];
        sir[0] = (p & 0xff0000) >> 16;
        sir[1] = (p & 0x00ff00) >> 8;
        sir[2] = (p & 0x0000ff);
        sir[3] = 0xff & (p >> 24);

        rbs = r1 - Math.abs(i);
        rsum += sir[0] * rbs;
        gsum += sir[1] * rbs;
        bsum += sir[2] * rbs;
        asum += sir[3] * rbs;
        if (i > 0) {
          rinsum += sir[0];
          ginsum += sir[1];
          binsum += sir[2];
          ainsum += sir[3];
        } else {
          routsum += sir[0];
          goutsum += sir[1];
          boutsum += sir[2];
          aoutsum += sir[3];
        }
      }
      stackpointer = mRadius;

      for (x = 0; x < w; x++) {
        r[yi] = dv[rsum];
        g[yi] = dv[gsum];
        b[yi] = dv[bsum];
        a[yi] = dv[asum];

        rsum -= routsum;
        gsum -= goutsum;
        bsum -= boutsum;
        asum -= aoutsum;

        stackstart = stackpointer - mRadius + div;
        sir = stack[stackstart % div];

        routsum -= sir[0];
        goutsum -= sir[1];
        boutsum -= sir[2];
        aoutsum -= sir[3];

        if (y == 0) {
          vmin[x] = Math.min(x + mRadius + 1, wm);
        }
        p = pix[yw + vmin[x]];

        sir[0] = (p & 0xff0000) >> 16;
        sir[1] = (p & 0x00ff00) >> 8;
        sir[2] = (p & 0x0000ff);
        sir[3] = 0xff & (p >> 24);

        rinsum += sir[0];
        ginsum += sir[1];
        binsum += sir[2];
        ainsum += sir[3];

        rsum += rinsum;
        gsum += ginsum;
        bsum += binsum;
        asum += ainsum;

        stackpointer = (stackpointer + 1) % div;
        sir = stack[(stackpointer) % div];

        routsum += sir[0];
        goutsum += sir[1];
        boutsum += sir[2];
        aoutsum += sir[3];

        rinsum -= sir[0];
        ginsum -= sir[1];
        binsum -= sir[2];
        ainsum -= sir[3];

        yi++;
      }
      yw += w;
    }
    for (x = 0; x < w; x++) {
      rinsum = ginsum = binsum = ainsum = routsum = goutsum = boutsum = aoutsum = rsum = gsum = bsum = asum = 0;
      yp = -mRadius * w;
      for (i = -mRadius; i <= mRadius; i++) {
        yi = Math.max(0, yp) + x;

        sir = stack[i + mRadius];

        sir[0] = r[yi];
        sir[1] = g[yi];
        sir[2] = b[yi];
        sir[3] = a[yi];

        rbs = r1 - Math.abs(i);

        rsum += r[yi] * rbs;
        gsum += g[yi] * rbs;
        bsum += b[yi] * rbs;
        asum += a[yi] * rbs;

        if (i > 0) {
          rinsum += sir[0];
          ginsum += sir[1];
          binsum += sir[2];
          ainsum += sir[3];
        } else {
          routsum += sir[0];
          goutsum += sir[1];
          boutsum += sir[2];
          aoutsum += sir[3];
        }

        if (i < hm) {
          yp += w;
        }
      }
      yi = x;
      stackpointer = mRadius;
      for (y = 0; y < h; y++) {
        pix[yi] = (dv[asum] << 24) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

        rsum -= routsum;
        gsum -= goutsum;
        bsum -= boutsum;
        asum -= aoutsum;

        stackstart = stackpointer - mRadius + div;
        sir = stack[stackstart % div];

        routsum -= sir[0];
        goutsum -= sir[1];
        boutsum -= sir[2];
        aoutsum -= sir[3];

        if (x == 0) {
          vmin[y] = Math.min(y + r1, hm) * w;
        }
        p = x + vmin[y];


        sir[0] = r[p];
        sir[1] = g[p];
        sir[2] = b[p];
        sir[3] = a[p];

        rinsum += sir[0];
        ginsum += sir[1];
        binsum += sir[2];
        ainsum += sir[3];

        rsum += rinsum;
        gsum += ginsum;
        bsum += binsum;
        asum += ainsum;

        stackpointer = (stackpointer + 1) % div;
        sir = stack[stackpointer];

        routsum += sir[0];
        goutsum += sir[1];
        boutsum += sir[2];
        aoutsum += sir[3];

        rinsum -= sir[0];
        ginsum -= sir[1];
        binsum -= sir[2];
        ainsum -= sir[3];

        yi += w;
      }
    }
    bitmap.setPixels(pix, 0, w, 0, 0, w, h);
    return (bitmap);
  }

  public static boolean isPackageInstalled(final String packageName) {
    try {
      ApplicationLoader.applicationContext.getPackageManager().getApplicationInfo(packageName, 0);
      return true;
    } catch (PackageManager.NameNotFoundException e) {
      return false;
    }
  }

  public static class SortFileName implements Comparator<File> {
    @Override
    public int compare(File f1, File f2) {
      return Long.compare(f2.lastModified(), f1.lastModified());
    }
  }

  public static String encodeBase64(Bitmap bitmap, int quality) {
    if (bitmap == null) {
      return null;
    }

    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
    return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
  }


  public static Bitmap decodeBase64Bitmap(String input) {
    if (TextUtils.isEmpty(input)) {
      return null;
    }
    byte[] decodedBytes = Base64.decode(input, 0);
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
  }

  public static String formatPrice(long l, boolean removeZero) {
    String price = String.valueOf(l);
    if (price.equals("0")) {
      return removeZero ? "" : "0";
    }
    price = price.replace(" ","");
    price = price.replace(",","");
    StringBuilder result = new StringBuilder();
    for (int i=0; i < price.length(); i++) {
      if (i > 0 && i % 3 == 0) {
        result.insert(0, ",");
      }
      result.insert(0, price.charAt(price.length() - 1 - i));
    }
    return result.toString();
  }

  public static String formatTime(long time) {
    if (time >= 3600) {
      long hor = time / 3600;
      long min = (time % 3600) / 60;
      return (hor < 10 ? "0" + hor : hor) + ":" + (min < 10 ? "0" + min : min);
    } else {
      long min = (int)(time / 60f);
      long sec = (int)(time % 60f);
      return (min < 10 ? "0" + min : min) + ":" + (sec < 10 ? "0" + sec : sec);
    }
  }

  public static String formatDuration(int duration, boolean isLong) {
    int h = duration / 3600;
    int m = duration / 60 % 60;
    int s = duration % 60;
    if (h == 0) {
      if (isLong) {
        return String.format(Locale.US, "%02d:%02d", m, s);
      } else {
        return String.format(Locale.US, "%d:%02d", m, s);
      }
    } else {
      return String.format(Locale.US, "%d:%02d:%02d", h, m, s);
    }
  }

  public static boolean isImage(@Nullable File file) {
    if (file == null) {
      return false;
    }
    String[] formats =  new String[] {".jpg", ".JPG", ".png", ".PNG", ".jpeg", ".JPEG"};
    for(String f : formats) {
      if (file.getName().endsWith(f)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isNumber(String str) {
    try {
      Long.parseLong(str);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static void openLink(Context context, String link) {
    startActivity(context, new Intent(Intent.ACTION_VIEW).setData(Uri.parse(link)));
  }

  public static void startActivity(Context context, Intent intent) {
    try {
      context.startActivity(intent);
    } catch (Exception e) {
      AppLog.e(AndroidUtilities.class, "startActivity: " + e.getMessage());
    }
  }

  public static void vibrate(long millis) {
//    ((Vibrator) ApplicationLoader.applicationContext.getSystemService(VIBRATOR_SERVICE)).vibrate(millis);
  }

  public static boolean isVoiceAvailable() {
    return ApplicationLoader.applicationContext.getPackageManager().queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0).size() != 0;
  }

  public static void shareText(Context context, String text) {
    if (TextUtils.isEmpty(text)) {
      return;
    }
    Intent sendIntent = new Intent(Intent.ACTION_SEND);
    sendIntent.putExtra(Intent.EXTRA_TEXT, text);
    sendIntent.setType("text/plain");
    startActivity(context, Intent.createChooser(sendIntent, context.getString(R.string.share)));
  }

  public static void showFile(Context context, File file) {
    if (file == null || !file.exists()) {
      toast(context, R.string.file_not_exists);
      return;
    }
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setDataAndType(FileUtils.getFileUri(context, file), getMimeType(file));
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }
    startActivity(context, intent);
  }

  public static void volumeTextSize(View view, int ratioSize) {
    if (view == null) return;
    if (view instanceof ViewGroup) {
      ViewGroup group = (ViewGroup) view;
      for (int i = 0; i < group.getChildCount(); ++i) {
        View mChild = group.getChildAt(i);
        volumeTextSize(mChild, ratioSize);
      }
    } else if (view instanceof TextView) {
      TextView textView = (TextView) view;
      textView.setTextSize(AndroidUtilities.pix(textView.getTextSize()) + ratioSize);
    }
  }

  public static String getMimeType(File file) {
    String realMimeType = null;
    try {
      String fileName = file.getName();
      MimeTypeMap myMime = MimeTypeMap.getSingleton();
      int idx = fileName.lastIndexOf('.');
      if (idx != -1) {
        String ext = fileName.substring(idx + 1);
        realMimeType = myMime.getMimeTypeFromExtension(ext.toLowerCase());
      }
    } catch (Exception e) {
      AppLog.e(AndroidUtilities.class, e.getMessage());
    }
    return realMimeType != null ? realMimeType : "text/plain";
  }

  public static String encodeBase64ToString(String str) {
    return str != null ? encodeBase64ToString(str.getBytes()) : "";
  }

  public static String encodeBase64ToString(byte[] enBytes) {
    return Base64.encodeToString(enBytes, Base64.DEFAULT);
  }

  public static byte[] encodeBase64(byte[] enBytes) {
    return Base64.encode(enBytes, Base64.DEFAULT);
  }

  public static byte[] decodeBase64(String str) {
    return Base64.decode(str, Base64.DEFAULT);
  }

  public static String decodeBase64ToString(String str) {
    if (str == null) {
      return "";
    }
    try {
      return new String(Base64.decode(str, Base64.DEFAULT), StandardCharsets.UTF_8);
    } catch (Exception e) {
      AppLog.e(AndroidUtilities.class, "decodeBase64ToString: " + e.getMessage());
    }
    return "";
  }

  public static void destroy() {
    statusBarHeight = 0;
    navigationBarHeight = 0;
    toolbarHeight = 0;
    density = 0;
    displaySize.x = 0;
    displaySize.y = 0;
    IRANSans_FaNum = null;
    instanceCreated = false;
  }
}
