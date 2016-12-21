package com.customview.xiaohui.mobilesafe.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.customview.xiaohui.mobilesafe.R;
import com.customview.xiaohui.mobilesafe.utils.MyConstants;
import com.customview.xiaohui.mobilesafe.utils.SPUtil;
import com.customview.xiaohui.mobilesafe.view.MyCardView;

public class SettingCenterActivity extends AppCompatActivity implements MyCardView.OnCheckBoxStatusListener {
    private MyCardView mAutoUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();//初始化视图
        initEvent();//初始化时间
        initData();//初始化数据

    }

    private void initEvent() {
        mAutoUpdate.SetOnCheckBoxStatusListener(this);
        /*mAutoUpdate.SetOnCheckBoxStatusListener(new MyCardView.OnCheckBoxStatusListener() {
            @Override
            public void getStatus(boolean isChecked) {
                if (isChecked){
                    Toast.makeText(getApplicationContext(),"测试成功",Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    private void initData() {

            mAutoUpdate.setStatus(SPUtil.getBoolen(this,MyConstants.ISAUTOUPDATE,false));

    }

    private void initView() {
        setContentView(R.layout.activity_setting_center);
        mAutoUpdate = (MyCardView) findViewById(R.id.mc_setting_center_auto_update);
    }

    /**
     *
     * @param cardView 当前操作的视图
     * @param isChecked 自定义view中CheckBox的状态状态
     */
    @Override
    public void getStatus(MyCardView cardView, boolean isChecked) {
        switch (cardView.getId()){
            //自动更新
            case R.id.mc_setting_center_auto_update:
                if (isChecked){
                    SPUtil.putBoolen(getApplicationContext(), MyConstants.ISAUTOUPDATE,true);
                }else {
                    SPUtil.putBoolen(getApplicationContext(), MyConstants.ISAUTOUPDATE,false);
                }
        }
    }
}
