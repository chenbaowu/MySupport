package com.cbw.view.fingerMove;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.cbw.supportlibs.R;


/**
 * Created by cbw on 2017/6/6.
 * <p>
 * 底图 加 可操作装饰
 */

public class FingerView extends BaseView {

    private int W, H;

    public FingerView(Context context, int w, int h) {
        super(context);
        this.W = w;
        this.H = h;
    }

    public FingerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void InitData() {
        super.InitData();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(Color.GREEN);
        canvas.save();
        canvas.concat(mGlobalMatrix);

        for (int i = 0; i < mShapeList.size(); i++) {

            drawItem(canvas, mShapeList.get(i));
        }

        canvas.restore();
    }

    private void drawBmp(Canvas canvas, Shape shape) {

        canvas.drawBitmap(shape.m_bmp, shape.m_matrix, shape.m_paint);
    }

    private void drawItem(Canvas canvas, Shape shape) {

        canvas.drawBitmap(shape.m_bmp, shape.m_matrix, shape.m_paint);
    }

    @Override
    protected void SingleDown(MotionEvent event) {

        m_isTouch = true;
        mCurShapeSel = GetSelectIndex(mDownX, mDownY);
        mTarget = GetSelectShapeByIndex(mCurShapeSel);
        if (mTarget != null) {
            Init_M_Data(mTarget, mDownX, mDownY);
        }

        this.invalidate();
    }

    @Override
    protected void SingleMove(MotionEvent event) {

        if (m_isTouch && mTarget != null) {
            Run_M(mTarget, event.getX(), event.getY());
        }

        this.invalidate();
    }

    @Override
    protected void SingleUp(MotionEvent event) {
        if (m_isTouch && mTarget != null) {
            Init_M_Data(mTarget, mUpX, mUpY);
        }
        if (mPointerCount == 1) {
            adjustShape();
        }
        mCurShapeSel = -1;
//        mTarget = null;
        m_isTouch = false;

        this.invalidate();
    }

    @Override
    protected void TwoDown(MotionEvent event) {

        m_isTouch = true;
        if (mTarget == null) {
            mCurShapeSel = GetSelectIndex((mDownX1 + mDownX2) / 2f, (mDownY1 + mDownY2) / 2f);
            mTarget = GetSelectShapeByIndex(mCurShapeSel);
        }
        if (mTarget != null) {
            Init_RZM_Data(mTarget, mDownX1, mDownY1, mDownX2, mDownY2);
        }

        this.invalidate();
    }

    @Override
    protected void TwoMove(MotionEvent event) {

        if (m_isTouch && mTarget != null) {
            Run_RZM(mTarget, event.getX(0), event.getY(0), event.getX(1), event.getY(1));
        }
        this.invalidate();
    }

    @Override
    protected void TwoUp(MotionEvent event) {
        SingleUp(event);
    }

    private void adjustShape() {

        if (mTarget == null) {
            return;
        }

        RectF showRectF = GetShowRect(mTarget);
        float cx = showRectF.centerX();
        float cy = showRectF.centerY();
        if (cx < 0 || cx > getWidth() || cy < 0 || cy > getHeight()) {
            DoTranslateANM(getWidth() / 2.0f, getHeight() / 2.0f, 300);
        }
    }

    public void setBmp() {

        Shape shape = new Shape();
        shape.m_bmp = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        shape.m_w = shape.m_bmp.getWidth();
        shape.m_h = shape.m_bmp.getHeight();
        shape.MAX_SCALE = 5f;
        shape.DEF_SCALE = 1f;
        shape.MIN_SCALE = 0.1f;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        shape.m_paint.set(paint);
        mShapeList.add(shape);

        UpdateUI();
    }

    public void addItem() {

        Shape shape = new Shape();
        shape.m_bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_mk);
        shape.m_w = shape.m_bmp.getWidth();
        shape.m_h = shape.m_bmp.getHeight();
        shape.MAX_SCALE = 5f;
        shape.DEF_SCALE = 1f;
        shape.MIN_SCALE = 0.1f;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        shape.m_paint.set(paint);
        mShapeList.add(shape);

        UpdateUI();
    }

}
