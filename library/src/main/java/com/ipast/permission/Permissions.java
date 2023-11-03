package com.ipast.permission;

import android.Manifest;
import android.os.Build;

/**
 * author:gang.cheng
 * description:
 * date:2023/10/30
 */
public class Permissions {
    public static final String[] EXTERNAL_STORAGE = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
}
