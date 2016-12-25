package com.customview.xiaohui.mobilesafe.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.customview.xiaohui.mobilesafe.R;

/**
 * Created by wizardev on 2016/12/20.
 */

public class MyCardView extends RelativeLayout {
    private static final String TAG = "Wizardev";
    private String mTitle;
    private String mContent;
    private View setView;
    private TextView settingTitle;
    private TextView settingContent;
    private CheckBox mCheckBox;
    private OnCheckBoxStatusListener mListener;
    boolean isChecked = false;
    private String[] mSplitContent;
    public MyCardView(Context context) {
        this(context, null);
    }

    public MyCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyCardView, 0, 0);
        mTitle = array.getString(R.styleable.MyCardView_title);
        mContent = array.getString(R.styleable.MyCardView_content);
        mSplitContent = mContent.split("-");
        array.recycle();
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isChecked = b;
                mListener.getStatus(b);
                if (b){
                    settingContent.setText(mSplitContent[0]);
                    settingContent.setTextColor(Color.GREEN);
                }else {
                    settingContent.setText(mSplitContent[1]);
                    settingContent.setTextColor(Color.RED);
                }
            }
        });
    }

    private void initData() {
        settingTitle.setText(mTitle);
        settingContent.setText(mSplitContent[1]);
    }

    private void initView() {
        setView = View.inflate(getContext(), R.layout.setting_item, null);
        settingTitle = (TextView) setView.findViewById(R.id.tv_setting_title);
        settingContent = (TextView) setView.findViewById(R.id.tv_setting_content);
        mCheckBox = (CheckBox) setView.findViewById(R.id.cb_setting_flag);
        addView(setView);
    }

    /**
     * 定义接口，获得checkbox的状态
     */
    public interface OnCheckBoxStatusListener{
        void getStatus(boolean isChecked);
    }

    public void SetOnCheckBoxStatusListener(OnCheckBoxStatusListener listener){
        this.mListener = listener;
    }

    public void setStatus(boolean b){
        mCheckBox.setChecked(b);
    }
}
