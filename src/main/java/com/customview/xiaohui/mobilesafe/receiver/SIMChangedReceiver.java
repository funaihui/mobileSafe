package com.customview.xiaohui.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.customview.xiaohui.mobilesafe.service.LostFoundService;
import com.customview.xiaohui.mobilesafe.utils.EncryptTools;
import com.customview.xiaohui.mobilesafe.utils.MyConstants;
import com.customview.xiaohui.mobilesafe.utils.SPUtil;

/**
 * Created by wizardev on 2016/12/18.
 */

public class SIMChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Wizardev", "SIMChangedReceiver: ");
        String oldSIMInfo = SPUtil.getString(context, MyConstants.SIM,"");
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String newSIMInfo = tm.getSimSerialNumber();
        if (!oldSIMInfo.equals(newSIMInfo)){
            String s = SPUtil.getString(context, MyConstants.SAVENUM, "");
            String saveNum = EncryptTools.deciphery(MyConstants.SEED,s);
            SmsManager sm = SmsManager.getDefault();
            sm.sendTextMessage(saveNum,"","我是小偷",null,null);
        }
        //开机启动服务
        if (SPUtil.getBoolen(context,MyConstants.ISRUNNING,false)){
            Intent service = new Intent(context, LostFoundService.class);
            context.startService(service);
        }
    }

}
