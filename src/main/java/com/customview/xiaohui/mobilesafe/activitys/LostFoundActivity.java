package com.customview.xiaohui.mobilesafe.activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.customview.xiaohui.mobilesafe.R;
import com.customview.xiaohui.mobilesafe.utils.EncryptTools;
import com.customview.xiaohui.mobilesafe.utils.MyConstants;
import com.customview.xiaohui.mobilesafe.utils.SPUtil;
import com.customview.xiaohui.mobilesafe.utils.ServiceUtils;

public class LostFoundActivity extends AppCompatActivity {
    private TextView showSafeNum;
    private TextView showSafeStatus;
    private Button enterGuide;
    private ImageView showSafeStatusPic;
    private Toolbar toolbar;
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
                Intent i = new Intent(LostFoundActivity.this, Setup1Activity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void initData() {
        String s = SPUtil.getString(getApplicationContext(), MyConstants.SAVENUM, "");
        String safeNum = EncryptTools.deciphery(MyConstants.SEED, s);
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

        toolbar = (Toolbar) findViewById(R.id.tb_lost_toolbar);

        setSupportActionBar(toolbar);

        showSafeNum = (TextView) findViewById(R.id.tv_lost_show_safenum);
        enterGuide = (Button) findViewById(R.id.bt_lost_enter_guide);
        showSafeStatus = (TextView) findViewById(R.id.tv_lost_show_safe_message);
        showSafeStatusPic = (ImageView) findViewById(R.id.iv_lost_show_safe_pic);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_lost_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_lost_modify:
                Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_SHORT).show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
