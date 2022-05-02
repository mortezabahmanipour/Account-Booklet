package ir.accountbooklet.android.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import ir.accountbooklet.android.Constants;
import ir.accountbooklet.android.Customs.TextView;
import ir.accountbooklet.android.Models.AccountModel;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.Theme;

public class EditAccountFragment extends BaseBottomSheetDialogFragment implements View.OnClickListener {

  public static EditAccountFragment newInstance() {
    return new EditAccountFragment();
  }

  private ViewGroup layoutBase;
  private TextView tvClearAccount;
  private TextView tvDeleteAccount;
  private TextView tvAddAmount;
  private TextView tvAddCard;
  private TextView tvEditAccount;
  private AccountModel account;
  private EditAccountFragmentListener listener;

  public interface EditAccountFragmentListener {
    void onClearAccount();
    void onDeleteAccount();
    void onAddAmount();
    void onAddCard();
    void onEditAccount();
  }

  public EditAccountFragment setInformation(AccountModel account, EditAccountFragmentListener listener) {
    this.listener = listener;
    this.account = account;
    return this;
  }

  private void initializeViews(View view) {
    layoutBase = view.findViewById(R.id.layoutBase);
    tvClearAccount = view.findViewById(R.id.tvClearAccount);
    tvDeleteAccount = view.findViewById(R.id.tvDeleteAccount);
    tvAddAmount = view.findViewById(R.id.tvAddAmount);
    tvAddCard = view.findViewById(R.id.tvAddCard);
    tvEditAccount = view.findViewById(R.id.tvEditAccount);
  }

  @SuppressLint("SetTextI18n")
  private void initializeDefault() {
    layoutBase.setBackground(Theme.createDrawable(0xFFFFFFFF, 0, 0, Theme.RECT, AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10), 0, 0, 0, 0));

    AndroidUtilities.setVisibility(tvClearAccount, account.type == Constants.TYPE_DEBTOR || account.type == Constants.TYPE_CREDITOR ? View.VISIBLE : View.GONE);
  }

  private void initializeListeners() {
    tvClearAccount.setOnClickListener(this);
    tvDeleteAccount.setOnClickListener(this);
    tvAddAmount.setOnClickListener(this);
    tvAddCard.setOnClickListener(this);
    tvEditAccount.setOnClickListener(this);
  }

  private void actionClearAccount() {
    if (listener != null) {
      listener.onClearAccount();
    }
    dismiss();
  }

  private void actionDeleteAccount() {
    if (listener != null) {
      listener.onDeleteAccount();
    }
    dismiss();
  }

  private void actionAddAmount() {
    if (listener != null) {
      listener.onAddAmount();
    }
    dismiss();
  }

  private void actionAddCard() {
    if (listener != null) {
      listener.onAddCard();
    }
    dismiss();
  }

  private void actionEditAccount() {
    if (listener != null) {
      listener.onEditAccount();
    }
    dismiss();
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogStyle);
  }

  @Override
  public View onCreateViewButtonSheet(Bundle savedInstanceState) {
    return View.inflate(context, R.layout.fragment_edit_account, null);
  }

  @Override
  public void onViewCreatedButtonSheet(Bundle savedInstanceState, View view) {
    initializeViews(view);
    initializeDefault();
    initializeListeners();
  }

  @SuppressLint("NonConstantResourceId")
  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.tvClearAccount:
        actionClearAccount();
        break;
      case R.id.tvDeleteAccount:
        actionDeleteAccount();
        break;
      case R.id.tvAddAmount:
        actionAddAmount();
        break;
      case R.id.tvAddCard:
        actionAddCard();
        break;
      case R.id.tvEditAccount:
        actionEditAccount();
        break;
    }
  }

  @Override
  public void onStateChanged(@NonNull View bottomSheet, int newState) {
    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
      dismiss();
    }
  }

  @Override
  public void onSlide(@NonNull View bottomSheet, float slideOffset) {

  }
}