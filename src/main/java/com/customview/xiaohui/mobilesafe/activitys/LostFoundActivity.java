package com.customview.xiaohui.mobilesafe.activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.customview.xiaohui.mobilesafe.R;
import com.customview.xiaohui.mobilesafe.utils.MyConstants;
import com.customview.xiaohui.mobilesafe.utils.SPUtil;
import com.customview.xiaohui.mobilesafe.utils.ServiceUtils;

public class LostFoundActivity extends AppCompatActivity {
    private TextView showSafeNum;
    private TextView showSafeStatus;
    private Button enterGuide;
    private ImageView showSafeStatusPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        enterGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LostFoundActivity.this,Setup1Activity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void initData() {
        String safeNum = SPUtil.getString(getApplicationContext(), MyConstants.SAVENUM,"");
        showSafeNum.setText(safeNum);
        if (ServiceUtils.isRunningService(getApplicationContext(), "com.customview.xiaohui.mobilesafe.service.LostFoundService")) {
            showSafeStatus.setTextColor(Color.GREEN);
            showSafeStatus.setText("防盗保护已开启");
            showSafeStatusPic.setImageResource(R.drawable.lock);
        } else {
            showSafeStatus.setTextColor(Color.RED);
            showSafeStatus.setText("防盗保护未开启");
            showSafeStatusPic.setImageResource(R.drawable.unlock);
        }
    }

    private void initView() {
        setContentView(R.layout.activity_lost_found);
        showSafeNum = (TextView) findViewById(R.id.tv_lost_show_safenum);
        enterGuide = (Button) findViewById(R.id.bt_lost_enter_guide);
        showSafeStatus = (TextView) findViewById(R.id.tv_lost_show_safe_message);
        showSafeStatusPic = (ImageView) findViewById(R.id.iv_lost_show_safe_pic);
    }
}
