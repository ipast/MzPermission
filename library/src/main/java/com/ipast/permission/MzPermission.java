package com.ipast.permission;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ipast.permission.callback.DialogRequestResultCallback;
import com.ipast.permission.callback.PermissionCheckCallback;
import com.ipast.permission.callback.PermissionRequestCallback;
import com.ipast.permission.callback.RequestResultCallback;
import com.ipast.permission.contract.AccessNotificationPolicyResultContract;
import com.ipast.permission.contract.AccessibilityServiceResultContract;
import com.ipast.permission.contract.AppManagerExternalStorageResultContract;
import com.ipast.permission.contract.BindDeviceAdminResultContract;
import com.ipast.permission.contract.LocationSourceSettingsResultContract;
import com.ipast.permission.contract.RequestInstallPackagesResultContract;
import com.ipast.permission.contract.WriteSettingsResultContract;
import com.ipast.permission.utils.PermissionCheckUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_NOTIFICATION_POLICY;
import static android.Manifest.permission.BIND_ACCESSIBILITY_SERVICE;
import static android.Manifest.permission.BIND_DEVICE_ADMIN;
import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.REQUEST_INSTALL_PACKAGES;
import static android.Manifest.permission.WRITE_SETTINGS;
import static com.ipast.permission.utils.PermissionCheckUtils.areNotificationsEnabled;
import static com.ipast.permission.utils.PermissionCheckUtils.canRequestPackageInstalls;
import static com.ipast.permission.utils.PermissionCheckUtils.canWrite;
import static com.ipast.permission.utils.PermissionCheckUtils.isExternalStorageManager;
import static com.ipast.permission.utils.PermissionCheckUtils.isGPSProviderEnabled;
import static com.ipast.permission.Permissions.EXTERNAL_STORAGE;


/**
 * author:gang.cheng
 * description:权限申请工具；
 * date:2021/12/1
 */
public class MzPermission {
    private final String TAG = getClass().getSimpleName();
    private ActivityResultCaller mCaller;
    private Context mContext;

    private String[] mNormalPermissions;

    private ActivityResultLauncher<String[]> mNormalLauncher;
    private HashMap<String, ActivityResultLauncher<Void>> mSpecialLaunchers;

    private RequestResultCallback mRequestResultCallback;
    private ActivityResultCallback<Boolean> mSpecialCallback;


    public MzPermission(@NonNull ComponentActivity activity) {
        this.mCaller = activity;
        this.mContext = activity;
    }

    public MzPermission(@NonNull Fragment fragment) {
        this.mCaller = fragment;
        this.mContext = fragment.getContext();
    }

    public MzPermission request(@NonNull String... permissions) {
        this.mNormalPermissions = permissions;
        return this;
    }


    private void addActivityResultLauncher(@NonNull String specialPermission) {
        ActivityResultLauncher<Void> launcher = getActivityResultLauncher(specialPermission);
        if (launcher == null) {
            return;
        }
        mSpecialLaunchers.put(specialPermission, launcher);
    }

    private ActivityResultLauncher<Void> getActivityResultLauncher(String permission) {
        ActivityResultContract<Void, Boolean> contract = getActivityResultContract(permission);
        if (contract == null) {
            return null;
        }
        return mCaller.registerForActivityResult(contract, mSpecialCallback);
    }

    private ActivityResultContract<Void, Boolean> getActivityResultContract(String permission) {
        switch (permission) {
            case MANAGE_EXTERNAL_STORAGE:
                return new AppManagerExternalStorageResultContract();
            case WRITE_SETTINGS:
                return new WriteSettingsResultContract();
            case BIND_DEVICE_ADMIN:
                return new BindDeviceAdminResultContract();
            case ACCESS_NOTIFICATION_POLICY:
                return new AccessNotificationPolicyResultContract();
            case REQUEST_INSTALL_PACKAGES:
                return new RequestInstallPackagesResultContract();
            case BIND_ACCESSIBILITY_SERVICE:
                return new AccessibilityServiceResultContract(mAccessibilityServiceClz);
            case Settings.ACTION_LOCATION_SOURCE_SETTINGS:
                return new LocationSourceSettingsResultContract();
            default:
                Log.d(TAG, "unknown permission : " + permission);
                return null;
        }
    }

    private ActivityResultLauncher<Void> getSpecialLauncher(String permission) {
        ActivityResultLauncher<Void> launcher = null;
        if (mSpecialLaunchers != null) {
            if (mSpecialLaunchers.containsKey(permission)) {
                launcher = mSpecialLaunchers.get(permission);
            }
        }
        if (launcher == null) {
            throw new IllegalArgumentException("you must register this permission first!");
        }
        return launcher;
    }


