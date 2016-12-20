package com.customview.xiaohui.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by wizardev on 2016/12/18.
 */

public class ServiceUtils {
    public static boolean isRunningService(Context context, String service) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(50);

        for (ActivityManager.RunningServiceInfo serviceInfo : runningServices) {
            if (serviceInfo.service.getClassName().equals(service)) {
                isRunning = true;
                break;
            }

        }
        return isRunning;
    }
}
