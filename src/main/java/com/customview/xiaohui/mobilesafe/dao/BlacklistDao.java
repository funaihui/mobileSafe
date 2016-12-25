package com.customview.xiaohui.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.customview.xiaohui.mobilesafe.db.BlackListDbHelper;
import com.customview.xiaohui.mobilesafe.domain.BlacklistBean;
import com.customview.xiaohui.mobilesafe.utils.MyConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wizardev on 2016/12/21.
 */

public class BlacklistDao {
    private static BlacklistDao mBlacklistDao;
    private BlackListDbHelper mHelper;

    private BlacklistDao(Context context) {
        mHelper = new BlackListDbHelper(context);

    }

    public static BlacklistDao getInstance(Context context) {
        if (mBlacklistDao == null) {
            mBlacklistDao = new BlacklistDao(context);
        }
        return mBlacklistDao;
    }

    public void add(BlacklistBean bean) {
        add(bean.getPhone(), bean.getMode());
    }

    /**
     * 添加数据到数据库
     *
     * @param phone
     * @param mode
     */
    public void add(String phone, int mode) {
        remove(phone);
        SQLiteDatabase sqLiteDatabase = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MyConstants.PHONE, phone);
        values.put(MyConstants.MODE, mode);
        sqLiteDatabase.insert(MyConstants.TABLENAME, MyConstants.PHONE, values);
        sqLiteDatabase.close();
    }

    /**
     * 删除指定的数据
     *
     * @param phone
     */
    public void remove(String phone) {
        SQLiteDatabase sqLiteDatabase = mHelper.getReadableDatabase();
        sqLiteDatabase.delete(MyConstants.TABLENAME, MyConstants.PHONE + "=?", new String[]{phone});
        sqLiteDatabase.close();
    }

    /**
     * 获取数据库的全部数据
     *
     * @return
     */
    public List<BlacklistBean> checkAll() {
        List<BlacklistBean> list = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = mHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(MyConstants.TABLENAME, new String[]{MyConstants.PHONE, MyConstants.MODE}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            BlacklistBean bean = new BlacklistBean();
            String phone = cursor.getString(0);
            int mode = cursor.getInt(1);
            bean.setPhone(phone);
            bean.setMode(mode);
            list.add(bean);
        }
        cursor.close();
        sqLiteDatabase.close();
        return list;


    }

    /**
     * 总得数据条目
     *
     * @return
     */
    public int totalCount() {
        SQLiteDatabase sqLiteDatabase = mHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(MyConstants.TABLENAME, new String[]{"count(1)"}, null, null, null, null, null);
        cursor.moveToNext();
        int total = cursor.getInt(0);
        cursor.close();
        sqLiteDatabase.close();
        return total;
    }

    /**
     * 总共多少页
     *
     * @param numPerPage 每页的条目数
     * @return 页数
     */
    public int pageNum(int numPerPage) {
        int pageNum = (totalCount() + numPerPage) / numPerPage;
        return pageNum;
    }

    /**
     * 查询每页的数据
     *
     * @param index      分页的起始位置
     * @param pagePerNum 每页的条目数
     * @return
     */
    public List<BlacklistBean> moreDatas(int index, int pagePerNum) {
        List<BlacklistBean> list = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = mHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select phone,mode from " + MyConstants.TABLENAME + " order by id desc limit ?,?",
                new String[]{index + "", pagePerNum + ""});
        while (cursor.moveToNext()) {
            BlacklistBean bean = new BlacklistBean();
            String phone = cursor.getString(0);
            int mode = cursor.getInt(1);
            bean.setPhone(phone);
            bean.setMode(mode);
            list.add(bean);
        }
        cursor.close();
        sqLiteDatabase.close();
        return list;
    }

    /**
     * 0:不拦截
     * 1：拦截电话
     * 2：拦截短信
     * 3：全部拦截
     * @param phone
     * @return
     */
    public int getMode(String phone){
        SQLiteDatabase sqLiteDatabase = mHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(MyConstants.TABLENAME,new String[]{MyConstants.MODE},"phone=?",new String[]{phone},null,null,null);
        int mode = 0;
        if (cursor.moveToNext()){
            mode = cursor.getInt(0);
        }
        return mode;
    }
}
