package com.customview.xiaohui.mobilesafe.activitys;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.customview.xiaohui.mobilesafe.R;
import com.customview.xiaohui.mobilesafe.engine.PhoneLocation;
import com.customview.xiaohui.mobilesafe.engine.SmsBackupEngine;

public class AdvancedToolsActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mQueryLocation;
    private EditText mEditText;
    private TextView mShowLocation;
    private TextView mSmsBackups;
    private TextView mSmsResume;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initEvent();
    }

    private void initEvent() {
        mQueryLocation.setOnClickListener(this);
        mSmsBackups.setOnClickListener(this);
        mSmsResume.setOnClickListener(this);
    }

    private void initView() {
        setContentView(R.layout.activity_anvanced_tools);
        mQueryLocation = (TextView) findViewById(R.id.tv_ad_tools_query_location);
        mSmsBackups = (TextView) findViewById(R.id.tv_ad_tools_backups);
        mSmsResume = (TextView) findViewById(R.id.tv_ad_tools_resume);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ad_tools_query_location:
                showQueryLocationDialog();
                break;
            case R.id.tv_ad_tools_backups:
                backupSms();

                break;
            case R.id.tv_ad_tools_resume:
                resumeSms();
                break;
        }
    }

    private void resumeSms() {
        SmsBackupEngine.smsResume(this, new SmsBackupEngine.SetProgressDialogListener() {
            @Override
            public void onShow() {
                progressDialog.show();
            }

            @Override
            public void onSetMax(int max) {
                progressDialog.setMax(max);
            }

            @Override
            public void onSetProgress(int progress) {
                progressDialog.setProgress(progress);
            }

            @Override
            public void onDismiss() {
                progressDialog.cancel();
            }
        });
    }

    private void backupSms() {
        SmsBackupEngine.backupSms(this, new SmsBackupEngine.SetProgressDialogListener() {
            @Override
            public void onShow() {
                progressDialog.show();
            }

            @Override
            public void onSetMax(int max) {
                progressDialog.setMax(max);
            }

            @Override
            public void onSetProgress(int progress) {
                progressDialog.setProgress(progress);
            }

            @Override
            public void onDismiss() {
                progressDialog.cancel();
            }
        });
    }

    private void showQueryLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = View.inflate(this, R.layout.qurey_location_dialog, null);

        mShowLocation = (TextView) v.findViewById(R.id.tv_dialog_show_location);
        mEditText = (EditText) v.findViewById(R.id.et_query_location_dialog_num);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String num = String.valueOf(s);
                String location = PhoneLocation.getCategray(num);
                mShowLocation.setText(location);
            }
        });
        builder.setView(v);
        builder.show();
    }
}
