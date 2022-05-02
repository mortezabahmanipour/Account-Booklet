package ir.accountbooklet.android.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;

import ir.accountbooklet.android.Customs.CircleProgressbar;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.LayoutHelper;
import ir.accountbooklet.android.Utils.Theme;

public class DialogLoading extends BaseDialog {
  public static DialogLoading newInstance(Context context) {
    return new DialogLoading(context);
  }

  private FrameLayout frameLayout;
  private CircleProgressbar pbLoading;

  private DialogLoading(Context context) {
    super(context);
    frameLayout = new FrameLayout(context) {
      @Override
      protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measure = MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(90), MeasureSpec.EXACTLY);
        super.onMeasure(measure, measure);
      }
    };
    frameLayout.setBackground(Theme.createDrawable(Theme.getColor(Theme.key_app_background), 0, 0, Theme.RECT, AndroidUtilities.dp(15)));

    pbLoading = new CircleProgressbar(context);
    frameLayout.addView(pbLoading, LayoutHelper.createFrame(AndroidUtilities.dp(40), AndroidUtilities.dp(40), Gravity.CENTER));
  }

  private void initializeDefaults() {
    setCancelable(false);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(frameLayout);
    initializeDefaults();
  }
}
