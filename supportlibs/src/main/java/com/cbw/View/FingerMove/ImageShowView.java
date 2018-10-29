package com.cbw.view.fingerMove;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.cbw.supportlibs.R;

/**
 * Created by cbw on 2017/12/13.
 * <p>
 * 图片展示view
 * 支持手势 双击缩放动画，自动复位动画等
 * 不支持滑动嵌套（ViewPager等）
 */

public class ImageShowView extends BaseView {

    public ImageShowView(Context context) {
        super(context);
    }

    public ImageShowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void InitData() {
        super.InitData();
        initGestureListener();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(Color.GRAY);
        canvas.save();
        canvas.concat(mGlobalMatrix);

        if (mTarget != null && mTarget.m_bmp != null) {
            canvas.drawBitmap(mTarget.m_bmp, mTarget.m_matrix, mTarget.m_paint);
        }

        canvas.restore();
    }

    @Override
    protected void SingleDown(MotionEvent event) {
//        Log.i("bbb", "SingleDown: " );

        Init_M_Data(mTarget, mDownX, mDownY);
        this.invalidate();
    }

    @Override
    protected void SingleMove(MotionEvent event) {
//        Log.i("bbb", "SingleMove: " );

        if (isNeedReInitOpData) {
            isNeedReInitOpData = false;
            Init_M_Data(mTarget, event.getX(), event.getY());
        }
        Run_M(mTarget, event.getX(), event.getY());
        this.invalidate();
    }

    @Override
    protected void SingleUp(MotionEvent event) {
//        Log.i("bbb", "SingleUp: " );

        adjustShape();
        this.invalidate();
    }

    @Override
    protected void TwoDown(MotionEvent event) {
//        Log.i("bbb", "TwoDown: " );

        isNeedReInitOpData = true;
        this.invalidate();
    }

    @Override
    protected void TwoMove(MotionEvent event) {
//        Log.i("bbb", "TwoMove: " );

        if (isNeedReInitOpData) {
            isNeedReInitOpData = false;
            Init_ZM_Data(mTarget, event.getX(0), event.getY(0), event.getX(1), event.getY(1));
        }
        Run_ZM(mTarget, event.getX(0), event.getY(0), event.getX(1), event.getY(1));

        this.invalidate();
    }

    @Override
    protected void TwoUp(MotionEvent event) {
//        Log.i("bbb", "TwoUp: " );

        isNeedReInitOpData = true;
        this.invalidate();
    }

    // 检测是否拦截parentView接收事件
    private boolean checkIsInterceptParent(float dx) {

        boolean isInterceptParent = true;

        RectF showRectF = GetShowRect(mTarget);

        if (showRectF.width() > getWidth() || showRectF.height() > getHeight()) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        if (showRectF.left == 0 && dx >= 0) {
            isInterceptParent = false;
            getParent().requestDisallowInterceptTouchEvent(false);
        }

        if (showRectF.right == getWidth() && dx <= 0) {
            isInterceptParent = false;
            getParent().requestDisallowInterceptTouchEvent(false);
        }

        return isInterceptParent;
    }

    private synchronized void adjustShape() {

        if (mTarget == null) {
            return;
        }

        isInAnm = true;

        RectF showRectF = GetShowRect(mTarget);
        float cx = GetCenterXY(mTarget)[0];
        float cy = GetCenterXY(mTarget)[1];

        if (showRectF.width() < getWidth()) {
            cx = getWidth() / 2.0f;
        } else {
            if (showRectF.left > 0) {
                cx = showRectF.width() / 2.0f;
            }
            if (showRectF.right < getWidth()) {
                cx = getWidth() / 2.0f - (showRectF.width() - getWidth()) / 2.0f;
            }
        }

        if (showRectF.height() < getHeight()) {
            cy = getHeight() / 2.0f;
        } else {
            if (showRectF.top > 0) {
                cy = showRectF.height() / 2.0f;
            }
            if (showRectF.bottom < getHeight()) {
                cy = getHeight() / 2.0f - (showRectF.height() - getHeight()) / 2.0f;
            }
        }

        float scale = 1f;
        float px = GetCenterXY(mTarget)[0];
        float py = GetCenterXY(mTarget)[1];
        float mCurScale = GetScaleXY(mTarget)[0];
        if (mCurScale < mTarget.DEF_SCALE * mGlobalScale) {
            scale = mTarget.DEF_SCALE * mGlobalScale / mCurScale;
        } else if (mCurScale > mTarget.MAX_SCALE * mGlobalScale) {
            scale = mTarget.MAX_SCALE * mGlobalScale / mCurScale;
            px = mOldPx;
            py = mOldPy;
        }

        DoTranslateAndScaleANM(cx, cy, scale, px, py, 300);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mTarget == null || isInAnm) {
//            Log.i("bbb", "return: " + isInAnm);
            return true;
        }

        if (mGestureDetector != null && mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        return super.onTouchEvent(event);
    }

    private GestureDetector mGestureDetector;

    private int FLING_MIN_DISTANCE = 100, FLING_MIN_VELOCITY = 200;

    public void initGestureListener() {

        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

                // 用户轻触触摸屏，由1个MotionEvent ACTION_DOWN触发
                @Override
                public boolean onDown(MotionEvent e) {
                    return false;
                }

                // 用户（轻触触摸屏后）松开，由一个1个MotionEvent ACTION_UP触发
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
//                    Log.i("bbb", "onSingleTapUp: ");
                    return false;
                }

