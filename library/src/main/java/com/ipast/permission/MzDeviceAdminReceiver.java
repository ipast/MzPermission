package com.ipast.permission;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MzDeviceAdminReceiver extends DeviceAdminReceiver {
    private final String TAG = getClass().getSimpleName();

    public MzDeviceAdminReceiver() {
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        Log.d(TAG, "device admin enable");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Log.d(TAG, "device admin disabled");
    }
}