    /**
     * 注册普通权限申请
     *
     * @return
     */
    public MzPermission registerForActivityResult() {
        this.mNormalLauncher = mCaller.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> result) {
                        execRequestPermissionResult(result);
                    }
                });
        return this;
    }

    private void execRequestPermissionResult(Map<String, Boolean> result) {
        List<String> deniedPermissionList = new ArrayList();
        for (Map.Entry<String, Boolean> entry : result.entrySet()) {
            if (entry.getValue()) {
                continue;
            }
            deniedPermissionList.add(entry.getKey());
        }
        int deniedPermissionSize = deniedPermissionList.size();
        boolean isGranted = deniedPermissionSize == 0;
        String[] deniedPermissions = deniedPermissionList.toArray(new String[deniedPermissionSize]);
        execRequestPermissionResultCallback(isGranted, deniedPermissions);
    }

    private void execRequestPermissionResultCallback(boolean isGranted, String[] deniedPermissions) {
        if (mRequestResultCallback == null) {
            return;
        }
        if (isGranted) {
            mRequestResultCallback.onPermissionGranted();
            return;
        }
        mRequestResultCallback.onPermissionsDenied(deniedPermissions);
    }

    private void combineActivityResultLauncher(@NonNull String specialPermission) {
        if (mSpecialLaunchers == null) {
            mSpecialLaunchers = new HashMap<>();
        }
        if (mSpecialCallback == null) {
            mSpecialCallback = new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    execRequestPermissionResultCallback(result, null);
                }
            };
        }
        addActivityResultLauncher(specialPermission);
    }

    public MzPermission registerLocationSourceSettings() {
        combineActivityResultLauncher(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        return this;
    }


    private Class<? extends AccessibilityService> mAccessibilityServiceClz;


    /**
     * android.permission.BIND_ACCESSIBILITY_SERVICE
     *
     * @param accessibilityServiceClz
     * @return
     */
    public MzPermission registerAccessibilityService(@NonNull Class<? extends AccessibilityService> accessibilityServiceClz) {
        this.mAccessibilityServiceClz = accessibilityServiceClz;
        combineActivityResultLauncher(BIND_ACCESSIBILITY_SERVICE);
        return this;
    }

    /**
     * Permission is only granted to system app
     * android.permission.WRITE_SETTINGS
     *
     * @return
     */
    public MzPermission registerWriteSettings() {
        combineActivityResultLauncher(WRITE_SETTINGS);
        return this;
    }

    /**
     * android.permission.BIND_DEVICE_ADMIN
     *
     * @return
     */
    public MzPermission registerBindDeviceAdmin() {
        combineActivityResultLauncher(BIND_DEVICE_ADMIN);
        return this;
    }

    /**
     * android.permission.REQUEST_INSTALL_PACKAGES
     *
     * @return
     */
    public MzPermission registerRequestInstallPackages() {
        combineActivityResultLauncher(REQUEST_INSTALL_PACKAGES);
        return this;
    }

    /**
     * android.permission.MANAGE_EXTERNAL_STORAGE
     *
     * @return
     */
    public MzPermission registerManagerExternalStorage() {
        combineActivityResultLauncher(MANAGE_EXTERNAL_STORAGE);
        return this;
    }

    /**
     * android.permission.ACCESS_NOTIFICATION_POLICY
     *
     * @return
     */
    public MzPermission registerAccessNotificationPolicy() {
        combineActivityResultLauncher(ACCESS_NOTIFICATION_POLICY);
        return this;
    }


    /**
     * 非特殊权限申请
     *
     * @param callback
     */
    public void launch(RequestResultCallback callback) {
        this.mRequestResultCallback = callback;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mNormalLauncher.launch(mNormalPermissions);
            return;
        }
        if (mRequestResultCallback != null) {
            mRequestResultCallback.onPermissionGranted();
        }
    }

    /**
     * Show settings to allow configuration of current location sources.
     *
     * @param callback
     */
    public void launchLocationSourceSettings(RequestResultCallback callback) {
        checkPermission(callback, new PermissionCheckCallback() {
            @Override
            public boolean checkPermission() {
                return isGPSProviderEnabled(mContext);
            }

            @Override
            public void requestPermission() {
                launchPermissionRequest(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            }
        });
    }

    /**
     * android.permission.REQUEST_INSTALL_PACKAGES
     *
     * @param callback
     */
    public void launchRequestInstallPackages(RequestResultCallback callback) {
        checkPermission(callback, new PermissionCheckCallback() {
            @Override
            public boolean checkPermission() {
                return canRequestPackageInstalls(mContext);
            }

            @Override
            public void requestPermission() {
                launchPermissionRequest(REQUEST_INSTALL_PACKAGES);
            }
        });
    }

    /**
     * Permission is only granted to system app
     * android.permission.WRITE_SETTINGS
     *
     * @param callback
     */
    public void launchWriteSettings(RequestResultCallback callback) {
        checkPermission(callback, new PermissionCheckCallback() {
            @Override
            public boolean checkPermission() {
                return canWrite(mContext);
            }

            @Override
            public void requestPermission() {
                launchPermissionRequest(WRITE_SETTINGS);
            }
        });
    }

    /**
     * android.permission.MANAGE_EXTERNAL_STORAGE
     *
     * @param callback
     */
    public void launchManagerExternalStorage(RequestResultCallback callback) {
        checkPermission(callback, new PermissionCheckCallback() {
            @Override
            public boolean checkPermission() {
                return isExternalStorageManager();
            }

            @Override
            public void requestPermission() {
                launchPermissionRequest(MANAGE_EXTERNAL_STORAGE);
            }
        });
    }


    /**
     * android.permission.MANAGE_EXTERNAL_STORAGE
     * android.permission.READ_EXTERNAL_STORAGE
     * android.permission.WRITE_EXTERNAL_STORAGE
     *
     * @param callback
     */
    public void launchExternalStorage(RequestResultCallback callback) {
        this.mRequestResultCallback = callback;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            launchManagerExternalStorage(callback);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            request(EXTERNAL_STORAGE).launch(callback);
            return;
        }
        if (mRequestResultCallback != null) {
            mRequestResultCallback.onPermissionGranted();
        }
    }


    /**
     * android.permission.BIND_DEVICE_ADMIN
     *
     * @param callback
     */
    public void launchBindDeviceAdmin(RequestResultCallback callback) {
        checkPermission(callback, new PermissionCheckCallback() {
            @Override
            public boolean checkPermission() {
                return PermissionCheckUtils.isAdminActive(mContext);
            }

            @Override
            public void requestPermission() {
                launchPermissionRequest(BIND_DEVICE_ADMIN);
            }
        });

    }

    /**
     * android.permission.ACCESS_NOTIFICATION_POLICY
     *
     * @param callback
     */
    public void launchAccessNotificationPolicy(RequestResultCallback callback) {
        checkPermission(callback, new PermissionCheckCallback() {
            @Override
            public boolean checkPermission() {
                return areNotificationsEnabled(mContext);
            }

            @Override
            public void requestPermission() {
                launchPermissionRequest(ACCESS_NOTIFICATION_POLICY);
            }
        });
    }

    /**
     * android.permission.BIND_ACCESSIBILITY_SERVICE
     *
     * @param callback
     */
    public void launchAccessibilityService(RequestResultCallback callback) {
        checkPermission(callback, new PermissionCheckCallback() {
            @Override
            public boolean checkPermission() {
                return PermissionCheckUtils.accessibilityServiceEnabled(mContext, mAccessibilityServiceClz);
            }

            @Override
            public void requestPermission() {
                launchPermissionRequest(BIND_ACCESSIBILITY_SERVICE);
            }
        });
    }

    private void launchPermissionRequest(String permission) {
        launchPermissionRequest(new PermissionRequestCallback() {
            @Override
            public void allowRequest() {
                getSpecialLauncher(permission).launch(null);
            }
        });
    }


    private void launchPermissionRequest(PermissionRequestCallback callback) {
        if (mRequestResultCallback != null && mRequestResultCallback instanceof DialogRequestResultCallback) {
            DialogRequestResultCallback dialogResultCallback = (DialogRequestResultCallback) mRequestResultCallback;
            dialogResultCallback.showRequestDialog(callback);
            return;
        }
        callback.allowRequest();
    }


    private void checkPermission(RequestResultCallback requestResultCallback, @NonNull PermissionCheckCallback checkCallback) {
        this.mRequestResultCallback = requestResultCallback;
        if (checkCallback.checkPermission()) {
            if (mRequestResultCallback != null) {
                mRequestResultCallback.onPermissionGranted();
            }
            return;
        }
        checkCallback.requestPermission();
    }

}
