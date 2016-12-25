package com.customview.xiaohui.mobilesafe.activitys;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.customview.xiaohui.mobilesafe.R;
import com.customview.xiaohui.mobilesafe.domain.VersionBean;
import com.customview.xiaohui.mobilesafe.utils.MyConstants;
import com.customview.xiaohui.mobilesafe.utils.SPUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {
    private RelativeLayout mRelativeLayout;
    private static final String TAG = "Wizardev";
    private TextView mVersionName;
    private int versionCode;
    private static final int LOADMAIN = 1;
    private static final int SHOWUPDATEDIALOG = 2;
    private static final int ERROR = 3;
    private long startTimeMillis;
    private VersionBean versionBean;
    private AnimationSet as;
    private ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        initView();
        initAnimation();
        initEvent();//监听事件
        timeInitialization();//处理耗时操作
        initData();
    }
    private void timeInitialization(){
        //一开始动画，就应该干耗时的业务（网络，本地数据初始化，数据的拷贝等）
        if (SPUtil.getBoolen(getApplicationContext(), MyConstants.ISAUTOUPDATE, false)) {
            //true 自动更新
            // 检测服务器的版本
            checkVersion();
        }
        //增加自己的耗时功能处理

    }
    private void initEvent() {
        //监听动画，处理业务逻辑
        as.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
               timeInitialization();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!SPUtil.getBoolen(getApplicationContext(), MyConstants.ISAUTOUPDATE, false)) {
                    loadMain();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    private void initData() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;
            mVersionName.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void checkVersion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int errorCode = -1;
                BufferedReader bufferedReader = null;
                HttpURLConnection connection = null;
                try {
                    startTimeMillis = System.currentTimeMillis();
                    URL url = null;
                    try {
                        url = new URL("http://www.wizardev.com/mobilesafe/version.json");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setDoInput(true);
                    connection.setRequestMethod("GET");
                    int code = connection.getResponseCode();
                    if (code == 200){
                        InputStream inputStream = connection.getInputStream();
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String line = "";
                        StringBuilder stringBuilder = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null){
                            stringBuilder.append(line);
                        }

                        VersionBean versionBean = parseJSON(stringBuilder.toString());

                    }else {
                        //找不到网页
                        errorCode = connection.getResponseCode();
                    }
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    //网络连接失败
                    errorCode = 4001;
                } catch (JSONException e) {
                    //Json格式错误
                    e.printStackTrace();
                    errorCode = 4002;
                }finally {
                    Message m= Message.obtain();
                    //判断错误代码

                    if (errorCode == -1){
                        m.what = isNewVersion(versionBean);
                    }else {
                        m.what = ERROR;
                        m.arg1 = errorCode;
                    }
                    //进行两秒延时
                    long endTimeMillis = System.currentTimeMillis();
                    if (endTimeMillis - startTimeMillis < 2000){
                        SystemClock.sleep(2000-(endTimeMillis-startTimeMillis));
                    }
                    handler.sendMessage(m);
                    try {
                        if (bufferedReader == null || connection == null){
                            return;
                        }
                        bufferedReader.close();
                        connection.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    private int isNewVersion(VersionBean versionBean) {
        int serverVersion = versionBean.getVersion();

        if (serverVersion == versionCode){
           /* Message message = Message.obtain();
            message.what = LOADMAIN;
            handler.sendMessage(message);*/
            return  LOADMAIN;
        }else {
            /*Message message = Message.obtain();
            message.what = SHOWUPDATEDIALOG;
            handler.sendMessage(message);*/
            return  SHOWUPDATEDIALOG;
        }
    }

    private VersionBean parseJSON(String stringBuilder) throws JSONException {
        versionBean = new VersionBean();

            JSONObject object = new JSONObject(stringBuilder);
            int version = object.getInt("version");
            String url = object.getString("url");
            String desc = object.getString("desc");

            versionBean.setVersion(version);
            versionBean.setUrl(url);
            versionBean.setDesc(desc);
            Log.i(TAG, "parseJSON: "+versionBean.toString());
            return versionBean;

    }

    private void initAnimation() {

        as = new AnimationSet(false);//动画合集
        //旋转动画
        RotateAnimation ra = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        ra.setFillAfter(true);
        ra.setDuration(2000);
        //比例动画
        ScaleAnimation sa = new ScaleAnimation(0,1,0,1,Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        sa.setFillAfter(true);
        sa.setDuration(2000);
        //添加动画
        as.addAnimation(ra);
        as.addAnimation(sa);
        mRelativeLayout.startAnimation(as);

    }

    private void initView() {
        setContentView(R.layout.activity_splash);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.activity_splash);
        mVersionName = (TextView) findViewById(R.id.tv_splash_version_name);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_splash_download_progress);
    }

    private void loadMain(){
        Intent i = new Intent(SplashActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }


    /**
     * 处理消息
     */
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOADMAIN:
                    loadMain();
                    break;
                //对错误进行处理
                case ERROR:
                    switch (msg.arg1){
                        case 404://资源找不到
                            Toast.makeText(getApplicationContext(), "404资源找不到", Toast.LENGTH_SHORT).show();
                            break;
                        case 4001://找不到网络
                            Toast.makeText(getApplicationContext(), "4001没有网络", Toast.LENGTH_SHORT).show();
                            break;
                        case 4002://json格式错误
                            Toast.makeText(getApplicationContext(), "4002json格式错误", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    loadMain();
                    break;
                case SHOWUPDATEDIALOG:
                    updateDialog();
                    break;
            }

        }
    };

    private void updateDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                loadMain();
            }
        });
        dialog.setTitle("提示").setMessage("是否更新新版本？新版本特性如下："+versionBean.getDesc())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        downloadApk();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loadMain();
                        finish();
                    }
                });
        dialog.show();
    }



    private void downloadApk() {
        try {
            RequestParams params = new RequestParams(versionBean.getUrl());
            params.setAutoRename(true);
            params.setSaveFilePath("/sdcard/mobilesafe/mobilesafe.apk");
            x.http().get(params, new Callback.ProgressCallback<File>() {
                @Override
                public void onSuccess(File result) {
                    install();

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {

                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }

                @Override
                public void onWaiting() {

                }

                @Override
                public void onStarted() {

                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setMax((int) total);
                    mProgressBar.setProgress((int) current);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    private void install() {
        /**
         * <intent-filter>
         <action android:name="android.intent.action.VIEW" />
         <category android:name="android.intent.category.DEFAULT" />
         <data android:scheme="content" />
         <data android:scheme="file" />
         <data android:mimeType="application/vnd.android.package-archive" />
         </intent-filter>
         */
        mProgressBar.setVisibility(View.GONE);
        Intent i = new Intent("android.intent.action.VIEW");
        Uri uri = Uri.fromFile(new File("/sdcard/mobilesafe","mobilesafe.apk"));
        i.addCategory("android.intent.category.DEFAULT");
        i.setDataAndType(uri, "com/customview/xiaohui/mobilesafe/application/vnd.android.package-archive");
        startActivityForResult(i,0);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        //如果用户取消更新apk，那么直接进入主界面
        loadMain();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
