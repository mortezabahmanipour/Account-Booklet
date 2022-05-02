package ir.accountbooklet.android.Dialogs;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import androidx.core.graphics.ColorUtils;
import ir.accountbooklet.android.Customs.ImageView;
import ir.accountbooklet.android.Customs.TextView;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.LayoutHelper;
import ir.accountbooklet.android.Utils.Theme;

public class DialogMessage extends BaseDialog implements View.OnClickListener {

  public static DialogMessage newInstance(Context context, Builder builder) {
    return new DialogMessage(context, builder);
  }
  private RelativeLayout contentView;
  private ImageView ivThumbnail;
  private TextView tvTitle;
  private TextView tvMessage;
  private TextView tvCheckBox;
  private TextView tvBtn1;
  private TextView tvBtn2;
  private Builder builder;

  public static class Builder {
    private String thumbnail;
    private String title;
    private String message;
    private String btn1;
    private String btn2;
    private int color2;
    private int btnId1;
    private int btnId2;
    private String textCheck;
    private boolean check;
    private boolean cancelable = true;
    private DialogMessageListener listener;

    public Builder thumbnail(String str) {
      this.thumbnail = str;
      return this;
    }

    public Builder title(String str) {
      this.title = str;
      return this;
    }

    public Builder message(String str) {
      this.message = str;
      return this;
    }

    public Builder btn(int id, String str) {
      this.btn1 = str;
      this.btnId1 = id;
      return this;
    }

    public Builder btn2(int id, String str) {
      this.btn2 = str;
      this.btnId2 = id;
      return this;
    }

    public Builder btn2(int id, String str, int color2) {
      this.btn2 = str;
      this.btnId2 = id;
      this.color2 = color2;
      return this;
    }

    public Builder check(String str, boolean check) {
      this.textCheck = str;
      this.check = check;
      return this;
    }

    public Builder cancelable(boolean cancelable) {
      this.cancelable = cancelable;
      return this;
    }

    public Builder listener(DialogMessageListener listener) {
      this.listener = listener;
      return this;
    }
  }

  public interface DialogMessageListener {
    void onDialogMessageClick(int id, boolean check);
  }

