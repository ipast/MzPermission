package com.ipast.permission.utils;

import android.accessibilityservice.AccessibilityService;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.ipast.permission.DefaultDeviceAdminReceiver;
import com.ipast.permission.utils.rom.ROMUtils;


/**
 * author:gang.cheng
 * description: 特殊权限检验工具
 * date:2023/4/13
 */
public class PermissionCheckUtils {
    private static final String TAG = PermissionCheckUtils.class.getSimpleName();

    /**
     * @param context
     * @return 是否有android.permission.ACCESS_NOTIFICATION_POLICY权限
     */
    public static boolean areNotificationsEnabled(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (ROMUtils.isVivo() || ROMUtils.isOppo()) {
            NotificationManagerCompat notification = NotificationManagerCompat.from(context);
            return notification.areNotificationsEnabled();
        }
        return true;
    }

    /**
     * @return 是否有android.permission.MANAGE_EXTERNAL_STORAGE权限
     */
    public static boolean isExternalStorageManager() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return true;
        }
        return Environment.isExternalStorageManager();
    }

    /**
     * @param context
     * @return 是否有android.permission.WRITE_SETTINGS权限
     */
    public static boolean canWrite(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return Settings.System.canWrite(context);
    }

    /**
     * @param context
     * @return 是否有android.permission.BIND_DEVICE_ADMIN权限
     */
    public static boolean isAdminActive(Context context) {
        DevicePolicyManager mPolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(context, DefaultDeviceAdminReceiver.class);
        return mPolicyManager.isAdminActive(adminReceiver);
    }


    /**
     * @param context
     * @return 是否有android.permission.REQUEST_INSTALL_PACKAGES权限
     */
    public static boolean canRequestPackageInstalls(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return true;
        }
        return context.getPackageManager().canRequestPackageInstalls();
    }


    /**
     * @param context
     * @param serviceClz
     * @return 是否有android.permission.BIND_ACCESSIBILITY_SERVICE权限
     */
    public static boolean accessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> serviceClz) {

        final String service = context.getPackageName() + "/" + serviceClz.getCanonicalName();
        Log.i(TAG, "service:" + service);

        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        if (accessibilityEnabled != 1) {
            return false;
        }
        String accessibilityServicesEnabled = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityServicesEnabled == null) {
            return false;
        }
        splitter.setString(accessibilityServicesEnabled);
        String accessibilityService;
        while (splitter.hasNext()) {
            accessibilityService = splitter.next();
            if (accessibilityService.equalsIgnoreCase(service)) {
                Log.d(TAG, "We've found the correct setting - accessibility is switched on!");
                return true;
            }
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


}
