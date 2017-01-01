package com.customview.xiaohui.mobilesafe.activitys;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.customview.xiaohui.mobilesafe.R;
import com.customview.xiaohui.mobilesafe.domain.RunningTaskBean;
import com.customview.xiaohui.mobilesafe.engine.LoadRunningAppEngine;
import com.customview.xiaohui.mobilesafe.utils.MyConstants;
import com.customview.xiaohui.mobilesafe.utils.PermissionListener;
import com.customview.xiaohui.mobilesafe.utils.PermissionUtils;
import com.customview.xiaohui.mobilesafe.utils.SPUtil;
import com.jaeger.library.StatusBarUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import dmax.dialog.SpotsDialog;

public class TaskManagerActivity extends PermissionActivity implements View.OnClickListener {
    private static final int LOADING = 1;
    private static final int FINISH = 2;

    private TextView mTaskRunning;
    private TextView mTaskMem;
    private TextView mTaskLabel;
    private SpotsDialog mDialog;
    private List<RunningTaskBean> userTasks = new CopyOnWriteArrayList<>();
    private List<RunningTaskBean> sysTasks = new CopyOnWriteArrayList<>();
    private ListView mShowAllTask;
    private MyAdapter mAdapter;
    private PopupWindow popupWindow;
    private long availMem;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    mDialog.show();
                    mShowAllTask.setVisibility(View.GONE);
                    mTaskLabel.setVisibility(View.GONE);
                    break;
                case FINISH:
                    mDialog.dismiss();
                    mTaskLabel.setVisibility(View.VISIBLE);
                    setTileMessage();
                    mAdapter.notifyDataSetChanged();
                    mShowAllTask.setVisibility(View.VISIBLE);

