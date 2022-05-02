package ir.accountbooklet.android.Fragments;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.adivery.sdk.AdiveryBannerAdView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import ir.accountbooklet.android.Constants;
import ir.accountbooklet.android.Customs.ImageView;
import ir.accountbooklet.android.Customs.MediaLister;
import ir.accountbooklet.android.Customs.ParallaxScrollView;
import ir.accountbooklet.android.Customs.Row.AmountsRow;
import ir.accountbooklet.android.Customs.Row.CardsRow;
import ir.accountbooklet.android.Customs.SelectorImageView;
import ir.accountbooklet.android.Customs.TextView;
import ir.accountbooklet.android.Dialogs.DialogAddAmount;
import ir.accountbooklet.android.Dialogs.DialogAddCard;
import ir.accountbooklet.android.Dialogs.DialogLoading;
import ir.accountbooklet.android.Dialogs.DialogMessage;
import ir.accountbooklet.android.Models.AccountModel;
import ir.accountbooklet.android.Models.AccountsModel;
import ir.accountbooklet.android.Models.AmountsModel;
import ir.accountbooklet.android.Models.CardsModel;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Session.Database;
import ir.accountbooklet.android.Session.Session;
import ir.accountbooklet.android.Utils.AdiveryUtils;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.DispatchQueue;
import ir.accountbooklet.android.Utils.FileUtils;
import ir.accountbooklet.android.Utils.LayoutHelper;
import ir.accountbooklet.android.Utils.LocaleController;
import ir.accountbooklet.android.Utils.MarketUtils;
import ir.accountbooklet.android.Utils.NotificationCenter;
import ir.accountbooklet.android.Utils.SpanUtil;
import ir.accountbooklet.android.Utils.Theme;

public class AccountFragment extends BaseDialogFragment implements View.OnClickListener, ViewTreeObserver.OnScrollChangedListener, NotificationCenter.MessageControllerListener {
  private SelectorImageView ivBack;
  private SelectorImageView ivShare;
  private TextView tvTitle;
  private TextView tvAccountName;
  private TextView tvTime;
  private TextView tvStatus;
  private TextView tvAmount;
  private TextView tvCarts;
  private TextView tvMobile;
  private TextView tvPhone;
  private TextView tvFatherName;
  private TextView tvAddress;
  private TextView tvPageNumber;
  private TextView tvDescription;
  private TextView tvEdit;
  private TextView tvCall;
  private ViewGroup layoutAmounts;
  private ViewGroup layoutCards;
  private ViewGroup layoutCard;
  private ViewGroup layoutMobile;
  private ViewGroup layoutPhone;
  private ViewGroup layoutFatherName;
  private ViewGroup layoutAddress;
  private ViewGroup layoutPageNumber;
  private ViewGroup layoutDescription;
  private ViewGroup layoutMedia;
  private MediaLister mediaLister;
  private AdiveryBannerAdView bannerLayout;
  private ParallaxScrollView scrollView;
  private ImageView ivImage;
  private DialogLoading dialogLoading;
  private AccountModel account = new AccountModel();
  private List<File> medias = new ArrayList<>();
  private final static DispatchQueue queue = new DispatchQueue("accountQueue");

  public static AccountFragment newInstance() {
    return new AccountFragment();
  }

  public AccountFragment setAccount(AccountsModel account) {
    this.account.id = account != null ? account.id : -1;
    return this;
  }

