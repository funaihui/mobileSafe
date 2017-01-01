package com.customview.xiaohui.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.customview.xiaohui.mobilesafe.domain.AppInfoBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wizardev on 2016/12/29.
 */

public class LoadAllSoftwareEngine {

    public static long getSDAvailable(){
        long SDAvailable = 0;
        SDAvailable = Environment.getExternalStorageDirectory().getFreeSpace();
        return SDAvailable;
    }
    public static long getRomAvailable(){
        long RomAvailable = 0;
        RomAvailable = Environment.getDataDirectory().getFreeSpace();
        return RomAvailable;
    }

    /**
     * 获得本机的所有应用，并封装AppInfoBean中
     * @param context
     * @return
     */
    public static List<AppInfoBean> loadAllSoftware(Context context){
        List<AppInfoBean> beanList = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo:packageInfoList) {
            AppInfoBean bean = new AppInfoBean();
            bean.setAppName((String) packageInfo.applicationInfo.loadLabel(packageManager));
            bean.setAppIcon(packageInfo.applicationInfo.loadIcon(packageManager));
            bean.setPackageName(packageInfo.packageName);
            String sourceDir = packageInfo.applicationInfo.sourceDir;//得到app的大小
            File file = new File(sourceDir);
            long length = file.length();
            bean.setAppSize(length);
            int flags = packageInfo.applicationInfo.flags;
            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0){
                bean.setSD(true);
            }else {
                bean.setSD(false);
            }
            if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0){
                bean.setSysApp(true);
            }else {
                bean.setSysApp(false);
            }

            String dir = packageInfo.applicationInfo.sourceDir;
            bean.setApkPath(dir);
            beanList.add(bean);
        }
        return beanList;
    }
}
