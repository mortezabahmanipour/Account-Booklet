package ir.accountbooklet.android.Dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;

import ir.accountbooklet.android.Constants;
import ir.accountbooklet.android.Customs.EditText;
import ir.accountbooklet.android.Customs.SelectorImageView;
import ir.accountbooklet.android.Customs.TextView;
import ir.accountbooklet.android.Models.AmountsModel;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.LocaleController;
import ir.accountbooklet.android.Utils.Theme;
import ir.hamsaa.persiandatepicker.Listener;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.util.PersianCalendar;

public class DialogAddAmount extends BaseDialog implements View.OnClickListener, TextWatcher {
  private ViewGroup baseLayout;
  private SelectorImageView ivClose;
  private TextView tvTitle;
  private TextView tvAmount;
  private TextView tvDescription;
  private TextView tvDate;
  private TextView tvPaid;
  private TextView tvAddAmount;
  private EditText etAmount;
  private EditText etDescription;
  private EditText etDate;
  private AmountsModel amount = new AmountsModel();
  private AddAmountListener listener;
  private int flag;
  private boolean pied;

  public interface AddAmountListener {
    void onAmount(AmountsModel amount);
  }

  public static DialogAddAmount newInstance(Context context) {
    return new DialogAddAmount(context);
  }

  private DialogAddAmount(Context context) {
    super(context);
  }

  public DialogAddAmount setFlag(int flag) {
    this.flag = flag;
    return this;
  }

  public DialogAddAmount setAmount(AmountsModel amount) {
    if (amount != null) {
      this.amount.id = amount.id;
      this.amount.accountId = amount.accountId;
      this.amount.amount = amount.amount;
      this.amount.description = amount.description;
      this.amount.date = amount.date;
    }
    return this;
  }

  public DialogAddAmount setListener(AddAmountListener listener) {
    this.listener = listener;
    return this;
  }

  private void initializeViews() {
    baseLayout = findViewById(R.id.baseLayout);
    ivClose = findViewById(R.id.ivClose);
    tvTitle = findViewById(R.id.tvTitle);
    tvAmount = findViewById(R.id.tvAmount);
    tvDescription = findViewById(R.id.tvDescription);
    tvDate = findViewById(R.id.tvDate);
    tvPaid = findViewById(R.id.tvPaid);
    tvAddAmount = findViewById(R.id.tvAddAmount);
    etAmount = findViewById(R.id.etAmount);
    etDescription = findViewById(R.id.etDescription);
    etDate = findViewById(R.id.etDate);
  }

  private void initializeDefaults() {
    baseLayout.setBackground(Theme.createDrawable(0xFFFFFFFF, 0, 0, Theme.RECT, AndroidUtilities.dp(7)));
    etDate.setInputType(InputType.TYPE_NULL);

    tvAddAmount.setText(flag == Constants.FLAG_CREATE ? R.string.add_amount : R.string.edit_amount);
    tvTitle.setText(tvAddAmount.getText());
  }

  private void initializeListeners() {
    ivClose.setOnClickListener(this);
    tvAddAmount.setOnClickListener(this);
    tvPaid.setOnClickListener(this);
    etDate.setOnClickListener(this);
    etAmount.addTextChangedListener(this);
    etDescription.addTextChangedListener(this);
    etDate.addTextChangedListener(this);
  }

  private void setInfo() {
    if (amount != null) {
      etAmount.setText(String.valueOf(Math.abs(amount.amount)));
      etDescription.setText(amount.description);
      etDate.setText(LocaleController.getLocaleDate(amount.date).getDate());
      pied = amount.amount < 0;
      setPaid();
    }
  }

  private void actionAddAmount() {
    amount.description = etDescription.getString();
    amount.amount = etAmount.getLong();
    amount.amount = pied ? -amount.amount : amount.amount;
    if (amount.amount == 0) {
      AndroidUtilities.toast(context, R.string.err_insert_amount);
      return;
    }
    if (listener != null) {
      listener.onAmount(amount);
    }
    dismiss();
  }

  private void actionDate() {
    new PersianDatePickerDialog(context)
      .setPositiveButtonString("باشه")
      .setNegativeButton("بیخیال")
      .setTodayButton("برو به امروز")
      .setTodayButtonVisible(true)
      .setMinYear(1375)
      .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
      .setInitDate(new PersianCalendar())
      .setActionTextColor(Color.GRAY)
      .setTypeFace(AndroidUtilities.IRANSans_FaNum)
      .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
      .setShowInBottomSheet(true)
      .setListener(new Listener() {
        @Override
        public void onDateSelected(PersianCalendar persianCalendar) {
          amount.date = persianCalendar.getTimeInMillis() / 1000;
          etDate.setText(LocaleController.getLocaleDate(amount.date).getDate());
        }
        @Override
        public void onDismissed() {
          amount.date = 0;
          etDate.setText(LocaleController.getLocaleDate(amount.date).getDate());
        }
      }).show();
  }

  private void actionPaid() {
    pied = !pied;
    setPaid();
  }

  private void setPaid() {
    tvPaid.setCompoundDrawables(null, null, pied ? Theme.check_box_on_drawable : Theme.check_box_off_drawable, null);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_add_amount);
    initializeViews();
    initializeDefaults();
    initializeListeners();
    setInfo();
  }

  @SuppressLint("NonConstantResourceId")
  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.ivClose:
        dismiss();
        break;
      case R.id.tvAddAmount:
        actionAddAmount();
        break;
      case R.id.etDate:
        actionDate();
        break;
      case R.id.tvPaid:
        actionPaid();
        break;
    }
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {

  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {

  }

  @Override
  public void afterTextChanged(Editable s) {
    if (s == etAmount.getText()) {
      AndroidUtilities.setVisibility(tvAmount, TextUtils.isEmpty(s) ? View.INVISIBLE : View.VISIBLE);
    } else if (s == etDescription.getText()) {
      AndroidUtilities.setVisibility(tvDescription, TextUtils.isEmpty(s) ? View.INVISIBLE : View.VISIBLE);
    } else if (s == etDate.getText()) {
      AndroidUtilities.setVisibility(tvDate, TextUtils.isEmpty(s) ? View.INVISIBLE : View.VISIBLE);
    }
  }
}
