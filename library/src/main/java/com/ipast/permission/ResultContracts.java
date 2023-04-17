package com.ipast.permission;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;



import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;



import static android.app.Activity.RESULT_OK;
import static com.ipast.permission.VerifierUtils.areNotificationsEnabled;
import static com.ipast.permission.VerifierUtils.canWrite;
import static com.ipast.permission.VerifierUtils.isExternalStorageManager;


/**
 * author:gang.cheng
 * description:
 * date:2023/4/13
 */
public class ResultContracts {

    public static class WriteSettingsResult extends ActivityResultContract<Void, Boolean> {
        private Context mContext;

        @RequiresApi(api = Build.VERSION_CODES.M)
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Void input) {
            this.mContext = context;
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            return intent;
        }

        @Override
        public Boolean parseResult(int resultCode, @Nullable Intent intent) {
            return canWrite(mContext);
        }
    }

    public static class RequestInstallPackagesResult extends ActivityResultContract<Void, Boolean> {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Void input) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            return intent;
        }

        @Override
        public Boolean parseResult(int resultCode, @Nullable Intent intent) {
            return resultCode == RESULT_OK;
        }
    }

    public static class AppManagerExternalStorageResult extends ActivityResultContract<Void, Boolean> {
        private Context mContext;

        @RequiresApi(api = Build.VERSION_CODES.R)
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Void input) {
            this.mContext = context;
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            return intent;
        }


        @Override
        public Boolean parseResult(int resultCode, @Nullable Intent intent) {
            return isExternalStorageManager();
        }
    }

    public static class ManagerExternalStorageResult extends ActivityResultContract<Void, Boolean> {
        private Context mContext;

        @RequiresApi(api = Build.VERSION_CODES.R)
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Void input) {
            this.mContext = context;
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            return intent;
        }


        @Override
        public Boolean parseResult(int resultCode, @Nullable Intent intent) {
            return isExternalStorageManager();
        }
    }

    public static class BindDeviceAdminResult extends ActivityResultContract<Void, Boolean> {
        private Context mContext;


        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Void input) {
            this.mContext = context;
            ComponentName adminReceiver = new ComponentName(context, MzDeviceAdminReceiver.class);
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminReceiver);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "开启后就可以使用锁屏功能了...");
            return intent;
        }


        @Override
        public Boolean parseResult(int resultCode, @Nullable Intent intent) {
            return VerifierUtils.isAdminActive(mContext);
        }
    }

    public static class AccessNotificationPolicyResult extends ActivityResultContract<Void, Boolean> {
        private Context mContext;

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Void input) {
            this.mContext = context;
            Intent intent = new Intent();
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
            // for Android 8 and above
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
            return intent;
        }

        @Override
        public Boolean parseResult(int resultCode, @Nullable Intent intent) {
            return areNotificationsEnabled(mContext);
        }
    }
}
