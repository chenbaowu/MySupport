package com.cbw.view.fingerMove;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by cbw on 2017/7/8.
 * <p>
 * Scroller 使用
 */

public class MoveViewGroup extends BaseViewGroup {

    public MoveViewGroup(Context context) {
        super(context);
    }

    public MoveViewGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void InitData() {
        super.InitData();
    }

    @Override
    protected void SingleDown(MotionEvent event) {
        mScroller.abortAnimation();
        Init_M_Data(mDownX, mDownY);
    }

    @Override
    protected void SingleMove(MotionEvent event, float velocityX, float velocityY) {

        if (isNeedReInitOpData) {
            isNeedReInitOpData = false;
            Init_M_Data((int) event.getX(), (int) event.getY());
        }
        Run_M((int) event.getX(), (int) event.getY());

    }

    @Override
    protected void SingleUp(MotionEvent event) {

//        Log.i("bbb", "mVelocityX: " + mVelocityX);
        if (mVelocityX > VELOCITY_MIN) {
            smoothScrollTo(-1000, getScrollY(), 500);
        } else if (mVelocityX < -VELOCITY_MIN) {
            smoothScrollTo(0, getScrollY(), 500);
        } else if (getScrollX() < -200) {
            smoothScrollTo(-1000, getScrollY(), 300);
        } else {
            smoothScrollTo(0, getScrollY(), 300);
        }
    }

    @Override
    protected void TwoDown(MotionEvent event) {

        isNeedReInitOpData = true;
    }

    @Override
    protected void TwoMove(MotionEvent event, float velocityX, float velocityY) {

        if (isNeedReInitOpData) {
            isNeedReInitOpData = false;
            Init_M_Data((int) event.getX(), (int) event.getY());
        }
        Run_M((int) event.getX(), (int) event.getY());
    }


    @Override
    protected void TwoUp(MotionEvent event) {

        isNeedReInitOpData = true;
    }

}
