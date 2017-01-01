package com.customview.xiaohui.mobilesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.customview.xiaohui.mobilesafe.R;
import com.customview.xiaohui.mobilesafe.service.WidgetService;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {
    private static final String TAG = "Wizardev";
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        /*int totalMem = (int) LoadRunningAppEngine.getTotalMem(context);

        int freeMem = (int) LoadRunningAppEngine.getFreeMem(context);
        views.setTextViewText(R.id.used_num, "" + freeMem / totalMem * 100);
        views.setProgressBar(R.id.process_num, totalMem, freeMem, false);*/
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        Log.i(TAG, "updateAppWidget: ");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        Log.i(TAG, "onUpdate: ");
    }

    @Override
    public void onEnabled(Context context) {

        Log.i(TAG, "onEnabled: ");
        Intent intent = new Intent(context, WidgetService.class);
        context.startService(intent);
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        Intent intent = new Intent(context, WidgetService.class);
        context.stopService(intent);
        Log.i(TAG, "onDisabled: ");

    }


}

