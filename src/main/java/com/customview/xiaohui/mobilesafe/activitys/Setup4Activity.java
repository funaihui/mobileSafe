package com.customview.xiaohui.mobilesafe.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.customview.xiaohui.mobilesafe.R;
import com.customview.xiaohui.mobilesafe.service.LostFoundService;
import com.customview.xiaohui.mobilesafe.utils.MyConstants;
import com.customview.xiaohui.mobilesafe.utils.SPUtil;
import com.customview.xiaohui.mobilesafe.utils.ServiceUtils;

public class Setup4Activity extends SetupBaseActivity {
    private static final String TAG = "Setup4Activity";
    private CheckBox mStartProtect;
    private TextView mShowMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPer();//检查权限
    }

    private void checkPer() {
        //检查权限
        int locationCheck = ContextCompat.checkSelfPermission(Setup4Activity.this,
                Manifest.permission.READ_PHONE_STATE);
        int receiveMessageCheck = ContextCompat.checkSelfPermission(Setup4Activity.this,
                Manifest.permission.RECEIVE_SMS);
        int sendMessageCheck = ContextCompat.checkSelfPermission(Setup4Activity.this,
                Manifest.permission.SEND_SMS);
        int fineLocationCheck = ContextCompat.checkSelfPermission(Setup4Activity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int readMessageCheck = ContextCompat.checkSelfPermission(Setup4Activity.this,
                Manifest.permission.READ_SMS);
        int writeExternalStorageCheck = ContextCompat.checkSelfPermission(Setup4Activity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int coarseLocationCheck = ContextCompat.checkSelfPermission(Setup4Activity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (locationCheck == PackageManager.PERMISSION_GRANTED
                && receiveMessageCheck == PackageManager.PERMISSION_GRANTED
                && sendMessageCheck == PackageManager.PERMISSION_GRANTED
                && fineLocationCheck == PackageManager.PERMISSION_GRANTED
                && readMessageCheck == PackageManager.PERMISSION_GRANTED
                && writeExternalStorageCheck == PackageManager.PERMISSION_GRANTED
                && coarseLocationCheck == PackageManager.PERMISSION_GRANTED) {
            initEvent();
        } else {
            //  Toast.makeText(getApplicationContext(),"没有响应的权限",Toast.LENGTH_SHORT).show();
            //没有权限，获取相应的权限
            ActivityCompat.requestPermissions(Setup4Activity.this,
                    new String[]{Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        }
    }

    @Override
    public void initData() {
        if (ServiceUtils.isRunningService(getApplicationContext(), "com.customview.xiaohui.mobilesafe.service.LostFoundService")) {
            mStartProtect.setChecked(true);
            mShowMessage.setTextColor(Color.GREEN);
            mShowMessage.setText("防盗保护已开启");
        } else {
            mStartProtect.setChecked(false);
            mShowMessage.setTextColor(Color.RED);
            mShowMessage.setText("防盗保护未开启");
        }
        super.initData();
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup4);
        mStartProtect = (CheckBox) findViewById(R.id.cb_setup4_start_protect);
        mShowMessage = (TextView) findViewById(R.id.tv_setup4_show_message);
    }

    @Override
    public void initEvent() {
        mStartProtect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    SPUtil.putBoolen(getApplicationContext(), MyConstants.ISRUNNING, true);
                    Intent service = new Intent(Setup4Activity.this, LostFoundService.class);
                    Log.i(TAG, "onCheckedChanged: " + b);
                    mShowMessage.setTextColor(Color.GREEN);
                    mShowMessage.setText("防盗保护已开启");
                    startService(service);
                } else {
                    SPUtil.putBoolen(getApplicationContext(), MyConstants.ISRUNNING, false);
                    Intent service = new Intent(Setup4Activity.this, LostFoundService.class);

                    Log.i(TAG, "onCheckedChanged: " + b);
                    mShowMessage.setTextColor(Color.RED);
                    mShowMessage.setText("防盗保护未开启");
                    stopService(service);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initEvent();
                } else {
                    Toast.makeText(getApplicationContext(), "请求权限失败", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }


    @Override
    public void next(View v) {
        if (mStartProtect.isChecked()) {
            nextActivity();
            SPUtil.putBoolen(getApplicationContext(), MyConstants.ISSTEUP, true);
        } else {
            Toast.makeText(getApplicationContext(), "请开启防盗保护", Toast.LENGTH_SHORT).show();
            return;
        }
        super.next(v);
    }

    @Override
    protected void previousActivity() {
        startActivity(Setup3Activity.class);
    }

    @Override
    protected void nextActivity() {
        Toast.makeText(getApplicationContext(), "恭喜你，防盗保护设置完成！", Toast.LENGTH_SHORT).show();

        startActivity(MainActivity.class);
    }
}
