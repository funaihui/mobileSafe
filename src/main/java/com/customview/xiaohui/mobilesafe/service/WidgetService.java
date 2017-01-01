package com.customview.xiaohui.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.customview.xiaohui.mobilesafe.R;
import com.customview.xiaohui.mobilesafe.engine.LoadRunningAppEngine;
import com.customview.xiaohui.mobilesafe.receiver.AppWidget;

import java.util.Timer;
import java.util.TimerTask;

public class WidgetService extends Service {

    private Timer timer;
    private TimerTask task;
    private AppWidgetManager awm;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {


        awm = AppWidgetManager.getInstance(getApplicationContext());

        timer = new Timer();

        task = new TimerTask() {

            @Override
            public void run() {


                ComponentName provider = new ComponentName(getApplicationContext(), AppWidget.class);

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.app_widget);


                int runningNumber = LoadRunningAppEngine.getRunningAppInfo(getApplicationContext()).size();

                long availMem =  LoadRunningAppEngine.getFreeMem(getApplicationContext());


                long totalMem =  LoadRunningAppEngine.getTotalMem(getApplicationContext());
                // String availMemStr = Formatter.formatFileSize(getApplicationContext(), availMem);

                float percent = (float) (1.0-((float) availMem / (float) totalMem));
                String s = percent + "";
                int i = s.indexOf(".");
                s = s.substring(i+1,i+3);
                views.setTextViewText(R.id.used_num, "" + s);

                views.setProgressBar(R.id.rom_progressBar1,100,Integer.valueOf(s),false);
                Intent intent = new Intent("com.wizardev.widget.cleartask");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                Intent intent1 = new Intent("com.customview.xiaohui.mobilesafe.activitys.MainActivity");
                PendingIntent pendingIntent1 = PendingIntent.getActivity(getApplicationContext(), 0, intent1, 0);
                views.setOnClickPendingIntent(R.id.kill_all, pendingIntent );
                views.setOnClickPendingIntent(R.id.img_icon, pendingIntent1 );


                awm.updateAppWidget(provider, views);

            }
        };
        timer.schedule(task, 0, 1000 * 5);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        timer = null;
        task = null;
        super.onDestroy();
    }

}
