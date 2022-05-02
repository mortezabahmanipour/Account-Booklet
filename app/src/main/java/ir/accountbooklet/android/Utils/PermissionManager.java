package ir.accountbooklet.android.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import ir.accountbooklet.android.Fragments.BaseBottomSheetDialogFragment;
import ir.accountbooklet.android.Fragments.BaseDialogFragment;

public class PermissionManager {
  public static boolean checkPermissions(Context context, String... permissions) {
    if (context == null) {
      return false;
    }
    boolean isGranted = true;
    for (String str : permissions) {
      if (ContextCompat.checkSelfPermission(context, str) != PackageManager.PERMISSION_GRANTED) {
        isGranted = false;
        break;
      }
    }
    return isGranted;
  }

  public static void requestPermissions(Context context, int request, String... permissions) {
    Activity activity = (Activity) context;
    if (activity == null) {
      return;
    }
    ActivityCompat.requestPermissions(activity, permissions, request);
  }

  public static void requestPermissionsResult(FragmentManager fragmentManager, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (fragmentManager == null) {
      return;
    }
    for (Fragment fragment : fragmentManager.getFragments()) {
      if (fragment == null) {
        continue;
      }
      if (fragment instanceof BaseBottomSheetDialogFragment && !((BaseBottomSheetDialogFragment) fragment).isDestroy()) {
        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
      } else if (fragment instanceof BaseDialogFragment && !((BaseDialogFragment) fragment).isDestroy()) {
        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
      } else {
        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
      }
    }
  }
}
