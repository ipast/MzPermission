package com.ipast.permission.callback;

/**
 * author:gang.cheng
 * description:
 * date:2023/10/30
 */
public interface DialogRequestResultCallback extends RequestResultCallback {

    void showRequestDialog(PermissionRequestCallback permissionRequestCallback);

}
