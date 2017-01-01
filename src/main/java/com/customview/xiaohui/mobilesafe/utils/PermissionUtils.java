package com.customview.xiaohui.mobilesafe.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wizardev on 2016/12/28.
 */

public class PermissionUtils {
    static List<Activity> activityLists = new ArrayList<>();
    public static void addActivity(Activity activity){
        activityLists.add(activity);
    }
    public static void removeAcrivity(Activity activity){
        activityLists.remove(activity);
    }
    public static Activity getTopActivity(){
        if (activityLists.isEmpty()){
            return null;
        }else {
            Activity TopActivity = activityLists.get(activityLists.size() - 1);
            return TopActivity;
        }
    }
}
