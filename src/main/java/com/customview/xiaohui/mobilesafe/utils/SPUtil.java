package com.customview.xiaohui.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wizardev on 2016/12/16.
 */

public class SPUtil {
    public static void putString(Context context,String key,String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyConstants.USERINFO,Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key,value).apply();
    }
    public static String getString(Context context,String key,String defValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyConstants.USERINFO,Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,defValue);
    }
    public static void putBoolen(Context context,String key,boolean value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyConstants.USERINFO,Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(key,value).apply();
    }
    public static boolean getBoolen(Context context,String key,boolean defValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyConstants.USERINFO,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key,defValue);
    }
}
