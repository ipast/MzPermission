package com.ipast.permission.contract;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ipast.permission.utils.PermissionCheckUtils;

/**
 * author:gang.cheng
 * description:
 * date:2023/11/8
 */
public class SystemAlertWindowResultContract extends ActivityResultContract<Void, Boolean> {
    private Context mContext;

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Void input) {
        this.mContext = context;
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        return intent;
    }

    @Override
    public Boolean parseResult(int resultCode, @Nullable Intent intent) {
        return PermissionCheckUtils.canDrawOverlays(mContext);
    }
}
