package com.customview.xiaohui.mobilesafe.activitys;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.customview.xiaohui.mobilesafe.R;
import com.customview.xiaohui.mobilesafe.domain.AppInfoBean;
import com.customview.xiaohui.mobilesafe.engine.LoadAllSoftwareEngine;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.onekeyshare.OnekeyShare;
import dmax.dialog.SpotsDialog;

public class SoftwareManageActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int LOADING = 1;
    private static final int FINISH = 2;
    private TextView mSDText;
    private TextView mRomText;
    private TextView mAppLabel;
    private ListView mShowAllSoftwrae;
    private SpotsDialog spotsDialog;
    private MyAdapter mAdapter;
    private List<AppInfoBean> appinfos;
    private AppInfoBean clcickPosition;
    private PopupWindow popupWindow;
    private List<AppInfoBean> userApp = new ArrayList<>();
    private List<AppInfoBean> sysApp = new ArrayList<>();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    spotsDialog.show();
                    mAppLabel.setVisibility(View.GONE);
                    mShowAllSoftwrae.setVisibility(View.GONE);
                    break;
                case FINISH:
                    spotsDialog.dismiss();
                    mAppLabel.setVisibility(View.VISIBLE);
                    mRomText.setText("Rom可用空间：" + Formatter.formatFileSize(getApplicationContext(), LoadAllSoftwareEngine.getRomAvailable()));
                    mSDText.setText("SD卡可用空间：" + Formatter.formatFileSize(getApplicationContext(), LoadAllSoftwareEngine.getSDAvailable()));
                    mAppLabel.setText("用户软件（" + userApp.size() + ")");
                    mShowAllSoftwrae.setVisibility(View.VISIBLE);
                    mAdapter.notifyDataSetChanged();
                    break;

            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {

        mShowAllSoftwrae.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == userApp.size() + 1) {
                    return;
                }
                int[] outlocation = new int[2];
                clcickPosition = (AppInfoBean) mShowAllSoftwrae.getItemAtPosition(position);
                view.getLocationInWindow(outlocation);
                showPopupWindow(view, outlocation[0] + 70, outlocation[1] - 20);
            }
        });
        /**
         * listview的滚动事件
         */
        mShowAllSoftwrae.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                closePopupWindow();
                if (firstVisibleItem >= userApp.size() + 1) {
                    // 显示系统apk
                    // 改变标签的内容为系统apk
                    mAppLabel.setText("系统软件(" + sysApp.size() + ")");
                } else {
                    mAppLabel.setText("用户软件(" + userApp.size() + ")");
                }
            }
        });
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(LOADING);

                appinfos = LoadAllSoftwareEngine.loadAllSoftware(SoftwareManageActivity.this);
                for (AppInfoBean appinfo : appinfos) {
                    if (appinfo.isSysApp()) {
                        sysApp.add(appinfo);
                    } else {
                        userApp.add(appinfo);
                    }
                }
                handler.sendEmptyMessage(FINISH);
            }
        }).start();
    }

    private void initView() {
        setContentView(R.layout.activity_software_manage);
        mRomText = (TextView) findViewById(R.id.tv_software_rom);
        mSDText = (TextView) findViewById(R.id.tv_software_sd);
        mAppLabel = (TextView) findViewById(R.id.tv_software_app_label);
        mShowAllSoftwrae = (ListView) findViewById(R.id.lv_software_show_all_software);
        spotsDialog = new SpotsDialog(SoftwareManageActivity.this);

        mAdapter = new MyAdapter();
        mShowAllSoftwrae.setAdapter(mAdapter);
        //romText.setText(android.text.format.Formatter.formatFileSize(this,));
        initPopupWindow();
    }

    /**
     * 初始化弹出窗体
     */
    private void initPopupWindow() {
        View view = View.inflate(getApplicationContext(), R.layout.click_listview_popup_window, null);

        popupWindow = new PopupWindow(view, -2, -2);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView uninstall = (ImageView) view.findViewById(R.id.iv_popup_window_uninstall);
        ImageView start = (ImageView) view.findViewById(R.id.iv_popup_window_start);
        ImageView share = (ImageView) view.findViewById(R.id.iv_popup_window_share);
        ImageView setting = (ImageView) view.findViewById(R.id.iv_popup_window_setting);
        uninstall.setOnClickListener(this);
        start.setOnClickListener(this);
        share.setOnClickListener(this);
        setting.setOnClickListener(this);

    }

    private void showPopupWindow(View parent, int x, int y) {
        popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, x, y);
        popupWindow.setAnimationStyle(-1);
    }

    private void closePopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    /**
     * popupwindow的点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_popup_window_uninstall:
                Toast.makeText(getApplicationContext(), "卸载", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_popup_window_start:
                //Toast.makeText(getApplicationContext(),"启动",Toast.LENGTH_SHORT).show();
                PackageManager pm = getPackageManager();
                String packageName = clcickPosition.getPackageName();
                Intent intent = pm.getLaunchIntentForPackage(packageName);
                startActivity(intent);
                break;
            case R.id.iv_popup_window_share:
                //Toast.makeText(getApplicationContext(), "分享", Toast.LENGTH_SHORT).show();
                showShare();
                break;
            case R.id.iv_popup_window_setting:
                //Toast.makeText(getApplicationContext(),"设置",Toast.LENGTH_SHORT).show();
                Intent setting = new Intent(
                        "android.settings.APPLICATION_DETAILS_SETTINGS");
                setting.setData(Uri.parse("package:" + clcickPosition.getPackageName()));
                startActivity(setting);

                break;
        }
    }

    /**
     * 分享软件
     */
    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
        oks.setTitle("标题");
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl("http://wizardev.com");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://wizardev.com");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("ShareSDK");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://wizardev.com");

