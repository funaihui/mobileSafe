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
