package ir.accountbooklet.android.Fragments;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import ir.accountbooklet.android.Activity.MainActivity;
import ir.accountbooklet.android.Constants;
import ir.accountbooklet.android.Customs.EditText;
import ir.accountbooklet.android.Customs.ImageView;
import ir.accountbooklet.android.Customs.MediaLister;
import ir.accountbooklet.android.Customs.Row.EditAmountsRow;
import ir.accountbooklet.android.Customs.Row.EditCardsRow;
import ir.accountbooklet.android.Customs.SelectorImageView;
import ir.accountbooklet.android.Customs.TextView;
import ir.accountbooklet.android.Dialogs.DialogAddAmount;
import ir.accountbooklet.android.Dialogs.DialogAddCard;
import ir.accountbooklet.android.Dialogs.DialogLoading;
import ir.accountbooklet.android.Dialogs.DialogMessage;
import ir.accountbooklet.android.Models.AccountModel;
import ir.accountbooklet.android.Models.AmountsModel;
import ir.accountbooklet.android.Models.CardsModel;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Session.Database;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.AppLog;
import ir.accountbooklet.android.Utils.CountryUtil;
import ir.accountbooklet.android.Utils.DispatchQueue;
import ir.accountbooklet.android.Utils.FileUtils;
import ir.accountbooklet.android.Utils.LayoutHelper;
import ir.accountbooklet.android.Utils.LocaleController;
import ir.accountbooklet.android.Utils.NotificationCenter;
import ir.accountbooklet.android.Utils.PermissionManager;
import ir.accountbooklet.android.Utils.Theme;
import ir.hamsaa.persiandatepicker.Listener;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.util.PersianCalendar;

import static android.app.Activity.RESULT_OK;

public class CreateAccountFragment extends BaseDialogFragment implements View.OnClickListener, TextWatcher, MediaLister.MediaListerListener {

  public static CreateAccountFragment newInstance() {
    return new CreateAccountFragment();
  }

  private ViewGroup baseLayout;
  private SelectorImageView ivBack;
  private TextView tvTitle;
  private TextView tvCreate;
  private TextView tvClear;
  private TextView tvImage;
  private TextView tvAddAmount;
  private TextView tvAddCard;
  private TextView tvName;
  private TextView tvMobile;
  private TextView tvChooseContactMobile;
  private TextView tvPhone;
  private TextView tvChooseContactPhone;
  private TextView tvFatherName;
  private TextView tvAddress;
  private TextView tvPageNumber;
  private TextView tvDate;
  private TextView tvDebtor;
  private TextView tvCreditor;
  private TextView tvDescription;
  private EditText etName;
  private EditText etMobile;
  private EditText etPhone;
  private EditText etFatherName;
  private EditText etAddress;
  private EditText etPageNumber;
  private EditText etDate;
  private EditText etDescription;
  private ImageView ivImage;
  private ViewGroup layoutAmounts;
  private ViewGroup layoutCards;
  private MediaLister mediaLister;
  private AccountModel account = new AccountModel();
  private DialogLoading dialogLoading;
  private int flag;
  private Bitmap currentProfileBitmap;
  private List<File> medias = new ArrayList<>();
  private final static DispatchQueue queue = new DispatchQueue("createAccountQueue");

  public CreateAccountFragment setFlag(int flag) {
    this.flag = flag;
    return this;
  }

  public CreateAccountFragment setAccount(AccountModel account) {
    if (account != null) {
      this.account.id = account.id;
    }
    return this;
  }

