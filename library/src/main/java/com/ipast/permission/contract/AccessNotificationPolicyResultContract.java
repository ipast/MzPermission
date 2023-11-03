package com.ipast.permission.contract;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.ipast.permission.utils.PermissionCheckUtils.areNotificationsEnabled;

/**
 * author:gang.cheng
 * description:
 * date:2023/10/30
 */
public class AccessNotificationPolicyResultContract extends ActivityResultContract<Void, Boolean> {
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