// 启动分享GUI
        oks.show(this);
    }

    /**
     * 创建listview的适配器
     */
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // return userApp.size() + 1 + sysApp.size() + 1;
            if (appinfos != null) {
                return userApp.size() + 1 + sysApp.size() + 1;
            }
            return 0;
        }

        @Override
        public AppInfoBean getItem(int position) {
            AppInfoBean bean = null;
            if (position <= userApp.size()) {
                bean = userApp.get(position - 1);
            } else {
                bean = sysApp.get(position - 1 - 1 - userApp.size());
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
                user.setText("用户软件（" + userApp.size() + "）");
                user.setTextColor(Color.WHITE);
                user.setBackgroundResource(R.color.colorAdvancedTools);
                return user;
            } else if (position == userApp.size() + 1) {
                //设置系统软件标签
                TextView sys = new TextView(getApplicationContext());
                sys.setText("系统软件（" + sysApp.size() + "）");
                sys.setTextColor(Color.WHITE);
                sys.setBackgroundResource(R.color.colorAdvancedTools);
                return sys;
            } else {
                ViewHolder holder = new ViewHolder();
                if (convertView != null && convertView instanceof RelativeLayout) {
                    holder = (ViewHolder) convertView.getTag();
                } else {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_software_item, null);
                    holder.appIcon = (ImageView) convertView.findViewById(R.id.iv_listview_app_icon);
                    holder.appName = (TextView) convertView.findViewById(R.id.tv_listview_app_name);
                    holder.appLocation = (TextView) convertView.findViewById(R.id.tv_listview_app_location);
                    holder.appSize = (TextView) convertView.findViewById(R.id.tv_listview_app_size);
                    convertView.setTag(holder);
                }
                AppInfoBean infoBean = getItem(position);
                holder.appIcon.setImageDrawable(infoBean.getAppIcon());
                holder.appName.setText(infoBean.getAppName());
                holder.appSize.setText(Formatter.formatFileSize(getApplicationContext(), infoBean.getAppSize()));
                if (infoBean.isSD()) {
                    holder.appLocation.setText("SD卡存储");
                } else {
                    holder.appLocation.setText("ROM卡存储");
                }
                return convertView;
            }
        }

        class ViewHolder {
            ImageView appIcon;
            TextView appName;
            TextView appLocation;
            TextView appSize;
        }
    }
}
