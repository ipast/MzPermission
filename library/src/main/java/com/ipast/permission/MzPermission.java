package com.ipast.permission;

import android.Manifest;
import android.content.Context;
import android.os.Build;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_NOTIFICATION_POLICY;
import static android.Manifest.permission.BIND_DEVICE_ADMIN;
import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.REQUEST_INSTALL_PACKAGES;
import static android.Manifest.permission.WRITE_SETTINGS;
import static com.ipast.permission.VerifierUtils.areNotificationsEnabled;
import static com.ipast.permission.VerifierUtils.canRequestPackageInstalls;
import static com.ipast.permission.VerifierUtils.canWrite;
import static com.ipast.permission.VerifierUtils.isExternalStorageManager;


/**
 * author:gang.cheng
 * description:权限申请工具；
 * date:2021/12/1
 */
public class MzPermission {
    private ActivityResultCaller caller;
    private Context context;
    private String[] normalPermissions;
    private ActivityResultLauncher<String[]> normalLauncher;
    private HashMap<String, ActivityResultLauncher<Void>> specialLauncherMap;
    private OnResultCallback onResultCallback;
    private ActivityResultCallback<Boolean> specialCallback;


    public MzPermission(@NonNull ComponentActivity activity) {
        this.caller = activity;
        this.context = activity;
        registerForActivityResult();
    }

    public MzPermission(@NonNull Fragment fragment) {
        this.caller = fragment;
        this.context = fragment.getContext();
        registerForActivityResult();
    }

    public MzPermission request(@NonNull String... permissions) {
        this.normalPermissions = permissions;
        return this;
    }

