package com.customview.xiaohui.mobilesafe.activitys;

import com.customview.xiaohui.mobilesafe.domain.ContactsBean;
import com.customview.xiaohui.mobilesafe.engine.ReadContacts;

import java.util.List;

public class ShowSmsLogsActivity extends BaseContactsActivity {


    @Override
    public List<ContactsBean> getContacts() {
        return ReadContacts.readSmslog(getApplicationContext());
    }
}
