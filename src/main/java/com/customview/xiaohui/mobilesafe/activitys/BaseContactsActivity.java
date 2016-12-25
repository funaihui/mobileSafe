package com.customview.xiaohui.mobilesafe.activitys;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.customview.xiaohui.mobilesafe.R;
import com.customview.xiaohui.mobilesafe.domain.ContactsBean;
import com.customview.xiaohui.mobilesafe.utils.MyConstants;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseContactsActivity extends ListActivity {
    private static final int LOADING = 0;
    private static final int FINISH = 1;
    private MyAdapter mAdapter;
    private ListView mListView;
    List<ContactsBean> mContacts = new ArrayList<>();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    showLoadingDialog();
                    break;
                case FINISH:
                    dismissLoadingDialog();
                    mAdapter.notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void dismissLoadingDialog() {
        if (mDialog != null){
            mDialog.dismiss();
            mDialog = null;
        }
    }

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               String num = mContacts.get(i).getNumber();
                Intent result = new Intent();
                result.putExtra(MyConstants.SAVENUM,num);
                setResult(1,result);
                finish();
            }
        });
    }

    private void showLoadingDialog() {

        mDialog = new ProgressDialog(this);
        mDialog.setTitle("提示");
        mDialog.setMessage("玩命加载中......");
        mDialog.show();
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                message.what = LOADING;
                handler.sendMessage(message);

                mContacts = getContacts();

                Message message1 = Message.obtain();
                message1.what = FINISH;
                handler.sendMessage(message1);
            }
        }).start();
    }
    public abstract List<ContactsBean> getContacts();
    private void initView() {
        mListView = getListView();
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (mContacts != null){
                return mContacts.size();
            }
           return 0;
        }

        @Override
        public Object getItem(int i) {
            return mContacts.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                holder = new ViewHolder();
                view = View.inflate(getApplicationContext(), R.layout.listview_contacts_item, null);
                view.setTag(holder);
                holder.contactName = (TextView) view.findViewById(R.id.tv_listview_contact_name);
                holder.contactNum = (TextView) view.findViewById(R.id.tv_listview_contact_num);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            String contactName = mContacts.get(i).getName();
            String contactNum = mContacts.get(i).getNumber();
            holder.contactName.setText(contactName);
            holder.contactNum.setText(contactNum);
            return view;
        }
    }

    class ViewHolder {
        TextView contactName;
        TextView contactNum;

    }
}
