package com.customview.xiaohui.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by wizardev on 2016/12/29.
 */

public class AppInfoBean {
    private String appName;
    private Drawable appIcon;
    private boolean isSysApp;
    private boolean isSD;
    private long appSize;
    private String packageName;
    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getAppSize() {
        return appSize;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    public boolean isSD() {
        return isSD;
    }

    public void setSD(boolean SD) {
        isSD = SD;
    }

    public boolean isSysApp() {
        return isSysApp;
    }

    public void setSysApp(boolean sysApp) {
        isSysApp = sysApp;
    }

    @Override
    public String toString() {
        return "AppInfoBean{" +
                "appIcon=" + appIcon +
                ", appName='" + appName + '\'' +
                ", isSysApp=" + isSysApp +
                ", isSD=" + isSD +
                ", appSize=" + appSize +
                '}';
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
