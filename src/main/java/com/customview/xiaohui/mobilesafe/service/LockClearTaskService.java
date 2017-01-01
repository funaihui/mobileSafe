package com.customview.xiaohui.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.customview.xiaohui.mobilesafe.receiver.LockClearTaskReceiver;
public class LockClearTaskService extends Service {


    private LockClearTaskReceiver mClearTaskReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        mClearTaskReceiver = new LockClearTaskReceiver();
        IntentFilter filter = new IntentFilter("android.intent.action.SCREEN_OFF");
        registerReceiver(mClearTaskReceiver,filter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mClearTaskReceiver);
        super.onDestroy();
    }
}
