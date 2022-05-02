package ir.accountbooklet.android.Customs.Row;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import ir.accountbooklet.android.Customs.TextView;
import ir.accountbooklet.android.Models.AmountsModel;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.LayoutHelper;
import ir.accountbooklet.android.Utils.LocaleController;
import ir.accountbooklet.android.Utils.SpanUtil;
import ir.accountbooklet.android.Utils.Theme;

public class AmountsRow extends FrameLayout {
  private TextView tvAmountName;
  private TextView tvAmount;
  private TextView tvDateName;
  private TextView tvDate;
  private View shadow;
  private TextView tvDescription;

  @SuppressLint("SetTextI18n")
  public AmountsRow setModel(AmountsModel amount) {
    if (amount == null) {
      return this;
    }
    final String price = AndroidUtilities.formatPrice(Math.abs(amount.amount), false);
    SpannableStringBuilder spannable = new SpannableStringBuilder(price + " تومان");
    SpanUtil.span(spannable, 0, price.length(), Theme.getColor(Theme.key_app_text), AndroidUtilities.dp(12), true);
    tvAmount.setText(spannable);
    if (amount.amount < 0) {
      String strPaidOn = getResources().getString(R.string.paid_on);
      Spannable spanDate = new SpannableString(strPaidOn + " " +  LocaleController.getLocaleDate(amount.date).getDateAndHour());
      SpanUtil.span(spanDate, 0, strPaidOn.length(), Theme.getColor(Theme.key_app_default), -1, false);
      tvDate.setText(spanDate);
    } else {
      tvDate.setText(LocaleController.getLocaleDate(amount.date).getDateAndHour());
    }
    tvDescription.setText(amount.description);
    AndroidUtilities.setVisibility(tvDescription, TextUtils.isEmpty(amount.description) ? View.GONE : View.VISIBLE);
    AndroidUtilities.setVisibility(shadow, TextUtils.isEmpty(amount.description) ? View.GONE : View.VISIBLE);
    return this;
  }

  public AmountsRow(@NonNull Context context) {
    super(context);
    setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    final int shadowColor = 0x15000000;
    setBackground(Theme.createDrawable(0x08000000, shadowColor, AndroidUtilities.dp(1), Theme.RECT, AndroidUtilities.dp(7)));

    tvAmountName = new TextView(context);
    tvAmountName.setTextSize(12);
    tvAmountName.setTextColor(Theme.getColor(Theme.key_app_text2));
    tvAmountName.setSingleLine();
    tvAmountName.setGravity(Gravity.CENTER);
    tvAmountName.setText(R.string.amount);
    addView(tvAmountName, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(40), 5, AndroidUtilities.dp(10), AndroidUtilities.dp(0), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    tvAmount = new TextView(context);
    tvAmount.setTextSize(10);
    tvAmount.setTextColor(Theme.getColor(Theme.key_app_text4));
    tvAmount.setSingleLine();
    tvAmount.setGravity(Gravity.CENTER);
    addView(tvAmount, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(40), 3, AndroidUtilities.dp(10), AndroidUtilities.dp(5), AndroidUtilities.dp(10), AndroidUtilities.dp(5)));

    shadow = new View(context);
    shadow.setBackgroundColor(shadowColor);
    addView(shadow, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(1), AndroidUtilities.dp(10), AndroidUtilities.dp(40), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    tvDateName = new TextView(context);
    tvDateName.setTextSize(12);
    tvDateName.setTextColor(Theme.getColor(Theme.key_app_text2));
    tvDateName.setSingleLine();
    tvDateName.setGravity(Gravity.CENTER);
    tvDateName.setText(R.string.date_amount);
    addView(tvDateName, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(40), 5, AndroidUtilities.dp(10), AndroidUtilities.dp(40), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    tvDate = new TextView(context);
    tvDate.setTextSize(12);
    tvDate.setTextColor(Theme.getColor(Theme.key_app_text));
    tvDate.setSingleLine();
    tvDate.setGravity(Gravity.CENTER);
    addView(tvDate, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(40), 3, AndroidUtilities.dp(10), AndroidUtilities.dp(40), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    shadow = new View(context);
    shadow.setBackgroundColor(shadowColor);
    addView(shadow, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(1), AndroidUtilities.dp(10), AndroidUtilities.dp(80), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    tvDescription = new TextView(context);
    tvDescription.setTextSize(12);
    tvDescription.setTextColor(Theme.getColor(Theme.key_app_text));
    tvDescription.setMinimumHeight(AndroidUtilities.dp(40));
    tvDescription.setPadding(0, AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8));
    addView(tvDescription, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(10), AndroidUtilities.dp(80), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));
  }
}
