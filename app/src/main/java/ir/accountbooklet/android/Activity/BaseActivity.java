package ir.accountbooklet.android.Activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.PermissionManager;
import ir.accountbooklet.android.Utils.RequestHelper;
import ir.accountbooklet.android.Utils.Theme;

public class BaseActivity extends AppCompatActivity {
  protected Context context;
  protected int tag = AndroidUtilities.random.nextInt();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = this;
    Window window = getWindow();
    if(window != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        int color = Theme.getColor(Theme.key_action_bar_default);
        if (color == 0xffffffff) {
          int flags = window.getDecorView().getSystemUiVisibility();
          flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
          window.getDecorView().setSystemUiVisibility(flags);
        }
      }
    }
    AndroidUtilities.createInstance(context);
    Theme.createInstance(context);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    PermissionManager.requestPermissionsResult(getSupportFragmentManager(), requestCode, permissions, grantResults);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    RequestHelper.getInstance().cancel(tag);
  }
}
