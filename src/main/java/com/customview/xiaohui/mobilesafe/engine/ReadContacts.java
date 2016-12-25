package com.customview.xiaohui.mobilesafe.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.customview.xiaohui.mobilesafe.domain.ContactsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wizardev on 2016/12/18.
 */

public class ReadContacts {
    private static final String TAG = "ReadContacts";

    public static List<ContactsBean> readSmslog(Context context){
        //1，电话日志的数据库
        //2,通过分析，db不能直接访问，需要内容提供者访问该数据库
        //3,看上层源码 找到uri content://sms
        Uri uri = Uri.parse("content://sms");
        //获取电话记录的联系人游标
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"address"}, null, null, " _id desc");
        List<ContactsBean> datas = new ArrayList<ContactsBean>();

        while (cursor.moveToNext()) {
            ContactsBean bean = new ContactsBean();

            String phone = cursor.getString(0);//获取号码
            //String name = cursor.getString(1);//获取名字

            //bean.setName(name);
            bean.setNumber(phone);

            //添加数据
            datas.add(bean);

        }
        return datas;

    }

    public static List<ContactsBean> ReadCallLogs(Context context){

        List<ContactsBean> list = new ArrayList<>();

        Uri callLogUri = Uri.parse("content://call_log/calls");
        Cursor cursor = context.getContentResolver().query(callLogUri, new String[]{"number","name"}, null, null, "_id desc");
        Log.i(TAG, "ReadCallLogs: "+cursor.moveToNext());

        while (cursor.moveToNext()){
            ContactsBean bean = new ContactsBean();
            bean.setName(cursor.getString(1));
            bean.setNumber(cursor.getString(0));
            list.add(bean);
        }
        return list;
    }


    /**
     *查询本机的联系人
     * @param context
     */
    public static List<ContactsBean> ReadAllContacts(Context context){
        /**
         * android:name="ContactsProvider2"
         android:authorities="contacts;com.android.contacts"
         android:label="@string/provider_label"
         android:multiprocess="false"
         android:readPermission="android.permission.READ_CONTACTS"
         android:writePermission="android.permission.WRITE_CONTACTS"
         */

        List<ContactsBean> contactsList = new ArrayList<>();
        Uri uriContacts = Uri.parse("content://com.android.contacts/contacts");
        Uri uriData = Uri.parse("content://com.android.contacts/data");
        Cursor cursor = context.getContentResolver().query(uriContacts, new String[]{"_id"}, null, null, null);
        while (cursor.moveToNext()){
            String id = cursor.getString(0);
            Log.i(TAG, "ReadAllContacts:id: "+id);
            ContactsBean bean = new ContactsBean();
            Cursor cursor1 = context.getContentResolver().query(uriData, new String[]{"data1","mimetype"}, "raw_contact_id=?", new String[]{id}, null);
            while (cursor1.moveToNext()){

                String data1 = cursor1.getString(0);
                Log.i(TAG, "ReadAllContacts:data1: "+data1);
                String mimeType = cursor1.getString(1);
                Log.i(TAG, "ReadAllContacts:mimeTypeID: "+mimeType);
                if (mimeType.equals("vnd.android.cursor.item/name")){
                    bean.setName(data1);
                }else if(mimeType.equals("vnd.android.cursor.item/phone_v2")){
                    bean.setNumber(data1);
                }
            }
            contactsList.add(bean);
            cursor1.close();
        }
        cursor.close();
        return contactsList;
    }
}