                    break;

            }
            super.handleMessage(msg);
        }
    };
    private ActivityManager am;
    private long mClearMem;

    private void setTileMessage() {
        mTaskLabel.setText("用户进程（" + userTasks.size() + "）");
        mTaskRunning.setText("总进程：" + (userTasks.size() + sysTasks.size()));
        availMem = LoadRunningAppEngine.getFreeMem(getApplicationContext());
        Log.i("Wizardev", "setTileMessage: "+availMem);
        String freeMem = Formatter.formatFileSize(getApplicationContext(), availMem);
        String totalMem = Formatter.formatFileSize(getApplicationContext(), LoadRunningAppEngine.getTotalMem(getApplicationContext()));
        mTaskMem.setText("可用/总内存:" + freeMem + "/" + totalMem);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
       // initData();
        initEvent();
        PermissionUtils.addActivity(this);
    }

    @Override
    protected void onResume() {
        initData();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        PermissionUtils.removeAcrivity(this);
        super.onDestroy();
    }

    /**
     * 创建toolbar菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_taskmanage_setting,menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * toolbar菜单点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_task_setting){
            //Toast.makeText(getApplicationContext(), "设置", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,TaskSettingActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(LOADING);
                List<RunningTaskBean> runningAppInfos = LoadRunningAppEngine.getRunningAppInfo(getApplicationContext());
                availMem = LoadRunningAppEngine.getFreeMem(getApplicationContext());
                userTasks.clear();
                sysTasks.clear();
                for (RunningTaskBean runningAppInfo : runningAppInfos) {
                    if (runningAppInfo.isSysApp()) {
                        sysTasks.add(runningAppInfo);
                    } else {
                        userTasks.add(runningAppInfo);
                    }
                }
                handler.sendEmptyMessage(FINISH);
            }
        }).start();
    }

    private void initPopupWindow() {
        View view = View.inflate(getApplicationContext(), R.layout.popupwindow_taskmanager_select, null);
        popupWindow = new PopupWindow(view);
        popupWindow.setWidth(-1);
        popupWindow.setHeight(-2);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setAnimationStyle(-1);
        ImageView clear = (ImageView) view.findViewById(R.id.iv_pw_task_manager_clear);
        ImageView fanSelect = (ImageView) view.findViewById(R.id.iv_pw_taskmanager_fanselect);
        ImageView selectAll = (ImageView) view.findViewById(R.id.iv_pw_taskmanager_select_all);
        ImageView setting = (ImageView) view.findViewById(R.id.iv_pw_task_manager_setting);
        clear.setOnClickListener(this);
        fanSelect.setOnClickListener(this);
        selectAll.setOnClickListener(this);
        setting.setOnClickListener(this);

    }

    private void closePopupWindow() {
        if (popupWindow != null && popupWindow.isShowing() && !checkSelector()) {
            popupWindow.dismiss();
        }
    }

    private void openPopupWindow() {
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAtLocation(mShowAllTask, Gravity.LEFT | Gravity.BOTTOM, 0, 0);
        }
    }

    private void initView() {
        setContentView(R.layout.activity_task_manager);
        StatusBarUtil.setColor(this, 0x03a9f4);//设置系统状态栏的颜色
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_taskmanager);
        setSupportActionBar(toolbar);
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        mDialog = new SpotsDialog(this, "玩命加载中...");
        mTaskMem = (TextView) findViewById(R.id.tv_task_manager_mem);
        mTaskRunning = (TextView) findViewById(R.id.tv_task_manager_running);
        mShowAllTask = (ListView) findViewById(R.id.lv_task_manager_info);
        mTaskLabel = (TextView) findViewById(R.id.tv_task_manager_label);
        mAdapter = new MyAdapter();
        mShowAllTask.setAdapter(mAdapter);
        initPopupWindow();
    }

    /**
     * 判断是否关闭popupWindow
     *
     * @return
     */
    private boolean checkSelector() {
        for (RunningTaskBean bean : userTasks) {
            if (bean.isChecked()) {
                return true;
            }
        }
        for (RunningTaskBean bean : sysTasks) {
            if (bean.isChecked()) {
                return true;
            }
        }
        return false;
    }

    private void initEvent() {

        /**
         * listview的滚动事件
         */
        mShowAllTask.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem >= userTasks.size() + 1) {
                    // 显示系统apk
                    // 改变标签的内容为系统apk
                    mTaskLabel.setText("系统进程(" + sysTasks.size() + ")");
                } else {
                    mTaskLabel.setText("用户进程(" + userTasks.size() + ")");
                }
            }
        });
    }

    /**
     * popupWindow的点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_pw_task_manager_clear:
                // Toast.makeText(getApplicationContext(),"清理",Toast.LENGTH_SHORT).show();
                //清理进程
                requestRunTimePermission(new String[]{Manifest.permission.KILL_BACKGROUND_PROCESSES}, new PermissionListener() {
                    @Override
                    public void onGranted() {
                        clearTask();
                    }

                    @Override
                    public void onDenied(List<String> s) {
                        Toast.makeText(getApplicationContext(), "没有相应的权限", Toast.LENGTH_SHORT).show();
                    }
                });

                closePopupWindow();
                break;
            case R.id.iv_pw_taskmanager_fanselect:
                //反选
                fanSelectAll();
                closePopupWindow();

                break;
            case R.id.iv_pw_taskmanager_select_all:
                // 全选
                selectAll();
                closePopupWindow();

                break;
            case R.id.iv_pw_task_manager_setting:
               // Toast.makeText(getApplicationContext(), "设置", Toast.LENGTH_SHORT).show();
                closePopupWindow();
                Intent intent = new Intent(this,TaskSettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    //清理进程
    private void clearTask() {
        // 有些进程删除不掉，增强用户体验除了自己都可以删掉
        // 让用户看到清理了几个进程，释放了多少内存
        // 每个用户选中的进程都要清理

        long mClearMem =  0;// 记录内存的数量
        int clearNum = 0;// 记录清理多少个进程

        // 循环用户进程
        for (RunningTaskBean bean : userTasks) {
            if (bean.isChecked()) {
                // 用户选择

                // 清理的个数累计
                clearNum++;

                // 清理内存的数量累计 byte
                mClearMem += bean.getAppRunningSize();

                // 清理
                am.killBackgroundProcesses(bean.getPackageName());

                // 从容器中删除该数据
                userTasks.remove(bean);
            }
        }

        // 循环系统进程,调用迭代器
        for (RunningTaskBean bean : sysTasks) {
            if (bean.isChecked()) {
                // 用户选择

                // 清理的个数累计
                clearNum++;

                // 清理内存的数量累计 byte
                mClearMem += bean.getAppRunningSize();

                // 清理
                am.killBackgroundProcesses(bean.getPackageName());

                // 从容器中删除该数据
                sysTasks.remove(bean);
            }
        }

        Toast.makeText(getApplicationContext(), "清理了" + clearNum +
                "个进程，释放了" + Formatter.formatFileSize(getApplicationContext(), mClearMem), Toast.LENGTH_SHORT).show();

        availMem += mClearMem;//增加可用内存
        setTileMessage();//更新标题的信息
        mAdapter.notifyDataSetChanged();//通知界面listview的更新
    }

    private void fanSelectAll() {
        for (RunningTaskBean bean : userTasks) {
            if (bean.getPackageName().equals(getPackageName())) {
                bean.setChecked(false);
            } else {
                bean.setChecked(!bean.isChecked());
            }
        }
        for (RunningTaskBean bean : sysTasks) {
            bean.setChecked(!bean.isChecked());
        }
        mAdapter.notifyDataSetChanged();
    }

    private void selectAll() {
        for (RunningTaskBean bean : userTasks) {
            if (bean.getPackageName().equals(getPackageName())) {
                bean.setChecked(false);
            } else {
                bean.setChecked(true);
            }
        }
        for (RunningTaskBean bean : sysTasks) {
            bean.setChecked(true);
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 创建listview的适配器
     */
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (SPUtil.getBoolen(getApplicationContext(), MyConstants.SHOWSYSTASK,false)){
                return userTasks.size() + 1 + sysTasks.size() + 1;
            }else {
                return userTasks.size() + 1;
            }
        }

        @Override
        public RunningTaskBean getItem(int position) {
            RunningTaskBean bean = null;
            if (position <= userTasks.size()) {
                bean = userTasks.get(position - 1);
            } else {
                bean = sysTasks.get(position - 1 - 1 - userTasks.size());
            }
            return bean;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                //设置用户软件标签
                TextView user = new TextView(getApplicationContext());
                user.setText("用户进程（" + userTasks.size() + "）");
                user.setTextColor(Color.WHITE);
                user.setBackgroundResource(R.color.colorAdvancedTools);
                return user;
            } else if (position == userTasks.size() + 1) {
                //设置系统软件标签
                TextView sys = new TextView(getApplicationContext());
                sys.setText("系统进程（" + sysTasks.size() + "）");
                sys.setTextColor(Color.WHITE);
                sys.setBackgroundResource(R.color.colorAdvancedTools);
                return sys;
            } else {

                TaskManagerActivity.ViewHolder holder = new ViewHolder();
                if (convertView != null && convertView instanceof RelativeLayout) {
                    holder = (TaskManagerActivity.ViewHolder) convertView.getTag();
                } else {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_task_manage_item, null);
                    holder.appIcon = (ImageView) convertView.findViewById(R.id.iv_listview_task_icon);
                    holder.appName = (TextView) convertView.findViewById(R.id.tv_listview_task_name);
                    holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.tv_listview_app_check);
                    holder.appSize = (TextView) convertView.findViewById(R.id.tv_listview_task_size);
                    convertView.setTag(holder);
                }
                final RunningTaskBean infoBean = getItem(position);
                holder.appIcon.setImageDrawable(infoBean.getAppIcon());
                holder.appName.setText(infoBean.getAppName());
                holder.appSize.setText(Formatter.formatFileSize(getApplicationContext(), infoBean.getAppRunningSize()));
                holder.mCheckBox
                        .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                infoBean.setChecked(isChecked);
                                if (isChecked) {
                                    openPopupWindow();
                                } else {
                                    closePopupWindow();
                                }

                            }
                        });
                // 从bean取出复选框的状态显示
                holder.mCheckBox.setChecked(infoBean.isChecked());
                if (infoBean.getPackageName().equals(getPackageName())) {
                    holder.mCheckBox.setVisibility(View.GONE);
                } else {
                    holder.mCheckBox.setVisibility(View.VISIBLE);
                }
                final ViewHolder finalHolder = holder;
                convertView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (infoBean.getPackageName().equals(getPackageName())) {
                            // 自己
                            finalHolder.mCheckBox.setChecked(false);
                        }
                        // 设置复选框的反选操作
                        finalHolder.mCheckBox.setChecked(!finalHolder.mCheckBox.isChecked());
                        mAdapter.notifyDataSetChanged();
                    }
                });
                return convertView;
            }
        }


    }
    private class ViewHolder {
        ImageView appIcon;
        TextView appName;
        CheckBox mCheckBox;
        TextView appSize;
    }
}
