package ir.accountbooklet.android.Activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import ir.accountbooklet.android.Customs.EditText;
import ir.accountbooklet.android.Customs.TextView;
import ir.accountbooklet.android.Dialogs.DialogMessage;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Session.Session;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.FingerprintUtils;

public class SignUpActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
    private EditText etName;
    private EditText etPassword;
    private EditText etRePassword;
    private TextView tvName;
    private TextView tvPassword;
    private TextView tvRePassword;
    private View tvSubmit;
    private FingerprintUtils fingerprint;
    private static final int REQUEST_FINGERPRINT = 1647;

    private void initializeViews() {
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        etRePassword = findViewById(R.id.etRePassword);
        tvName = findViewById(R.id.tvName);
        tvPassword = findViewById(R.id.tvPassword);
        tvRePassword = findViewById(R.id.tvRePassword);
        tvSubmit = findViewById(R.id.tvSubmit);
    }

    private void initializeDefault() {

    }

    private void initializeListeners() {
        tvSubmit.setOnClickListener(this);
        etName.addTextChangedListener(this);
        etPassword.addTextChangedListener(this);
        etRePassword.addTextChangedListener(this);
    }

    private boolean checkActive() {
        if (Session.getInstance().isActive()) {
            LoginActivity.startActivity(context);
            finish();
            return true;
        }
        return false;
    }

    private void actionSubmit() {
        String name = etName.getString();
        String password = etPassword.getString();
        String rePassword = etRePassword.getString();
        if (name.length() < 3) {
            AndroidUtilities.toast(context, R.string.message_err_name);
        } else if (password.length() < 4 || password.length() > 9 || rePassword.length() < 4 || rePassword.length() > 9) {
            AndroidUtilities.toast(context, R.string.err_length_password);
        } else if (!password.equals(rePassword)) {
            AndroidUtilities.toast(context, R.string.err_equals_password);
        } else {
            Session.getInstance().setName(name);
            Session.getInstance().setPassword(password);
            fingerprint = new FingerprintUtils();
            if (fingerprint.isHardwareDetected() && fingerprint.isKeyguardSecure()) {
                DialogMessage.newInstance(context, new DialogMessage.Builder().cancelable(false).title("اثر انگشت").message("آیا مایل هستید ورود شما به وسیله اثر انگشت انجام شود؟").btn(0, "خیر").btn2(1, "بله").listener((id, check) -> {
                    if (id == 0) {
                        loginSuccessful();
                    } else if (id == 1) {
                        fingerprint.requestPermission(this, REQUEST_FINGERPRINT);
                    }
                })).show();
            } else {
                loginSuccessful();
            }
        }
    }

    private void loginSuccessful() {
        AndroidUtilities.toast(context, R.string.login_is_success);
        MainActivity.startActivity(context);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkActive()) {
            return;
        }
        setContentView(R.layout.activity_sign_up);
        initializeViews();
        initializeDefault();
        initializeListeners();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(tvSubmit)) {
            actionSubmit();
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
        } else if (s == etPassword.getText()) {
            AndroidUtilities.setVisibility(tvPassword, TextUtils.isEmpty(s) ? View.INVISIBLE : View.VISIBLE);
        } else if (s == etRePassword.getText()) {
            AndroidUtilities.setVisibility(tvRePassword, TextUtils.isEmpty(s) ? View.INVISIBLE : View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (fingerprint != null && fingerprint.checkPermission()) {
            if (fingerprint.hasEnrolledFingerprints()) {
                Session.getInstance().setFingerprintLogin(true);
                loginSuccessful();
            } else {
                DialogMessage.newInstance(context, new DialogMessage.Builder().cancelable(false).title("گذاشتن اثر انگشت").message("در حال حاظر اثر انگشتی برای دستگاه شما وجود ندارد، آیا مایل به گذاشتن اثر انگشت برای دستگاه خود هستید؟").btn(0, "خیر").btn2(1, "بله").listener((id2, check2) -> {
                    if (id2 == 0) {
                        loginSuccessful();
                    } else if (id2 == 1) {
                        fingerprint.openSecuritySettings(this);
                    }
                })).show();
            }
        }
    }
}
