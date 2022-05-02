package ir.accountbooklet.android.Models;

import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;

import ir.accountbooklet.android.Customs.RecyclerListView;
import ir.accountbooklet.android.Utils.AndroidUtilities;

public class MediasModel extends RecyclerListView.BaseModel {
  public File file;
  public String url;
  public String thumbnail;
  public int mediaId;
  public long dateTaken;
  public int duration;
  public int width;
  public int height;
  public long size;
  public boolean isVideo;

  public MediasModel() {

  }

  public MediasModel(File file) {
    if (file != null && !AndroidUtilities.isImage(file)) {
      return;
    }
    Uri uri = Uri.fromFile(file);
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
    this.file = file;
    this.height = options.outHeight;
    this.width = options.outWidth;
  }

  public MediasModel(File file, String url, String thumbnail, int mediaId, long dateTaken, int duration, int width, int height, long size, boolean isVideo) {
    this.file = file;
    this.url = url;
    this.thumbnail = thumbnail;
    this.mediaId = mediaId;
    this.dateTaken = dateTaken;
    this.duration = duration;
    this.width = width;
    this.height = height;
    this.size = size;
    this.isVideo = isVideo;
  }
}