  private void initialiseViews(View view) {
    baseLayout = view.findViewById(R.id.baseLayout);
    ivBack = view.findViewById(R.id.ivBack);
    tvTitle = view.findViewById(R.id.tvTitle);
    tvCreate = view.findViewById(R.id.tvCreate);
    tvClear = view.findViewById(R.id.tvClear);
    tvImage = view.findViewById(R.id.tvImage);
    tvAddAmount = view.findViewById(R.id.tvAddAmount);
    tvAddCard = view.findViewById(R.id.tvAddCard);
    tvName = view.findViewById(R.id.tvName);
    tvMobile = view.findViewById(R.id.tvMobile);
    tvChooseContactMobile = view.findViewById(R.id.tvChooseContactMobile);
    tvPhone = view.findViewById(R.id.tvPhone);
    tvChooseContactPhone = view.findViewById(R.id.tvChooseContactPhone);
    tvFatherName = view.findViewById(R.id.tvFatherName);
    tvAddress = view.findViewById(R.id.tvAddress);
    tvPageNumber = view.findViewById(R.id.tvPageNumber);
    tvDate = view.findViewById(R.id.tvDate);
    tvDebtor = view.findViewById(R.id.tvDebtor);
    tvCreditor = view.findViewById(R.id.tvCreditor);
    tvDescription = view.findViewById(R.id.tvDescription);
    etName = view.findViewById(R.id.etName);
    etMobile = view.findViewById(R.id.etMobile);
    etPhone = view.findViewById(R.id.etPhone);
    etFatherName = view.findViewById(R.id.etFatherName);
    etAddress = view.findViewById(R.id.etAddress);
    etPageNumber = view.findViewById(R.id.etPageNumber);
    etDate = view.findViewById(R.id.etDate);
    etDescription = view.findViewById(R.id.etDescription);
    layoutAmounts = view.findViewById(R.id.layoutAmounts);
    layoutCards = view.findViewById(R.id.layoutCards);
    mediaLister = view.findViewById(R.id.mediaLister);
    ivImage = view.findViewById(R.id.ivImage);
  }

  private void initialiseDefaults() {
    dialogLoading = DialogLoading.newInstance(context);
    ivImage.setCircle(true);
    if (flag == Constants.FLAG_CREATE) {
      tvTitle.setText(R.string.new_account);
      tvCreate.setText(R.string.create_account);
    } else {
      tvTitle.setText(R.string.edit_account);
      tvCreate.setText(R.string.edit);
    }
    etDate.setInputType(InputType.TYPE_NULL);
  }

  private void initialiseListeners() {
    ivBack.setOnClickListener(this);
    tvCreate.setOnClickListener(this);
    tvClear.setOnClickListener(this);
    tvImage.setOnClickListener(this);
    tvAddAmount.setOnClickListener(this);
    tvAddCard.setOnClickListener(this);
    tvDebtor.setOnClickListener(this);
    tvCreditor.setOnClickListener(this);
    etDate.setOnClickListener(this);
    tvChooseContactMobile.setOnClickListener(this);
    tvChooseContactPhone.setOnClickListener(this);
    etName.addTextChangedListener(this);
    etMobile.addTextChangedListener(this);
    etPhone.addTextChangedListener(this);
    etFatherName.addTextChangedListener(this);
    etAddress.addTextChangedListener(this);
    etPageNumber.addTextChangedListener(this);
    etDate.addTextChangedListener(this);
    etDescription.addTextChangedListener(this);
    mediaLister.setOnMediaChangeListener(this);
  }

  private void loadInformation() {
    queue.postRunnable(() -> {
      if (flag == Constants.FLAG_EDIT) {
        account = Database.getInstance().getAccount(account.id);
      }
      if (account.date > 0) {
        File profileImage = new File(FileUtils.MEDIA_PATH, account.date + ".jpg");
        if (!profileImage.exists()) {
          profileImage = new File(FileUtils.MEDIA_PATH, account.id + ".jpg");
        }
        final Bitmap bitmap = BitmapFactory.decodeFile(profileImage.getPath());
        account.bitmap = bitmap;
        currentProfileBitmap = bitmap;
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
      }
      AndroidUtilities.runOnUIThread(() -> {
        setImage();
        setAmounts();
        setCards();
        setType();
        setMediaLister();
        etName.setText(account.accountName);
        etMobile.setText(account.mobile);
        etPhone.setText(account.phone);
        etFatherName.setText(account.fatherName);
        etAddress.setText(account.address);
        etDescription.setText(account.description);
        etPageNumber.setText(account.pageNumber <= 0 ? "" : String.valueOf(account.pageNumber));
        etDate.setText(LocaleController.getLocaleDate(account.date).getDate());
      });
    });
  }

  private void actionBack() {
    dismiss();
  }

