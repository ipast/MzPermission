package com.ipast.permission;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DefaultDeviceAdminReceiver extends DeviceAdminReceiver {
    private final String TAG = getClass().getSimpleName();

    public DefaultDeviceAdminReceiver() {
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