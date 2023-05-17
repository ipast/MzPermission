package com.ipast.mzpermission;

import android.app.Application;

import com.kongzue.dialogx.DialogX;

/**
 * author:gang.cheng
 * description:
 * date:2023/5/17
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DialogX.init(this);
    }
}
