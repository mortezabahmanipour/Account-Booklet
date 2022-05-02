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
import ir.accountbooklet.android.Models.CardsModel;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.LayoutHelper;
import ir.accountbooklet.android.Utils.SpanUtil;
import ir.accountbooklet.android.Utils.Theme;

public class EditCardsRow extends FrameLayout {
  private TextView tvDelete;
  private TextView tvEdit;
  private TextView tvCardHolderNameName;
  private TextView tvCardHolderName;
  private TextView tvCardNumberName;
  private TextView tvCardNumber;
  private TextView tvAccountNumberName;
  private TextView tvAccountNumber;
  private TextView tvShabaNumberName;
  private TextView tvShabaNumber;
  private TextView tvBankNameName;
  private TextView tvBankName;
  private View shadow1;
  private View shadow2;
  private View shadow3;
  private View shadow4;
  private int indexView;
  private EditCardsListener listener;

  public interface EditCardsListener {
    void onDelete();
    void onEdit();
  }

  @SuppressLint("SetTextI18n")
  public EditCardsRow setModel(CardsModel card, EditCardsListener listener) {
    this.listener = listener;
    if (card == null) {
      return this;
    }
    tvCardHolderName.setText(card.cardHolderName);
    indexView = 2;
    if (!TextUtils.isEmpty(card.cardNumber)) {
      tvCardNumber.setText(card.cardNumber);
      AndroidUtilities.setVisibility(tvCardNumberName, View.VISIBLE);
      AndroidUtilities.setVisibility(tvCardNumber, View.VISIBLE);
      AndroidUtilities.setVisibility(shadow1, View.VISIBLE);
      LayoutParams params = (LayoutParams) tvCardNumber.getLayoutParams();
      params.topMargin = indexView * AndroidUtilities.dp(40);
      tvCardNumber.setLayoutParams(params);
      params = (LayoutParams) tvCardNumberName.getLayoutParams();
      params.topMargin = indexView * AndroidUtilities.dp(40);
      tvCardNumberName.setLayoutParams(params);
      LayoutParams shadow = (LayoutParams) shadow1.getLayoutParams();
      shadow.topMargin = indexView * AndroidUtilities.dp(40);
      shadow1.setLayoutParams(shadow);
      indexView++;
    } else {
      AndroidUtilities.setVisibility(tvCardNumberName, View.GONE);
      AndroidUtilities.setVisibility(tvCardNumber, View.GONE);
      AndroidUtilities.setVisibility(shadow1, View.GONE);
    }
    if (!TextUtils.isEmpty(card.accountNumber)) {
      tvAccountNumber.setText(card.accountNumber);
      AndroidUtilities.setVisibility(tvAccountNumberName, View.VISIBLE);
      AndroidUtilities.setVisibility(tvAccountNumber, View.VISIBLE);
      AndroidUtilities.setVisibility(shadow2, View.VISIBLE);
      LayoutParams params = (LayoutParams) tvAccountNumber.getLayoutParams();
      params.topMargin = indexView * AndroidUtilities.dp(40);
      tvAccountNumber.setLayoutParams(params);
      params = (LayoutParams) tvAccountNumberName.getLayoutParams();
      params.topMargin = indexView * AndroidUtilities.dp(40);
      tvAccountNumberName.setLayoutParams(params);
      LayoutParams shadow = (LayoutParams) shadow2.getLayoutParams();
      shadow.topMargin = indexView * AndroidUtilities.dp(40);
      shadow2.setLayoutParams(shadow);
      indexView++;
    } else {
      AndroidUtilities.setVisibility(tvAccountNumberName, View.GONE);
      AndroidUtilities.setVisibility(tvAccountNumber, View.GONE);
      AndroidUtilities.setVisibility(shadow2, View.GONE);
    }
    if (!TextUtils.isEmpty(card.shabaNumber)) {
      tvShabaNumber.setText("IR " + card.shabaNumber);
      AndroidUtilities.setVisibility(tvShabaNumberName, View.VISIBLE);
      AndroidUtilities.setVisibility(tvShabaNumber, View.VISIBLE);
      AndroidUtilities.setVisibility(shadow3, View.VISIBLE);
      LayoutParams params = (LayoutParams) tvShabaNumber.getLayoutParams();
      params.topMargin = indexView * AndroidUtilities.dp(40);
      tvShabaNumber.setLayoutParams(params);
      params = (LayoutParams) tvShabaNumberName.getLayoutParams();
      params.topMargin = indexView * AndroidUtilities.dp(40);
      tvShabaNumberName.setLayoutParams(params);
      LayoutParams shadowP = (LayoutParams) shadow3.getLayoutParams();
      shadowP.topMargin = indexView * AndroidUtilities.dp(40);
      shadow3.setLayoutParams(shadowP);
      indexView++;
    } else {
      AndroidUtilities.setVisibility(tvShabaNumberName, View.GONE);
      AndroidUtilities.setVisibility(tvShabaNumber, View.GONE);
      AndroidUtilities.setVisibility(shadow3, View.GONE);
    }
    if (!TextUtils.isEmpty(card.bankName)) {
      tvBankName.setText(card.bankName);
      AndroidUtilities.setVisibility(tvBankNameName, View.VISIBLE);
      AndroidUtilities.setVisibility(tvBankName, View.VISIBLE);
      AndroidUtilities.setVisibility(shadow4, View.VISIBLE);
      LayoutParams params = (LayoutParams) tvBankName.getLayoutParams();
      params.topMargin = indexView * AndroidUtilities.dp(40);
      tvBankName.setLayoutParams(params);
      params = (LayoutParams) tvBankNameName.getLayoutParams();
      params.topMargin = indexView * AndroidUtilities.dp(40);
      tvBankNameName.setLayoutParams(params);
      LayoutParams shadowP = (LayoutParams) shadow4.getLayoutParams();
      shadowP.topMargin = indexView * AndroidUtilities.dp(40);
      shadow4.setLayoutParams(shadowP);
      indexView++;
    } else {
      AndroidUtilities.setVisibility(tvBankNameName, View.GONE);
      AndroidUtilities.setVisibility(tvBankName, View.GONE);
      AndroidUtilities.setVisibility(shadow4, View.GONE);
    }
    return this;
  }

