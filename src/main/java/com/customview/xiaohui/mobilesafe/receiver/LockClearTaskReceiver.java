package com.customview.xiaohui.mobilesafe.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.customview.xiaohui.mobilesafe.domain.RunningTaskBean;
import com.customview.xiaohui.mobilesafe.engine.LoadRunningAppEngine;

import java.util.List;

/**
 * Created by wizardev on 2017/1/1.
 */

 public class LockClearTaskReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskBean> runningAppInfos = LoadRunningAppEngine.getRunningAppInfo(context);
        for (RunningTaskBean runningAppInfo : runningAppInfos) {
            mActivityManager.killBackgroundProcesses(runningAppInfo.getPackageName());
            Log.i("Wizardev", "onReceive: 锁屏清理进程"+runningAppInfo.getPackageName());
        }
        Toast.makeText(context,"清理完成",Toast.LENGTH_SHORT).show();
    }
}