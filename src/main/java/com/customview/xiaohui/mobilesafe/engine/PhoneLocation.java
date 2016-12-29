package com.customview.xiaohui.mobilesafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wizardev on 2016/12/25.
 */

public class PhoneLocation {

    public static String getCategray(String num) {
        String location = "";
        Pattern p = Pattern.compile("1{1}[34857]{1}[0-9]{9}");
        Matcher m = p.matcher(num);
        boolean b = m.matches();
        if (b) {
            location = getMobileLocation(num);
        } else if (num.length() >= 11) {
            location = getTelPhone(num);
        } else {

        }
        return location;
    }

    private static String getTelPhone(String num) {
        String location = "";
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase("/data/data/com.customview.xiaohui.mobilesafe/files/address.db",
                null, SQLiteDatabase.OPEN_READONLY);
        String quhao = "";

        if (num.charAt(1) == 1 || num.charAt(1) == 2) {
            quhao = num.substring(1, 3);
        } else {
            quhao = num.substring(1, 4);
        }
        Cursor data2 = sqLiteDatabase.query("data2", new String[]{"location"}, "area=?", new String[]{quhao}, null, null, null);
        if (data2.moveToNext()) {
            location = data2.getString(0);
        }
        return location;
    }

    private static String getMobileLocation(String phone) {
        String location = "";
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase("/data/data/com.customview.xiaohui.mobilesafe/files/address.db",
                null, SQLiteDatabase.OPEN_READONLY);
        phone = phone.substring(0,7);
        Cursor data1 = sqLiteDatabase.query("data1", new String[]{"outkey"}, "id=?", new String[]{phone}, null, null, null);
        if (data1.moveToNext()) {
            String outkey = data1.getString(0);
            Cursor data2 = sqLiteDatabase.query("data2", new String[]{"location"}, "id=?", new String[]{outkey}, null, null, null);
            if (data2.moveToNext()) {
                location = data2.getString(0);
            }
            data2.close();
        }
        data1.close();
        sqLiteDatabase.close();
        return location;
    }
}
