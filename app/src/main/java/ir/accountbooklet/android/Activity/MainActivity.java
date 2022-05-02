package ir.accountbooklet.android.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import ir.accountbooklet.android.Constants;
import ir.accountbooklet.android.Customs.RecyclerListView;
import ir.accountbooklet.android.Customs.Row.AccountsRow;
import ir.accountbooklet.android.Customs.SelectorImageView;
import ir.accountbooklet.android.Customs.TextView;
import ir.accountbooklet.android.Fragments.AccountFragment;
import ir.accountbooklet.android.Fragments.CreateAccountFragment;
import ir.accountbooklet.android.Fragments.FilterFragment;
import ir.accountbooklet.android.Fragments.FragmentController;
import ir.accountbooklet.android.Fragments.MoreFragment;
import ir.accountbooklet.android.Fragments.SettingFragment;
import ir.accountbooklet.android.Listeners.FragmentNavigation;
import ir.accountbooklet.android.Models.AccountsModel;
import ir.accountbooklet.android.Models.HeaderInfoModel;
import ir.accountbooklet.android.Models.FilterModel;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Session.Database;
import ir.accountbooklet.android.Session.Session;
import ir.accountbooklet.android.Utils.AdiveryUtils;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.AppLog;
import ir.accountbooklet.android.Utils.DispatchQueue;
import ir.accountbooklet.android.Utils.MortinoUtils;
import ir.accountbooklet.android.Utils.NotificationCenter;
import ir.accountbooklet.android.Utils.SpanUtil;
import ir.accountbooklet.android.Utils.Theme;

import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;

