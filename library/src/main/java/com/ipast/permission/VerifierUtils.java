package com.ipast.permission;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.core.app.NotificationManagerCompat;


/**
 * author:gang.cheng
 * description: 特殊权限检验工具
 * date:2023/4/13
 */
public class VerifierUtils {

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
}