  private DialogMessage(Context context, Builder builder) {
    super(context);

    this.builder = builder;

    contentView = new RelativeLayout(context);
    contentView.setBackground(Theme.createDrawable(Theme.getColor(Theme.key_app_background), 0, 0, Theme.RECT, AndroidUtilities.dp(7)));

    LinearLayout baseLayout = new LinearLayout(context);
    baseLayout.setOrientation(LinearLayout.VERTICAL);
    baseLayout.setMotionEventSplittingEnabled(false);
    contentView.addView(baseLayout, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

    if (builder == null) {
      return;
    }

    setCancelable(builder.cancelable);

    tvTitle = new TextView(context);
    tvTitle.setTextColor(Theme.getColor(Theme.key_app_text5));
    tvTitle.setTextSize(16);
    tvTitle.setGravity(Gravity.CENTER_VERTICAL);

    boolean hasThumbnail = !TextUtils.isEmpty(builder.thumbnail);
    if (hasThumbnail) {
      FrameLayout frameTitle = new FrameLayout(context);
      ivThumbnail = new ImageView(context);
      ivThumbnail.setCircle(true);
      frameTitle.addView(ivThumbnail, LayoutHelper.createFrame(AndroidUtilities.dp(40), AndroidUtilities.dp(40), 5, AndroidUtilities.dp(20), AndroidUtilities.dp(0), AndroidUtilities.dp(20), AndroidUtilities.dp(0)));
      frameTitle.addView(tvTitle, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, AndroidUtilities.dp(20), 0, AndroidUtilities.dp(55), 0));
      baseLayout.addView(frameTitle, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(0), AndroidUtilities.dp(20), AndroidUtilities.dp(0), AndroidUtilities.dp(15)));
    } else {
      baseLayout.addView(tvTitle, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(20), AndroidUtilities.dp(20), AndroidUtilities.dp(20), AndroidUtilities.dp(10)));
    }

    tvMessage = new TextView(context);
    tvMessage.setTextColor(Theme.getColor(Theme.key_app_text));
    tvMessage.setTextSize(12);
    tvMessage.setTypefaceStyle(Typeface.BOLD);
    baseLayout.addView(tvMessage, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(20), AndroidUtilities.dp(0), AndroidUtilities.dp(20), AndroidUtilities.dp(15)));

    boolean hasCheck = !TextUtils.isEmpty(builder.textCheck);
    if (hasCheck) {
      tvCheckBox = new TextView(context);
      tvCheckBox.setTextColor(Theme.getColor(Theme.key_app_text3));
      tvCheckBox.setTextSize(12);
      tvCheckBox.setGravity(Gravity.CENTER_VERTICAL | 5);
      tvCheckBox.setDrawableSize(AndroidUtilities.dp(22));
      tvCheckBox.setCompoundDrawablePadding(AndroidUtilities.dp(8));
      tvCheckBox.setPadding(AndroidUtilities.dp(20), 0, AndroidUtilities.dp(17), 0);
      tvCheckBox.setSelectorBuilder(new Theme.SelectorBuilder().pressedColor(Theme.getColor(Theme.key_app_selector)));
      baseLayout.addView(tvCheckBox, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(40), AndroidUtilities.dp(0), AndroidUtilities.dp(0), AndroidUtilities.dp(0), AndroidUtilities.dp(0)));
    }

    if (!TextUtils.isEmpty(builder.btn1)) {
      tvBtn1 = new TextView(context);
      tvBtn1.setTextColor(Theme.getColor(Theme.key_app_text3));
      tvBtn1.setSingleLine(true);
      tvBtn1.setTextSize(12);
      tvBtn1.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(5), AndroidUtilities.dp(10), AndroidUtilities.dp(5));
      tvBtn1.setGravity(Gravity.CENTER_VERTICAL);
      tvBtn1.setSelectorBuilder(new Theme.SelectorBuilder().pressedColor(Theme.getColor(Theme.key_app_selector)).style(Theme.RECT).radii(AndroidUtilities.dp(17.5f)));
      baseLayout.addView(tvBtn1, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(35), AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(0), AndroidUtilities.dp(10)));
    }

    if (!TextUtils.isEmpty(builder.btn2)) {
      tvBtn2 = new TextView(context);
      tvBtn2.setTextColor(builder.color2 != 0 ? builder.color2 : Theme.getColor(Theme.key_app_default3));
      tvBtn2.setSingleLine(true);
      tvBtn2.setTextSize(12);
//      tvBtn2.setFaceStyle(Typeface.BOLD);
      tvBtn2.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(5), AndroidUtilities.dp(10), AndroidUtilities.dp(5));
      tvBtn2.setGravity(Gravity.CENTER_VERTICAL);
      tvBtn2.setSelectorBuilder(new Theme.SelectorBuilder().pressedColor(ColorUtils.setAlphaComponent(builder.color2 != 0 ? builder.color2 : Theme.getColor(Theme.key_app_default3), 30)).style(Theme.RECT).radii(AndroidUtilities.dp(17.5f)));
      int marginLeft = AndroidUtilities.dp(10) + (tvBtn1 != null ? (tvBtn1.getPaddingLeft() + tvBtn1.getPaddingRight() + (int) tvBtn1.getPaint().measureText(builder.btn1) + AndroidUtilities.dp(10)) : 0);
      baseLayout.addView(tvBtn2, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(35), marginLeft, AndroidUtilities.dp(-45), AndroidUtilities.dp(0), AndroidUtilities.dp(10)));
    }
  }

  private void initializeListeners() {
    if (tvCheckBox != null) {
      tvCheckBox.setOnClickListener(this);
    }
    if (tvBtn1 != null) {
      tvBtn1.setOnClickListener(this);
    }
    if (tvBtn2 != null) {
      tvBtn2.setOnClickListener(this);
    }
  }

  private void setInfo() {
    if (ivThumbnail != null && !TextUtils.isEmpty(builder.thumbnail)) {
      Picasso.get().load(builder.thumbnail).centerCrop().resize(AndroidUtilities.dp(40), AndroidUtilities.dp(40)).into(ivThumbnail);
    }

    tvTitle.setText(builder.title);
    tvMessage.setText(builder.message);
    if (tvCheckBox != null) {
      tvCheckBox.setText(builder.textCheck);
      checkBoxCheck();
    }
    if (tvBtn1 != null) {
      tvBtn1.setText(builder.btn1);
    }
    if (tvBtn2 != null) {
      tvBtn2.setText(builder.btn2);
    }
  }

  private void checkBoxCheck() {
    if (builder.check) {
      tvCheckBox.setCompoundDrawables(null, null, context.getResources().getDrawable(R.drawable.ic_check_box_on), null);
      tvCheckBox.setDrawableTintColor(Theme.getColor(Theme.key_app_accent));
    } else {
      tvCheckBox.setCompoundDrawables(null, null, context.getResources().getDrawable(R.drawable.ic_check_box_off), null);
      tvCheckBox.setDrawableTintColor(0x60000000);
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(contentView);
    initializeListeners();
    setInfo();
  }

  @Override
  public void onClick(View v) {
    if (v == tvCheckBox) {
      builder.check = !builder.check;
      checkBoxCheck();
    } else if (v == tvBtn1) {
      if (builder.listener != null) {
        builder.listener.onDialogMessageClick(builder.btnId1, builder.check);
      }
      dismiss();
    } else if (v == tvBtn2) {
      if (builder.listener != null) {
        builder.listener.onDialogMessageClick(builder.btnId2, builder.check);
      }
      dismiss();
    }
  }
}
