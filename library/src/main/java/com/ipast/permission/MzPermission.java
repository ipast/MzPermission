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

import com.ipast.permission.callback.DialogPermissionRequestCallback;
import com.ipast.permission.callback.PermissionCheckCallback;
import com.ipast.permission.callback.PermissionLaunchCallback;
import com.ipast.permission.callback.PermissionRequestCallback;
import com.ipast.permission.callback.PermissionRequestResultCallback;
import com.ipast.permission.contract.AccessNotificationPolicyResultContract;
import com.ipast.permission.contract.AccessibilityServiceResultContract;
import com.ipast.permission.contract.AppManagerExternalStorageResultContract;
import com.ipast.permission.contract.BindDeviceAdminResultContract;
import com.ipast.permission.contract.LocationSourceSettingsResultContract;
import com.ipast.permission.contract.RequestInstallPackagesResultContract;
import com.ipast.permission.contract.SystemAlertWindowResultContract;
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
import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;
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

    private ActivityResultLauncher<String[]> mNormalLauncher;
    private HashMap<String, ActivityResultLauncher<Void>> mSpecialLaunchers;

    private PermissionRequestResultCallback mPermissionRequestResultCallback;
    private ActivityResultCallback<Boolean> mSpecialCallback;


    public MzPermission(@NonNull ComponentActivity activity) {
        this.mCaller = activity;
        this.mContext = activity;
    }

    public MzPermission(@NonNull Fragment fragment) {
        this.mCaller = fragment;
        this.mContext = fragment.getContext();
    }

    private String[] mNormalPermissions;

    public MzPermission request(@NonNull String... permissions) {
        this.mNormalPermissions = permissions;
        return this;
    }

    private void registerForActivityResult(@NonNull String permission) {
        ActivityResultLauncher<Void> launcher = getActivityResultLauncher(permission);
        addActivityResultLauncher(permission, launcher);
    }

    private void addActivityResultLauncher(@NonNull String permission, ActivityResultLauncher<Void> launcher) {
        if (launcher == null) {
            return;
        }
        if (mSpecialLaunchers == null) {
            mSpecialLaunchers = new HashMap<>();
        }
        mSpecialLaunchers.put(permission, launcher);
    }

    public void registerForActivityResult(@NonNull String permission, ActivityResultContract<Void, Boolean> contract) {
        ActivityResultLauncher<Void> launcher = registerForActivityResult(contract);
        addActivityResultLauncher(permission, launcher);
    }

    private ActivityResultLauncher<Void> getActivityResultLauncher(String permission) {
        ActivityResultContract<Void, Boolean> contract = getActivityResultContract(permission);
        if (contract == null) {
            return null;
        }
        return registerForActivityResult(contract);
    }

    private ActivityResultLauncher<Void> registerForActivityResult(ActivityResultContract<Void, Boolean> contract) {
        if (mSpecialCallback == null) {
            mSpecialCallback = new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    execRequestPermissionResultCallback(result, null);
                }
            };
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
            case SYSTEM_ALERT_WINDOW:
                return new SystemAlertWindowResultContract();
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
        if (mPermissionRequestResultCallback == null) {
            return;
        }
        if (isGranted) {
            mPermissionRequestResultCallback.onPermissionGranted();
            return;
        }
        mPermissionRequestResultCallback.onPermissionsDenied(deniedPermissions);
    }

    public MzPermission registerSystemAlertWindow() {
        registerForActivityResult(SYSTEM_ALERT_WINDOW);
        return this;
    }

    public MzPermission registerLocationSourceSettings() {
        registerForActivityResult(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
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
        registerForActivityResult(BIND_ACCESSIBILITY_SERVICE);
        return this;
    }

    /**
     * Permission is only granted to system app
     * android.permission.WRITE_SETTINGS
     *
     * @return
     */
    public MzPermission registerWriteSettings() {
        registerForActivityResult(WRITE_SETTINGS);
        return this;
    }

    /**
     * android.permission.BIND_DEVICE_ADMIN
     *
     * @return
     */
    public MzPermission registerBindDeviceAdmin() {
        registerForActivityResult(BIND_DEVICE_ADMIN);
        return this;
    }

    /**
     * android.permission.REQUEST_INSTALL_PACKAGES
     *
     * @return
     */
    public MzPermission registerRequestInstallPackages() {
        registerForActivityResult(REQUEST_INSTALL_PACKAGES);
        return this;
    }

    /**
     * android.permission.MANAGE_EXTERNAL_STORAGE
     *
     * @return
     */
    public MzPermission registerManagerExternalStorage() {
        registerForActivityResult(MANAGE_EXTERNAL_STORAGE);
        return this;
    }

    /**
     * android.permission.ACCESS_NOTIFICATION_POLICY
     *
     * @return
     */
    public MzPermission registerAccessNotificationPolicy() {
        registerForActivityResult(ACCESS_NOTIFICATION_POLICY);
        return this;
    }


    /**
     * 非特殊权限申请
     *
     * @param callback
     */
    public void launch(PermissionRequestResultCallback callback) {
        this.mPermissionRequestResultCallback = callback;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mNormalLauncher.launch(mNormalPermissions);
            return;
        }
        if (mPermissionRequestResultCallback != null) {
            mPermissionRequestResultCallback.onPermissionGranted();
        }
    }

    public void launchSystemAlertWindow(PermissionRequestResultCallback callback) {
        checkSelfPermission(callback, new PermissionCheckCallback() {
            @Override
            public boolean checkPermission() {
                return PermissionCheckUtils.canDrawOverlays(mContext);
            }
        }, new PermissionLaunchCallback() {
            @Override
            public void requestPermission() {
                launchPermissionRequest(SYSTEM_ALERT_WINDOW);
            }
        });
    }

    /**
     * Show settings to allow configuration of current location sources.
     *
     * @param callback
     */
    public void launchLocationSourceSettings(PermissionRequestResultCallback callback) {
        checkSelfPermission(callback, new PermissionCheckCallback() {
            @Override
            public boolean checkPermission() {
                return isGPSProviderEnabled(mContext);
            }
        }, new PermissionLaunchCallback() {
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
    public void launchRequestInstallPackages(PermissionRequestResultCallback callback) {
        checkSelfPermission(callback, new PermissionCheckCallback() {
            @Override
            public boolean checkPermission() {
                return canRequestPackageInstalls(mContext);
            }
        }, new PermissionLaunchCallback() {
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
    public void launchWriteSettings(PermissionRequestResultCallback callback) {
        checkSelfPermission(callback, new PermissionCheckCallback() {
            @Override
            public boolean checkPermission() {
                return canWrite(mContext);
            }
        }, new PermissionLaunchCallback() {
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
    public void launchManagerExternalStorage(PermissionRequestResultCallback callback) {
        checkSelfPermission(callback, new PermissionCheckCallback() {
            @Override
            public boolean checkPermission() {
                return isExternalStorageManager();
            }
        }, new PermissionLaunchCallback() {

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
    public void launchExternalStorage(PermissionRequestResultCallback callback) {
        this.mPermissionRequestResultCallback = callback;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            launchManagerExternalStorage(callback);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            request(EXTERNAL_STORAGE).launch(callback);
            return;
        }
        if (mPermissionRequestResultCallback != null) {
            mPermissionRequestResultCallback.onPermissionGranted();
        }
    }


    /**
     * android.permission.BIND_DEVICE_ADMIN
     *
     * @param callback
     */
    public void launchBindDeviceAdmin(PermissionRequestResultCallback callback) {
        checkSelfPermission(callback, new PermissionCheckCallback() {
            @Override
            public boolean checkPermission() {
                return PermissionCheckUtils.isAdminActive(mContext);
            }
        }, new PermissionLaunchCallback() {
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
    public void launchAccessNotificationPolicy(PermissionRequestResultCallback callback) {
        checkSelfPermission(callback, new PermissionCheckCallback() {
            @Override
            public boolean checkPermission() {
                return areNotificationsEnabled(mContext);
            }
        }, new PermissionLaunchCallback() {
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
    public void launchAccessibilityService(PermissionRequestResultCallback callback) {
        checkSelfPermission(callback, new PermissionCheckCallback() {
            @Override
            public boolean checkPermission() {
                return PermissionCheckUtils.accessibilityServiceEnabled(mContext, mAccessibilityServiceClz);
            }
        }, new PermissionLaunchCallback() {
            @Override
            public void requestPermission() {
                launchPermissionRequest(BIND_ACCESSIBILITY_SERVICE);
            }
        });
    }

    private void checkSelfPermission(PermissionRequestResultCallback permissionRequestResultCallback,
                                     PermissionCheckCallback permissionCheckCallback,
                                     @NonNull PermissionLaunchCallback permissionLaunchCallback) {
        this.mPermissionRequestResultCallback = permissionRequestResultCallback;
        if (permissionCheckCallback != null && permissionCheckCallback.checkPermission()) {
            if (mPermissionRequestResultCallback != null) {
                mPermissionRequestResultCallback.onPermissionGranted();
            }
            return;
        }
        permissionLaunchCallback.requestPermission();
    }

    public void launchPermissionRequest(String permission, PermissionRequestResultCallback permissionRequestResultCallback) {
        launchPermissionRequest(permission, null, permissionRequestResultCallback);
    }

    public void launchPermissionRequest(String permission, @NonNull PermissionCheckCallback checkCallback,
                                        PermissionRequestResultCallback permissionRequestResultCallback) {
        checkSelfPermission(permissionRequestResultCallback, checkCallback, new PermissionLaunchCallback() {
            @Override
            public void requestPermission() {
                launchPermissionRequest(permission);
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
        if (mPermissionRequestResultCallback != null && mPermissionRequestResultCallback instanceof DialogPermissionRequestCallback) {
            DialogPermissionRequestCallback dialogResultCallback = (DialogPermissionRequestCallback) mPermissionRequestResultCallback;
            dialogResultCallback.showRequestDialog(callback);
            return;
        }
        callback.allowRequest();
    }


}
