package com.ipast.permission.contract;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ipast.permission.DefaultDeviceAdminReceiver;
import com.ipast.permission.utils.PermissionCheckUtils;

/**
 * author:gang.cheng
 * description:
 * date:2023/10/30
 */
public class BindDeviceAdminResultContract extends ActivityResultContract<Void, Boolean> {
    private Context mContext;


    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Void input) {
        this.mContext = context;
        ComponentName adminReceiver = new ComponentName(context, DefaultDeviceAdminReceiver.class);
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminReceiver);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "开启后就可以使用锁屏功能了...");
        return intent;
    }


    @Override
    public Boolean parseResult(int resultCode, @Nullable Intent intent) {
        return PermissionCheckUtils.isAdminActive(mContext);
    }
}
