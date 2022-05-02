package ir.accountbooklet.android.Dialogs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;

import ir.accountbooklet.android.BuildConfig;
import ir.accountbooklet.android.Customs.EditText;
import ir.accountbooklet.android.Customs.TextView;
import ir.accountbooklet.android.R;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.LayoutHelper;
import ir.accountbooklet.android.Utils.Theme;


public class DialogRelateToUs extends BaseDialog implements View.OnClickListener {
  private RelativeLayout layoutBase;
  private TextView tvTitle;
  private TextView tvLeft;
  private TextView tvOk;
  private EditText etSubject;
  private EditText etMessage;

  private DialogRelateToUs(Context context) {
    super(context);
  }

  public static DialogRelateToUs newInstance(Context context) {
    return new DialogRelateToUs(context);
  }

  private void initializeLayout() {
    layoutBase = new RelativeLayout(context);
    layoutBase.setFocusable(true);
    layoutBase.setFocusableInTouchMode(true);
    layoutBase.setMotionEventSplittingEnabled(false);
    layoutBase.setBackground(Theme.createDrawable(Theme.getColor(Theme.key_app_primary), 0, 0, Theme.RECT, AndroidUtilities.dp(7)));

    tvTitle = new TextView(context);
    tvTitle.setTextColor(0xFFFFFFFF);
    tvTitle.setSingleLine(true);
    tvTitle.setTextSize(12);
    tvTitle.setText(R.string.relate_to_us);
    tvTitle.setId(R.id.title);

    Theme.SelectorBuilder selectorBuilder = new Theme.SelectorBuilder().pressedColor(0xFFFFFFFF);

    tvLeft = new TextView(context);
    tvLeft.setSelectorBuilder(selectorBuilder);
    tvLeft.setTextColor(0xffffffff);
    tvLeft.setSingleLine(true);
    tvLeft.setTextSize(12);
    tvLeft.setText("بی\u200Cخیال");
    tvLeft.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10));
    tvLeft.setMinimumWidth(AndroidUtilities.dp(40));
    tvLeft.setGravity(Gravity.CENTER);
    tvLeft.setId(R.id.command1);

    tvOk = new TextView(context);
    tvOk.setSelectorBuilder(selectorBuilder);
    tvOk.setTextColor(0xffffffff);
    tvOk.setSingleLine(true);
    tvOk.setTextSize(12);
    tvOk.setText("ارسال");
    tvOk.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10));
    tvOk.setMinimumWidth(AndroidUtilities.dp(40));
    tvOk.setGravity(Gravity.CENTER);
    tvOk.setId(R.id.command2);

    etSubject = new EditText(context);
    etSubject.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(15));
    etSubject.setHint("عنوان");
    etSubject.setHintTextColor(0x90ffffff);
    etSubject.setTextColor(0xffffffff);
    etSubject.setSingleLine(true);
    etSubject.setTextSize(12);
    etSubject.setId(R.id.et1);

    etMessage = new EditText(context);
    etMessage.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(15));
    etMessage.setHint("سوال یا مشکل خود را بنویسید");
    etMessage.setHintTextColor(0x90ffffff);
    etMessage.setTextColor(0xffffffff);
    etMessage.setTextSize(12);
    etMessage.setId(R.id.et2);
    InputFilter[] filterArray = new InputFilter[1];
    filterArray[0] = new InputFilter.LengthFilter(250);
    etMessage.setFilters(filterArray);

    layoutBase.addView(tvTitle, LayoutHelper.createRelative(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, AndroidUtilities.dp(10), AndroidUtilities.dp(15), 0, RelativeLayout.ALIGN_PARENT_RIGHT));
    layoutBase.addView(etSubject, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(15), AndroidUtilities.dp(10), AndroidUtilities.dp(15) ,0, RelativeLayout.BELOW, R.id.title));
    layoutBase.addView(etMessage, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(15), AndroidUtilities.dp(10), AndroidUtilities.dp(15) ,0, RelativeLayout.BELOW, R.id.et1));
    layoutBase.addView(tvLeft, LayoutHelper.createRelative(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(10), AndroidUtilities.dp(5), 0, AndroidUtilities.dp(10), RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.BELOW, R.id.et2));
    layoutBase.addView(tvOk, LayoutHelper.createRelative(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(75), AndroidUtilities.dp(5), 0, AndroidUtilities.dp(10), RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.BELOW, R.id.et2));
  }

  private void initializeListeners() {
    tvLeft.setOnClickListener(this);
    tvOk.setOnClickListener(this);
  }

  private void actionRelate() {
    String subject = etSubject.getText() != null ? etSubject.getText().toString().trim() : "";
    String message = etMessage.getText() != null ? etMessage.getText().toString().trim() : "";
    if (TextUtils.isEmpty(message)) {
      AndroidUtilities.toast(context, "متن را وارد کنید");
      return;
    }
    message = message + "\n\n" + BuildConfig.APPLICATION_ID;
    Intent intent = new Intent(Intent.ACTION_SENDTO);
    intent.setData(Uri.parse("mailto:www.mortezabahmanipoor@gmail.com"));
    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
    intent.putExtra(Intent.EXTRA_TEXT, message);
    AndroidUtilities.startActivity(context, intent);

    dismiss();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initializeLayout();
    setContentView(layoutBase);
    initializeListeners();
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.command1:
        dismiss();
        break;
      case R.id.command2:
        actionRelate();
        break;
    }
  }
}