  private void actionCreate() {
    account.accountName = etName.getString();
    account.mobile = etMobile.getString();
    account.phone = etPhone.getString();
    account.fatherName = etFatherName.getString();
    account.address = etAddress.getString();
    account.description = etDescription.getString();
    account.pageNumber = etPageNumber.getInt();
    if (account.amounts == null || account.amounts.isEmpty()) {
      AndroidUtilities.runOnUIThread(() -> AndroidUtilities.toast(context, R.string.err_insert_amount));
      return;
    } else {
      for (AmountsModel amount : account.amounts) {
        amount.accountId = account.id;
        amount.date = amount.date > 0 ? amount.date : System.currentTimeMillis() / 1000;
      }
    }
    if (account.cards != null && !account.cards.isEmpty()) {
      for (CardsModel card : account.cards) {
        card.accountId = account.id;
      }
    }
    if (account.accountName.isEmpty()) {
      AndroidUtilities.runOnUIThread(() -> AndroidUtilities.toast(context, R.string.err_insert_account_name));
      return;
    }
    if (account.accountName.length() < 3) {
      AndroidUtilities.runOnUIThread(() -> AndroidUtilities.toast(context, R.string.err_account_name));
      return;
    }
    if (!account.mobile.isEmpty() && account.mobile.length() != 11) {
      AndroidUtilities.runOnUIThread(() -> AndroidUtilities.toast(context, R.string.err_mobile));
      return;
    }
    if (!account.phone.isEmpty() && account.phone.length() < 3) {
      AndroidUtilities.runOnUIThread(() -> AndroidUtilities.toast(context, R.string.err_phone));
      return;
    }
    if (account.mobile.isEmpty() && account.phone.isEmpty()) {
      AndroidUtilities.runOnUIThread(() -> AndroidUtilities.toast(context, R.string.err_insert_number));
      return;
    }
    if (!(account.type == Constants.TYPE_DEBTOR || account.type == Constants.TYPE_CREDITOR)) {
      AndroidUtilities.runOnUIThread(() -> AndroidUtilities.toast(context, R.string.err_select_type));
      return;
    }
    if (!PermissionManager.checkPermissions(context, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
      PermissionManager.requestPermissions(context, Constants.REQUEST_PERMISSION_WRITE_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
      return;
    }
    if (flag == Constants.FLAG_CREATE) {
      createOrUpdateAccount();
    } else {
      DialogMessage.newInstance(context, new DialogMessage.Builder().title(context.getString(R.string.edit_account)).message(context.getString(R.string.message_edit_account)).btn(0, "نه").btn2(1, context.getString(R.string.edit_account), Theme.getColor(Theme.key_app_default3)).listener((id, isCheck) -> {
        if (id == 1) {
          createOrUpdateAccount();
        }
      })).show();
    }
  }

  private void createOrUpdateAccount() {
    queue.postRunnable(() -> {
      AndroidUtilities.runOnUIThread(() -> dialogLoading.show());
      account.date = account.date > 0 ? account.date : System.currentTimeMillis() / 1000;
      if (flag == Constants.FLAG_CREATE) {
        Database.getInstance().insertAccount(account);
      } else {
        Database.getInstance().updateAccount(account);
      }
      createAttachFiles();
      AndroidUtilities.runOnUIThread(() -> {
        NotificationCenter.getInstance().postNotification(NotificationCenter.dataChanged, account.id);
        dialogLoading.dismiss();
        dismiss();
      }, 500);
    });
  }

  private void getContactAndSet(Intent data, int req) {
    EditText tv;
    if (req == 10) {
      tv = etMobile;
    } else if (req == 11) {
      tv = etPhone;
    } else {
      return;
    }
    try {
      String number;
//      String name = null;
      Uri uri = data.getData();
      if (uri != null) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
          cursor.moveToFirst();
          int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//          int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
//          name = cursor.getString(nameIndex);
          number = cursor.getString(phoneIndex);
          cursor.close();
          for (String str : CountryUtil.codes) {
            if (number.startsWith(str)) {
              number = number.replace(str, "0");
              break;
            }
          }
          number = number.replace(" ", "");
          if (AndroidUtilities.isNumber(number)) {
            tv.setText(number);
          }
        }
      }
    } catch (Exception e) {
      AppLog.e(MainActivity.class, e.getMessage());
    }
  }

