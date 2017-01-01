package com.customview.xiaohui.mobilesafe.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.customview.xiaohui.mobilesafe.R;
import com.customview.xiaohui.mobilesafe.service.LockClearTaskService;
import com.customview.xiaohui.mobilesafe.utils.MyConstants;
import com.customview.xiaohui.mobilesafe.utils.SPUtil;
import com.customview.xiaohui.mobilesafe.utils.ServiceUtils;

public class TaskSettingActivity extends AppCompatActivity {

    private CheckBox mLockScreenClear;
    private CheckBox mShowSysTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();

    }

    private void initData() {
        mLockScreenClear.setChecked(ServiceUtils.isRunningService(this,"com.customview.xiaohui.mobilesafe.service.LockClearTaskService"));
        mShowSysTask.setChecked(SPUtil.getBoolen(this,MyConstants.SHOWSYSTASK,false));
    }

    private void initEvent() {
        mLockScreenClear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //SPUtil.putBoolen(getApplicationContext(), MyConstants.LOCKCLEARN,isChecked);
                if (isChecked){
                    Intent intent = new Intent(TaskSettingActivity.this, LockClearTaskService.class);
                    startService(intent);
                }else {
                    Intent intent = new Intent(TaskSettingActivity.this, LockClearTaskService.class);
                    stopService(intent);
                }
            }
        });
        mShowSysTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtil.putBoolen(getApplicationContext(), MyConstants.SHOWSYSTASK,isChecked);
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_task_setting);
        mLockScreenClear = (CheckBox) findViewById(R.id.cb_tasksetting_cleartask);
        mShowSysTask = (CheckBox) findViewById(R.id.cb_tasksetting_show_systask);
    }
}
