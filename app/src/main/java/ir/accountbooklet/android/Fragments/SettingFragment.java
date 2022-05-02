package ir.accountbooklet.android.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import ir.accountbooklet.android.Constants;
import ir.accountbooklet.android.Customs.EditText;
import ir.accountbooklet.android.Customs.SelectorImageView;
import ir.accountbooklet.android.Customs.TextView;
import ir.accountbooklet.android.Dialogs.DialogMessage;
import ir.accountbooklet.android.Dialogs.DialogRestoreBackup;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Session.Database;
import ir.accountbooklet.android.Session.Session;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.FingerprintUtils;
import ir.accountbooklet.android.Utils.NotificationCenter;
import ir.accountbooklet.android.Utils.PermissionManager;
import ir.accountbooklet.android.Utils.PolicyManager;

public class SettingFragment extends BaseDialogFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

  public static SettingFragment newInstance() {
    return new SettingFragment();
  }

  private ViewGroup baseLayout;
  private SelectorImageView ivBack;
  private TextView tvTitle;
  private TextView tvChangeName;
  private TextView tvChangePassword;
  private TextView tvBackup;
  private TextView tvRestoreBackup;
  private EditText etName;
  private EditText etPassword;
  private EditText etNewPassword;
  private SwitchCompat swPreventUninstallApp;
  private SwitchCompat swFingerprint;
  private PolicyManager policyManager;
  private FingerprintUtils fingerprint;
  private static final int REQUEST_FINGERPRINT = 1647;

  private void initialiseViews(View view) {
    baseLayout = view.findViewById(R.id.baseLayout);
    ivBack = view.findViewById(R.id.ivBack);
    tvTitle = view.findViewById(R.id.tvTitle);
    tvChangeName = view.findViewById(R.id.tvChangeName);
    tvChangePassword = view.findViewById(R.id.tvChangePassword);
    tvBackup = view.findViewById(R.id.tvBackup);
    tvRestoreBackup = view.findViewById(R.id.tvRestoreBackup);
    etName = view.findViewById(R.id.etName);
    etPassword = view.findViewById(R.id.etPassword);
    etNewPassword = view.findViewById(R.id.etNewPassword);
    swPreventUninstallApp = view.findViewById(R.id.swPreventUninstallApp);
    swFingerprint = view.findViewById(R.id.swFingerprint);
  }

  private void initialiseDefaults() {
    policyManager = new PolicyManager(context);

    tvTitle.setText(R.string.settings);
    swPreventUninstallApp.setTypeface(AndroidUtilities.IRANSans_FaNum);
    swFingerprint.setTypeface(AndroidUtilities.IRANSans_FaNum);
  }

  private void initialiseListeners() {
    ivBack.setOnClickListener(this);
    tvChangeName.setOnClickListener(this);
    tvChangePassword.setOnClickListener(this);
    tvBackup.setOnClickListener(this);
    tvRestoreBackup.setOnClickListener(this);
    swPreventUninstallApp.setOnCheckedChangeListener(this);
    swFingerprint.setOnCheckedChangeListener(this);
  }

  private void setInfo() {
    etName.setText(Session.getInstance().getName());
  }

  private void actionBack() {
    dismiss();
  }

  private void actionChangeName() {
    String name = etName.getString();
    if (name.length() < 3) {
      AndroidUtilities.toast(context, R.string.message_err_name);
    } else {
      DialogMessage.newInstance(context, new DialogMessage.Builder().title(context.getString(R.string.change_name_family_name)).message(context.getString(R.string.message_change_username)).btn(0, "خیر").btn2(1, "جایگزین کردن").listener((id, isCheck) -> {
        if (id == 1) {
          baseLayout.requestFocus();
          Session.getInstance().setName(name);
          AndroidUtilities.toast(context, R.string.message_success_save_name);
          NotificationCenter.getInstance().postNotification(NotificationCenter.dataChanged);
          AndroidUtilities.hideKeyboard(etName);
        }
      })).show();
    }
  }

  private void actionChangePassword() {
    String password = etPassword.getString();
    String oldPassword = Session.getInstance().getPassword();
    String newPassword = etNewPassword.getString();
    if (!password.equals(oldPassword)) {
      AndroidUtilities.toast(context, R.string.err_password);
    } else if (newPassword.length() < 4) {
      AndroidUtilities.toast(context, R.string.err_length_password);
    } else {
      DialogMessage.newInstance(context, new DialogMessage.Builder().title(context.getString(R.string.change_password)).message(context.getString(R.string.message_change_password)).btn(0, "خیر").btn2(1, "جایگزین کردن").listener((id, isCheck) -> {
        if (id == 1) {
          baseLayout.requestFocus();
          etPassword.setText(null);
          etNewPassword.setText(null);
          Session.getInstance().setPassword(newPassword);
          AndroidUtilities.toast(context, R.string.message_success_save_password);
          AndroidUtilities.hideKeyboard(etPassword);
          AndroidUtilities.hideKeyboard(etNewPassword);
        }
      })).show();
    }
  }

  private void actionBackUp() {
    if (!PermissionManager.checkPermissions(context, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
      PermissionManager.requestPermissions(context, Constants.REQUEST_PERMISSION_WRITE_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
      return;
    }
    DialogMessage.newInstance(context, new DialogMessage.Builder().title(context.getString(R.string.backup)).message(context.getString(R.string.message_permission_backup)).btn(0, "خیر").btn2(1, "بله").listener((id, isCheck) -> {
      if (id == 1) {
        if (Database.getInstance().exportDatabase()) {
          AndroidUtilities.toast(context, R.string.message_success_backup);
        } else {
          AndroidUtilities.toast(context, R.string.message_failed_backup);
        }
      }
    })).show();
  }

  private void actionRestoreBackUp() {
    if (!PermissionManager.checkPermissions(context, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
      PermissionManager.requestPermissions(context, Constants.REQUEST_PERMISSION_WRITE_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
      return;
    }
    DialogRestoreBackup.newInstance(context).setListener(restore -> {
      if (restore == null) {
        return;
      }
      DialogMessage.newInstance(context, new DialogMessage.Builder().title(context.getString(R.string.restore_backup)).message(context.getString(R.string.message_permission_restore_backup)).btn(0, "خیر").btn2(1, "بله").listener((id, isCheck) -> {
        if (id == 1) {
          if (Database.getInstance().restoreDatabase(restore.path)) {
            AndroidUtilities.toast(context, R.string.message_success_restore_backup);
            NotificationCenter.getInstance().postNotification(NotificationCenter.dataChanged);
          } else {
            AndroidUtilities.toast(context, R.string.message_failed_backup);
          }
        }
      })).show();
    }).show();
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_setting, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initialiseViews(view);
    initialiseDefaults();
    initialiseListeners();
    setInfo();
  }

  @SuppressLint("NonConstantResourceId")
  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.ivBack:
        actionBack();
        break;
      case R.id.tvChangeName:
        actionChangeName();
        break;
      case R.id.tvChangePassword:
        actionChangePassword();
        break;
      case R.id.tvBackup:
        actionBackUp();
        break;
      case R.id.tvRestoreBackup:
        actionRestoreBackUp();
        break;
    }
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    if (buttonView.equals(swPreventUninstallApp)) {
      if (isChecked) {
        if (!policyManager.isAdminActive()) {
          Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
          intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, policyManager.getComponentName());
          intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, context.getString(R.string.message_prevent_uninstall_app));
          startActivityForResult(intent, PolicyManager.DPM_ACTIVATION_REQUEST_CODE);
        }
      } else {
        if (policyManager.isAdminActive()) {
          policyManager.disableAdmin();
        }
      }
    } else if (buttonView.equals(swFingerprint)) {
      if (isChecked) {
        if (fingerprint == null) {
          fingerprint = new FingerprintUtils();
        }
        if (fingerprint.isHardwareDetected() && fingerprint.isKeyguardSecure()) {
          fingerprint.requestPermission(getActivity(), REQUEST_FINGERPRINT);
        } else {
          buttonView.setChecked(false);
          AndroidUtilities.toast(context, R.string.err_fingerprint_hardware_detected);
        }
      } else {
        Session.getInstance().setFingerprintLogin(false);
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    swPreventUninstallApp.setChecked(policyManager.isAdminActive());
    swFingerprint.setChecked(Session.getInstance().isFingerprintLogin());
  }

  @Override
  public void dismiss() {
    View currentFocus = getDialog() != null ? getDialog().getCurrentFocus() : null;
    AndroidUtilities.hideKeyboard(currentFocus != null ? currentFocus : new View(context));
    super.dismiss();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_FINGERPRINT && fingerprint != null && fingerprint.checkPermission()) {
      if (fingerprint.hasEnrolledFingerprints()) {
        Session.getInstance().setFingerprintLogin(true);
      } else {
        DialogMessage.newInstance(context, new DialogMessage.Builder().cancelable(false).title("گذاشتن اثر انگشت").message("در حال حاظر اثر انگشتی برای دستگاه شما وجود ندارد، آیا مایل به گذاشتن اثر انگشت برای دستگاه خود هستید؟").btn(0, "خیر").btn2(1, "بله").listener((id2, check2) -> {
          swFingerprint.setChecked(false);
          if (id2 == 1) {
            fingerprint.openSecuritySettings(getActivity());
          }
        })).show();
      }
    }
  }
}