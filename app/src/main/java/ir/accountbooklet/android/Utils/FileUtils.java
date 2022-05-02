package ir.accountbooklet.android.Utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import androidx.core.content.FileProvider;
import ir.accountbooklet.android.ApplicationLoader;
import ir.accountbooklet.android.BuildConfig;

public class FileUtils {

  public static String DIR_PATH;
  public static String APP_PATH;
  public static String MEDIA_PATH;
  public static String BACKUP_PATH;
  public static final String FOLDER_DIR = "DaftarcheHesab";

  static {
    DIR_PATH = getRootDirs().get(0).getPath();
    loadDirs();
  }

  public static void loadDirs() {
    File cachePath = getCacheDir();
    if (!cachePath.isDirectory()) {
      try {
        mkdirs(cachePath);
      } catch (Exception e) {
        AppLog.e(FileUtils.class, e.getMessage());
      }
    }
    try {
      if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
        File path = Environment.getExternalStorageDirectory();
        if (Build.VERSION.SDK_INT >= 19 && !TextUtils.isEmpty(DIR_PATH)) {
          ArrayList<File> dirs = getRootDirs();
          for (int a = 0, N = dirs.size(); a < N; a++) {
            File dir = dirs.get(a);
            if (dir.getAbsolutePath().startsWith(DIR_PATH)) {
              path = dir;
              break;
            }
          }
        }
        File downloaderPath = new File(path, FOLDER_DIR);
        if (Build.VERSION.SDK_INT >= 30) {
          File newPath = ApplicationLoader.applicationContext.getExternalFilesDir(null);
          if (newPath.getAbsolutePath().startsWith(DIR_PATH)) {
            downloaderPath = new File(newPath, FOLDER_DIR);
          }
        }
        mkdirs(downloaderPath);
        if (Build.VERSION.SDK_INT >= 19 && !downloaderPath.isDirectory()) {
          ArrayList<File> dirs = getDataDirs();
          for (int a = 0, N = dirs.size(); a < N; a++) {
            File dir = dirs.get(a);
            if (dir.getAbsolutePath().startsWith(DIR_PATH)) {
              path = dir;
              downloaderPath = new File(path, FOLDER_DIR);
              mkdirs(downloaderPath);
              break;
            }
          }
        }
        APP_PATH = downloaderPath.getPath();
      }
    } catch (Exception e) {
      AppLog.e(FileUtils.class, e.getMessage());
    }
    if (TextUtils.isEmpty(APP_PATH)) {
      APP_PATH = DIR_PATH + "/" + FOLDER_DIR;
    }
    MEDIA_PATH = APP_PATH + "/" + "Media";
    BACKUP_PATH = APP_PATH + "/" + "Backup";
  }

  public static ArrayList<File> getRootDirs() {
    ArrayList<File> result = null;
    if (Build.VERSION.SDK_INT >= 19) {
      File[] dirs = ApplicationLoader.applicationContext.getExternalFilesDirs(null);
      if (dirs != null) {
        for (File dir : dirs) {
          if (dir == null) {
            continue;
          }
          String path = dir.getAbsolutePath();
          int idx = path.indexOf("/Android");
          if (idx >= 0) {
            if (result == null) {
              result = new ArrayList<>();
            }
            result.add(new File(path.substring(0, idx)));
          }
        }
      }
    }
    if (result == null) {
      result = new ArrayList<>();
    }
    if (result.isEmpty()) {
      result.add(Environment.getExternalStorageDirectory());
    }
    return result;
  }

  public static File getCacheDir() {
    String state = null;
    try {
      state = Environment.getExternalStorageState();
    } catch (Exception e) {
      AppLog.e(FileUtils.class, e.getMessage());
    }
    if (state == null || state.startsWith(Environment.MEDIA_MOUNTED)) {
      try {
        File file;
        if (Build.VERSION.SDK_INT >= 19) {
          File[] dirs = ApplicationLoader.applicationContext.getExternalCacheDirs();
          file = dirs[0];
          if (!TextUtils.isEmpty(DIR_PATH)) {
            for (File dir : dirs) {
              if (dir != null && dir.getAbsolutePath().startsWith(DIR_PATH)) {
                file = dir;
                break;
              }
            }
          }
        } else {
          file = ApplicationLoader.applicationContext.getExternalCacheDir();
        }
        if (file != null) {
          return file;
        }
      } catch (Exception e) {
        AppLog.e(FileUtils.class, e.getMessage());
      }
    }
    try {
      File file = ApplicationLoader.applicationContext.getCacheDir();
      if (file != null) {
        return file;
      }
    } catch (Exception e) {
      AppLog.e(FileUtils.class, e.getMessage());
    }
    return new File("");
  }

  public static ArrayList<File> getDataDirs() {
    ArrayList<File> result = null;
    if (Build.VERSION.SDK_INT >= 19) {
      File[] dirs = ApplicationLoader.applicationContext.getExternalFilesDirs(null);
      if (dirs != null) {
        for (File dir : dirs) {
          if (dir == null) {
            continue;
          }
          if (result == null) {
            result = new ArrayList<>();
          }
          result.add(dir);
        }
      }
    }
    if (result == null) {
      result = new ArrayList<>();
    }
    if (result.isEmpty()) {
      result.add(Environment.getExternalStorageDirectory());
    }
    return result;
  }

  public static boolean mkdirs(File file) {
    return file != null && (file.exists() || file.mkdirs());
  }

  public static boolean createNewFile(File file) {
    try {
      return file != null && (file.exists() || file.createNewFile());
    } catch (IOException e) {
      return false;
    }
  }

  public static boolean delete(File file) {
    return file != null && (!file.exists() || file.delete());
  }

  public static void checkCreateFolders() {
    try {
      StringBuilder builder = new StringBuilder();
      if(mkdirs(new File(APP_PATH))) {
        builder.append(APP_PATH);
        builder.append(",");
      }
      if(mkdirs(new File(MEDIA_PATH))) {
        builder.append(MEDIA_PATH);
        builder.append(",");
      }
      if(mkdirs(new File(BACKUP_PATH))) {
        builder.append(BACKUP_PATH);
        builder.append(",");
      }
      File noMedia = new File(MEDIA_PATH, ".nomedia");
      if(createNewFile(noMedia)) {
        builder.append(noMedia.getPath());
        builder.append(",");
      }
      AndroidUtilities.addMediaToGallery(builder.toString().split(","));
    } catch (Exception e) {
      AppLog.e(FileUtils.class, "createFolders ->" + e.getMessage());
    }
  }

  public static void copyFile(File from, File to) {
    try {
      InputStream in = new FileInputStream(from);
      OutputStream out = new FileOutputStream(to);
      byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      }
      out.close();
      in.close();
    } catch (Exception e) {
      AppLog.e(FileUtils.class, "copyFile: " + e.getMessage());
      delete(to);
    }
  }

  public static Uri getFileUri(Context context, File file) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
    } else {
      return Uri.fromFile(file);
    }
  }
}