    private void combine(@NonNull String specialPermission) {
        if (specialLauncherMap == null) {
            specialLauncherMap = new HashMap<>();
        }
        if (specialCallback == null) {
            specialCallback = new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (onResultCallback != null) {
                        if (result) {
                            onResultCallback.onPermissionGranted();
                        } else {
                            onResultCallback.onPermissionsDenied();
                        }
                    }
                }
            };
        }
        putForActivityResult(specialPermission);
    }

    private void putForActivityResult(@NonNull String specialPermission) {
        switch (specialPermission) {
            case MANAGE_EXTERNAL_STORAGE:
                specialLauncherMap.put(
                        specialPermission,
                        caller.registerForActivityResult(new ResultContracts.AppManagerExternalStorageResult(), specialCallback)
                );
                break;
            case WRITE_SETTINGS:
                specialLauncherMap.put(
                        specialPermission,
                        caller.registerForActivityResult(new ResultContracts.WriteSettingsResult(), specialCallback)
                );

                break;
            case BIND_DEVICE_ADMIN:
                specialLauncherMap.put(
                        specialPermission,
                        caller.registerForActivityResult(new ResultContracts.BindDeviceAdminResult(), specialCallback)
                );
                break;
            case ACCESS_NOTIFICATION_POLICY:
                specialLauncherMap.put(
                        specialPermission,
                        caller.registerForActivityResult(new ResultContracts.AccessNotificationPolicyResult(), specialCallback)
                );
                break;
            case REQUEST_INSTALL_PACKAGES:
                specialLauncherMap.put(
                        specialPermission,
                        caller.registerForActivityResult(new ResultContracts.RequestInstallPackagesResult(), specialCallback)
                );
                break;
        }
    }

    private void registerForActivityResult() {
        this.normalLauncher = caller.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean isGranted = true;
                    List<String> deniedPermissions = new ArrayList();
                    for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                        if (!entry.getValue()) {
                            isGranted = false;
                            deniedPermissions.add(entry.getKey());
                        }
                    }
                    if (onResultCallback != null) {
                        if (isGranted) {
                            onResultCallback.onPermissionGranted();
                        } else {
                            onResultCallback.onPermissionsDenied(deniedPermissions.toArray(new String[deniedPermissions.size()]));
                        }
                    }
                });
    }

    /**
     * Permission is only granted to system app
     * android.permission.WRITE_SETTINGS
     *
     * @return
     */
    public MzPermission registerWriteSettingsResult() {
        combine(WRITE_SETTINGS);
        return this;
    }

    /**
     * android.permission.BIND_DEVICE_ADMIN
     *
     * @return
     */
    public MzPermission registerBindDeviceAdminResult() {
        combine(BIND_DEVICE_ADMIN);
        return this;
    }
    /**
     * android.permission.REQUEST_INSTALL_PACKAGES
     *
     * @return
     */
    public MzPermission registerRequestInstallPackagesResult() {
        combine(REQUEST_INSTALL_PACKAGES);
        return this;
    }
    /**
     * android.permission.MANAGE_EXTERNAL_STORAGE
     *
     * @return
     */
    public MzPermission registerManagerExternalStorageResult() {
        combine(MANAGE_EXTERNAL_STORAGE);
        return this;
    }

    /**
     * android.permission.ACCESS_NOTIFICATION_POLICY
     *
     * @return
     */
    public MzPermission registerAccessNotificationPolicyResult() {
        combine(ACCESS_NOTIFICATION_POLICY);
        return this;
    }


    /**
     * 非特殊权限申请
     *
     * @param callback
     */
    public void launch(OnResultCallback callback) {
        this.onResultCallback = callback;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            normalLauncher.launch(normalPermissions);
            return;
        }
        if (onResultCallback != null) {
            onResultCallback.onPermissionGranted();
        }
    }
    /**
     * android.permission.REQUEST_INSTALL_PACKAGES
     *
     * @param callback
     */
    public void launchRequestInstallPackages(OnResultCallback callback) {
        this.onResultCallback = callback;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!canRequestPackageInstalls(context)) {
                if (onResultCallback != null && onResultCallback instanceof OnDialogResultCallback) {
                    OnDialogResultCallback onDialogResultCallback = (OnDialogResultCallback) onResultCallback;
                    onDialogResultCallback.showRequestDialog(new OnLaunchCallback() {
                        @Override
                        public void allowLaunch() {
                            getSpecialLauncher(REQUEST_INSTALL_PACKAGES).launch(null);
                        }
                    });
                } else {
                    getSpecialLauncher(REQUEST_INSTALL_PACKAGES).launch(null);
                }

                return;
            }
        }
        if (onResultCallback != null) {
            onResultCallback.onPermissionGranted();
        }
    }
    /**
     * Permission is only granted to system app
     * android.permission.WRITE_SETTINGS
     *
     * @param callback
     */
    public void launchWriteSettings(OnResultCallback callback) {
        this.onResultCallback = callback;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!canWrite(context)) {
                if (onResultCallback != null && onResultCallback instanceof OnDialogResultCallback) {
                    OnDialogResultCallback onDialogResultCallback = (OnDialogResultCallback) onResultCallback;
                    onDialogResultCallback.showRequestDialog(new OnLaunchCallback() {
                        @Override
                        public void allowLaunch() {
                            getSpecialLauncher(WRITE_SETTINGS).launch(null);
                        }
                    });
                } else {
                    getSpecialLauncher(WRITE_SETTINGS).launch(null);
                }
                return;
            }
        }
        if (onResultCallback != null) {
            onResultCallback.onPermissionGranted();
        }
    }

    /**
     * android.permission.MANAGE_EXTERNAL_STORAGE
     *
     * @param callback
     */
    public void launchManagerExternalStorage(OnResultCallback callback) {
        this.onResultCallback = callback;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!isExternalStorageManager()) {
                if (onResultCallback != null && onResultCallback instanceof OnDialogResultCallback) {
                    OnDialogResultCallback onDialogResultCallback = (OnDialogResultCallback) onResultCallback;
                    onDialogResultCallback.showRequestDialog(new OnLaunchCallback() {
                        @Override
                        public void allowLaunch() {
                            getSpecialLauncher(MANAGE_EXTERNAL_STORAGE).launch(null);
                        }
                    });
                } else {
                    getSpecialLauncher(MANAGE_EXTERNAL_STORAGE).launch(null);
                }

                return;
            }
        }
        if (onResultCallback != null) {
            onResultCallback.onPermissionGranted();
        }
    }

    public static final String[] EXTERNAL_STORAGE = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * android.permission.MANAGE_EXTERNAL_STORAGE
     * android.permission.READ_EXTERNAL_STORAGE
     * android.permission.WRITE_EXTERNAL_STORAGE
     *
     * @param callback
     */
    public void launchExternalStorage(OnResultCallback callback) {
        this.onResultCallback = callback;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (onResultCallback != null && onResultCallback instanceof OnDialogResultCallback) {
                OnDialogResultCallback onDialogResultCallback = (OnDialogResultCallback) onResultCallback;
                onDialogResultCallback.showRequestDialog(new OnLaunchCallback() {
                    @Override
                    public void allowLaunch() {
                        getSpecialLauncher(MANAGE_EXTERNAL_STORAGE).launch(null);
                    }
                });
            } else {
                getSpecialLauncher(MANAGE_EXTERNAL_STORAGE).launch(null);
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.request(EXTERNAL_STORAGE).launch(callback);
        } else {
            if (onResultCallback != null) {
                onResultCallback.onPermissionGranted();
            }
        }
    }

    /**
     * android.permission.BIND_DEVICE_ADMIN
     *
     * @param callback
     */
    public void launchBindDeviceAdmin(OnResultCallback callback) {
        this.onResultCallback = callback;
        if (!VerifierUtils.isAdminActive(context)) {
            if (onResultCallback != null && onResultCallback instanceof OnDialogResultCallback) {
                OnDialogResultCallback onDialogResultCallback = (OnDialogResultCallback) onResultCallback;
                onDialogResultCallback.showRequestDialog(new OnLaunchCallback() {
                    @Override
                    public void allowLaunch() {
                        getSpecialLauncher(BIND_DEVICE_ADMIN).launch(null);
                    }
                });
            } else {
                getSpecialLauncher(BIND_DEVICE_ADMIN).launch(null);
            }
            return;
        }
        if (onResultCallback != null) {
            onResultCallback.onPermissionGranted();
        }
    }

    /**
     * android.permission.ACCESS_NOTIFICATION_POLICY
     *
     * @param callback
     */
    public void launchAccessNotificationPolicy(OnResultCallback callback) {
        this.onResultCallback = callback;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!areNotificationsEnabled(context)) {
                if (onResultCallback != null && onResultCallback instanceof OnDialogResultCallback) {
                    OnDialogResultCallback onDialogResultCallback = (OnDialogResultCallback) onResultCallback;
                    onDialogResultCallback.showRequestDialog(new OnLaunchCallback() {
                        @Override
                        public void allowLaunch() {
                            getSpecialLauncher(ACCESS_NOTIFICATION_POLICY).launch(null);
                        }
                    });
                } else {
                    getSpecialLauncher(ACCESS_NOTIFICATION_POLICY).launch(null);
                }
                return;
            }
        }
        if (onResultCallback != null) {
            onResultCallback.onPermissionGranted();
        }
    }

    private ActivityResultLauncher<Void> getSpecialLauncher(String permission) {
        ActivityResultLauncher<Void> launcher = null;
        if (specialLauncherMap != null) {
            if (specialLauncherMap.containsKey(permission)) {
                launcher = specialLauncherMap.get(permission);
            }
        }
        if (launcher == null) {
            throw new IllegalArgumentException("you must register this permission first!");
        }
        return launcher;
    }


    /**
     * 权限申请回调接口
     */
    public interface OnResultCallback {

        void onPermissionGranted();

        void onPermissionsDenied(String... permissions);
    }


    public interface OnDialogResultCallback extends OnResultCallback {

        void showRequestDialog(OnLaunchCallback onLaunchCallback);

    }

    public interface OnLaunchCallback {
        void allowLaunch();
    }
}
