package com.customview.xiaohui.mobilesafe.activitys;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.customview.xiaohui.mobilesafe.utils.PermissionListener;
import com.customview.xiaohui.mobilesafe.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

public class PermissionActivity extends AppCompatActivity {
    private static PermissionListener mListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    static List<String> mPermissions = new ArrayList<>();

    public static void requestRunTimePermission(String[] permissions,PermissionListener listener){
        mListener = listener;
        Activity topActivity = PermissionUtils.getTopActivity();
        for (String permission:permissions) {
            Log.i("Wizardev", "getPermission: "+permission);
            if (ContextCompat.checkSelfPermission(topActivity, permission) != PackageManager.PERMISSION_GRANTED){
                mPermissions.add(permission);
            }
        }
        if (!mPermissions.isEmpty()){
            ActivityCompat.requestPermissions(topActivity,mPermissions.toArray(new String[mPermissions.size()]),1);
        }else {
            mListener.onGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                List<String> deniedPermissions = new ArrayList<>();
                if (grantResults.length>0){
                    for (int i = 0; i < mPermissions.size(); i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            deniedPermissions.add(permissions[i]);
                        }
                    }
                    if (deniedPermissions.isEmpty()){
                        mListener.onGranted();
                    }else {
                        mListener.onDenied(deniedPermissions);
                    }
                }
                break;
        }
    }
}
