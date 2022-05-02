package ir.accountbooklet.android.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import androidx.annotation.RequiresApi;
import ir.accountbooklet.android.ApplicationLoader;

public class FingerprintUtils {

  private static final String KEY_NAME = "14as1231ff12fs54s1fc19ss43057ac7";
  private Cipher cipher;
  private KeyStore keyStore;
  private KeyGenerator keyGenerator;
  private FingerprintManager.CryptoObject cryptoObject;
  private CancellationSignal cancellationSignal;
  private FingerprintManager fingerprintManager;
  private KeyguardManager keyguardManager;

  public FingerprintUtils() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      return;
    }
    fingerprintManager = (FingerprintManager) ApplicationLoader.applicationContext.getSystemService(Context.FINGERPRINT_SERVICE);
    keyguardManager = (KeyguardManager) ApplicationLoader.applicationContext.getSystemService(Context.KEYGUARD_SERVICE);
  }

  public boolean isHardwareDetected() {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || fingerprintManager == null || fingerprintManager.isHardwareDetected();
  }

  public boolean hasEnrolledFingerprints() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && fingerprintManager != null && fingerprintManager.hasEnrolledFingerprints();
  }

  public boolean isKeyguardSecure() {
    return keyguardManager != null && keyguardManager.isKeyguardSecure();
  }

  public boolean checkPermission() {
    return PermissionManager.checkPermissions(ApplicationLoader.applicationContext, Manifest.permission.USE_FINGERPRINT);
  }

  public void requestPermission(Activity act, int request) {
    PermissionManager.requestPermissions(act, request, Manifest.permission.USE_FINGERPRINT);
  }

  public void openSecuritySettings(Activity act) {
    if (act == null) {
      return;
    }
    act.startActivity(new Intent(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? Settings.ACTION_FINGERPRINT_ENROLL : Settings.ACTION_SECURITY_SETTINGS));
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  public boolean startAuthenticate(FingerprintManager.AuthenticationCallback listener) {
    cancelAuthenticate();
    generateKey();
    if (initCipher()) {
      cryptoObject = new FingerprintManager.CryptoObject(cipher);
      cancellationSignal = new CancellationSignal();
      fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, listener, null);
      return true;
    }
    return false;
  }

  public void cancelAuthenticate() {
    if (cancellationSignal != null && !cancellationSignal.isCanceled()) {
      cancellationSignal.cancel();
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  private void generateKey() {
    try {
      keyStore = KeyStore.getInstance("AndroidKeyStore");
      keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
      keyStore.load(null);
      keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
        .setUserAuthenticationRequired(true)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
        .build());
      keyGenerator.generateKey();
    } catch (Throwable e) {
      AppLog.e(getClass(), e.getMessage());
    }
  }

  private boolean initCipher() {
    try {
      cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
      keyStore.load(null);
      SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
      cipher.init(Cipher.ENCRYPT_MODE, key);
      return true;
    } catch (Throwable e) {
      AppLog.e(getClass(), e.getMessage());
      return false;
    }
  }
}
