# MzPermission
 简易的Android权限申请工具
## 添加依赖
To use this library your minSdkVersion must be >= 21.
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.ipast:MzPermission:0.0.8'
}
```
## 使用说明
### 初始化
在Activity onCreate()中初始化并注册需要的权限申请:
```
private MzPermission mzPermission;

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

    //注册自定义的特殊权限申请
     mzPermission.registerForActivityResult(SPECIAL_PERMISSION, new CustomSpecialPermissionResultContract());
}
```
CustomSpecialPermissionResultContract继承ActivityResultContract<Void, Boolean>:

```
public class CustomSpecialPermissionResultContract extends ActivityResultContract<Void, Boolean> {
  
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Void input) {

        return null;
    }

    @Override
    public Boolean parseResult(int resultCode, @Nullable Intent intent) {
        return false;
    }
}
```
### 权限申请
#### 普通权限申请 ：
```
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

```
#### 特殊权限申请：
需要跳到指定界面手动打开

不需要添加dialog提醒：
```
  mzPermission.launchBindDeviceAdmin(new PermissionRequestResultCallback() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionsDenied(String... permissions) {

            }
        });
```
添加dialog提醒：

在Dialog确定按钮回调方法中调用 PermissionRequestCallback.allowRequest()来允许工具申请权限
```
 mzPermission.launchSystemAlertWindow(new DialogPermissionRequestCallback() {
                   @Override
                    public void showRequestDialog(PermissionRequestCallback permissionRequestCallback) {
                       MessageDialog.show("权限申请", "需要悬浮窗口权限，请手动开启！", "确定", "取消")
                                   .setOkButton(new OnDialogButtonClickListener<MessageDialog>() {
                                    @Override
                                    public boolean onClick(MessageDialog baseDialog, View v) {
                                        //Dialog点击确认按钮，允许申请权限
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

``` 
#### 自定义特殊权限申请：
是否添加dialog提醒，同上特殊权限申请

添加权限校验：

PermissionCheckCallback chekcPrmission()为当前权限校验回调

```
  mzPermission.launchPermissionRequest(SPECIAL_PERMISSION, new PermissionCheckCallback() {
                //权限检验，PermissionCheckCallback可为null
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
```

不需要权限校验：

默认权限未允许，申请时直接跳转到指定权限打开界面

```
  mzPermission.launchPermissionRequest(SPECIAL_PERMISSION,new DialogPermissionRequestCallback() {
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
```















