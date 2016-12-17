package com.customview.xiaohui.mobilesafe.activitys;

import android.os.Bundle;

import com.customview.xiaohui.mobilesafe.R;

public class Setup4Activity extends SetupBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup4);
    }

    @Override
    protected void previousActivity() {
        startActivity(Setup3Activity.class);
    }

    @Override
    protected void nextActivity() {
        startActivity(MainActivity.class);
    }
}
