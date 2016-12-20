package com.customview.xiaohui.mobilesafe.activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
                    SPUtil.putBoolen(getApplicationContext(),MyConstants.ISRUNNING,true);
                    Intent service = new Intent(Setup4Activity.this, LostFoundService.class);
                    Log.i(TAG, "onCheckedChanged: " + b);
                    mShowMessage.setTextColor(Color.GREEN);
                    mShowMessage.setText("防盗保护已开启");
                    startService(service);
                } else {
                    SPUtil.putBoolen(getApplicationContext(),MyConstants.ISRUNNING,false);
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
