package com.customview.xiaohui.mobilesafe.activitys;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.customview.xiaohui.mobilesafe.R;
import com.customview.xiaohui.mobilesafe.dao.BlacklistDao;
import com.customview.xiaohui.mobilesafe.domain.BlacklistBean;
import com.customview.xiaohui.mobilesafe.utils.MyConstants;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class BlacklistActivity extends AppCompatActivity {
    private static final int LOADING = 0;
    private static final int FINISH = 1;
    private static final int PERPAGENUM = 20;
    private static final String TAG = "BlacklistActivity";
    private Toolbar mToolbar;
    private TextView mShowMessage;
    private RecyclerView mRecyclerView;
    private ProgressBar mShowProgress;
    private List<BlacklistBean> list = new ArrayList<>();
    private List<BlacklistBean> datas;
    private MyAdapter mAdapter;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //正在查询
                case LOADING:
                    //显示进度条
                    mShowProgress.setVisibility(View.VISIBLE);
                    //隐藏recycleView
                    mRecyclerView.setVisibility(View.GONE);
                    //隐藏textView
                    mShowMessage.setVisibility(View.GONE);
                    break;
                //查询完成
                case FINISH:

                    //隐藏r进度条
                    mShowProgress.setVisibility(View.GONE);
                    if (list.size() != 0) {

                        //说明有数据，则显示recycleView
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mShowMessage.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mRecyclerView.setVisibility(View.GONE);

                        //没有数据，
                        mShowMessage.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };
    private AlertDialog mAlertDialog;
    private BlacklistDao mBlacklistDao = BlacklistDao.getInstance(BlacklistActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
        checkPer();
    }

    private void checkPer() {
        int selfPermission = ActivityCompat.checkSelfPermission(BlacklistActivity.this, Manifest.permission.READ_CALL_LOG);
        if (selfPermission == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CALL_LOG},1);
        }else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CALL_LOG},1);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }
        }
    }

    private void initEvent() {
        /**
         * 设置recycleView的滚动事件
         */
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    //判断是当前layoutManager是否为LinearLayoutManager
                    // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                        //获取最后一个可见view的位置
                        int lastItemPosition = linearManager.findLastVisibleItemPosition();
                        //获取第一个可见view的位置
                        int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                        // Log.i("Wizardev", "onScrollStateChanged: "+lastItemPosition + "   " + firstItemPosition);
                        if (lastItemPosition == list.size() - 1) {
                            initData();
                        }
                    }
                }
            }
        });
    }

    private void initData() {
        //查询数据的数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(LOADING);
                // list = mBlacklistDao.checkAll();
                datas = mBlacklistDao.moreDatas(BlacklistActivity.this.list.size(), PERPAGENUM);
                BlacklistActivity.this.list.addAll(datas);
                handler.sendEmptyMessage(FINISH);
            }
        }).start();

    }

    private void initView() {
        // StatusBarUtils.setStatusBarColor(BlacklistActivity.this,"#009688");//设置系统状态栏的颜色
        setContentView(R.layout.activity_blacklist);
        StatusBarUtil.setColor(BlacklistActivity.this, 0x009688);//设置系统状态栏的颜色

        mToolbar = (Toolbar) findViewById(R.id.tb_blacklist);
        setSupportActionBar(mToolbar);

        mShowMessage = (TextView) findViewById(R.id.tv_blacklist_show_message);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_blacklist_show_data);
        mShowProgress = (ProgressBar) findViewById(R.id.pb_blacklist_show_progress);
        mAdapter = new MyAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置item之间的分割
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = 10;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDrawOver(c, parent, state);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 创建菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_blacklist_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 为菜单添加功能
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_blacklist_add_usehand:
                showAddDialog("");
                break;
            case R.id.menu_blacklist_add_from_contacts: {
                Intent intent = new Intent(BlacklistActivity.this, ShowContactsActivity.class);
                startActivityForResult(intent, 1);
            }
            break;
            case R.id.menu_blacklist_add_from_calllog: {
                Intent intent = new Intent(BlacklistActivity.this, ShowCallLogsActivity.class);
                startActivityForResult(intent, 1);
            }
            break;
            case R.id.menu_blacklist_add_from_message: {
                Intent intent = new Intent(BlacklistActivity.this, ShowSmsLogsActivity.class);
                startActivityForResult(intent, 1);
            }
            break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && requestCode == 1) {
            String num = data.getStringExtra(MyConstants.SAVENUM);

            showAddDialog(num);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showAddDialog(String num) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(getApplicationContext(), R.layout.add_blacklist_dialog, null);
        final EditText et_phone = (EditText) view.findViewById(R.id.et_dialog_input_number);
        et_phone.setText(num);
        Button positive = (Button) view.findViewById(R.id.b_dialog_blacklist_positive);
        Button negative = (Button) view.findViewById(R.id.b_dialog_blacklist_negative);
        final CheckBox cb_phone = (CheckBox) view.findViewById(R.id.cd_blacklist_phone);
        final CheckBox cb_sms = (CheckBox) view.findViewById(R.id.cd_blacklist_sms);
        //取消按钮的事件
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        //确定按钮的事件
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_phone.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getApplicationContext(), "号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!cb_phone.isChecked() && !cb_sms.isChecked()) {
                    Toast.makeText(getApplicationContext(), "至少选择一种模式", Toast.LENGTH_SHORT).show();
                    return;
                }
                int mode = 0;
                if (cb_phone.isChecked()) {
                    mode |= MyConstants.TEL;
                }
                if (cb_sms.isChecked()) {
                    mode |= MyConstants.SMS;
                }
                BlacklistBean bean = new BlacklistBean();
                bean.setMode(mode);
                bean.setPhone(phone);
                mBlacklistDao.add(bean);
                mAlertDialog.dismiss();
                list.remove(bean);
                //数据添加到list的首位
                list.add(0, bean);

                handler.sendEmptyMessage(FINISH);
                //mAdapter.notifyDataSetChanged();
            }
        });
        builder.setView(view);
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(getApplicationContext(), R.layout.recycle_view_item, null);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final BlacklistBean bean = list.get(position);
            final String phone = bean.getPhone();

            switch (bean.getMode()) {
                case MyConstants.TEL:
                    holder.mode.setText("电话拦截");
                    break;
                case MyConstants.SMS:
                    holder.mode.setText("短信拦截");
                    break;
                case MyConstants.ALL:
                    holder.mode.setText("全部拦截");
                    break;
            }
            holder.phone.setText(phone);

            /**
             * 删除数据
             */
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(BlacklistActivity.this);
                    builder.setTitle("提示")
                            .setMessage("确定从黑名单中移除此条数据吗？")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mBlacklistDao.remove(phone);
                            list.remove(position);
                            handler.sendEmptyMessage(FINISH);
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    //mAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView phone;
        TextView mode;
        ImageView delete;

        public MyViewHolder(View itemView) {
            super(itemView);
            phone = (TextView) itemView.findViewById(R.id.tv_recycleview_show_number);
            mode = (TextView) itemView.findViewById(R.id.tv_recycleview_show_mode);
            delete = (ImageView) itemView.findViewById(R.id.iv_recycleview_delete);
        }
    }
}