  private boolean createAttachFiles() {
    try {
      FileUtils.checkCreateFolders();
      boolean needCreateProfile = false;
      File profileImage = new File(FileUtils.MEDIA_PATH, account.date + ".jpg");
      if (profileImage.exists()) {
        FileUtils.delete(profileImage);
        needCreateProfile = true;
      }
      File imageFile = new File(FileUtils.MEDIA_PATH, account.id + ".jpg");
      if (account.bitmap == null) {
        FileUtils.delete(imageFile);
        needCreateProfile = false;
      } else if (!account.bitmap.equals(currentProfileBitmap)) {
        needCreateProfile = true;
      }
      if (needCreateProfile) {
        OutputStream os = new FileOutputStream(imageFile);
        account.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        os.flush();
        os.close();
      }
      List<MediaLister.Media> mediaList = mediaLister.getMedias();
      if (mediaList.isEmpty()) {
        for (File f : medias) {
          FileUtils.delete(f);
        }
      } else {
        Random random = new Random();
        final List<File> medias = new ArrayList<>(this.medias);
        for (MediaLister.Media media : mediaList) {
          if (media.bitmap != null) {
            File file = new File(FileUtils.MEDIA_PATH, account.id + "_" + random.nextInt(10000) + "_" + random.nextInt(10000) + ".jpg");
            OutputStream outputStream = new FileOutputStream(file);
            media.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
          } else if (media.file != null) {
            boolean find = false;
            for (File f : medias) {
              if (media.file.equals(f)) {
                medias.remove(f);
                find = true;
                break;
              }
            }
            if (!find) {
              File file = new File(FileUtils.MEDIA_PATH, account.id + "_" + random.nextInt(10000) + "_" + random.nextInt(10000) + media.file.getName().substring(media.file.getName().lastIndexOf(".")));
              FileUtils.copyFile(media.file, file);
            }
          }
        }
        for (File f : medias) {
          FileUtils.delete(f);
        }
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private void actionClear() {
    if (flag == Constants.FLAG_CREATE) {
      account.clear();
    }
    loadInformation();
  }

  private void actionImage() {
    MediasFragment.MediaSelectListener listener = file -> {
      if (mFragmentNavigation != null) {
        mFragmentNavigation.showFragment(ImageCrapperFragment.newInstance().setImage(file, bitmap -> {
          account.bitmap = bitmap;
          setImage();
        }));
      }
    };
    if (account.bitmap != null) {
      DialogMessage.newInstance(context, new DialogMessage.Builder().title(context.getString(R.string.change_image_profile)).message(context.getString(R.string.message_select_image)).btn(0, context.getString(R.string.change_image_profile)).btn2(1, context.getString(R.string.delete_image), Theme.getColor(Theme.key_app_accent)).listener((id, isCheck) -> {
        if (id == 0) {
          if (mFragmentNavigation != null) {
            mFragmentNavigation.showFragment(MediasFragment.newInstance().setFlag(Constants.FLAG_IMAGE).setListener(listener));
          }
        } else if (id == 1) {
          account.bitmap = null;
          setImage();
        }
      })).show();
    } else {
      if (mFragmentNavigation != null) {
        mFragmentNavigation.showFragment(MediasFragment.newInstance().setFlag(Constants.FLAG_IMAGE).setListener(listener));
      }
    }
  }

  private void setImage() {
    if (account.bitmap != null) {
      ivImage.setImageBitmap(account.bitmap);
      tvImage.setText(R.string.change_image_profile);
    } else {
      ivImage.setImageResource(R.drawable.ic_post_no_image);
      tvImage.setText(R.string.choose_image_profile);
    }
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
          account.date = persianCalendar.getTimeInMillis() / 1000;
          etDate.setText(LocaleController.getLocaleDate(account.date).getDate());
        }
        @Override
        public void onDismissed() {
          account.date = 0;
          etDate.setText(LocaleController.getLocaleDate(account.date).getDate());
        }
      }).show();
  }

  private void actionDebtor() {
    account.type = Constants.TYPE_DEBTOR;
    setType();
  }

  private void actionCreditor() {
    account.type = Constants.TYPE_CREDITOR;
    setType();
  }

  private void setType() {
    if (account.type == Constants.TYPE_DEBTOR) {
      tvDebtor.setCompoundDrawables(null, null, Theme.app_radio_button_on_drawable, null);
    } else {
      tvDebtor.setCompoundDrawables(null, null, Theme.app_radio_button_off_drawable, null);
    }
    if (account.type == Constants.TYPE_CREDITOR) {
      tvCreditor.setCompoundDrawables(null, null, Theme.app_radio_button_on_drawable, null);
    } else {
      tvCreditor.setCompoundDrawables(null, null, Theme.app_radio_button_off_drawable, null);
    }
  }

