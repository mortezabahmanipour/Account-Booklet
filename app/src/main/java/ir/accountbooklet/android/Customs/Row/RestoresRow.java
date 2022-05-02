package ir.accountbooklet.android.Customs.Row;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import ir.accountbooklet.android.Customs.TextView;
import ir.accountbooklet.android.Dialogs.DialogRestoreBackup;
import ir.accountbooklet.android.Utils.AndroidUtilities;
import ir.accountbooklet.android.Utils.Theme;

public class RestoresRow extends TextView {

  public void setModel(DialogRestoreBackup.RestoreModel restore) {
    if (restore == null) {
      return;
    }
    setText(restore.name);
  }

  public RestoresRow(@NonNull Context context) {
    super(context);
    setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    setTextSize(12);
    setTextColor(Theme.getColor(Theme.key_app_default3));
    setSingleLine();
    setTypefaceStyle(Typeface.BOLD);
    setEllipsize(TextUtils.TruncateAt.END);
    setGravity(Gravity.CENTER | 5);
    setPadding(AndroidUtilities.dp(15), AndroidUtilities.dp(10), AndroidUtilities.dp(15), AndroidUtilities.dp(10));
    setSelectorBuilder(new Theme.SelectorBuilder().pressedColor(Theme.getColor(Theme.key_app_selector)).strokeWidth(AndroidUtilities.dp(1)).strokeColor(0x10000000).radii(AndroidUtilities.dp(7)).style(Theme.RECT));
  }
}
