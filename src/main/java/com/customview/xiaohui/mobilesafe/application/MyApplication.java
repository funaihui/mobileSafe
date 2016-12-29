package com.customview.xiaohui.mobilesafe.application;

import android.app.Application;

import org.xutils.x;

import cn.sharesdk.framework.ShareSDK;

/**
 * Created by wizardev on 2016/12/16.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ShareSDK.initSDK(this);
        x.Ext.init(this);

    }
}
