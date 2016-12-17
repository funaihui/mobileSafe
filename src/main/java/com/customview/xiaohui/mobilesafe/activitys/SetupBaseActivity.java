package com.customview.xiaohui.mobilesafe.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.customview.xiaohui.mobilesafe.R;

/**
 * Created by wizardev on 2016/12/17.
 */

public abstract class SetupBaseActivity extends AppCompatActivity {
    private GestureDetector gd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initGesture();
        initData();
        initEvent();
    }

    public void initData() {
    }

    public void initEvent() {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gd.onTouchEvent(event);
        return true;
    }

    /**
     * 滑动监听
     */
    private void initGesture() {
        gd = new GestureDetector(this, new GestureDetector.OnGestureListener() {

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

                if (v > 100) {
                    float differ = motionEvent.getX() - motionEvent1.getX();
                    if (differ < 0 && Math.abs(differ) > 100) {
                        //说明是上一步
                        previous(null);
                    }
                    if (differ > 0 && Math.abs(differ) > 100) {
                        //说明是下一步
                        next(null);
                    }
                }

                return true;
            }

            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }


        });
    }

    public abstract void initView();

    public void next(View v) {
        nextActivity();
        nextAnimation();
    }

    private void nextAnimation() {
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }

    public void previous(View v) {
        previousActivity();
        previousAnimation();
    }

    private void previousAnimation() {
        overridePendingTransition(R.anim.previous_in, R.anim.previous_out);
    }

    protected abstract void previousActivity();

    protected abstract void nextActivity();

    public void startActivity(Class type) {
        Intent i = new Intent(this, type);
        startActivity(i);
        finish();
    }
}
