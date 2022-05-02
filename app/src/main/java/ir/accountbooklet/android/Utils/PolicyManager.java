package ir.accountbooklet.android.Utils;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;

public class PolicyManager {
    public static final int DPM_ACTIVATION_REQUEST_CODE = 1000;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;

    public PolicyManager(Context context) {
        devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(context, SampleDeviceAdminReceiver.class);
    }

    public boolean isAdminActive() {
        return devicePolicyManager.isAdminActive(componentName);
    }

    public ComponentName getComponentName() {
        return componentName;
    }

    public void disableAdmin() {
        devicePolicyManager.removeActiveAdmin(componentName);
    }
}