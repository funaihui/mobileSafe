package com.customview.xiaohui.mobilesafe.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.customview.xiaohui.mobilesafe.R;
import com.customview.xiaohui.mobilesafe.service.BlacklistService;
import com.customview.xiaohui.mobilesafe.service.PhoneLocationService;
import com.customview.xiaohui.mobilesafe.utils.MyConstants;
import com.customview.xiaohui.mobilesafe.utils.SPUtil;
import com.customview.xiaohui.mobilesafe.utils.ServiceUtils;
import com.customview.xiaohui.mobilesafe.view.MyCardView;

public class SettingCenterActivity extends AppCompatActivity {
    private MyCardView mAutoUpdate;
    private MyCardView mBlacklist;
    private MyCardView mPhoneLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();//初始化视图

        initEvent();//初始化时间
        initData();//初始化数据
        initPermission();

    }

    private void initPermission() {
        //检查权限
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

            //有权限,进行响应的操作
            //ActivityCompat.requestPermissions(Setup2Activity.this, new String[]{Manifest.permission.READ_PHONE_STATE},1);
            // getSIMinfo();
        } else if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            //  Toast.makeText(getApplicationContext(),"没有响应的权限",Toast.LENGTH_SHORT).show();
            //没有权限，获取相应的权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, 1);

        }
    }

    private void initEvent() {
        mAutoUpdate.SetOnCheckBoxStatusListener(new MyCardView.OnCheckBoxStatusListener() {
            @Override
            public void getStatus(boolean isChecked) {
                if (isChecked) {
                    SPUtil.putBoolen(getApplicationContext(), MyConstants.ISAUTOUPDATE, true);
                } else {
                    SPUtil.putBoolen(getApplicationContext(), MyConstants.ISAUTOUPDATE, false);
                }
            }
        });
        mBlacklist.SetOnCheckBoxStatusListener(new MyCardView.OnCheckBoxStatusListener() {
            @Override
            public void getStatus(boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(SettingCenterActivity.this, BlacklistService.class);
                    startService(intent);
                } else {
                    Intent intent = new Intent(SettingCenterActivity.this, BlacklistService.class);
                    stopService(intent);
                }
            }
        });
        mPhoneLocation.SetOnCheckBoxStatusListener(new MyCardView.OnCheckBoxStatusListener() {
            @Override
            public void getStatus(boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(SettingCenterActivity.this, PhoneLocationService.class);
                    startService(intent);
                } else {
                    Intent intent = new Intent(SettingCenterActivity.this, PhoneLocationService.class);
                    stopService(intent);
                }
            }
        });

    }

    private void initData() {

        mAutoUpdate.setStatus(SPUtil.getBoolen(this, MyConstants.ISAUTOUPDATE, false));
        if (ServiceUtils.isRunningService(getApplicationContext(), "com.customview.xiaohui.mobilesafe.service.BlacklistService")) {
            mBlacklist.setStatus(true);
        } else {
            mBlacklist.setStatus(false);
        }

        if (ServiceUtils.isRunningService(getApplicationContext(), "com.customview.xiaohui.mobilesafe.service.PhoneLocationService")) {
            mPhoneLocation.setStatus(true);
        } else {
            mPhoneLocation.setStatus(false);
        }


    }

    private void initView() {
        setContentView(R.layout.activity_setting_center);
        mAutoUpdate = (MyCardView) findViewById(R.id.mc_setting_center_auto_update);
        mBlacklist = (MyCardView) findViewById(R.id.mc_setting_center_blacklist);
        mPhoneLocation = (MyCardView) findViewById(R.id.mc_setting_center_location_service);
    }

    /**
     *
     * @param cardView 当前操作的视图
     * @param isChecked 自定义view中CheckBox的状态状态
    /*  *//*
    @Override
    public void getStatus(MyCardView cardView, boolean isChecked) {
        switch (cardView.getId()){
            //自动更新
            case R.id.mc_setting_center_auto_update:

                break;
            case R.id.mc_setting_center_blacklist:

                break;
        }
    }*/
}
