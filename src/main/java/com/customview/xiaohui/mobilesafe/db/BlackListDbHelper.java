package com.customview.xiaohui.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.customview.xiaohui.mobilesafe.utils.MyConstants;

/**
 * Created by wizardev on 2016/12/21.
 */

public class BlackListDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String BLACKLIST = "create table blacklist(" +
            "id integer primary key autoincrement," +
            "phone text," +
            "mode int);";
    public BlackListDbHelper(Context context) {
        super(context, MyConstants.TABLENAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(BLACKLIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
