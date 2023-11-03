package com.ipast.mzpermission;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ipast.permission.MzPermission;
import com.ipast.permission.Permissions;
import com.ipast.permission.callback.DialogRequestResultCallback;
import com.ipast.permission.callback.PermissionRequestCallback;
import com.ipast.permission.callback.RequestResultCallback;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private MzPermission mzPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mzPermission = new MzPermission(this)
                .registerForActivityResult()
                .registerAccessNotificationPolicy()
                .registerWriteSettings()
                .registerManagerExternalStorage()
                .registerRequestInstallPackages()
                .registerBindDeviceAdmin()
                .registerAccessibilityService(MyAccessibilityService.class)
                .registerLocationSourceSettings();
         //requestExternalStoragePermission();
         requestBindDeviceAdmin();
        // requestAccessibilityService();
        //requestLocationSourceSettings();
    }

    private void requestLocationSourceSettings() {
        mzPermission.launchLocationSourceSettings(new DialogRequestResultCallback() {
            @Override
            public void showRequestDialog(PermissionRequestCallback callback) {
                MessageDialog.show("权限申请", "需要使用位置信息权限，请手动开启！", "确定", "取消")
                        .setOkButton(new OnDialogButtonClickListener<MessageDialog>() {
                            @Override
                            public boolean onClick(MessageDialog baseDialog, View v) {
                                callback.allowRequest();
                                return false;
                            }
                        })
                        .setCancelButton(new OnDialogButtonClickListener<MessageDialog>() {
                            @Override
                            public boolean onClick(MessageDialog dialog, View v) {

                                return false;
                            }
                        });
            }

            @Override
            public void onPermissionGranted() {
                Log.d(TAG, "onPermissionGranted()");
            }

            @Override
            public void onPermissionsDenied(String... permissions) {
                Log.d(TAG, "onPermissionsDenied()");
            }
        });

    }

    private void requestAccessibilityService() {
        mzPermission.launchAccessibilityService(new DialogRequestResultCallback() {
            @Override
            public void showRequestDialog(PermissionRequestCallback callback) {
                MessageDialog.show("权限申请", "需要打开无障碍辅助权限，请手动开启！", "确定", "取消")
                        .setOkButton(new OnDialogButtonClickListener<MessageDialog>() {
                            @Override
                            public boolean onClick(MessageDialog baseDialog, View v) {
                                callback.allowRequest();
                                return false;
                            }
                        })
                        .setCancelButton(new OnDialogButtonClickListener<MessageDialog>() {
                            @Override
                            public boolean onClick(MessageDialog dialog, View v) {

                                return false;
                            }
                        });
            }

            @Override
            public void onPermissionGranted() {
                Log.d(TAG, "onPermissionGranted()");
            }

            @Override
            public void onPermissionsDenied(String... permissions) {
                Log.d(TAG, "onPermissionsDenied()");
            }
        });

    }

    private void requestBindDeviceAdmin() {
        mzPermission.launchBindDeviceAdmin(new RequestResultCallback() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionsDenied(String... permissions) {

            }
        });
    }

    private void requestInstallPackages() {
        mzPermission.launchRequestInstallPackages(new DialogRequestResultCallback() {
            @Override
            public void showRequestDialog(PermissionRequestCallback callback) {
                MessageDialog.show("权限申请", "需要开启应用通知权限，请手动开启！", "确定", "取消")
                        .setOkButton(new OnDialogButtonClickListener<MessageDialog>() {
                            @Override
                            public boolean onClick(MessageDialog baseDialog, View v) {
                                callback.allowRequest();
                                return false;
                            }
                        })
                        .setCancelButton(new OnDialogButtonClickListener<MessageDialog>() {
                            @Override
                            public boolean onClick(MessageDialog dialog, View v) {

                                return false;
                            }
                        });
            }

            @Override
            public void onPermissionGranted() {
                Log.d(TAG, "onPermissionGranted()");
            }

            @Override
            public void onPermissionsDenied(String... permissions) {
                Log.d(TAG, "onPermissionsDenied()");
            }
        });

    }

    private void requestExternalStoragePermission() {
        mzPermission.launchManagerExternalStorage(new RequestResultCallback() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionsDenied(String... permissions) {

            }
        });
    }

    private void requestNormalPermissions() {
        mzPermission.request(Permissions.EXTERNAL_STORAGE)
                .launch(new RequestResultCallback() {
                    @Override
                    public void onPermissionGranted() {
                        Log.d(TAG, "onPermissionGranted()");
                    }

                    @Override
                    public void onPermissionsDenied(String... permissions) {
                        Log.d(TAG, "onPermissionDenied()");
                    }
                });
    }
}