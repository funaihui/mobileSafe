package com.customview.xiaohui.mobilesafe.activitys;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.customview.xiaohui.mobilesafe.R;
import com.customview.xiaohui.mobilesafe.utils.MD5Utils;
import com.customview.xiaohui.mobilesafe.utils.MyConstants;
import com.customview.xiaohui.mobilesafe.utils.SPUtil;

import static android.view.View.inflate;

public class MainActivity extends AppCompatActivity {
    private GridView mGridView;
    private int[] icons = {R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app
            , R.drawable.taskmanager, R.drawable.netmanager, R.drawable.trojan
            , R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings};

    private String names[] = {"手机防盗", "通讯卫士", "软件管家", "进程管理", "流量统计", "病毒查杀", "缓存清理", "高级工具", "设置中心"};
    private AlertDialog mAlertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        if (TextUtils.isEmpty(SPUtil.getString(getApplicationContext(),MyConstants.PASSWORD,""))){
                            showSettingPassDialog();  
                        }else {
                            showEnterDialog();
                        }
                        
                        break;
                    case 1:
                        Intent blacklist = new Intent(MainActivity.this,BlacklistActivity.class);
                        startActivity(blacklist);
                        break;
                    case 2:
                        Intent softwareManager = new Intent(MainActivity.this,SoftwareManageActivity.class);
                        startActivity(softwareManager);
                        break;
                    case 7:
                        Intent advancedTools = new Intent(MainActivity.this,AdvancedToolsActivity.class);
                        startActivity(advancedTools);
                        break;
                    case 8:
                        Intent settingCenter = new Intent(MainActivity.this,SettingCenterActivity.class);
                        startActivity(settingCenter);
                        break;
                }
            }
        });
    }

    private void showEnterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(MainActivity.this,R.layout.enter_dialog,null);

        final EditText etPassword = (EditText)view.findViewById(R.id.et_dialog_enter_password);
        Button button = (Button) view.findViewById(R.id.b_dialog_enter_commit);
        builder.setView(view);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //设置密码
                String passone = etPassword.getText().toString().trim();
                if (TextUtils.isEmpty(passone)){
                    Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String password = SPUtil.getString(getApplicationContext(),MyConstants.PASSWORD,"");
                    String enterword = MD5Utils.md5(passone);
                    if (enterword.equals(password)){
                        if (SPUtil.getBoolen(getApplicationContext(),MyConstants.ISSTEUP,false)){
                            Intent i = new Intent(MainActivity.this, LostFoundActivity.class);
                            startActivity(i);
                        }else {
                            Intent i = new Intent(MainActivity.this, Setup1Activity.class);
                            startActivity(i);
                        }

                        mAlertDialog.dismiss();
                    }else {
                        Toast.makeText(getApplicationContext(), "密码输入错误", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void showSettingPassDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this,R.layout.set_pass_layout,null);
        final EditText et_password = (EditText)view.findViewById(R.id.et_dialog_password);
        final EditText et_repassword = (EditText)view.findViewById(R.id.et_dialog_repassword);
        Button button = (Button) view.findViewById(R.id.b_dialog_commit);
        builder.setView(view);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //设置密码
                String passone = et_password.getText().toString().trim();
                String passtwo = et_repassword.getText().toString().trim();
                if (TextUtils.isEmpty(passtwo) || TextUtils.isEmpty(passone)){
                    Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!passone.equals(passtwo)){//密码不一致
                    Toast.makeText(getApplicationContext(), "密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //保存密码
                    //保存密码到sp中
                    String password = MD5Utils.md5(passone);
                    SPUtil.putString(getApplicationContext(), MyConstants.PASSWORD, password);
                    mAlertDialog.dismiss();
                }

            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void initData() {
        mGridView.setAdapter(new MyAdapter());
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        mGridView = (GridView) findViewById(R.id.gv_home_show_icon);

    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return icons.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflate(getApplicationContext(), R.layout.gridview_item, null);
            }
            ImageView icon = (ImageView) view.findViewById(R.id.iv_gridview_icon);
            TextView iconName = (TextView) view.findViewById(R.id.tv_gridview_icon_name);
            icon.setImageResource(icons[i]);
            iconName.setText(names[i]);
            return view;
        }
    }
}
