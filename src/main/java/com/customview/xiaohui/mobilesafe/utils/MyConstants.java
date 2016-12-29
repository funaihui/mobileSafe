package com.customview.xiaohui.mobilesafe.utils;

/**
 * Created by wizardev on 2016/12/16.
 */

public interface MyConstants {
    String USERINFO = "userinfo";
    String PASSWORD = "password";
    String SIM = "sim";
    String SAVENUM = "savenum";
    String ISSTEUP = "issetup";
    String ISRUNNING = "isrunning";
    String ISAUTOUPDATE = "isautoupdate";
    String TABLENAME = "blacklist";
    String PHONE = "phone";
    String MODE = "mode";
    String TOASTX = "toastX";
    String TOASTY = "toastY";
    int SEED = 110;
    int TEL = 1 <<0;
    int SMS = 1 << 1;
    int ALL = TEL | SMS;
}
