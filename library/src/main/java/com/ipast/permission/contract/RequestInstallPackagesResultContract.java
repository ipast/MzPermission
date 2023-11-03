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

import static android.app.Activity.RESULT_OK;

/**
 * author:gang.cheng
 * description:
 * date:2023/10/30
 */
public class RequestInstallPackagesResultContract extends ActivityResultContract<Void, Boolean> {

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