import com.adivery.sdk.AdiveryBannerAdView;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener, FragmentNavigation, RecyclerListView.RecyclerListViewListener, RecyclerListView.RecyclerListViewClickListener, AppBarLayout.OnOffsetChangedListener, NotificationCenter.MessageControllerListener {

  public static void startActivity(Context context) {
    context.startActivity(new Intent(context, MainActivity.class));
  }

  private TextView tvTitle;
  private TextView tvName;
  private TextView tvDebtor;
  private TextView tvCreditor;
  private TextView tvClearing;
  private TextView tvDebtorCount;
  private TextView tvCreditorCount;
  private TextView tvClearingCount;
  private TextView tvFilters;
  private TextView tvSort;
  private TextView tvAdd;
  private TextView tvMessage;
  private ViewGroup layoutTitle;
  private AdiveryBannerAdView bannerLayout;
  private AppBarLayout appBar;
  private SelectorImageView ivSettings;
  private SelectorImageView ivMore;
  private RecyclerListView recyclerListView;
  private AccountsAdapter adapter;
  private List<RecyclerListView.BaseModel> accounts = new ArrayList<>();
  private AnimatorSet animatorSet;
  private AnimatorSet animatorSetTitle;
  private FragmentController fragmentController;
  private FilterModel filters = new FilterModel();
  private final static DispatchQueue queue = new DispatchQueue("mainActivityQueue");
  private boolean isShowAdd;
  private boolean isShowTitle;
  private float layoutTitleProgress;
  private boolean sort = true;

  private static class AccountsAdapter extends RecyclerListView.BaseAdapter<RecyclerListView.BaseModel> {
    public AccountsAdapter(Context context, List<RecyclerListView.BaseModel> list) {
      super(context, list);
    }

    @Override
    public @NonNull RecyclerListView.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
      return new RecyclerListView.Holder(new AccountsRow(context));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerListView.Holder holder, int position) {
      ((AccountsRow) holder.itemView).setModel((AccountsModel) list.get(position));
    }
  }

  private void initializeViews() {
    ivSettings = findViewById(R.id.ivSettings);
    ivMore = findViewById(R.id.ivMore);
    tvTitle = findViewById(R.id.tvTitle);
    tvName = findViewById(R.id.tvName);
    tvDebtor = findViewById(R.id.tvDebtor);
    tvCreditor = findViewById(R.id.tvCreditor);
    tvClearing = findViewById(R.id.tvClearing);
    tvDebtorCount = findViewById(R.id.tvDebtorCount);
    tvCreditorCount = findViewById(R.id.tvCreditorCount);
    tvClearingCount = findViewById(R.id.tvClearingCount);
    tvFilters = findViewById(R.id.tvFilters);
    tvSort = findViewById(R.id.tvSort);
    tvAdd = findViewById(R.id.tvAdd);
    tvMessage = findViewById(R.id.tvMessage);
    layoutTitle = findViewById(R.id.layoutTitle);
    bannerLayout = findViewById(R.id.bannerLayout);
    appBar = findViewById(R.id.appBar);
    recyclerListView = findViewById(R.id.recyclerListView);
  }

  @SuppressLint("SetTextI18n")
  private void initializeDefault() {
    fragmentController = FragmentController.newInstance(getSupportFragmentManager());
    tvTitle.setText(R.string.app_name);
    tvSort.setText(context.getString(R.string.sort) + " (" + (sort ? "جدید" : "قدیم") + ")");

    ViewGroup.LayoutParams params = tvAdd.getLayoutParams();
    params.width = (AndroidUtilities.displaySize.x - AndroidUtilities.dp(50)) / 2;
    tvAdd.setLayoutParams(params);

    tvFilters.setText(SpanUtil.span(new SpannableStringBuilder(tvFilters.getText()), 0, -1, tvFilters.getCurrentTextColor(), -1, false, 0, 0, 0, Theme.filter_list_drawable, AndroidUtilities.dp(17), AndroidUtilities.dp(10), 0, 0, 0, 0));
    tvSort.setText(SpanUtil.span(new SpannableStringBuilder(tvSort.getText()), 0, -1, tvSort.getCurrentTextColor(), -1, false, 0, 0, 0, Theme.sort_drawable, AndroidUtilities.dp(17), AndroidUtilities.dp(10), 0, 0, 0, 0));
    tvAdd.setText(SpanUtil.span(new SpannableStringBuilder(tvAdd.getText()), 0, -1, tvAdd.getCurrentTextColor(), -1, false, 0, 0, 0, Theme.positive_drawable, AndroidUtilities.dp(17), AndroidUtilities.dp(10), 0, 0, 0, 0));
  }

  private void initializeRecyclerView() {
    recyclerListView.setAdapter(adapter = new AccountsAdapter(context, accounts));
  }

  private void initializeListeners() {
    ivSettings.setOnClickListener(this);
    ivMore.setOnClickListener(this);
    tvFilters.setOnClickListener(this);
    tvSort.setOnClickListener(this);
    tvAdd.setOnClickListener(this);
    recyclerListView.setRecyclerListViewListener(this);
    recyclerListView.setRecyclerListViewClickListener(this);
    appBar.addOnOffsetChangedListener(this);
    NotificationCenter.getInstance().addListener(this, NotificationCenter.dataChanged);
  }

  private void setInfo() {
    tvName.setText(Session.getInstance().getName());
    HeaderInfoModel amount = Database.getInstance().getAmounts();
    setAmountText(tvDebtor, amount.debtorAmount);
    tvDebtorCount.setText(String.valueOf(amount.debtorCount));
    setAmountText(tvCreditor, amount.creditorAmount);
    tvCreditorCount.setText(String.valueOf(amount.creditorCount));
    setAmountText(tvClearing, amount.clearingAmount);
    tvClearingCount.setText(String.valueOf(amount.clearingCount));
    reloadAccounts();
  }

  private void loadAccounts() {
    recyclerListView.setCheckingPosition(false);
    queue.postRunnable(() -> {
      int id = 0;
      if (accounts.size() > 0) {
        id = ((AccountsModel) accounts.get(accounts.size() - 1)).id;
      }
      final List<AccountsModel> list = Database.getInstance().getAccounts(id, 50, filters.type, filters.str, filters.date, filters.dateTo, sort);
      accounts.addAll(list);
      AndroidUtilities.runOnUIThread(() -> {
        adapter.notifyDataSetChanged();
        if (accounts.isEmpty()) {
          AndroidUtilities.setVisibility(tvMessage, View.VISIBLE);
          if (filters.count() == 0) {
            tvMessage.setText(R.string.list_is_empty);
          } else {
            tvMessage.setText(R.string.not_found_result);
          }
        } else {
          AndroidUtilities.setVisibility(tvMessage, View.GONE);
          recyclerListView.setCheckingPosition(list.size() >= 50);
        }
      });
    });
  }

  private void reloadAccounts() {
    recyclerListView.scrollToPosition(0);
    accounts.clear();
    loadAccounts();
  }

  private void setAmountText(TextView text, long amount) {
    String price = AndroidUtilities.formatPrice(amount, false);
    SpannableStringBuilder spannable = new SpannableStringBuilder(price + " تومان");
    SpanUtil.span(spannable, 0, price.length(), Theme.getColor(Theme.key_app_text), AndroidUtilities.dp(12), true);
    text.setText(spannable);
  }

  @SuppressLint("SetTextI18n")
  private void setFilters() {
    int count = filters.count();
    tvFilters.setText(context.getString(R.string.filters) + (count > 0 ? " (" + count + ")" : ""));
    tvFilters.setText(SpanUtil.span(new SpannableStringBuilder(tvFilters.getText()), 0, -1, tvFilters.getTextColors().getDefaultColor(), -1, false, 0, 0, 0, Theme.filter_list_drawable, AndroidUtilities.dp(17), AndroidUtilities.dp(7), 0, 0, 0, 0));
    tvSort.setText(context.getString(R.string.sort) + " (" + (sort ? "جدید" : "قدیم") + ")");
    tvSort.setText(SpanUtil.span(new SpannableStringBuilder(tvSort.getText()), 0, -1, tvSort.getTextColors().getDefaultColor(), -1, false, 0, 0, 0, Theme.sort_drawable, AndroidUtilities.dp(17), AndroidUtilities.dp(7), 0, 0, 0, 0));
    reloadAccounts();
  }

  private void loadInformation() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      return;
    }
    MortinoUtils.loadInformation(context, tag, null, jObject -> {
      try {
        String adiveryKey = jObject.getString(Constants.ADIVERY_KEY);
        String adiveryBannerMain = jObject.getString(Constants.ADIVERY_BANNER_MAIN);
        String adiveryBannerAccount = jObject.getString(Constants.ADIVERY_BANNER_ACCOUNT);
        if (!TextUtils.isEmpty(adiveryKey) && !(adiveryKey = adiveryKey.trim()).isEmpty()) {
          Session.getInstance().setAdiveryKey(adiveryKey);
        }
        if (!TextUtils.isEmpty(adiveryBannerMain) && !(adiveryBannerMain = adiveryBannerMain.trim()).isEmpty()) {
          Session.getInstance().setAdiveryBannerMain(adiveryBannerMain);
        }
        if (!TextUtils.isEmpty(adiveryBannerAccount) && !(adiveryBannerAccount = adiveryBannerAccount.trim()).isEmpty()) {
          Session.getInstance().setAdiveryBannerAccount(adiveryBannerAccount);
        }
        AdiveryUtils.getInstance().check(jObject.getInt(Constants.COUNT), jObject.getInt(Constants.X_AD), jObject.getBoolean(Constants.SHOW_USER_AD));
        AndroidUtilities.runOnUIThread(() -> AdiveryUtils.getInstance().showBanner(bannerLayout, Session.getInstance().getAdiveryBannerMain()));
      } catch (Exception e) {
        AppLog.e(MainActivity.class, e);
      }
    });
  }

  private void actionSetting() {
    showFragment(SettingFragment.newInstance());
  }

  private void actionMore() {
    showFragment(MoreFragment.newInstance());
  }

  private void actionFilters() {
    showFragment(FilterFragment.newInstance().setFilters(filters).setListener(filters -> {
      MainActivity.this.filters = filters;
      setFilters();
    }));
  }

  private void actionSort() {
    if (recyclerListView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
      return;
    }
    sort = !sort;
    setFilters();
  }

  private void actionAdd() {
    showFragment(CreateAccountFragment.newInstance().setFlag(Constants.FLAG_CREATE));
  }

  private void animationAddChat(boolean show) {
    if (isShowAdd == show) {
      return;
    }
    isShowAdd = show;
    if (animatorSet != null && animatorSet.isRunning()) {
      animatorSet.cancel();
    }
    animatorSet = new AnimatorSet();
    animatorSet.playTogether(ObjectAnimator.ofFloat(tvAdd, View.TRANSLATION_Y, tvAdd.getTranslationY(), isShowAdd ? 0 : AndroidUtilities.dp(55)));
    animatorSet.playTogether(ObjectAnimator.ofFloat(tvAdd, View.ALPHA, tvAdd.getAlpha(), isShowAdd ? 1.0f : 0.0f));
    animatorSet.setDuration(350);
    animatorSet.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        animatorSet = null;
      }
    });
    animatorSet.start();
  }

  private void animationTitle(boolean show) {
    if (isShowTitle == show) {
      return;
    }
    isShowTitle = show;
    if (animatorSetTitle != null && animatorSetTitle.isRunning()) {
      animatorSetTitle.cancel();
    }
    animatorSetTitle = new AnimatorSet();
    animatorSetTitle.playTogether(ObjectAnimator.ofFloat(this, "layoutTitleProgress",  layoutTitleProgress, isShowTitle ? 1.0f : 0.0f));
    animatorSetTitle.setDuration(350);
    animatorSetTitle.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        animatorSetTitle = null;
      }
    });
    animatorSetTitle.start();
  }

  @Keep
  private void setLayoutTitleProgress(float progress) {
    this.layoutTitleProgress = progress;
    layoutTitle.setBackgroundColor(ColorUtils.blendARGB(0xFFFFFFFF, Theme.getColor(Theme.key_app_primary), progress));
    Theme.SelectorBuilder builder = tvFilters.getSelectorBuilder();
    builder.color(ColorUtils.blendARGB(Theme.getColor(Theme.key_app_primary), 0xFFD7D7D7, progress));
    tvFilters.setSelectorBuilder(builder);
    tvSort.setSelectorBuilder(builder);
    tvFilters.setTextColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_app_text2), Theme.getColor(Theme.key_app_text), progress));
    tvSort.setTextColor(tvFilters.getCurrentTextColor());
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initializeViews();
    initializeDefault();
    initializeRecyclerView();
    initializeListeners();
    loadInformation();
    setInfo();
  }

  @SuppressLint("NonConstantResourceId")
  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.ivSettings:
        actionSetting();
        break;
      case R.id.ivMore:
        actionMore();
        break;
      case R.id.tvFilters:
        actionFilters();
        break;
      case R.id.tvSort:
        actionSort();
        break;
      case R.id.tvAdd:
        actionAdd();
        break;
    }
  }

  @Override
  public void showFragment(DialogFragment fragment) {
    fragmentController.showFragment(fragment);
  }

  @Override
  public void onRecyclerListViewPointPosition(RecyclerView recyclerView, int position, boolean up) {
    if (!up) {
      loadAccounts();
    }
  }

  @Override
  public void onRecyclerListViewPositionChanged(RecyclerView recyclerView, int firstPosition, int lastPosition) {

  }

  @Override
  public void onRecyclerListViewScrollValueChanged(RecyclerView recyclerView, boolean value) {

  }

  @Override
  public void onRecyclerListViewScrollChanged(RecyclerView recyclerView, int currentScroll, int dx, final int dy) {
    AndroidUtilities.runOnUIThread(() -> animationAddChat(dy <= 0));
  }

  @Override
  public void onRecyclerListViewScrollStateChanged(RecyclerView recyclerView, int newState) {

  }

  @Override
  public void onRecyclerListViewPositionClicked(RecyclerView recyclerView, int position) {
    showFragment(AccountFragment.newInstance().setAccount((AccountsModel) accounts.get(position)));
  }

  @Override
  public void onRecyclerListViewPositionLongClicked(RecyclerView recyclerView, int position) {

  }

  @Override
  public void onOffsetChanged(AppBarLayout appBarLayout, final int verticalOffset) {
    animationTitle(appBar.getTotalScrollRange() + verticalOffset == 0);
  }

  @Override
  public void onNotificationReceived(int event, @NonNull Object... data) {
    if (event == NotificationCenter.dataChanged) {
      setInfo();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    NotificationCenter.getInstance().removeListener(this, NotificationCenter.dataChanged);
    if (animatorSet != null && animatorSet.isRunning()) {
      animatorSet.cancel();
    }
    if (animatorSetTitle != null && animatorSetTitle.isRunning()) {
      animatorSetTitle.cancel();
    }
    appBar.removeOnOffsetChangedListener(this);
  }
}
