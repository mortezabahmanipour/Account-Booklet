package ir.accountbooklet.android.Customs.Row;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class EditAmountsRow extends FrameLayout {
  private TextView tvDelete;
  private TextView tvEdit;
  private TextView tvAmountName;
  private TextView tvAmount;
  private TextView tvDateName;
  private TextView tvDate;
  private View shadow;
  private TextView tvDescription;
  private EditAmountsListener listener;

  public interface EditAmountsListener {
    void onDelete();
    void onEdit();
  }

  @SuppressLint("SetTextI18n")
  public EditAmountsRow setModel(AmountsModel amount, EditAmountsListener listener) {
    this.listener = listener;
    if (amount == null) {
      return this;
    }
    final String price = AndroidUtilities.formatPrice(Math.abs(amount.amount), false);
    SpannableStringBuilder spannable = new SpannableStringBuilder(price + " تومان");
    SpanUtil.span(spannable, 0, price.length(), Theme.getColor(Theme.key_app_text), AndroidUtilities.dp(12), true);
    tvAmount.setText(spannable);
    String date = amount.date > 0 ? LocaleController.getLocaleDate(amount.date).getDateAndHour() : "تاریخ ورود خودکار";
    if (amount.amount < 0) {
      String strPaidOn = getResources().getString(R.string.paid_on);
      SpannableStringBuilder spanDate = new SpannableStringBuilder(strPaidOn + " " + date);
      SpanUtil.span(spanDate, 0, strPaidOn.length(), Theme.getColor(Theme.key_app_default), -1, false);
      tvDate.setText(spanDate);
    } else {
      tvDate.setText(date);
    }
    tvDescription.setText(amount.description);
    AndroidUtilities.setVisibility(tvDescription, TextUtils.isEmpty(amount.description) ? View.GONE : View.VISIBLE);
    AndroidUtilities.setVisibility(shadow, TextUtils.isEmpty(amount.description) ? View.GONE : View.VISIBLE);
    return this;
  }

  public EditAmountsRow(@NonNull Context context) {
    super(context);
    setMotionEventSplittingEnabled(false);
    setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    final int shadowColor = 0x15000000;
    setBackground(Theme.createDrawable(0x08000000, shadowColor, AndroidUtilities.dp(1), Theme.RECT, AndroidUtilities.dp(7)));

    final int actionSize = (AndroidUtilities.displaySize.x - AndroidUtilities.dp(40)) / 2;
    tvDelete = new TextView(context);
    tvDelete.setTextColor(Theme.getColor(Theme.key_app_accent));
    tvDelete.setTextSize(12);
    tvDelete.setGravity(Gravity.CENTER);
    tvDelete.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10));
    tvDelete.setSelectorBuilder(new Theme.SelectorBuilder().pressedColor(Theme.getColor(Theme.key_app_selector)).style(Theme.RECT).radii(AndroidUtilities.dp(7), 0, 0, 0));
    tvDelete.setText(SpanUtil.span(new SpannableStringBuilder("حذف کردن"), 0, -1, tvDelete.getCurrentTextColor(), -1, false, 0, 0, 0, Theme.delete_drawable, AndroidUtilities.dp(17), AndroidUtilities.dp(7), 0, 0, 0, 0));
    tvDelete.setOnClickListener(v -> {
      if (listener != null) {
        listener.onDelete();
      }
    });
    addView(tvDelete, LayoutHelper.createFrame(actionSize, AndroidUtilities.dp(40), 3, AndroidUtilities.dp(0), AndroidUtilities.dp(0), AndroidUtilities.dp(0), AndroidUtilities.dp(0)));

    tvEdit = new TextView(context);
    tvEdit.setTextColor(Theme.getColor(Theme.key_app_default4));
    tvEdit.setTextSize(12);
    tvEdit.setGravity(Gravity.CENTER);
    tvEdit.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10));
    tvEdit.setSelectorBuilder(new Theme.SelectorBuilder().pressedColor(Theme.getColor(Theme.key_app_selector)).style(Theme.RECT).radii(0, AndroidUtilities.dp(7), 0, 0));
    tvEdit.setText(SpanUtil.span(new SpannableStringBuilder("ویرایش کردن"), 0, -1, tvEdit.getCurrentTextColor(), -1, false, 0, 0, 0, Theme.edit_drawable, AndroidUtilities.dp(17), AndroidUtilities.dp(7), 0, 0, 0, 0));
    tvEdit.setOnClickListener(v -> {
      if (listener != null) {
        listener.onEdit();
      }
    });
    addView(tvEdit, LayoutHelper.createFrame(actionSize, AndroidUtilities.dp(40), 5, AndroidUtilities.dp(0), AndroidUtilities.dp(0), AndroidUtilities.dp(0), AndroidUtilities.dp(0)));

    shadow = new View(context);
    shadow.setBackgroundColor(shadowColor);
    addView(shadow, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(1), AndroidUtilities.dp(10), AndroidUtilities.dp(40), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    shadow = new View(context);
    shadow.setBackgroundColor(shadowColor);
    addView(shadow, LayoutHelper.createFrame(AndroidUtilities.dp(1), AndroidUtilities.dp(20), Gravity.CENTER_HORIZONTAL, AndroidUtilities.dp(0), AndroidUtilities.dp(10), AndroidUtilities.dp(0), AndroidUtilities.dp(0)));

    tvAmountName = new TextView(context);
    tvAmountName.setTextSize(12);
    tvAmountName.setTextColor(Theme.getColor(Theme.key_app_text2));
    tvAmountName.setSingleLine();
    tvAmountName.setGravity(Gravity.CENTER);
    tvAmountName.setText(R.string.amount);
    addView(tvAmountName, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(40), 5, AndroidUtilities.dp(10), AndroidUtilities.dp(40), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    tvAmount = new TextView(context);
    tvAmount.setTextSize(10);
    tvAmount.setTextColor(Theme.getColor(Theme.key_app_text4));
    tvAmount.setSingleLine();
    tvAmount.setGravity(Gravity.CENTER);
    addView(tvAmount, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(40), 3, AndroidUtilities.dp(10), AndroidUtilities.dp(40), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    tvDateName = new TextView(context);
    tvDateName.setTextSize(12);
    tvDateName.setTextColor(Theme.getColor(Theme.key_app_text2));
    tvDateName.setSingleLine();
    tvDateName.setGravity(Gravity.CENTER);
    tvDateName.setText(R.string.date_amount);
    addView(tvDateName, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(40), 5, AndroidUtilities.dp(10), AndroidUtilities.dp(80), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    tvDate = new TextView(context);
    tvDate.setTextSize(12);
    tvDate.setTextColor(Theme.getColor(Theme.key_app_text));
    tvDate.setSingleLine();
    tvDate.setGravity(Gravity.CENTER);
    addView(tvDate, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(40), 3, AndroidUtilities.dp(10), AndroidUtilities.dp(80), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    shadow = new View(context);
    shadow.setBackgroundColor(shadowColor);
    addView(shadow, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(1), AndroidUtilities.dp(10), AndroidUtilities.dp(80), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    tvDescription = new TextView(context);
    tvDescription.setTextSize(12);
    tvDescription.setTextColor(Theme.getColor(Theme.key_app_text));
    tvDescription.setMinimumHeight(AndroidUtilities.dp(40));
    tvDescription.setPadding(0, AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8));
    addView(tvDescription, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(10), AndroidUtilities.dp(120), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    shadow = new View(context);
    shadow.setBackgroundColor(shadowColor);
    addView(shadow, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(1), AndroidUtilities.dp(10), AndroidUtilities.dp(120), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));
  }
}