  private void initialiseViews(View view) {
    ivBack = view.findViewById(R.id.ivBack);
    ivShare = view.findViewById(R.id.ivShare);
    tvTitle = view.findViewById(R.id.tvTitle);
    tvAccountName = view.findViewById(R.id.tvAccountName);
    tvTime = view.findViewById(R.id.tvTime);
    tvStatus = view.findViewById(R.id.tvStatus);
    tvAmount = view.findViewById(R.id.tvAmount);
    tvCarts = view.findViewById(R.id.tvCarts);
    tvMobile = view.findViewById(R.id.tvMobile);
    tvPhone = view.findViewById(R.id.tvPhone);
    tvFatherName = view.findViewById(R.id.tvFatherName);
    tvAddress = view.findViewById(R.id.tvAddress);
    tvPageNumber = view.findViewById(R.id.tvPageNumber);
    tvDescription = view.findViewById(R.id.tvDescription);
    tvEdit = view.findViewById(R.id.tvEdit);
    tvCall = view.findViewById(R.id.tvCall);
    layoutAmounts = view.findViewById(R.id.layoutAmounts);
    layoutCards = view.findViewById(R.id.layoutCards);
    layoutCard = view.findViewById(R.id.layoutCard);
    layoutMobile = view.findViewById(R.id.layoutMobile);
    layoutPhone = view.findViewById(R.id.layoutPhone);
    layoutFatherName = view.findViewById(R.id.layoutFatherName);
    layoutAddress = view.findViewById(R.id.layoutAddress);
    layoutPageNumber = view.findViewById(R.id.layoutPageNumber);
    layoutDescription = view.findViewById(R.id.layoutDescription);
    layoutMedia = view.findViewById(R.id.layoutMedia);
    mediaLister = view.findViewById(R.id.mediaLister);
    bannerLayout = view.findViewById(R.id.bannerLayout);
    scrollView = view.findViewById(R.id.scrollView);
    ivImage = view.findViewById(R.id.ivImage);
  }

  private void initialiseDefaults() {
    ivImage.setSquare(true);

    tvCall.setText(SpanUtil.span(new SpannableStringBuilder(tvCall.getText()), 0, -1, tvCall.getCurrentTextColor(), -1, false, 0, 0, 0, Theme.call_account_drawable, AndroidUtilities.dp(17), AndroidUtilities.dp(10), 0, 0, 0, 0));
    tvEdit.setText(SpanUtil.span(new SpannableStringBuilder(tvEdit.getText()), 0, -1, tvEdit.getCurrentTextColor(), -1, false, 0, 0, 0, Theme.edit_account_drawable, AndroidUtilities.dp(17), AndroidUtilities.dp(10), 0, 0, 0, 0));
    mediaLister.setMode(MediaLister.MODE_GALLERY);
    mediaLister.setFlag(MediaLister.FLAG_RIGHT_TO_LEFT);
  }

  private void initialiseListeners() {
    ivBack.setOnClickListener(this);
    ivShare.setOnClickListener(this);
    tvAmount.setOnClickListener(this);
    tvCarts.setOnClickListener(this);
    tvEdit.setOnClickListener(this);
    tvCall.setOnClickListener(this);
    scrollView.getViewTreeObserver().addOnScrollChangedListener(this);
    NotificationCenter.getInstance().addListener(this, NotificationCenter.dataChanged);
  }

