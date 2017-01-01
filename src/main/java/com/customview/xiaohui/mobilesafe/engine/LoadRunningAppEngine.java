package com.customview.xiaohui.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Debug;
import android.util.Log;

import com.customview.xiaohui.mobilesafe.domain.RunningTaskBean;
import com.jaredrummler.android.processes.ProcessManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wizardev on 2016/12/30.
 */

public class LoadRunningAppEngine {
    public static long getTotalMem(Context context) {
        long allMem = 0;
        //这种方法获取运行内存，要求的API版本太高，不宜采取
       /* ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long totalMem = memoryInfo.totalMem;*/
        /**
         * 从配置文件中读取内存大小
         */
        File file = new File("/proc/meminfo");
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String totalMem = bf.readLine();
            int start = totalMem.indexOf(":");
            int end = totalMem.indexOf("k");
            totalMem = totalMem.substring(start+1, end).trim();
            allMem = Long.valueOf(totalMem) * 1024;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allMem;
    }

    public static long getFreeMem(Context context) {
        long freeMem = 0;
        //获得可用运行内存
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        freeMem = memoryInfo.availMem;
        return freeMem;
    }

    public static List<RunningTaskBean> getRunningAppInfo(Context context) {
        List<RunningTaskBean> list = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //安卓5.0以上需要特殊的类获取运行的进程
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = ProcessManager.getRunningAppProcessInfo(context);
            Log.i("Wizardev", "getRunningAppInfo: "+runningAppProcessInfos.size());
            getProcessInfo(list, packageManager, activityManager, runningAppProcessInfos);
        }else {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();


            getProcessInfo(list, packageManager, activityManager, runningAppProcesses);
        }
        return list;
    }

    private static void getProcessInfo(List<RunningTaskBean> list, PackageManager packageManager, ActivityManager activityManager, List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos) {
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo: runningAppProcessInfos) {
            RunningTaskBean bean = new RunningTaskBean();
            String processName = runningAppProcessInfo.processName;//获得包名
            bean.setPackageName(processName);
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(processName, 0);
                CharSequence charSequence = packageInfo.applicationInfo.loadLabel(packageManager);
                bean.setAppName((String) charSequence);
                bean.setAppIcon(packageInfo.applicationInfo.loadIcon(packageManager));
                int flags = packageInfo.applicationInfo.flags;
                if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    bean.setSysApp(true);
                } else {
                    bean.setSysApp(false);
                }
            } catch (PackageManager.NameNotFoundException e) {
                continue;
                // e.printStackTrace();
            }
            Debug.MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
            long size = memoryInfo[0].getTotalPrivateDirty() * 1024;
            bean.setAppRunningSize(size);
            list.add(bean);
        }
    }
}