package com.customview.xiaohui.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.customview.xiaohui.mobilesafe.R;
import com.customview.xiaohui.mobilesafe.engine.PhoneLocation;
import com.customview.xiaohui.mobilesafe.utils.MyConstants;
import com.customview.xiaohui.mobilesafe.utils.SPUtil;

public class PhoneLocationService extends Service {
    private static final String TAG = "Wizardev";
    private PhoneStateListener listener;
    private TelephonyManager tm;
    private WindowManager wm;
    private WindowManager.LayoutParams params;
    private View v;
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "PhoneLocationServiceOnCreate: ");
        /**
         * 当API大于23时WindowManager.LayoutParams.TYPE_PRIORITY_PHONE需要在运行时获取相应的权限
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
               // startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            }
        }
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        initToastParams();
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        listener = new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, final String incomingNumber) {
                switch (state){
                    case TelephonyManager.CALL_STATE_IDLE:
                        hideToast();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        hideToast();
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        String location = PhoneLocation.getCategray(incomingNumber);
                        showToast(location);
                       // Toast.makeText(getApplicationContext(),,Toast.LENGTH_SHORT).show();
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    private void hideToast() {
        if (v != null){
            wm.removeView(v);
            v = null;
        }
    }

    private void showToast(String location) {
        if (v != null){
            wm.removeView(v);
            v = null;
        }

        v = View.inflate(getApplicationContext(), R.layout.sy_toast,null);
        TextView showLocation = (TextView) v.findViewById(R.id.tv_toast_show_location);
        showLocation.setText(location);
        v.setOnTouchListener(new View.OnTouchListener() {
            float startX;
            float startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i(TAG, "onTouch:getX: "+event.getX()+"getRawX: "+event.getRawX());

                switch (event.getAction()){

                    case MotionEvent.ACTION_DOWN:

                        startX = event.getRawX();
                        startY = event.getRawY();
                       // Log.i(TAG, "onTouch:getX: "+event.getX()+"getRawX: "+event.getRawX());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float moveX = event.getRawX();
                        float moveY = event.getRawY();
                        float differX = moveX - startX;
                        float differY = moveY -startY;
                        params.x += differX;
                        params.y += differY;
                        startX = moveX;
                        startY = moveY;
                        wm.updateViewLayout(v,params);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (params.x <0){
                            params.x = 0;
                        }else if (params.x+v.getWidth() > wm.getDefaultDisplay().getWidth()){
                            params.x = wm.getDefaultDisplay().getWidth() - v.getWidth();
                        }
                        if (params.y < 0){
                            params.y = 0;
                        }else if (params.y + v.getHeight() > wm.getDefaultDisplay().getHeight()){
                            params.y = wm.getDefaultDisplay().getHeight() - v.getHeight();
                        }
                        SPUtil.putString(getApplicationContext(), MyConstants.TOASTX, params.x + "");
                        SPUtil.putString(getApplicationContext(), MyConstants.TOASTY, params.y + "");
                        break;
                }

                return false;
            }
        });
        wm.addView(v,params);
    }

    private void initToastParams() {
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.gravity = Gravity.LEFT|Gravity.TOP ;
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        params.x = Integer.parseInt(SPUtil.getString(getApplicationContext(), MyConstants.TOASTX,"0"));
        params.y = Integer.parseInt(SPUtil.getString(getApplicationContext(), MyConstants.TOASTY,"0"));
        //params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
               /* | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;*/

    }

    @Override
    public void onDestroy() {
        if (tm != null){
            tm.listen(listener, PhoneStateListener.LISTEN_NONE);
            tm = null;
        }

        super.onDestroy();
    }
}
