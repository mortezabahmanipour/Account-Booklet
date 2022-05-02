package ir.accountbooklet.android.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Utils.Theme;

public class BaseDialog extends Dialog {
  protected Context context;

  protected BaseDialog(@NonNull Context context) {
    super(context, R.style.TransparentDialog);
    this.context = context;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Window window = getWindow();
    if (window !=  null) {
      WindowManager.LayoutParams params = new WindowManager.LayoutParams();
      params.copyFrom(window.getAttributes());
      params.dimAmount = 0.25f;
      params.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        int color = Theme.getColor(Theme.key_action_bar_default);
        if (color == 0xFFFFFFFF) {
          params.flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
      }
      if (Build.VERSION.SDK_INT >= 28) {
        params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
      }
      window.setAttributes(params);
    }
  }

  @Override
  public void dismiss() {
    try {
      super.dismiss();
    } catch (Exception e) {
      // don't prompt
    }
  }
}
