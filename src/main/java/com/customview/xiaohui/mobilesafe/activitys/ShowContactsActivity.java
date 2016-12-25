package com.customview.xiaohui.mobilesafe.activitys;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.customview.xiaohui.mobilesafe.domain.ContactsBean;
import com.customview.xiaohui.mobilesafe.engine.ReadContacts;

import java.util.List;

public class ShowContactsActivity extends BaseContactsActivity {


    @Override
    public List<ContactsBean> getContacts() {
        //首先获取权限
        int permissionCheck = ContextCompat.checkSelfPermission(ShowContactsActivity.this,
                Manifest.permission.READ_CONTACTS);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

            //有权限,进行响应的操作
            return ReadContacts.ReadAllContacts(getApplicationContext());
        } else if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            //  Toast.makeText(getApplicationContext(),"没有响应的权限",Toast.LENGTH_SHORT).show();
            //没有权限，获取相应的权限
            ActivityCompat.requestPermissions(ShowContactsActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }
        return null;

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //有权限
                    //  return ReadContacts.ReadAllContacts(getApplicationContext());
                    getContacts();

                } else {
                    Toast.makeText(getApplicationContext(), "请求权限失败", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}
