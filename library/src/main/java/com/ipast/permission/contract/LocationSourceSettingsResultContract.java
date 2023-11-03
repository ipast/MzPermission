package com.ipast.permission.contract;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.ipast.permission.utils.PermissionCheckUtils.isGPSProviderEnabled;

/**
 * author:gang.cheng
 * description:
 * date:2023/10/30
 */
public class LocationSourceSettingsResultContract extends ActivityResultContract<Void, Boolean> {
    private Context mContext;

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Void input) {
        this.mContext = context;
        return new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    }

    @Override
    public Boolean parseResult(int resultCode, @Nullable Intent intent) {
        return isGPSProviderEnabled(mContext);
    }
}