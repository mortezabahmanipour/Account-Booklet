package ir.accountbooklet.android.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import ir.accountbooklet.android.Customs.TextView;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.MarketUtils;
import ir.accountbooklet.android.Utils.Theme;

public class MoreFragment extends BaseBottomSheetDialogFragment implements View.OnClickListener {

  public static MoreFragment newInstance() {
    return new MoreFragment();
  }

  private ViewGroup layoutBase;
  private TextView tvShareToPeople;
  private TextView tvRatingUs;

  private void initialize(View view) {
    layoutBase = view.findViewById(R.id.layoutBase);
    tvShareToPeople = view.findViewById(R.id.tvShareToPeople);
    tvRatingUs = view.findViewById(R.id.tvRatingUs);
  }

  @SuppressLint("SetTextI18n")
  private void initializeDefault() {
    layoutBase.setBackground(Theme.createDrawable(0xFFFFFFFF, 0, 0, Theme.RECT, AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10), 0, 0, 0, 0));
  }

  private void initializeListeners() {
    tvShareToPeople.setOnClickListener(this);
    tvRatingUs.setOnClickListener(this);
  }

  private void actionShareToPeople() {
    String text = "اگه دنبال یه برنامه خوب می گردی تا حساب کتاب های مغازه تون رو داشته باشی و لیست طلب کارها و بده کارها دم دستتون باشه برنامه ( دفترچه حساب ) رو به شما معرفی می کنم";
    text += "\n\n";
    text += "لینک دانلود";
    text += "\n";
    text += MarketUtils.getMarketLinkShare(false);
    Intent sendIntent = new Intent(Intent.ACTION_SEND);
    sendIntent.putExtra(Intent.EXTRA_SUBJECT, "معرفی برنامه به دیگران");
    sendIntent.putExtra(Intent.EXTRA_TEXT, text);
    sendIntent.setType("text/plain");
    context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.share)));
    dismiss();
  }

  private void actionRatingUs() {
    MarketUtils.sendComment(context);
    dismiss();
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogStyle);
  }

  @Override
  public View onCreateViewButtonSheet(Bundle savedInstanceState) {
    return View.inflate(context, R.layout.fragment_more, null);
  }

  @Override
  public void onViewCreatedButtonSheet(Bundle savedInstanceState, View view) {
    initialize(view);
    initializeDefault();
    initializeListeners();
  }

  @SuppressLint("NonConstantResourceId")
  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.tvShareToPeople:
        actionShareToPeople();
        break;
      case R.id.tvRatingUs:
        actionRatingUs();
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