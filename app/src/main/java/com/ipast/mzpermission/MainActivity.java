package com.ipast.mzpermission;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ipast.permission.MzPermission;
import com.ipast.permission.Permissions;
import com.ipast.permission.callback.DialogPermissionRequestCallback;
import com.ipast.permission.callback.PermissionCheckCallback;
import com.ipast.permission.callback.PermissionRequestCallback;
import com.ipast.permission.callback.PermissionRequestResultCallback;
import com.ipast.permission.utils.PermissionCheckUtils;
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
                .registerForActivityResult()  //普通权限
                .registerAccessNotificationPolicy() //通知权限
                .registerWriteSettings()   //修改系统设置权限
                .registerManagerExternalStorage()  //管理所有文件权限
                .registerRequestInstallPackages()  //安装未知来源应用权限
                .registerBindDeviceAdmin()  //设备管理权限
                .registerAccessibilityService(MyAccessibilityService.class) //辅助功能权限
                .registerLocationSourceSettings()  //位置信息权限
                .registerSystemAlertWindow();  //悬浮窗权限

        //requestExternalStoragePermission();
        //requestBindDeviceAdmin();
        //requestAccessibilityService();
        //requestLocationSourceSettings();
        //requestSystemAlertWindow();

        registerCustomSpecialPermission();
        requestCustomSpecialPermission();
    }

    private final String SPECIAL_PERMISSION = "specialPermission";

    /**
     * 注册自定义的特殊权限申请
     */
    private void registerCustomSpecialPermission() {
        mzPermission.registerForActivityResult(SPECIAL_PERMISSION, new CustomSpecialPermissionResultContract());
    }

    private void requestCustomSpecialPermission() {
        mzPermission.launchPermissionRequest(SPECIAL_PERMISSION, new PermissionCheckCallback() {
                    @Override
                    public boolean checkPermission() {
                        return PermissionCheckUtils.canDrawOverlays(MainActivity.this);
                    }
                },
                new DialogPermissionRequestCallback() {
                    @Override
                    public void showRequestDialog(PermissionRequestCallback permissionRequestCallback) {
                        MessageDialog.show("权限申请", "需要悬浮窗口权限，请手动开启！", "确定", "取消")
                                .setOkButton(new OnDialogButtonClickListener<MessageDialog>() {
                                    @Override
                                    public boolean onClick(MessageDialog baseDialog, View v) {
                                        permissionRequestCallback.allowRequest();
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
                    public void onPermissionsDenied(String[] permissions) {
                        Log.d(TAG, "onPermissionsDenied()");
                    }
                });
    }

    private void requestSystemAlertWindow() {
        mzPermission.launchSystemAlertWindow(new DialogPermissionRequestCallback() {
            @Override
            public void showRequestDialog(PermissionRequestCallback callback) {
                MessageDialog.show("权限申请", "需要悬浮窗口权限，请手动开启！", "确定", "取消")
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
            public void onPermissionsDenied(String[] permissions) {
                Log.d(TAG, "onPermissionsDenied()");
            }
        });

    }

    private void requestLocationSourceSettings() {
        mzPermission.launchLocationSourceSettings(new DialogPermissionRequestCallback() {
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
            public void onPermissionsDenied(String[] permissions) {
                Log.d(TAG, "onPermissionsDenied()");
            }
        });

    }

    private void requestAccessibilityService() {
        mzPermission.launchAccessibilityService(new DialogPermissionRequestCallback() {
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
            public void onPermissionsDenied(String[] permissions) {
                Log.d(TAG, "onPermissionsDenied()");
            }
        });

    }

    private void requestBindDeviceAdmin() {
        mzPermission.launchBindDeviceAdmin(new PermissionRequestResultCallback() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionsDenied(String... permissions) {

            }
        });
    }

    private void requestInstallPackages() {
        mzPermission.launchRequestInstallPackages(new DialogPermissionRequestCallback() {
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
            public void onPermissionsDenied(String[] permissions) {
                Log.d(TAG, "onPermissionsDenied()");
            }
        });

    }

    private void requestExternalStoragePermission() {
        mzPermission.launchManagerExternalStorage(new PermissionRequestResultCallback() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionsDenied(String[] permissions) {

            }
        });
    }

    private void requestNormalPermissions() {
        mzPermission.request(Permissions.EXTERNAL_STORAGE)
                .launch(new PermissionRequestResultCallback() {
                    @Override
                    public void onPermissionGranted() {
                        Log.d(TAG, "onPermissionGranted()");
                    }

                    @Override
                    public void onPermissionsDenied(String[] permissions) {
                        Log.d(TAG, "onPermissionDenied()");
                    }
                });
    }
}