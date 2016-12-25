package com.customview.xiaohui.mobilesafe.activitys;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.customview.xiaohui.mobilesafe.R;
import com.customview.xiaohui.mobilesafe.utils.MyConstants;
import com.customview.xiaohui.mobilesafe.utils.SPUtil;

public class Setup2Activity extends SetupBaseActivity {
    private static final String TAG = "Wizardev";
    private ImageView lockImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initData() {

        if (TextUtils.isEmpty(SPUtil.getString(getApplicationContext(), MyConstants.SIM, ""))) {
            lockImg.setImageResource(R.drawable.unlock);
        } else {
            lockImg.setImageResource(R.drawable.lock);
        }
    }

    @Override
    public void initEvent() {
        Button bindSIM = (Button) findViewById(R.id.bt_setup2_bind_sim);
        bindSIM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //检查权限
                int permissionCheck = ContextCompat.checkSelfPermission(Setup2Activity.this,
                        Manifest.permission.READ_PHONE_STATE);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

                    //有权限,进行响应的操作
                    //ActivityCompat.requestPermissions(Setup2Activity.this, new String[]{Manifest.permission.READ_PHONE_STATE},1);
                    getSIMinfo();
                } else if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                    //  Toast.makeText(getApplicationContext(),"没有响应的权限",Toast.LENGTH_SHORT).show();
                    //没有权限，获取相应的权限
                    ActivityCompat.requestPermissions(Setup2Activity.this,
                            new String[]{Manifest.permission.READ_PHONE_STATE}, 1);

                }

            }
        });
        super.initEvent();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //有权限
                    getSIMinfo();
                } else {
                    Toast.makeText(getApplicationContext(), "请求权限失败", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }

    }

    private void getSIMinfo() {
        if (TextUtils.isEmpty(SPUtil.getString(getApplicationContext(), MyConstants.SIM, ""))) {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String simSerialNumber = telephonyManager.getSimSerialNumber();
            if (TextUtils.isEmpty(simSerialNumber)) {
                Toast.makeText(getApplicationContext(), "请插入SIM卡", Toast.LENGTH_SHORT).show();
                return;
            }
            SPUtil.putString(getApplicationContext(), MyConstants.SIM, simSerialNumber);
            lockImg.setImageResource(R.drawable.lock);
        } else {
            SPUtil.putString(getApplicationContext(), MyConstants.SIM, "");
            lockImg.setImageResource(R.drawable.unlock);
        }
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup2);
        lockImg = (ImageView) findViewById(R.id.iv_setup2_lock);
    }

    @Override
    protected void previousActivity() {
        startActivity(Setup1Activity.class);
    }

    @Override
    public void next(View v) {
        if (TextUtils.isEmpty(SPUtil.getString(getApplicationContext(), MyConstants.SIM, ""))) {
            Toast.makeText(this, "请绑定SIM卡", Toast.LENGTH_SHORT).show();
            return;
        }
        super.next(v);
    }

    @Override
    protected void nextActivity() {
        startActivity(Setup3Activity.class);
    }
}
