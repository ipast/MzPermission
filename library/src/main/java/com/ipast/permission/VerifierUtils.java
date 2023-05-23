package com.ipast.permission;

import android.accessibilityservice.AccessibilityService;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;


/**
 * author:gang.cheng
 * description: 特殊权限检验工具
 * date:2023/4/13
 */
public class VerifierUtils {
    private static final String TAG = "Permission Verifier";

    /**
     * @param context
     * @return 是否有android.permission.ACCESS_NOTIFICATION_POLICY权限
     */
    public static boolean areNotificationsEnabled(Context context) {
        if (ROMUtils.isVivo() || ROMUtils.isOppo()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NotificationManagerCompat notification = NotificationManagerCompat.from(context);
                return notification.areNotificationsEnabled();
            }
        }
        return true;
    }

    /**
     * @return 是否有android.permission.MANAGE_EXTERNAL_STORAGE权限
     */
    public static boolean isExternalStorageManager() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param context
     * @return 是否有android.permission.WRITE_SETTINGS权限
     */
    public static boolean canWrite(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param context
     * @return 是否有android.permission.BIND_DEVICE_ADMIN权限
     */
    public static boolean isAdminActive(Context context) {
        DevicePolicyManager mPolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(context, MzDeviceAdminReceiver.class);
        return mPolicyManager.isAdminActive(adminReceiver);
    }


    /**
     * @param context
     * @return 是否有android.permission.REQUEST_INSTALL_PACKAGES权限
     */
    public static boolean canRequestPackageInstalls(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return context.getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }


    /**
     * @param context
     * @param serviceClz
     * @return 是否有android.permission.BIND_ACCESSIBILITY_SERVICE权限
     */
    public static boolean accessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> serviceClz) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName() + "/" + serviceClz.getCanonicalName();
        Log.i(TAG, "service:" + service);
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.d(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.d(TAG, "Accessibility is disabled");
        }
        return false;
    }

    /**
     * This provider determines location using GNSS satellites.
     *
     * @param context
     * @return
     */
    public static boolean isGPSProviderEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


}
