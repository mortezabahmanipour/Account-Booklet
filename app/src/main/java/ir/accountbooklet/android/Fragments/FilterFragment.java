package ir.accountbooklet.android.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import ir.accountbooklet.android.Constants;
import ir.accountbooklet.android.Customs.EditText;
import ir.accountbooklet.android.Customs.SelectorImageView;
import ir.accountbooklet.android.Customs.TextView;
import ir.accountbooklet.android.Models.FilterModel;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.LocaleController;
import ir.accountbooklet.android.Utils.Theme;
import ir.hamsaa.persiandatepicker.Listener;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.util.PersianCalendar;

public class FilterFragment extends BaseBottomSheetDialogFragment implements View.OnClickListener {

  public static FilterFragment newInstance() {
    return new FilterFragment();
  }

  private ViewGroup layoutBase;
  private SelectorImageView ivBack;
  private TextView tvTitle;
  private TextView tvApply;
  private TextView tvClear;
  private EditText etSearch;
  private EditText etDateFrom;
  private EditText etDateTo;
  private ViewGroup layoutType;
  private FilterListener listener;
  private FilterModel filters = new FilterModel();
  private AnimatorSet animatorSet;
  private TextView[] textsType = new TextView[4];

  public interface FilterListener {
    void onFilter(FilterModel request);
  }

  public FilterFragment setListener(FilterListener listener) {
    this.listener = listener;
    return this;
  }

  public FilterFragment setFilters(FilterModel filters) {
    if (filters != null) {
      this.filters.str = filters.str;
      this.filters.date = filters.date;
      this.filters.dateTo = filters.dateTo;
      this.filters.type = filters.type;
    }
    return this;
  }

  private void initialize(View view) {
    layoutBase = view.findViewById(R.id.layoutBase);
    ivBack = view.findViewById(R.id.ivBack);
    tvTitle = view.findViewById(R.id.tvTitle);
    tvApply = view.findViewById(R.id.tvApply);
    tvClear = view.findViewById(R.id.tvClear);
    etSearch = view.findViewById(R.id.etSearch);
    etDateFrom = view.findViewById(R.id.etDateFrom);
    etDateTo = view.findViewById(R.id.etDateTo);
    layoutType = view.findViewById(R.id.layoutType);
  }

  @SuppressLint("SetTextI18n")
  private void initializeDefault() {
    bottomSheetBehavior.setPeekHeight(AndroidUtilities.dp(500));

    tvTitle.setText(R.string.filters);
    layoutBase.setBackground(Theme.createDrawable(0xFFFFFFFF, 0, 0, Theme.RECT, AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10), 0, 0, 0, 0));

    InputFilter[] fArray = new InputFilter[1];
    fArray[0] = new InputFilter.LengthFilter(Constants.MAX_NAME);
    etSearch.setFilters(fArray);
    etDateTo.setInputType(InputType.TYPE_NULL);
    etDateFrom.setInputType(InputType.TYPE_NULL);