                // 单击之后短时间内没有再次单击
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
//                    Log.i("bbb", "onSingleTapConfirmed: ");

                    return true;
                }

                /**
                 * 用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发
                 *
                 * @param distanceX 向右为负数
                 * @param distanceY 向下为负数
                 */
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                    return !checkIsInterceptParent(-distanceX);
                }

                // 用户按下触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE, 1个ACTION_UP触发
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                        // Fling left
                    } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                        // Fling right
                    }
                    return false;
                }

                /**
                 * 用户轻触触摸屏，尚未松开或拖动 ,用户轻触触摸屏，尚未松开或拖动
                 * Touch了还没有滑动时触发
                 * onDown只要Touch Down一定立刻触发
                 * Touch Down后过一会没有滑动先触发onShowPress再触发onLongPress
                 * Touch Down后一直不滑动，onDown -> onShowPress -> onLongPress这个顺序触发
                 */
                @Override
                public void onShowPress(MotionEvent e) {
                    super.onShowPress(e);
                }

                // Touch了不移动一直Touch down时触发
                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);
                }

                // 双击的第二下Touch down时触发
                @Override
                public boolean onDoubleTap(MotionEvent e) {

//                    Log.i("bbb", "onDoubleTap GetScaleXY(mTarget)[0]: " + GetScaleXY(mTarget)[0]);

                    if (mTarget != null) {
                        float ds = mTarget.MAX_SCALE / mTarget.DEF_SCALE;
                        if (GetScaleXY(mTarget)[0] == mTarget.DEF_SCALE * mGlobalScale) {
                            DoScaleANM(ds, e.getX(), e.getY(), 300);
                        } else {
                            ds = mTarget.DEF_SCALE * mGlobalScale / GetScaleXY(mTarget)[0];
                            DoTranslateAndScaleANM(getWidth() / 2.0f, getHeight() / 2.0f, ds, GetCenterXY(mTarget)[0], GetCenterXY(mTarget)[1], 300);
                        }
                    }
                    return true;
                }

                // 双击的第二下Touch down和up都会触发，可用e.getAction()区分
                @Override
                public boolean onDoubleTapEvent(MotionEvent e) {

                    return false;
                }

                @Override
                public boolean onContextClick(MotionEvent e) {
                    return super.onContextClick(e);
                }
            });
        }
    }

    public void setImage() {

        post(new Runnable() {
            @Override
            public void run() {
                mTarget = new Shape();
                mTarget.m_bmp = BitmapFactory.decodeResource(getResources(), R.drawable.test);
                if (mTarget.m_bmp == null) {
                    mTarget = null;
                    return;
                }
                mTarget.m_w = mTarget.m_bmp.getWidth();
                mTarget.m_h = mTarget.m_bmp.getHeight();
                initShowBmpSize();
                mTarget.DEF_SCALE = mScale;
                mTarget.MAX_SCALE = 2 * mScale;
                mTarget.MIN_SCALE = 0.5f * mScale;
                mTarget.isLimitScale = false;
                mTarget.m_matrix.postScale(mTarget.DEF_SCALE, mTarget.DEF_SCALE);
                mTarget.m_x = (getWidth() - showBmpW) / 2.0f;
                mTarget.m_y = (getHeight() - showBmpH) / 2.0f;
                mTarget.m_matrix.postTranslate(mTarget.m_x, mTarget.m_y);
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setFilterBitmap(true);
                mTarget.m_paint.set(paint);
                mShapeList.add(mTarget);

                UpdateUI();
            }
        });
    }

    public void setImage(final String path) {

        post(new Runnable() {
            @Override
            public void run() {
                mTarget = new Shape();
                mTarget.m_bmp = BitmapFactory.decodeFile(path);
                if (mTarget.m_bmp == null) {
                    mTarget = null;
                    return;
                }
                mTarget.m_w = mTarget.m_bmp.getWidth();
                mTarget.m_h = mTarget.m_bmp.getHeight();
                initShowBmpSize();
                mTarget.DEF_SCALE = mScale;
                mTarget.MAX_SCALE = 2 * mScale;
                mTarget.MIN_SCALE = 0.5f * mScale;
                mTarget.isLimitScale = false;
                mTarget.m_matrix.postScale(mTarget.DEF_SCALE, mTarget.DEF_SCALE);
                mTarget.m_x = (getWidth() - showBmpW) / 2.0f;
                mTarget.m_y = (getHeight() - showBmpH) / 2.0f;
                mTarget.m_matrix.postTranslate(mTarget.m_x, mTarget.m_y);
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setFilterBitmap(true);
                mTarget.m_paint.set(paint);
                mShapeList.add(mTarget);

                UpdateUI();
            }
        });
    }

    private float mScale = 1f;
    private float showBmpW, showBmpH;

    private void initShowBmpSize() {

        if (mTarget == null) {
            return;
        }

        float scaleW = getWidth() * 1.0f / mTarget.m_w;
        float scaleH = getHeight() * 1.0f / mTarget.m_h;
        mScale = scaleW < scaleH ? scaleW : scaleH;

        showBmpW = mTarget.m_w * mScale;
        showBmpH = mTarget.m_h * mScale;

//        Log.i("bbb", "initShowBmpSize: " + showBmpW + " , " + showBmpH);
//        Log.i("bbb", "des: " + mScale);
    }
}
