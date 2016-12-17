package com.customview.xiaohui.mobilesafe.activitys;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.customview.xiaohui.mobilesafe.R;
import com.customview.xiaohui.mobilesafe.utils.MyConstants;
import com.customview.xiaohui.mobilesafe.utils.SPUtil;

public class Setup3Activity extends SetupBaseActivity {
private EditText mInputNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup3);

        mInputNum = (EditText) findViewById(R.id.et_setup3_phone_number);
    }

    @Override
    public void initData() {
        if (!TextUtils.isEmpty(SPUtil.getString(getApplicationContext(),MyConstants.SAVENUM,""))){
           mInputNum.setText(SPUtil.getString(getApplicationContext(),MyConstants.SAVENUM,""));
        }
    }

    @Override
    public void next(View v) {
        if (TextUtils.isEmpty(mInputNum.getText().toString().trim())){
            Toast.makeText(getApplicationContext(),"请输入安全号码",Toast.LENGTH_SHORT).show();
            return;
        }else {
            SPUtil.putString(getApplicationContext(), MyConstants.SAVENUM,mInputNum.getText().toString().trim());
        }
        super.next(v);
    }

    @Override
    protected void previousActivity() {
        startActivity(Setup2Activity.class);
    }

    @Override
    protected void nextActivity() {
        startActivity(Setup4Activity.class);
    }
}