  private void setMediaLister() {
    mediaLister.removeAllMedia();
    for (int i=0; i < medias.size(); i++) {
      mediaLister.addMedia(String.valueOf(i), medias.get(i), null, null);
    }
  }

  private void actionAddAmount() {
    clearFocus();
    DialogAddAmount.newInstance(context).setFlag(Constants.FLAG_CREATE).setListener(amount -> {
      if (account.amounts == null) {
        account.amounts = new ArrayList<>();
      }
      account.amounts.add(0, amount);
      setAmounts();
    }).show();
  }

  private void actionAddCard() {
    clearFocus();
    DialogAddCard.newInstance(context).setFlag(Constants.FLAG_CREATE).setListener(card -> {
      if (account.cards == null) {
        account.cards = new ArrayList<>();
      }
      account.cards.add(0, card);
      setCards();
    }).show();
  }

  private void clearFocus() {
    if (!baseLayout.isFocused()) {
     baseLayout.requestFocus();
    }
  }

  private void actionChooseContactMobile() {
    Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
    startActivityForResult(contactPickerIntent, 10);
  }

  private void actionChooseContactPhone() {
    Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
    startActivityForResult(contactPickerIntent, 11);
  }

  private void setAmounts() {
    layoutAmounts.removeAllViews();
    AndroidUtilities.setVisibility(layoutAmounts, account.amounts == null || account.amounts.isEmpty() ? View.GONE : View.VISIBLE);
    if (account.amounts != null) {
      for (int i=account.amounts.size() - 1; i >= 0; i--) {
        final AmountsModel am = account.amounts.get(i);
        final EditAmountsRow amountRow = new EditAmountsRow(context);
        amountRow.setAlpha(0.0f);
        AndroidUtilities.runOnUIThread(() -> ObjectAnimator.ofFloat(amountRow, View.ALPHA, 0.0f, 1.0f).setDuration(200).start(), 120 + (100 * layoutAmounts.getChildCount()));
        amountRow.setModel(am, new EditAmountsRow.EditAmountsListener() {
          @Override
          public void onDelete() {
            DialogMessage.newInstance(context, new DialogMessage.Builder().title("حذف مبلغ").message("آیا مبلغ انتخاب شده حذف شود؟").btn(0, "خیر").btn2(1, "حذف کردن", Theme.getColor(Theme.key_app_accent)).listener((id, check) -> {
              if (id == 1) {
                account.amounts.remove(am);
                setAmounts();
              }
            })).show();
          }

          @Override
          public void onEdit() {
            DialogAddAmount.newInstance(context).setFlag(Constants.FLAG_EDIT).setAmount(am).setListener(amount -> {
              if (amount != null) {
                am.id = amount.id;
                am.accountId = amount.accountId;
                am.amount = amount.amount;
                am.description = amount.description;
                am.date = amount.date;
              }
              amountRow.setModel(am, this);
            }).show();
          }
        });
        layoutAmounts.addView(amountRow, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, layoutAmounts.getChildCount() > 0 ? AndroidUtilities.dp(10) : 0, 0, 0));
      }
    }
  }

  private void setCards() {
    layoutCards.removeAllViews();
    AndroidUtilities.setVisibility(layoutCards, account.cards == null || account.cards.isEmpty() ? View.GONE : View.VISIBLE);
    if (account.cards != null) {
      for (int i=account.cards.size() - 1; i >= 0; i--) {
        final CardsModel ca = account.cards.get(i);
        final EditCardsRow cardRow = new EditCardsRow(context);
        cardRow.setAlpha(0.0f);
        AndroidUtilities.runOnUIThread(() -> ObjectAnimator.ofFloat(cardRow, View.ALPHA, 0.0f, 1.0f).setDuration(200).start(), 100 + (100 * layoutAmounts.getChildCount()));
        cardRow.setModel(ca, new EditCardsRow.EditCardsListener() {
          @Override
          public void onDelete() {
            DialogMessage.newInstance(context, new DialogMessage.Builder().title("حذف کارت").message("آیا کارت انتخاب شده حذف شود؟").btn(0, "خیر").btn2(1, "حذف کردن", Theme.getColor(Theme.key_app_accent)).listener((id, check) -> {
              if (id == 1) {
                account.cards.remove(ca);
                setCards();
              }
            })).show();
          }

          @Override
          public void onEdit() {
            DialogAddCard.newInstance(context).setFlag(Constants.FLAG_EDIT).setCard(ca).setListener(card -> {
              if (card != null) {
                ca.id = card.id;
                ca.accountId = card.accountId;
                ca.cardHolderName = card.cardHolderName;
                ca.bankName = card.bankName;
                ca.cardNumber = card.cardNumber;
                ca.accountNumber = card.accountNumber;
                ca.shabaNumber = card.shabaNumber;
              }
              cardRow.setModel(ca, this);
            }).show();
          }
        });
        layoutCards.addView(cardRow, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, layoutCards.getChildCount() > 0 ? AndroidUtilities.dp(10) : 0, 0, 0));
      }
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_crate_account, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (flag == Constants.FLAG_EDIT && account.id < 0) {
      dismiss();
      return;
    }
    initialiseViews(view);
    initialiseDefaults();
    initialiseListeners();
    loadInformation();
  }

  @SuppressLint("NonConstantResourceId")
  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.ivBack:
        actionBack();
        break;
      case R.id.tvCreate:
        actionCreate();
        break;
      case R.id.tvClear:
        actionClear();
        break;
      case R.id.tvImage:
        actionImage();
        break;
      case R.id.etDate:
        actionDate();
        break;
      case R.id.tvDebtor:
        actionDebtor();
        break;
      case R.id.tvCreditor:
        actionCreditor();
        break;
      case R.id.tvAddAmount:
        actionAddAmount();
        break;
      case R.id.tvAddCard:
        actionAddCard();
        break;
      case R.id.tvChooseContactMobile:
        actionChooseContactMobile();
        break;
      case R.id.tvChooseContactPhone:
        actionChooseContactPhone();
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
    if (s == etName.getText()) {
      AndroidUtilities.setVisibility(tvName, TextUtils.isEmpty(s) ? View.INVISIBLE : View.VISIBLE);
    } else if (s == etMobile.getText()) {
      AndroidUtilities.setVisibility(tvMobile, TextUtils.isEmpty(s) ? View.INVISIBLE : View.VISIBLE);
    } else if (s == etPhone.getText()) {
      AndroidUtilities.setVisibility(tvPhone, TextUtils.isEmpty(s) ? View.INVISIBLE : View.VISIBLE);
    } else if (s == etFatherName.getText()) {
      AndroidUtilities.setVisibility(tvFatherName, TextUtils.isEmpty(s) ? View.INVISIBLE : View.VISIBLE);
    } else if (s == etAddress.getText()) {
      AndroidUtilities.setVisibility(tvAddress, TextUtils.isEmpty(s) ? View.INVISIBLE : View.VISIBLE);
    } else if (s == etPageNumber.getText()) {
      AndroidUtilities.setVisibility(tvPageNumber, TextUtils.isEmpty(s) ? View.INVISIBLE : View.VISIBLE);
    } else if (s == etDate.getText()) {
      AndroidUtilities.setVisibility(tvDate, TextUtils.isEmpty(s) ? View.INVISIBLE : View.VISIBLE);
    } else if (s == etDescription.getText()) {
      AndroidUtilities.setVisibility(tvDescription, TextUtils.isEmpty(s) ? View.INVISIBLE : View.VISIBLE);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (resultCode == RESULT_OK) {
      getContactAndSet(data, requestCode);
    }
  }

  @Override
  public void onMediaAdd() {
    mFragmentNavigation.showFragment(MediasFragment.newInstance().setFlag(Constants.FLAG_IMAGE_VIDEO).setListener(file -> {
      if (AndroidUtilities.isImage(file)) {
        mFragmentNavigation.showFragment(ImageCrapperFragment.newInstance().setFixAspectRatio(false).setImage(file, bitmap -> mediaLister.addMedia(String.valueOf(System.currentTimeMillis()), null, null, bitmap)));
      } else {
        mediaLister.addMedia(String.valueOf(System.currentTimeMillis()), file, null, null);
      }
    }));
  }

  @Override
  public void onMediaChanged() {

  }

  @Override
  public void dismiss() {
    View currentFocus = getDialog() != null ? getDialog().getCurrentFocus() : null;
    AndroidUtilities.hideKeyboard(currentFocus != null ? currentFocus : new View(context));
    super.dismiss();
  }
}