package ir.accountbooklet.android.Models;

import android.text.TextUtils;

public class FilterModel {
  public String str;
  public long date;
  public long dateTo;
  public int type = -1;

  public int count() {
    int count = 0;
    if (!TextUtils.isEmpty(str)) {
      count++;
    }
    if (date > 0 || dateTo > 0) {
      count++;
    }
    if (type >= 0) {
      count++;
    }
    return count;
  }

  public void clear() {
    str = null;
    date = 0;
    dateTo = 0;
    type = -1;
  }
}
