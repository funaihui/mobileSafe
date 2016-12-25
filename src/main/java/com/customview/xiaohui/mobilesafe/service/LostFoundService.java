package com.customview.xiaohui.mobilesafe.service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.telephony.SmsMessage;
import android.util.Log;

import com.customview.xiaohui.mobilesafe.R;
import com.customview.xiaohui.mobilesafe.receiver.DeviceAdminSample;

/**
 * Created by wizardev on 2016/12/18.
 */

public class LostFoundService extends Service {
    boolean isPlay = false;
    private SmsReceiver smsReceiver;
    private DevicePolicyManager policyManager;
    private ComponentName name;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("Wizardev", "LostFoundServiceonCreate: ");
        smsReceiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(smsReceiver, filter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }

    class SmsReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            Object[] datas = (Object[]) extras.get("pdus");
            for (Object data : datas) {
                SmsMessage sm = null;
                sm = SmsMessage.createFromPdu((byte[]) data);
                String messageBody;
                if (sm.getMessageBody() == null){
                    return;
                }else {

                    messageBody = sm.getMessageBody();
                }

                Log.i("Wizardev", "onReceive:messageBody " + messageBody);
                if (messageBody.equals("#*lockscreen*#")) {

                    policyManager = (DevicePolicyManager) context.getSystemService(DEVICE_POLICY_SERVICE);
                    name = new ComponentName(context, DeviceAdminSample.class);
                    if (policyManager.isAdminActive(name)) {
                        policyManager.resetPassword("1234", DevicePolicyManager.RESET_PASSWORD_DO_NOT_ASK_CREDENTIALS_ON_BOOT);
                        policyManager.lockNow();
                        abortBroadcast();
                    }
                } else if (messageBody.equals("#*gps*#")) {
                    Intent locationService = new Intent(context, LocationService.class);
                    startService(locationService);
                    abortBroadcast();
                } else if (messageBody.equals("#*wipedata*#")) {
                    if (policyManager.isAdminActive(name)) {
                        policyManager.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                        abortBroadcast();
                    }
                } else if (messageBody.equals("#*music*#")) {
                    abortBroadcast();
                    if (isPlay) {
                        return;
                    }
                    MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.dou);
                    mediaPlayer.setVolume(1f, 1f);
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            isPlay = false;
                        }
                    });
                    isPlay = true;
                }
            }
        }


    }

}
