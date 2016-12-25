package com.customview.xiaohui.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.customview.xiaohui.mobilesafe.dao.BlacklistDao;
import com.customview.xiaohui.mobilesafe.utils.MyConstants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BlacklistService extends Service {
    private static final String TAG = "Wizardev";
    private BlacklistSmsReceiver smsReceiver;
    private BlacklistDao blacklistDao;
    private TelephonyManager telephony = null;
    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        blacklistDao = BlacklistDao.getInstance(getApplicationContext());
        //注册监听广播
        smsReceiver = new BlacklistSmsReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(smsReceiver, filter);
        Log.i(TAG, "onCreate: BlacklistService");
        //监听电话
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        PhoneStateListener listener = new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, final String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING){
                    Log.i(TAG, "onCallStateChanged: "+incomingNumber);
                    int mode = blacklistDao.getMode(incomingNumber);
                    if ((MyConstants.TEL & mode) != 0){
                        Log.i(TAG, "onCallStateChanged: 电话拦截");
                        getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true,
                                new ContentObserver(new Handler()) {

                                    @Override
                                    public void onChange(boolean selfChange) {

                                        deleteCalllog(incomingNumber);

                                        getContentResolver().unregisterContentObserver(this);
                                        super.onChange(selfChange);
                                    }

                                });
                        try {
                            overPhone();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };

        telephonyManager.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);

    }

    private void deleteCalllog(String incomingNumber) {
        Uri uri = Uri.parse("content://call_log/calls");
        getContentResolver().delete(uri,"number=?",new String[]{incomingNumber});
    }

    private void overPhone() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, RemoteException {
        Class clazz = Class.forName("android.os.ServiceManager");
        //Class clazz = TelephonyManager.class;
        Method method = clazz.getDeclaredMethod("getService", String.class);
        method.setAccessible(true);
        IBinder invoke = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});

        ITelephony iTelephony = ITelephony.Stub.asInterface(invoke);

        iTelephony.endCall();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 在android4.4以上版本拦截短信失败，由于google的安全机制
     */
    private class BlacklistSmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] datas = (Object[]) intent.getExtras().get("pdus");
            Log.i("Wizardev", "onReceive:address " + datas);

            for (Object sms : datas) {
                SmsMessage sm = null;
                sm = SmsMessage.createFromPdu((byte[]) sms);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    sm = SmsMessage.createFromPdu((byte[]) sms,"utf-8");
                }
                String address = sm.getOriginatingAddress();
                int mode = blacklistDao.getMode(address);
                if ((MyConstants.SMS & mode) != 0){
                    //说明需要拦截短信
                    abortBroadcast();//终止广播
                }
            }

        }
    }

    /*private class PhoneReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            PhoneStateListener listener = new PhoneStateListener(){
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    if (state == TelephonyManager.CALL_STATE_RINGING){
                        Log.i(TAG, "onCallStateChanged: "+incomingNumber);
                        int mode = blacklistDao.getMode(incomingNumber);
                        if ((MyConstants.TEL & mode) != 0){
                            Log.i(TAG, "onCallStateChanged: 电话拦截");
                        }
                    }
                    super.onCallStateChanged(state, incomingNumber);
                }
            };

            telephonyManager.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);
        }
    }*/
}
