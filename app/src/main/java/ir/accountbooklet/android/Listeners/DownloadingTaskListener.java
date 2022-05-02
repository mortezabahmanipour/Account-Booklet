package ir.accountbooklet.android.Listeners;

import java.io.File;

public abstract class DownloadingTaskListener {
  public abstract void CallBack(boolean okDownload, File file);
}
