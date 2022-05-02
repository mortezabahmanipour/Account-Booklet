package ir.accountbooklet.android.Utils;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

public class SampleDeviceAdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        AndroidUtilities.toast(context, "غیرفعال شد");
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        AndroidUtilities.toast(context, "فعال شد");
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        AndroidUtilities.toast(context, "درخواست محافظت غیرفعال گردید");
        return super.onDisableRequested(context, intent);
    }
}