    for (int i=0; i < layoutType.getChildCount(); i++) {
      View view = layoutType.getChildAt(i);
      if (view instanceof TextView) {
        textsType[layoutType.getChildCount() - (i + 1)] = (TextView) view;
      }
    }
  }

  private void initializeListeners() {
    ivBack.setOnClickListener(this);
    tvApply.setOnClickListener(this);
    tvClear.setOnClickListener(this);
    etDateFrom.setOnClickListener(this);
    etDateTo.setOnClickListener(this);
    for (TextView view : textsType) {
      view.setOnClickListener(this);
    }
  }

  private void setFilter() {
    etSearch.setText(filters.str);
    etDateFrom.setText(LocaleController.getLocaleDate(filters.date).getDate());
    etDateTo.setText(LocaleController.getLocaleDate(filters.dateTo).getDate());
    setType(filters.type, false);
  }

  private void setType(int type, boolean animation) {
    if (filters.type == type && animation || animatorSet != null && animatorSet.isRunning() && animation) {
      return;
    }
    final TextView oldTv = textsType[filters.type + 1];
    final TextView newTv = textsType[type + 1];
    filters.type = type;
    if (!animation) {
      for (TextView text : textsType) {
        if (newTv.equals(text)) {
          text.setBackgroundColor(Theme.getColor(Theme.key_app_primary));
          text.setTextColor(Theme.getColor(Theme.key_app_text2));
        } else {
          text.setBackgroundColor(0xFFFFFFFF);
          text.setTextColor(Theme.getColor(Theme.key_app_text3));
        }
      }
      return;
    }
    animatorSet = new AnimatorSet();
    animatorSet.playTogether(
      ObjectAnimator.ofObject(oldTv, "backgroundColor",  new ArgbEvaluator(), Theme.getColor(Theme.key_app_primary), 0xFFFFFFFF),
      ObjectAnimator.ofObject(oldTv, "textColor",  new ArgbEvaluator(), Theme.getColor(Theme.key_app_text2), Theme.getColor(Theme.key_app_text3)),
      ObjectAnimator.ofObject(newTv, "backgroundColor",  new ArgbEvaluator(), 0xFFFFFFFF, Theme.getColor(Theme.key_app_primary)),
      ObjectAnimator.ofObject(newTv, "textColor",  new ArgbEvaluator(), Theme.getColor(Theme.key_app_text3), Theme.getColor(Theme.key_app_text2)));
    animatorSet.setDuration(200);
    animatorSet.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        animatorSet = null;
      }
    });
    animatorSet.start();
  }

  private void actionBack() {
    dismiss();
  }

  private void actionFilter() {
    filters.str = etSearch.getString();
    if (listener != null) {
      listener.onFilter(filters);
    }
    dismiss();
  }

  private void actionClear() {
    filters.clear();
    setFilter();
  }

  private void actionPickDate(final int i) {
    PersianDatePickerDialog picker = new PersianDatePickerDialog(context)
      .setPositiveButtonString("باشه")
      .setNegativeButton("بیخیال")
      .setTodayButton("برو به امروز")
      .setTodayButtonVisible(true)
      .setMinYear(1375)
      .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
      .setInitDate(new PersianCalendar())
      .setActionTextColor(Theme.getColor(Theme.key_app_text3))
      .setTitleColor(Theme.getColor(Theme.key_app_text))
      .setTypeFace(AndroidUtilities.IRANSans_FaNum)
      .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
      .setShowInBottomSheet(true)
      .setListener(new Listener() {
        @Override
        public void onDateSelected(PersianCalendar persianCalendar) {
          int second = persianCalendar.getTime().getSeconds() + (persianCalendar.getTime().getMinutes() * 60) + (persianCalendar.getTime().getHours() * 3600);
          if (i == 1) {
            filters.date = (persianCalendar.getTimeInMillis() / 1000) - second;
          } else {
            filters.dateTo = (persianCalendar.getTimeInMillis() / 1000) + (24 * 60 * 60 - second - 1);
          }
          setFilter();
        }
        @Override
        public void onDismissed() {
          if (i == 1) {
            filters.date = 0;
          } else {
            filters.dateTo = 0;
          }
          setFilter();
        }
      });
    picker.show();
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogStyle);
  }

  @Override
  public View onCreateViewButtonSheet(Bundle savedInstanceState) {
    return View.inflate(context, R.layout.fragment_filter, null);
  }

  @Override
  public void onViewCreatedButtonSheet(Bundle savedInstanceState, View view) {
    initialize(view);
    initializeDefault();
    initializeListeners();
    setFilter();
  }

  @SuppressLint("NonConstantResourceId")
  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.ivBack:
        actionBack();
        break;
      case R.id.tvApply:
        actionFilter();
        break;
      case R.id.tvClear:
        actionClear();
        break;
      case R.id.etDateFrom:
        actionPickDate(1);
        break;
      case R.id.etDateTo:
        actionPickDate(2);
        break;
      default:
        for (int i=0; i < textsType.length; i++) {
          TextView tx = textsType[i];
          if (tx.equals(view)) {
            setType(i - 1, true);
          }
        }
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

  @Override
  public void dismiss() {
    AndroidUtilities.hideKeyboard(etSearch);
    super.dismiss();
    if (animatorSet != null && animatorSet.isRunning()) {
      animatorSet.cancel();
    }
  }
}