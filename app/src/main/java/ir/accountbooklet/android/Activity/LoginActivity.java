package ir.accountbooklet.android.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import java.util.Locale;

import androidx.annotation.NonNull;
import ir.accountbooklet.android.Customs.EditText;
import ir.accountbooklet.android.Customs.ImageView;
import ir.accountbooklet.android.Customs.TextView;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Session.Session;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.FingerprintUtils;
import ir.accountbooklet.android.Utils.Theme;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    private ViewGroup numberPasswordLayout;
    private ViewGroup fingerprintLayout;
    private EditText etPassword;
    private TextView tvTitle;
    private TextView tvNum0;
    private TextView tvNum1;
    private TextView tvNum2;
    private TextView tvNum3;
    private TextView tvNum4;
    private TextView tvNum5;
    private TextView tvNum6;
    private TextView tvNum7;
    private TextView tvNum8;
    private TextView tvNum9;
    private TextView tvLogin;
    private TextView tvClear;
    private TextView tvFingerprintDescription;
    private TextView tvChangeLoginType;
    private ImageView ivFingerprint;
    private final static int STATUS_LOGIN_PASSWORD = 0;
    private final static int STATUS_LOGIN_FINGERPRINT = 1;
    private static final int REQUEST_FINGERPRINT = 1648;
    private int currentLoginStatus = 0;
    private FingerprintUtils fingerprintUtils = new FingerprintUtils();
    private long startTimeErr = 0;

    private void initializeViews() {
        numberPasswordLayout = findViewById(R.id.numberPasswordLayout);
        fingerprintLayout = findViewById(R.id.fingerprintLayout);
        etPassword = findViewById(R.id.etPassword);
        tvTitle = findViewById(R.id.tvTitle);
        tvNum0 = findViewById(R.id.tvNum0);
        tvNum1 = findViewById(R.id.tvNum1);
        tvNum2 = findViewById(R.id.tvNum2);
        tvNum3 = findViewById(R.id.tvNum3);
        tvNum4 = findViewById(R.id.tvNum4);
        tvNum5 = findViewById(R.id.tvNum5);
        tvNum6 = findViewById(R.id.tvNum6);
        tvNum7 = findViewById(R.id.tvNum7);
        tvNum8 = findViewById(R.id.tvNum8);
        tvNum9 = findViewById(R.id.tvNum9);
        tvLogin = findViewById(R.id.tvLogin);
        tvClear = findViewById(R.id.tvClear);
        tvFingerprintDescription = findViewById(R.id.tvFingerprintDescription);
        tvChangeLoginType = findViewById(R.id.tvChangeLoginType);
        ivFingerprint = findViewById(R.id.ivFingerprint);
    }

    private void initializeDefault() {
        ivFingerprint.setImageDrawable(Theme.fingerprint_drawable);
        boolean fingerprint = Session.getInstance().isFingerprintLogin() && fingerprintUtils.isHardwareDetected() && fingerprintUtils.isKeyguardSecure() && fingerprintUtils.hasEnrolledFingerprints();
        AndroidUtilities.setVisibility(tvChangeLoginType, fingerprint ? View.VISIBLE : View.GONE);
        setLoginStatus(fingerprint ? STATUS_LOGIN_FINGERPRINT : STATUS_LOGIN_PASSWORD);
    }

    private void initializeListeners() {
        tvNum0.setOnClickListener(this);
        tvNum1.setOnClickListener(this);
        tvNum2.setOnClickListener(this);
        tvNum3.setOnClickListener(this);
        tvNum4.setOnClickListener(this);
        tvNum5.setOnClickListener(this);
        tvNum6.setOnClickListener(this);
        tvNum7.setOnClickListener(this);
        tvNum8.setOnClickListener(this);
        tvNum9.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        tvClear.setOnClickListener(this);
        tvChangeLoginType.setOnClickListener(this);
    }

    private void inputNumber(String num) {
        num = etPassword.getString() + num;
        if (num.length() > 9) {
            etPassword.setText("");
            AndroidUtilities.toast(context, R.string.err_9_length_password);
        } else {
            etPassword.setText(num);
            checkPass();
        }
    }

    private boolean checkPass() {
        if(Session.getInstance().getPassword().equals(etPassword.getString())) {
            MainActivity.startActivity(context);
            finish();
            return true;
        }
        return false;
    }

    private void actionChangeLoginType() {
        currentLoginStatus = currentLoginStatus == STATUS_LOGIN_PASSWORD ? STATUS_LOGIN_FINGERPRINT : STATUS_LOGIN_PASSWORD;
        tvChangeLoginType.setEnabled(false);
        AndroidUtilities.runOnUIThread(() -> tvChangeLoginType.setEnabled(true), 1000);
        boolean statusPassword = currentLoginStatus == STATUS_LOGIN_PASSWORD;
        AnimatorSet animatorSet = new AnimatorSet();
        float[] alphaIn = {0.0f, 1.0f};
        float[] alphaOut = {1.0f, 0.0f};
        float[] translationIn = {AndroidUtilities.dpf(-150), 0.0f};
        float[] translationOut = {0.0f, AndroidUtilities.dpf(150)};
        AndroidUtilities.setVisibility(statusPassword ? numberPasswordLayout : fingerprintLayout, View.VISIBLE);
        animatorSet.playTogether(ObjectAnimator.ofFloat(fingerprintLayout, "alpha", statusPassword ? alphaOut : alphaIn));
        animatorSet.playTogether(ObjectAnimator.ofFloat(fingerprintLayout, "translationX", statusPassword ? translationOut : translationIn));
        animatorSet.playTogether(ObjectAnimator.ofFloat(numberPasswordLayout, "alpha", statusPassword ? alphaIn : alphaOut));
        animatorSet.playTogether(ObjectAnimator.ofFloat(numberPasswordLayout, "translationX", statusPassword ? translationIn : translationOut));
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new DecelerateInterpolator(1.5f));
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(animatorSet)) {
                    setLoginStatus(currentLoginStatus);
                }
            }
        });
        animatorSet.start();
    }

    private void setLoginStatus(int status) {
        currentLoginStatus = status;
        if (status == STATUS_LOGIN_PASSWORD) {
            tvTitle.setText(R.string.login_password);
            tvChangeLoginType.setText(R.string.login_fingerprint);
            AndroidUtilities.setVisibility(fingerprintLayout, View.GONE);
            AndroidUtilities.setVisibility(numberPasswordLayout, View.VISIBLE);
            fingerprintUtils.cancelAuthenticate();
        } else if (status == STATUS_LOGIN_FINGERPRINT) {
            tvTitle.setText(R.string.login_fingerprint);
            tvChangeLoginType.setText(R.string.login_password);
            AndroidUtilities.setVisibility(fingerprintLayout, View.VISIBLE);
            AndroidUtilities.setVisibility(numberPasswordLayout, View.GONE);
            fingerprintUtils.requestPermission(this, REQUEST_FINGERPRINT);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeViews();
        initializeDefault();
        initializeListeners();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvNum0:
                inputNumber("0");
                break;
            case R.id.tvNum1:
                inputNumber("1");
                break;
            case R.id.tvNum2:
                inputNumber("2");
                break;
            case R.id.tvNum3:
                inputNumber("3");
                break;
            case R.id.tvNum4:
                inputNumber("4");
                break;
            case R.id.tvNum5:
                inputNumber("5");
                break;
            case R.id.tvNum6:
                inputNumber("6");
                break;
            case R.id.tvNum7:
                inputNumber("7");
                break;
            case R.id.tvNum8:
                inputNumber("8");
                break;
            case R.id.tvNum9:
                inputNumber("9");
                break;
            case R.id.tvClear:
                etPassword.setText("");
                break;
            case R.id.tvLogin:
                if (!checkPass()) {
                    etPassword.setText("");
                    AndroidUtilities.toast(context, R.string.err_password);
                }
                break;
            case R.id.tvChangeLoginType:
                actionChangeLoginType();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_FINGERPRINT && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintUtils.startAuthenticate(new FingerprintManager.AuthenticationCallback() {

                private String strHelp = "";

                private void startTimer() {
                    long time = (System.currentTimeMillis() - startTimeErr) / 1000;
                    if (time > 30) {
                        tvFingerprintDescription.setText(R.string.description_fingerprint);
                        tvFingerprintDescription.setTextColor(Theme.getColor(Theme.key_app_text2));
                        fingerprintUtils.requestPermission(LoginActivity.this, REQUEST_FINGERPRINT);
                        startTimeErr = 0;
                        return;
                    }
                    AndroidUtilities.runOnUIThread(() -> {
                        StringBuilder builder = new StringBuilder();
                        builder.append(strHelp);
                        builder.append("\n");
                        builder.append(AndroidUtilities.formatTime(30 - time));
                        tvFingerprintDescription.setText(builder);
                        tvFingerprintDescription.setTextColor(Theme.getColor(Theme.key_app_default2));
                        startTimer();
                    }, 1000);
                }

                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    tvFingerprintDescription.setText(String.format(Locale.US, context.getString(R.string.description_fingerprint_err), errString));
                    tvFingerprintDescription.setTextColor(Theme.getColor(Theme.key_app_default2));
                    if (errorCode == 7) {
                        if (startTimeErr == 0) {
                            fingerprintUtils.cancelAuthenticate();
                            startTimeErr = System.currentTimeMillis();
                            strHelp = tvFingerprintDescription.getText().toString();
                            startTimer();
                        }
                    } else {
                        AndroidUtilities.runOnUIThread(() -> {
                            tvFingerprintDescription.setText(R.string.description_fingerprint);
                            tvFingerprintDescription.setTextColor(Theme.getColor(Theme.key_app_text2));
                        }, 2000);
                    }
                }

                @Override
                public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                    tvFingerprintDescription.setText(String.format(Locale.US, context.getString(R.string.description_fingerprint_help), helpString));
                    tvFingerprintDescription.setTextColor(Theme.getColor(Theme.key_app_default4));
                    if (helpCode == 7) {
                        if (startTimeErr == 0) {
                            fingerprintUtils.cancelAuthenticate();
                            startTimeErr = System.currentTimeMillis();
                            strHelp = tvFingerprintDescription.getText().toString();
                            startTimer();
                        }
                    } else {
                        AndroidUtilities.runOnUIThread(() -> {
                            tvFingerprintDescription.setText(R.string.description_fingerprint);
                            tvFingerprintDescription.setTextColor(Theme.getColor(Theme.key_app_default4));
                        }, 2000);
                    }
                }

                @Override
                public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                    tvFingerprintDescription.setText(R.string.description_fingerprint_successful);
                    tvFingerprintDescription.setTextColor(Theme.getColor(Theme.key_app_default));
                    tvChangeLoginType.setEnabled(false);
                    AndroidUtilities.runOnUIThread(() -> {
                        MainActivity.startActivity(context);
                        finish();
                    }, 2000);
                }

                @Override
                public void onAuthenticationFailed() {
                    tvFingerprintDescription.setText(R.string.description_fingerprint_failed);
                    tvFingerprintDescription.setTextColor(Theme.getColor(Theme.key_app_default2));
                    AndroidUtilities.runOnUIThread(() -> {
                        tvFingerprintDescription.setText(R.string.description_fingerprint);
                        tvFingerprintDescription.setTextColor(Theme.getColor(Theme.key_app_text2));
                    }, 2000);
//                    tvChangeLoginType.setEnabled(false);
//                    AndroidUtilities.runOnUIThread(() -> actionChangeLoginType(), 1500);
                }
            });
        }
    }
}