  private void loadInformation() {
    AndroidUtilities.setVisibility(scrollView, View.GONE);
    queue.postRunnable(() -> {
      account = Database.getInstance().getAccount(account.id);
      medias.clear();
      File[] listMedias = new File(FileUtils.MEDIA_PATH).listFiles(file -> file.getName().startsWith(account.id + "_") && (file.length() / 1024f) / 1024f <= Constants.MAX_LENGTH_FILE);
      if (listMedias != null && listMedias.length > 0) {
        medias.addAll(Arrays.asList(listMedias));
        Collections.sort(medias, (o1, o2) -> {
          if (o1 == null || o2 == null) {
            return 0;
          }
          long f1 = o1.lastModified();
          long f2 = o2.lastModified();
          if (f2 > f1) {
            return -1;
          } else if (f2 < f1) {
            return 1;
          }
          return 0;
        });
      }
      AndroidUtilities.runOnUIThread(() -> {
        AndroidUtilities.setVisibility(scrollView, View.VISIBLE);
//        scrollView.scrollBy(0, 0);
        File profileImage = new File(FileUtils.MEDIA_PATH, account.date + ".jpg");
        if (!profileImage.exists()) {
          profileImage = new File(FileUtils.MEDIA_PATH, account.id + ".jpg");
        }
        if (profileImage.exists()) {
          AndroidUtilities.setVisibility(ivImage, View.VISIBLE);
          Glide.with(context).load(profileImage).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(ivImage);
          AndroidUtilities.runOnUIThread(() -> {
            ObjectAnimator objectAnimator = ObjectAnimator.ofInt(scrollView, "scrollY", scrollView.getScrollY(), AndroidUtilities.displaySize.x / 3);
            objectAnimator.setDuration(500);
//            objectAnimator.setInterpolator(new OvershootInterpolator());
            objectAnimator.start();
          }, 1000);
//          scrollView.scrollBy(0, AndroidUtilities.displaySize.x / 3);
        } else {
          AndroidUtilities.setVisibility(ivImage, View.GONE);
        }
        tvAccountName.setText(account.accountName);
        if (account.type == Constants.TYPE_DEBTOR) {
          tvStatus.setText(R.string.debtor_);
          tvStatus.setTextColor(Theme.getColor(Theme.key_app_default4));
          tvTime.setText(LocaleController.getLocaleDate(account.date).getDateWithCalculate());
        } else if(account.type == Constants.TYPE_CREDITOR) {
          tvStatus.setText(R.string.creditor_);
          tvStatus.setTextColor(Theme.getColor(Theme.key_app_accent));
          tvTime.setText(LocaleController.getLocaleDate(account.date).getDateWithCalculate());
        } else if(account.type == Constants.TYPE_DEBTOR_CLEARING) {
          tvTime.setText(account.dateClearing > 0 ? String.format("شروع: %s پایان: %s", LocaleController.getLocaleDate(account.date).getDateWithCalculate(), LocaleController.getLocaleDate(account.dateClearing).getDateWithCalculate()) : LocaleController.getLocaleDate(account.date).getDateWithCalculate());
          tvStatus.setText(R.string.debtor_clearing);
          tvStatus.setTextColor(Theme.getColor(Theme.key_app_default));
        } else if(account.type == Constants.TYPE_CREDITOR_CLEARING) {
          tvTime.setText(account.dateClearing > 0 ? String.format("شروع: %s پایان: %s", LocaleController.getLocaleDate(account.date).getDateWithCalculate(), LocaleController.getLocaleDate(account.dateClearing).getDateWithCalculate()) : LocaleController.getLocaleDate(account.date).getDateWithCalculate());
          tvStatus.setText(R.string.creditor_clearing);
          tvStatus.setTextColor(Theme.getColor(Theme.key_app_default));
        } else {
          tvTime.setText(account.dateClearing > 0 ? String.format("شروع: %s پایان: %s", LocaleController.getLocaleDate(account.date).getDateWithCalculate(), LocaleController.getLocaleDate(account.dateClearing).getDateWithCalculate()) : LocaleController.getLocaleDate(account.date).getDateWithCalculate());
          tvStatus.setText(R.string.clearing);
          tvStatus.setTextColor(Theme.getColor(Theme.key_app_default));
        }
        setAmounts();
        setCards();
        tvMobile.setText(account.mobile);
        AndroidUtilities.setVisibility(layoutMobile, TextUtils.isEmpty(account.mobile) ? View.GONE : View.VISIBLE);
        tvPhone.setText(account.phone);
        AndroidUtilities.setVisibility(layoutPhone, TextUtils.isEmpty(account.phone) ? View.GONE : View.VISIBLE);
        tvFatherName.setText(account.fatherName);
        AndroidUtilities.setVisibility(layoutFatherName, TextUtils.isEmpty(account.fatherName) ? View.GONE : View.VISIBLE);
        tvAddress.setText(account.address);
        AndroidUtilities.setVisibility(layoutAddress, TextUtils.isEmpty(account.address) ? View.GONE : View.VISIBLE);
        tvPageNumber.setText(String.valueOf(account.pageNumber));
        AndroidUtilities.setVisibility(layoutPageNumber, account.pageNumber == 0 ? View.GONE : View.VISIBLE);
        tvDescription.setText(account.description);
        AndroidUtilities.setVisibility(layoutDescription, TextUtils.isEmpty(account.description) ? View.GONE : View.VISIBLE);
        AndroidUtilities.setVisibility(layoutMedia, medias.isEmpty() ? View.GONE : View.VISIBLE);
        setMediaLister();
      });
    });
  }

