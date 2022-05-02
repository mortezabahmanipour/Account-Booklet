package ir.accountbooklet.android.Dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;

import ir.accountbooklet.android.Constants;
import ir.accountbooklet.android.Customs.EditText;
import ir.accountbooklet.android.Customs.SelectorImageView;
import ir.accountbooklet.android.Customs.TextView;
import ir.accountbooklet.android.Models.CardsModel;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.Theme;

public class DialogAddCard extends BaseDialog implements View.OnClickListener, TextWatcher {
  private ViewGroup baseLayout;
  private SelectorImageView ivClose;
  private TextView tvTitle;
  private TextView tvCardHolderName;
  private TextView tvCardNumber;
  private TextView tvBankName;
  private TextView tvAccountNumber;
  private TextView tvShabaNumber;
  private TextView tvAddCard;
  private EditText etCardHolderName;
  private EditText etCardNumber;
  private EditText etBankName;
  private EditText etAccountNumber;
  private EditText etShabaNumber;
  private CardsModel amount = new CardsModel();
  private AddCardListener listener;
  private int flag;

  public interface AddCardListener {
    void onCard(CardsModel card);
  }

  public static DialogAddCard newInstance(Context context) {
    return new DialogAddCard(context);
  }

  private DialogAddCard(Context context) {
    super(context);
  }

  public DialogAddCard setFlag(int flag) {
    this.flag = flag;
    return this;
  }

  public DialogAddCard setCard(CardsModel card) {
    if (card != null) {
      this.amount.id = card.id;
      this.amount.accountId = card.accountId;
      this.amount.cardHolderName = card.cardHolderName;
      this.amount.bankName = card.bankName;
      this.amount.cardNumber = card.cardNumber;
      this.amount.accountNumber = card.accountNumber;
      this.amount.shabaNumber = card.shabaNumber;
    }
    return this;
  }

  public DialogAddCard setListener(AddCardListener listener) {
    this.listener = listener;
    return this;
  }

  private void initializeViews() {
    baseLayout = findViewById(R.id.baseLayout);
    ivClose = findViewById(R.id.ivClose);
    tvTitle = findViewById(R.id.tvTitle);
    tvCardHolderName = findViewById(R.id.tvCardHolderName);
    tvCardNumber = findViewById(R.id.tvCardNumber);
    tvBankName = findViewById(R.id.tvBankName);
    tvAccountNumber = findViewById(R.id.tvAccountNumber);
    tvShabaNumber = findViewById(R.id.tvShabaNumber);
    tvAddCard = findViewById(R.id.tvAddCard);
    etCardHolderName = findViewById(R.id.etCardHolderName);
    etCardNumber = findViewById(R.id.etCardNumber);
    etBankName = findViewById(R.id.etBankName);
    etAccountNumber = findViewById(R.id.etAccountNumber);
    etShabaNumber = findViewById(R.id.etShabaNumber);
  }

  private void initializeDefaults() {
    baseLayout.setBackground(Theme.createDrawable(0xFFFFFFFF, 0, 0, Theme.RECT, AndroidUtilities.dp(7)));

    tvAddCard.setText(flag == Constants.FLAG_CREATE ? R.string.add_card : R.string.edit_card);
    tvTitle.setText(tvAddCard.getText());
  }

  private void initializeListeners() {
    ivClose.setOnClickListener(this);
    tvAddCard.setOnClickListener(this);
    etCardHolderName.addTextChangedListener(this);
    etCardNumber.addTextChangedListener(this);
    etBankName.addTextChangedListener(this);
    etAccountNumber.addTextChangedListener(this);
    etShabaNumber.addTextChangedListener(this);
  }

  private void setInfo() {
    if (amount != null) {
      etCardHolderName.setText(amount.cardHolderName);
      etCardNumber.setText(amount.cardNumber);
      etBankName.setText(amount.bankName);
      etAccountNumber.setText(amount.accountNumber);
      etShabaNumber.setText(amount.shabaNumber);
    }
  }

  private void actionAddCard() {
    amount.cardHolderName = etCardHolderName.getString();
    amount.cardNumber = etCardNumber.getString();
    amount.bankName = etBankName.getString();
    amount.accountNumber = etAccountNumber.getString();
    amount.shabaNumber = etShabaNumber.getString();
    if (TextUtils.isEmpty(amount.cardHolderName) || amount.cardHolderName.length() < 3) {
      AndroidUtilities.toast(context, context.getString(R.string.card_holder_name) + " باید بیشتر از 3 حرف باشد");
      return;
    }
    if (!TextUtils.isEmpty(amount.bankName) && amount.bankName.length() < 3) {
      AndroidUtilities.toast(context, context.getString(R.string.bank_name) + " باید بیشتر از 3 حرف باشد");
      return;
    }
    if (TextUtils.isEmpty(amount.cardNumber) && TextUtils.isEmpty(amount.accountNumber) && TextUtils.isEmpty(amount.shabaNumber)) {
      AndroidUtilities.toast(context, "حداقل باید یک شماره برای واریز پول وجود داشته باشد");
      return;
    }
    if (listener != null) {
      listener.onCard(amount);
    }
    dismiss();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_add_card);
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
      case R.id.tvAddCard:
        actionAddCard();
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
    if (s == etCardHolderName.getText()) {
      AndroidUtilities.setVisibility(tvCardHolderName, TextUtils.isEmpty(s) ? View.INVISIBLE : View.VISIBLE);
    } else if (s == etCardNumber.getText()) {
      AndroidUtilities.setVisibility(tvCardNumber, TextUtils.isEmpty(s) ? View.INVISIBLE : View.VISIBLE);
    } else if (s == etBankName.getText()) {
      AndroidUtilities.setVisibility(tvBankName, TextUtils.isEmpty(s) ? View.INVISIBLE : View.VISIBLE);
    } else if (s == etAccountNumber.getText()) {
      AndroidUtilities.setVisibility(tvAccountNumber, TextUtils.isEmpty(s) ? View.INVISIBLE : View.VISIBLE);
    } else if (s == etShabaNumber.getText()) {
      AndroidUtilities.setVisibility(tvShabaNumber, TextUtils.isEmpty(s) ? View.INVISIBLE : View.VISIBLE);
    }
  }
}
