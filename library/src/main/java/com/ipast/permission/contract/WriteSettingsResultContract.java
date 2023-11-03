package com.ipast.permission.contract;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import static com.ipast.permission.utils.PermissionCheckUtils.canWrite;

/**
 * author:gang.cheng
 * description:
 * date:2023/10/30
 */
public class WriteSettingsResultContract extends ActivityResultContract<Void, Boolean> {
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