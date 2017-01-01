package com.customview.xiaohui.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by wizardev on 2016/12/30.
 */

public class RunningTaskBean {
    private String appName;
    private Drawable appIcon;
    private boolean isSysApp;
    private long appRunningSize;
    private String packageName;
    private boolean isChecked;

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

    public long getAppRunningSize() {
        return appRunningSize;
    }

    public void setAppRunningSize(long appRunningSize) {
        this.appRunningSize = appRunningSize;
    }

    public boolean isSysApp() {
        return isSysApp;
    }

    public void setSysApp(boolean sysApp) {
        isSysApp = sysApp;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String toString() {
        return "RunningTaskBean{" +
                "appIcon=" + appIcon +
                ", appName='" + appName + '\'' +
                ", isSysApp=" + isSysApp +
                ", appRunningSize=" + appRunningSize +
                ", packageName='" + packageName + '\'' +
                '}';
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
