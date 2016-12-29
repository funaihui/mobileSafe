package com.customview.xiaohui.mobilesafe.engine;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.customview.xiaohui.mobilesafe.domain.SmsBean;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by wizardev on 2016/12/28.
 */

public class SmsBackupEngine {

    private static SetProgressDialogListener mDialogListener;
    private static class Data{
        int progress;
    }

    /**
     * 通过接口回调，设置dialog
     */
    public interface SetProgressDialogListener{
        void onShow();
        void onSetMax(int max);
        void onSetProgress(int progress);
        void onDismiss();
    }

    /**
     * 短信的备份
     * @param context
     * @param listener
     */
    public static void backupSms(final Activity context, SetProgressDialogListener listener) {
        mDialogListener = listener;

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Uri uri = Uri.parse("content://sms");
                //获取电话记录的联系人游标
                final Cursor cursor = context.getContentResolver().query(uri, new String[]{"address", "date", "body","read"}, null, null, null);
                final int count = cursor.getCount();
                File smsBackup = new File(Environment.getExternalStorageDirectory(), "smsBack.json");
                    if (smsBackup.exists()){
                        smsBackup.delete();
                    }
                try {
                    PrintWriter out = new PrintWriter(new FileOutputStream(smsBackup));

                    context.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //  progressDialog.show();
                            mDialogListener.onShow();
                            //progressDialog.setMax(cursor.getCount());
                            mDialogListener.onSetMax(count);
                        }
                    });
                    final Data data = new Data();
                    out.println("{" + "\"count\":" + "\"" + cursor.getCount() + "\",");
                    out.println("\"messages\":[");
                    while (cursor.moveToNext()) {
                        data.progress++;
                        if (cursor.isFirst()) {
                            out.println("{");
                        } else {
                            out.println(",{");
                        }
                        out.println("\"address\":" + "\"" + cursor.getString(0) + "\",");
                        out.println("\"date\":" + "\"" + cursor.getString(1) + "\",");
                        out.println("\"body\":" + "\"" + cursor.getString(2) + "\",");
                        out.println("\"read\":" + "\"" + cursor.getString(3) + "\"");
                        out.println("}");
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // progressDialog.setProgress(data.progress);
                                mDialogListener.onSetProgress(data.progress);
                            }
                        });

                    }
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            /// progressDialog.cancel();
                            mDialogListener.onDismiss();
                        }
                    });
                    out.println("]}");
                    out.close();
                    cursor.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 短信的还原
     * @param context
     * @param listener
     */
    public static void smsResume(final Activity context, final SetProgressDialogListener listener){
        mDialogListener = listener;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Uri uri = Uri.parse("content://sms");
                File smsBackup = new File(Environment.getExternalStorageDirectory(), "smsBack.json");

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(smsBackup)));
                    StringBuilder builder = new StringBuilder();
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null){
                        builder.append(line);
                    }
                    String jsonString = builder.toString();
                    final SmsBean smsBean = parseJson(jsonString);
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onShow();
                            listener.onSetMax(Integer.valueOf(smsBean.getCount()));
                        }
                    });
                    final Data data = new Data();
                    List<SmsBean.MessagesBean> messages = smsBean.getMessages();
                    for (SmsBean.MessagesBean message : messages) {
                        data.progress++;
                        ContentValues values = new ContentValues();
                        values.put("address",message.getAddress());
                        values.put("body",message.getBody());
                        values.put("date",message.getDate());
                        values.put("read",message.getRead());
                        context.getContentResolver().insert(uri,values);
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSetProgress(data.progress);
                            }
                        });
                    }
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onDismiss();
                        }
                    });
                    bufferedReader.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static SmsBean parseJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, SmsBean.class);

    }


}
