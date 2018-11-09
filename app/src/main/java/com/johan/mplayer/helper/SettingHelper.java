package com.johan.mplayer.helper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

/**
 * Created by johan on 2018/4/3.
 */

public class SettingHelper {

    // 包名
    private static final String SCHEME = "package";
    // 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    // 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
    private static final String APP_PKG_NAME_22 = "pkg";
    // InstalledAppDetails所在包名
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    // InstalledAppDetails类名
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

    /**
     * 跳转到Setting页面
     * @param activity
     */
    public static void goSetting(Activity activity) {
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) {
            // 2.3（ApiLevel 9）以上，使用SDK提供的接口
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, activity.getPackageName(), null);
            intent.setData(uri);
        } else {
            // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
            // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, activity.getPackageName());
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

}
