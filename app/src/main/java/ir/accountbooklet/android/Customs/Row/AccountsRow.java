package ir.accountbooklet.android.Customs.Row;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import ir.accountbooklet.android.Constants;
import ir.accountbooklet.android.Customs.ImageView;
import ir.accountbooklet.android.Customs.TextView;
import ir.accountbooklet.android.Models.AccountsModel;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.FileUtils;
import ir.accountbooklet.android.Utils.LayoutHelper;
import ir.accountbooklet.android.Utils.LocaleController;
import ir.accountbooklet.android.Utils.SpanUtil;
import ir.accountbooklet.android.Utils.Theme;

public class AccountsRow extends FrameLayout {
  private ImageView ivThumbnail;
  private TextView tvUsername;
  private TextView tvTime;
  private TextView tvAmount;
  private TextView tvType;
  private int height = AndroidUtilities.dp(70);

  public void setModel(AccountsModel account) {
    if (account == null) {
      return;
    }
    tvUsername.setText(account.accountName);
    String price = AndroidUtilities.formatPrice(account.amount, false);
    SpannableStringBuilder spannable = new SpannableStringBuilder(price + " تومان");
    SpanUtil.span(spannable, 0, price.length(), Theme.getColor(Theme.key_app_text), AndroidUtilities.dp(12), true);
    tvAmount.setText(spannable);
    if (account.type == Constants.TYPE_DEBTOR) {
      tvType.setText(R.string.debtor);
      tvType.setTextColor(Theme.getColor(Theme.key_app_default4));
      tvTime.setText(LocaleController.getLocaleDate(account.date).getDateWithCalculate());
    } else if(account.type == Constants.TYPE_CREDITOR) {
      tvType.setText(R.string.creditor);
      tvType.setTextColor(Theme.getColor(Theme.key_app_accent));
      tvTime.setText(LocaleController.getLocaleDate(account.date).getDateWithCalculate());
    } else {
      tvType.setText(R.string.clearing);
      tvType.setTextColor(Theme.getColor(Theme.key_app_default));
      tvTime.setText(account.dateClearing > 0 ? String.format("شروع: %s پایان: %s", LocaleController.getLocaleDate(account.date).getDateWithCalculate(), LocaleController.getLocaleDate(account.dateClearing).getDateWithCalculate()) : LocaleController.getLocaleDate(account.date).getDateWithCalculate());
    }
    tvType.setBackground(Theme.createDrawable(ColorUtils.setAlphaComponent(tvType.getCurrentTextColor(), 30), 0, 0, Theme.RECT, AndroidUtilities.dp(12.5f)));
    String name = account.accountName.toUpperCase().substring(0, 1);
    Bitmap bitmap = Bitmap.createBitmap(AndroidUtilities.dp(50), AndroidUtilities.dp(50), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    Rect rect = new Rect(0, 0, AndroidUtilities.dp(50), AndroidUtilities.dp(50));
    TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    textPaint.setColor(0x08000000);
    canvas.drawRect(rect, textPaint);
    textPaint.setColor(Theme.getColor(Theme.key_app_text2));
    textPaint.setTypeface(AndroidUtilities.IRANSans_FaNum);
    textPaint.setTextSize(AndroidUtilities.dp(14));
    canvas.drawText(name, (rect.width() / 2f) - (textPaint.measureText(name) / 2f), (rect.height() / 2f) + (textPaint.getTextSize() / 3f), textPaint);
    Drawable drawable = new BitmapDrawable(getContext().getResources(), bitmap);
    File thumbnail = new File(FileUtils.MEDIA_PATH, account.id + ".jpg");
    Glide.with(getContext()).load(thumbnail).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).centerCrop().override(AndroidUtilities.dp(50)).placeholder(drawable).into(ivThumbnail);
  }

  public AccountsRow(@NonNull Context context) {
    super(context);
    ivThumbnail = new ImageView(context);
    ivThumbnail.setCircle(true);
    addView(ivThumbnail, LayoutHelper.createFrame(AndroidUtilities.dp(50), AndroidUtilities.dp(50), Gravity.CENTER_VERTICAL | 5, AndroidUtilities.dp(20), 0, AndroidUtilities.dp(20), 0));

    tvUsername = new TextView(context);
    tvUsername.setTextSize(12);
    tvUsername.setTextColor(Theme.getColor(Theme.key_app_text2));
    tvUsername.setSingleLine();
    tvUsername.setTypefaceStyle(Typeface.BOLD);
    tvUsername.setEllipsize(TextUtils.TruncateAt.END);
    tvUsername.setGravity(Gravity.CENTER | 5);
    addView(tvUsername, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(25), AndroidUtilities.dp(20), AndroidUtilities.dp(13), AndroidUtilities.dp(80), 0));

    tvTime = new TextView(context);
    tvTime.setTextSize(10);
    tvTime.setTextColor(Theme.getColor(Theme.key_app_text4));
    tvTime.setSingleLine();
    tvTime.setEllipsize(TextUtils.TruncateAt.END);
    tvTime.setGravity(Gravity.CENTER | 5);
    addView(tvTime, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(25), 5, AndroidUtilities.dp(20), AndroidUtilities.dp(38), AndroidUtilities.dp(80), 0));

    tvType = new TextView(context);
    tvType.setTextSize(10);
    tvType.setSingleLine();
    tvType.setGravity(Gravity.CENTER);
    tvType.setTypefaceStyle(Typeface.BOLD);
    tvType.setPadding(AndroidUtilities.dp(6), 0, AndroidUtilities.dp(6), 0);
    addView(tvType, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(20), 3, AndroidUtilities.dp(20), AndroidUtilities.dp(40.5f), AndroidUtilities.dp(80), 0));

    tvAmount = new TextView(context);
    tvAmount.setTextColor(Theme.getColor(Theme.key_app_text4));
    tvAmount.setTextSize(10);
    tvAmount.setSingleLine();
    tvAmount.setGravity(Gravity.CENTER);
//    tvAmount.setCompoundDrawables(Theme.arrow_left_drawable, null, null, null);
//    tvAmount.setDrawableSize(AndroidUtilities.dp(20));
//    tvAmount.setCompoundDrawablePadding(AndroidUtilities.dp(2));
//    tvAmount.setTypefaceStyle(Typeface.BOLD);
    addView(tvAmount, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(25), 3, AndroidUtilities.dp(20), AndroidUtilities.dp(13), AndroidUtilities.dp(80), AndroidUtilities.dp(10)));

    View shadow = new View(context);
    shadow.setBackgroundColor(0x10000000);
    addView(shadow, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(1), Gravity.BOTTOM, AndroidUtilities.dp(20), AndroidUtilities.dp(0), AndroidUtilities.dp(80), 0));

    Theme.setSelectorView(this, new Theme.SelectorBuilder().pressedColor(Theme.getColor(Theme.key_app_selector)));
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
  }
}
