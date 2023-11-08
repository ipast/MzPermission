package com.ipast.permission.callback;

/**
 * author:gang.cheng
 * description:
 * date:2023/10/30
 */
public interface PermissionRequestResultCallback {
    void onPermissionGranted();

    void onPermissionsDenied(String[] permissions);
}
