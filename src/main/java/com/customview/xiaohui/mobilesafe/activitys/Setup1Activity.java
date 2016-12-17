package com.customview.xiaohui.mobilesafe.activitys;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.customview.xiaohui.mobilesafe.R;
import com.customview.xiaohui.mobilesafe.utils.MyConstants;
import com.customview.xiaohui.mobilesafe.utils.SPUtil;

public class Setup1Activity extends SetupBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }




    public void initView() {
        setContentView(R.layout.activity_setup1);

    }

    @Override
    protected void previousActivity() {

    }

    @Override
    protected void nextActivity() {
        startActivity(Setup2Activity.class);
    }
}
