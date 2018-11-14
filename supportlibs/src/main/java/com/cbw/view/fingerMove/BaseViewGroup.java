package com.cbw.view.fingerMove;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created by cbw on 2017/7/7.
 * <p>
 * 基于Scroller惯性滑动的基类
 * 使用速度检测 VelocityTracker
 */

public abstract class BaseViewGroup extends FrameLayout {

    protected Context mContext;
    protected int mDownX, mDownY;
    protected int mMoveX, mMoveY;
    protected int mUpX, mUpY;

    protected int mDownX1, mDownY1;
    protected int mDownX2, mDownY2;

    private boolean m_isTouch = false; // 触摸屏幕的时候 true，有任何手离开的时候 false
    protected boolean isNeedReInitOpData = false; // 用于多手触控导致的
    protected int mPointerCount;

    protected int mOldX, mOldY;

    // 速度监控器
    private VelocityTracker mVelocityTracker;
    public float mVelocityX, mVelocityY; // 正数向右 负数向左

    public static final int VELOCITY_MIN = 1200;

    public BaseViewGroup(Context context) {
        super(context);
        mContext = context;
        InitData();
    }

    public BaseViewGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        InitData();
    }

    protected void InitData() {
        mScroller = new Scroller(mContext);
    }

    public boolean mUILock;

    public void setUILock(boolean UILock) {
        this.mUILock = UILock;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mUILock) {
            return true;
        }

        mPointerCount = event.getPointerCount();
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                mDownX = (int) event.getX();
                mDownY = (int) event.getY();
                SingleDown(event);
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                mDownX1 = (int) event.getX(0);
                mDownY1 = (int) event.getY(0);
                mDownX2 = (int) event.getX(1);
                mDownY2 = (int) event.getY(1);
                TwoDown(event);
                break;
            }

            case MotionEvent.ACTION_MOVE: {

                //设置units的值为1000，意思为一秒时间内运动了多少个像素
                mVelocityTracker.computeCurrentVelocity(1000,ViewConfiguration.getMaximumFlingVelocity());
                mVelocityX = mVelocityTracker.getXVelocity();
                mVelocityY = mVelocityTracker.getYVelocity();

                if (event.getPointerCount() == 1) {
                    mMoveX = (int) event.getX();
                    mMoveY = (int) event.getY();
                    SingleMove(event, mVelocityX, mVelocityY);
                } else {
                    TwoMove(event, mVelocityX, mVelocityY);
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                mUpX = (int) event.getX();
                mUpY = (int) event.getY();
                SingleUp(event);

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                TwoUp(event);
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE: {
                if (event.getPointerCount() == 1) {
                    SingleUp(event);
                } else if (event.getPointerCount() > 1) {
                    TwoUp(event);
                }

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
            }
        }

        return true;
    }

    /**
     * Scroller 改变view显示内容的边距，不改变touch的xy，实现原理相当于canvas.translate();
     * 对于viewGroup子view的触点范围会跟着改变
     * 正数向左 负数向右滑动
     */
    public Scroller mScroller;
    private int mScrollX, mScrollY;

    public void smoothScrollTo(int dstX, int dstY ,int time) {

        mScrollX = getScrollX();
        mScrollY = getScrollY();
        int deltaX = dstX - mScrollX;
        int deltaY = dstY - mScrollY;
        mScroller.startScroll(mScrollX, mScrollY, deltaX, deltaY, time);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller != null && mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    protected void Init_M_Data(int x, int y) {
        mScrollX = getScrollX();
        mScrollY = getScrollY();
        mOldX = x;
        mOldY = y;
    }

    protected void Run_M(int x, int y) {
        scrollTo(mScrollX + mOldX - x, mScrollY + mOldY - y);
    }

    protected abstract void SingleDown(MotionEvent event);

    protected abstract void SingleMove(MotionEvent event, float velocityX, float velocityY);

    protected abstract void SingleUp(MotionEvent event);

    protected abstract void TwoDown(MotionEvent event);

    protected abstract void TwoMove(MotionEvent event, float velocityX, float velocityY);

    protected abstract void TwoUp(MotionEvent event);

}