  private void setMediaLister() {
    mediaLister.removeAllMedia();
    for (int i=0; i < medias.size(); i++) {
      mediaLister.addMedia(String.valueOf(i), medias.get(i), null, null);
    }
  }

  private void setAmounts() {
    long amount = 0;
    layoutAmounts.removeAllViews();
    if (account.amounts != null) {
      for (AmountsModel am : account.amounts) {
        amount += am.amount;
        layoutAmounts.addView(new AmountsRow(context).setModel(am), LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, AndroidUtilities.dp(10), 0, 0));
      }
    }
    Spannable spannable = new SpannableString(AndroidUtilities.formatPrice(amount, false) + " تومان");
    spannable.setSpan(new CharacterStyle() {
      @Override
      public void updateDrawState(@NonNull TextPaint ds) {
        ds.setTextSize(AndroidUtilities.dp(10));
        ds.setColor(Theme.getColor(Theme.key_app_text4));
      }
    }, spannable.length() - 5, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    tvAmount.setText(spannable);
  }

  private void setCards() {
    layoutCards.removeAllViews();
    if (account.cards != null) {
      for (CardsModel ca : account.cards) {
        layoutCards.addView(new CardsRow(context).setModel(ca), LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, AndroidUtilities.dp(10), 0, 0));
      }
    }
    AndroidUtilities.setVisibility(layoutCard, account.cards == null || account.cards.isEmpty() ? View.GONE : View.VISIBLE);
  }

  private void actionBack() {
    dismiss();
  }

  private void actionShare() {
    StringBuilder builder = new StringBuilder();
    builder.append("نام: ");
    builder.append(account.accountName);
    builder.append("\n--------------------------------\n");
    if (account.type == Constants.TYPE_DEBTOR) {
      builder.append("تاریخ ساخت حساب: ");
      builder.append(LocaleController.getLocaleDate(account.date).getDateAndHour());
      builder.append("\n--------------------------------\n");
      builder.append("وضعیت: ");
      builder.append(context.getString(R.string.debtor));
      builder.append("\n--------------------------------\n");
    } else if(account.type == Constants.TYPE_CREDITOR) {
      builder.append("تاریخ ساخت حساب: ");
      builder.append(LocaleController.getLocaleDate(account.date).getDateAndHour());
      builder.append("\n--------------------------------\n");
      builder.append("وضعیت: ");
      builder.append(context.getString(R.string.creditor));
      builder.append("\n--------------------------------\n");
    } else {
      builder.append("تاریخ ساخت حساب: ");
      builder.append(account.dateClearing > 0 ? String.format("شروع: %s پایان: %s", LocaleController.getLocaleDate(account.date).getDateAndHour(), LocaleController.getLocaleDate(account.dateClearing).getDateAndHour()) : LocaleController.getLocaleDate(account.date).getDateAndHour());
      builder.append("\n--------------------------------\n");
      builder.append("وضعیت: ");
      builder.append(context.getString(R.string.clearing));
      builder.append("\n--------------------------------\n");
    }
    if (account.amounts != null && !account.amounts.isEmpty()) {
      builder.append("قیمت ها: ");
      builder.append("\n");
      for (AmountsModel amount : account.amounts) {
        if (amount.amount < 0) {
          builder.append("پرداخت شده: ");
        }
        builder.append(AndroidUtilities.formatPrice(Math.abs(amount.amount), false));
        builder.append(" تومان");
        builder.append("\n");
        builder.append("تاریخ ثبت مبلغ: ");
        builder.append(LocaleController.getLocaleDate(amount.date).getDateAndHour());
        builder.append("\n");
        if (!TextUtils.isEmpty(amount.description)) {
          builder.append("توضیح: ");
          builder.append(amount.description);
          builder.append("\n");
        }
        if (account.amounts.indexOf(amount) < account.amounts.size() - 1) {
          builder.append("\n");
        }
      }
    }
    builder.append("--------------------------------\n");
    if (!TextUtils.isEmpty(account.mobile)) {
      builder.append("شماره موبایل: ");
      builder.append(account.mobile);
      builder.append("\n--------------------------------\n");
    }
    if (!TextUtils.isEmpty(account.phone)) {
      builder.append("شماره تلفن ثابت: ");
      builder.append(account.phone);
      builder.append("\n--------------------------------\n");
    }
    if (!TextUtils.isEmpty(account.fatherName)) {
      builder.append("نام پدر: ");
      builder.append(account.fatherName);
      builder.append("\n--------------------------------\n");
    }
    if (!TextUtils.isEmpty(account.address)) {
      builder.append("آدرس: ");
      builder.append(account.address);
      builder.append("\n--------------------------------\n");
    }
    if (!TextUtils.isEmpty(account.description)) {
      builder.append("توضیحات: ");
      builder.append(account.description);
      builder.append("\n--------------------------------\n");
    }
    builder.append(context.getString(R.string.app_name));
    builder.append("\n\n");
    builder.append("لینک دانلود: ");
    builder.append("\n");
    builder.append(MarketUtils.getMarketLinkShare(false));
    AndroidUtilities.shareText(context, builder.toString());
  }

  @SuppressLint("UseCompatLoadingForDrawables")
  private void actionAmounts() {
    if (layoutAmounts.getVisibility() == View.GONE) {
      layoutAmounts.setVisibility(View.VISIBLE);
      tvAmount.setCompoundDrawables(context.getResources().getDrawable(R.drawable.ic_arrow_drop_up), null, null, null);
    } else {
      layoutAmounts.setVisibility(View.GONE);
      tvAmount.setCompoundDrawables(context.getResources().getDrawable(R.drawable.ic_arrow_drop_down), null, null, null);
    }
  }

  @SuppressLint("UseCompatLoadingForDrawables")
  private void actionCards() {
    if (layoutCards.getVisibility() == View.GONE) {
      layoutCards.setVisibility(View.VISIBLE);
      tvCarts.setCompoundDrawables(context.getResources().getDrawable(R.drawable.ic_arrow_drop_up), null, null, null);
    } else {
      layoutCards.setVisibility(View.GONE);
      tvCarts.setCompoundDrawables(context.getResources().getDrawable(R.drawable.ic_arrow_drop_down), null, null, null);
    }
  }

  private void actionEdit() {
    mFragmentNavigation.showFragment(EditAccountFragment.newInstance().setInformation(account, new EditAccountFragment.EditAccountFragmentListener() {

      @Override
      public void onClearAccount() {
        DialogMessage.newInstance(context, new DialogMessage.Builder().title(context.getString(R.string.clear_account)).message(context.getString(R.string.message_clear_account)).btn(0, "خیر").btn2(1, context.getString(R.string.clear_account), Theme.getColor(Theme.key_app_default)).listener((id, check) -> {
          if (id == 1) {
            account.dateClearing = System.currentTimeMillis() / 1000;
            Database.getInstance().clearingAccount(account);
            NotificationCenter.getInstance().postNotification(NotificationCenter.dataChanged);
            loadInformation();
          }
        })).show();
      }

      @Override
      public void onDeleteAccount() {
        DialogMessage.newInstance(context, new DialogMessage.Builder().title(context.getString(R.string.delete_account)).message(context.getString(R.string.message_delete_account)).btn(0, "خیر").btn2(1, "حذف کردن", Theme.getColor(Theme.key_app_accent)).listener((id, check) -> {
          if (id == 1) {
            processDeleteAccount();
          }
        })).show();
      }

      @Override
      public void onAddAmount() {
        DialogAddAmount.newInstance(context).setFlag(Constants.FLAG_CREATE).setListener(amount -> {
          amount.accountId = account.id;
          amount.date = amount.date > 0 ? amount.date : System.currentTimeMillis() / 1000;
          if (account.amounts == null) {
            account.amounts = new ArrayList<>();
          }
          account.amounts.add(0, amount);
          Database.getInstance().insertAmount(amount);
          setAmounts();
          NotificationCenter.getInstance().postNotification(NotificationCenter.dataChanged);
        }).show();
      }

      @Override
      public void onAddCard() {
        DialogAddCard.newInstance(context).setFlag(Constants.FLAG_CREATE).setListener(card -> {
          card.accountId = account.id;
          if (account.cards == null) {
            account.cards = new ArrayList<>();
          }
          account.cards.add(0, card);
          Database.getInstance().insertCard(card);
          setCards();
          NotificationCenter.getInstance().postNotification(NotificationCenter.dataChanged);
        }).show();
      }

      @Override
      public void onEditAccount() {
        mFragmentNavigation.showFragment(CreateAccountFragment.newInstance().setAccount(account).setFlag(Constants.FLAG_EDIT));
      }
    }));
  }

  private void processDeleteAccount() {
    initializeDialogLoading();
    dialogLoading.show();
    queue.postRunnable(() -> {
      Database.getInstance().deleteAccount(account);
      FileUtils.delete(new File(FileUtils.MEDIA_PATH, account.id + ".jpg"));
      FileUtils.delete(new File(FileUtils.MEDIA_PATH, account.date + ".jpg"));
      File[] listMedias = new File(FileUtils.MEDIA_PATH).listFiles(file -> file.getName().startsWith(account.id + "_") && (file.length() / 1024f) / 1024f <= Constants.MAX_LENGTH_FILE);
      if (listMedias != null && listMedias.length > 0) {
        for (File file : listMedias) {
          FileUtils.delete(file);
        }
      }
      AndroidUtilities.runOnUIThread(() -> {
        dialogLoading.dismiss();
        NotificationCenter.getInstance().postNotification(NotificationCenter.dataChanged);
        dismiss();
      });
    });
  }

  private void actionCall() {
    DialogMessage.newInstance(context, new DialogMessage.Builder().title(context.getString(R.string.call)).message(context.getString(R.string.message_call)).btn(TextUtils.isEmpty(account.phone) ? 1 : 0, TextUtils.isEmpty(account.phone) ? account.mobile : account.phone).btn2(1, TextUtils.isEmpty(account.phone) ? null : account.mobile, 0x90000000).listener((id, check) -> {
      if (id == 1) {
        AndroidUtilities.startActivity(context, new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + account.mobile)));
      } else {
        AndroidUtilities.startActivity(context, new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + account.phone)));
      }
    })).show();
  }

  private void initializeDialogLoading() {
    if (dialogLoading == null) {
      dialogLoading = DialogLoading.newInstance(context);
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_account, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    if (account.id < 0) {
      dismiss();
      return;
    }
    initialiseViews(view);
    initialiseDefaults();
    initialiseListeners();
    AdiveryUtils.getInstance().showBanner(bannerLayout, Session.getInstance().getAdiveryBannerAccount());
    loadInformation();
  }

  @SuppressLint("NonConstantResourceId")
  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.ivBack:
        actionBack();
        break;
      case R.id.ivShare:
        actionShare();
        break;
      case R.id.tvAmount:
        actionAmounts();
        break;
      case R.id.tvCarts:
        actionCards();
        break;
      case R.id.tvEdit:
        actionEdit();
        break;
      case R.id.tvCall:
        actionCall();
        break;
    }
  }

  @Override
  public void onScrollChanged() {
    final int currentScroll = scrollView.getScrollY();
    if (currentScroll >= ((ViewGroup) scrollView.getChildAt(0)).getChildAt(1).getTop() + tvAccountName.getMeasuredHeight() + AndroidUtilities.dp(20)) {
      if (TextUtils.isEmpty(tvTitle.getText())) {
        tvTitle.setText(tvAccountName.getText());
      }
    } else {
      if (!TextUtils.isEmpty(tvTitle.getText())) {
        tvTitle.setText(null);
      }
    }
  }

  @Override
  public void onNotificationReceived(int event, @NonNull Object... data) {
    if (event == NotificationCenter.dataChanged) {
      if (data.length > 0 && data[0] instanceof Integer && (int) data[0] == account.id) {
        loadInformation();
      }
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    scrollView.getViewTreeObserver().removeOnScrollChangedListener(this);
    NotificationCenter.getInstance().removeListener(this, NotificationCenter.dataChanged);
  }
}