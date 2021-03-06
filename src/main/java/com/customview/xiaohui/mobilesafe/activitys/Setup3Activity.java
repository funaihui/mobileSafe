package com.customview.xiaohui.mobilesafe.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.customview.xiaohui.mobilesafe.R;
import com.customview.xiaohui.mobilesafe.utils.EncryptTools;
import com.customview.xiaohui.mobilesafe.utils.MyConstants;
import com.customview.xiaohui.mobilesafe.utils.SPUtil;

public class Setup3Activity extends SetupBaseActivity {
private EditText mInputNum;
    private Button mSelectNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup3);
        mSelectNum = (Button) findViewById(R.id.bt_setup3_select_number);
        mInputNum = (EditText) findViewById(R.id.et_setup3_phone_number);
    }

    @Override
    public void initData() {
        if (!TextUtils.isEmpty(SPUtil.getString(getApplicationContext(),MyConstants.SAVENUM,""))){
           mInputNum.setText(EncryptTools.deciphery(MyConstants.SEED,SPUtil.getString(getApplicationContext(),MyConstants.SAVENUM,"")));
        }
    }

    @Override
    public void next(View v) {
        String inputNum = mInputNum.getText().toString().trim();
        String safeNum = EncryptTools.encrypt(MyConstants.SEED,inputNum);
        if (TextUtils.isEmpty(safeNum)){
            Toast.makeText(getApplicationContext(),"请输入安全号码",Toast.LENGTH_SHORT).show();
            return;
        }else {
            SPUtil.putString(getApplicationContext(), MyConstants.SAVENUM, safeNum);
        }
        super.next(v);
    }

    @Override
    protected void previousActivity() {
        startActivity(Setup2Activity.class);
    }

    @Override
    public void initEvent() {
        mSelectNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Setup3Activity.this,ShowContactsActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && requestCode ==1){
            String num = data.getStringExtra(MyConstants.SAVENUM);
            mInputNum.setText(num);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void nextActivity() {
        startActivity(Setup4Activity.class);
    }
}