  public EditCardsRow(@NonNull Context context) {
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

    View shadow = new View(context);
    shadow.setBackgroundColor(shadowColor);
    addView(shadow, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(1), AndroidUtilities.dp(10), AndroidUtilities.dp(40), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    shadow = new View(context);
    shadow.setBackgroundColor(shadowColor);
    addView(shadow, LayoutHelper.createFrame(AndroidUtilities.dp(1), AndroidUtilities.dp(20), Gravity.CENTER_HORIZONTAL, AndroidUtilities.dp(0), AndroidUtilities.dp(10), AndroidUtilities.dp(0), AndroidUtilities.dp(0)));

    tvCardHolderNameName = new TextView(context);
    tvCardHolderNameName.setTextSize(12);
    tvCardHolderNameName.setTextColor(Theme.getColor(Theme.key_app_text2));
    tvCardHolderNameName.setSingleLine();
    tvCardHolderNameName.setGravity(Gravity.CENTER);
    tvCardHolderNameName.setText(R.string.card_holder_name);
    addView(tvCardHolderNameName, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(40), 5, AndroidUtilities.dp(10), AndroidUtilities.dp(40), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    tvCardHolderName = new TextView(context);
    tvCardHolderName.setTextSize(12);
    tvCardHolderName.setTextColor(Theme.getColor(Theme.key_app_text));
    tvCardHolderName.setSingleLine();
    tvCardHolderName.setGravity(Gravity.CENTER);
    addView(tvCardHolderName, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(40), 3, AndroidUtilities.dp(10), AndroidUtilities.dp(40), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    tvCardNumberName = new TextView(context);
    tvCardNumberName.setTextSize(12);
    tvCardNumberName.setTextColor(Theme.getColor(Theme.key_app_text2));
    tvCardNumberName.setSingleLine();
    tvCardNumberName.setGravity(Gravity.CENTER);
    tvCardNumberName.setText(R.string.card_number);
    addView(tvCardNumberName, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(40), 5, AndroidUtilities.dp(10), AndroidUtilities.dp(80), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    tvCardNumber = new TextView(context);
    tvCardNumber.setTextSize(12);
    tvCardNumber.setTextColor(Theme.getColor(Theme.key_app_text));
    tvCardNumber.setSingleLine();
    tvCardNumber.setGravity(Gravity.CENTER);
    addView(tvCardNumber, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(40), 3, AndroidUtilities.dp(10), AndroidUtilities.dp(80), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    shadow1 = new View(context);
    shadow1.setBackgroundColor(shadowColor);
    addView(shadow1, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(1), AndroidUtilities.dp(10), AndroidUtilities.dp(80), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    tvAccountNumberName = new TextView(context);
    tvAccountNumberName.setTextSize(12);
    tvAccountNumberName.setTextColor(Theme.getColor(Theme.key_app_text2));
    tvAccountNumberName.setSingleLine();
    tvAccountNumberName.setGravity(Gravity.CENTER);
    tvAccountNumberName.setText(R.string.account_number);
    addView(tvAccountNumberName, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(40), 5, AndroidUtilities.dp(10), AndroidUtilities.dp(40), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    tvAccountNumber = new TextView(context);
    tvAccountNumber.setTextSize(12);
    tvAccountNumber.setTextColor(Theme.getColor(Theme.key_app_text));
    tvAccountNumber.setSingleLine();
    tvAccountNumber.setGravity(Gravity.CENTER);
    addView(tvAccountNumber, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(40), 3, AndroidUtilities.dp(10), AndroidUtilities.dp(40), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    shadow2 = new View(context);
    shadow2.setBackgroundColor(shadowColor);
    addView(shadow2, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(1), AndroidUtilities.dp(10), AndroidUtilities.dp(80), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    tvShabaNumberName = new TextView(context);
    tvShabaNumberName.setTextSize(12);
    tvShabaNumberName.setTextColor(Theme.getColor(Theme.key_app_text2));
    tvShabaNumberName.setSingleLine();
    tvShabaNumberName.setGravity(Gravity.CENTER);
    tvShabaNumberName.setText(R.string.shaba_number);
    addView(tvShabaNumberName, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(40), 5, AndroidUtilities.dp(10), AndroidUtilities.dp(40), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    tvShabaNumber = new TextView(context);
    tvShabaNumber.setTextSize(12);
    tvShabaNumber.setTextColor(Theme.getColor(Theme.key_app_text));
    tvShabaNumber.setSingleLine();
    tvShabaNumber.setGravity(Gravity.CENTER);
    addView(tvShabaNumber, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(40), 3, AndroidUtilities.dp(10), AndroidUtilities.dp(40), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    shadow3 = new View(context);
    shadow3.setBackgroundColor(shadowColor);
    addView(shadow3, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(1), AndroidUtilities.dp(10), AndroidUtilities.dp(80), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    tvBankNameName = new TextView(context);
    tvBankNameName.setTextSize(12);
    tvBankNameName.setTextColor(Theme.getColor(Theme.key_app_text2));
    tvBankNameName.setSingleLine();
    tvBankNameName.setGravity(Gravity.CENTER);
    tvBankNameName.setText(R.string.bank_name);
    addView(tvBankNameName, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(40), 5, AndroidUtilities.dp(10), AndroidUtilities.dp(40), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    tvBankName = new TextView(context);
    tvBankName.setTextSize(12);
    tvBankName.setTextColor(Theme.getColor(Theme.key_app_text));
    tvBankName.setSingleLine();
    tvBankName.setGravity(Gravity.CENTER);
    addView(tvBankName, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(40), 3, AndroidUtilities.dp(10), AndroidUtilities.dp(40), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));

    shadow4 = new View(context);
    shadow4.setBackgroundColor(shadowColor);
    addView(shadow4, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(1), AndroidUtilities.dp(10), AndroidUtilities.dp(80), AndroidUtilities.dp(10), AndroidUtilities.dp(0)));
  }
}
