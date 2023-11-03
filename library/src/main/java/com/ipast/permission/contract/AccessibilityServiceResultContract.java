package com.ipast.permission.contract;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ipast.permission.utils.PermissionCheckUtils;

/**
 * author:gang.cheng
 * description:
 * date:2023/10/30
 */
public class AccessibilityServiceResultContract extends ActivityResultContract<Void, Boolean> {
    private Context mContext;
    private Class<? extends AccessibilityService> serviceClz;

    public AccessibilityServiceResultContract(Class<? extends AccessibilityService> serviceClz) {
        this.serviceClz = serviceClz;
    }

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Void input) {
        this.mContext = context;
        return new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
    }

    @Override
    public Boolean parseResult(int resultCode, @Nullable Intent intent) {
        return PermissionCheckUtils.accessibilityServiceEnabled(mContext,serviceClz);
    }
}