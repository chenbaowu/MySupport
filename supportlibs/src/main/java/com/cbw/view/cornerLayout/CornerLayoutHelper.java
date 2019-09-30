package com.cbw.view.cornerLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.cbw.supportlibs.R;


/**
 * Created by cbw on 2019/7/11
 * 圆角布局实现工具
 */
public class CornerLayoutHelper implements ICornerApi {

    private Paint mClipPaint;
    private Paint mStrokePaint;
    private Path mTempPath;
    public Path mClipPath;
    private RectF mRectF;

    private int mViewWidth, mViewHeight;
    private float[] mRadius;
    private float mStrokeWidth;
    private int mStrokeColor;

    /**
     * 是否圆形，如果是：radius的设置将失效，半径默认是宽高（减去边距）的最小值
     */
    private boolean mAsCircle;

    public void init(Context context, AttributeSet attrs) {
        mClipPaint = new Paint();
        mClipPaint.setAntiAlias(true);
        mClipPaint.setStyle(Paint.Style.FILL);
        mStrokePaint = new Paint();
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mTempPath = new Path();
        mClipPath = new Path();
        mRadius = new float[8];
        mRectF = new RectF();

        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CornerLinearLayout);
        if (typedArray != null) {
            if (typedArray.hasValue(R.styleable.CornerLinearLayout_radius)) {
                float radius = typedArray.getDimensionPixelSize(R.styleable.CornerLinearLayout_radius, 0);
                setRadius(radius);
            } else {
                if (typedArray.hasValue(R.styleable.CornerLinearLayout_topLeftRadius)) {
                    float radius = typedArray.getDimensionPixelSize(R.styleable.CornerLinearLayout_topLeftRadius, 0);
                    setTopLeftRadius(radius);
                }
                if (typedArray.hasValue(R.styleable.CornerLinearLayout_topRightRadius)) {
                    float radius = typedArray.getDimensionPixelSize(R.styleable.CornerLinearLayout_topRightRadius, 0);
                    setTopRightRadius(radius);
                }
                if (typedArray.hasValue(R.styleable.CornerLinearLayout_bottomRightRadius)) {
                    float radius = typedArray.getDimensionPixelSize(R.styleable.CornerLinearLayout_bottomRightRadius, 0);
                    setBottomRightRadius(radius);
                }
                if (typedArray.hasValue(R.styleable.CornerLinearLayout_bottomLeftRadius)) {
                    float radius = typedArray.getDimensionPixelSize(R.styleable.CornerLinearLayout_bottomLeftRadius, 0);
                    setBottomLeftRadius(radius);
                }
            }
            if (typedArray.hasValue(R.styleable.CornerLinearLayout_strokeWidth)) {
                float strokeWidth = typedArray.getDimensionPixelSize(R.styleable.CornerLinearLayout_strokeWidth, 0);
                setStrokeWidth(strokeWidth);
            }
            if (typedArray.hasValue(R.styleable.CornerLinearLayout_strokeColor)) {
                int strokeColor = typedArray.getColor(R.styleable.CornerLinearLayout_strokeColor, 0);
                setStrokeColor(strokeColor);
            }
            if (typedArray.hasValue(R.styleable.CornerLinearLayout_asCircle)) {
                mAsCircle = typedArray.getBoolean(R.styleable.CornerLinearLayout_asCircle, false);
            }
            typedArray.recycle();
        }
    }

    public void onSizeChanged(int w, int h, View view) {
        mViewWidth = w;
        mViewHeight = h;
        mRectF.left = view.getPaddingLeft();
        mRectF.top = view.getPaddingTop();
        mRectF.right = w - view.getPaddingRight();
        mRectF.bottom = h - view.getPaddingBottom();

        refreshClipPath();
    }

    private void refreshClipPath() {
        if (mRectF.isEmpty()) return;
        mTempPath.reset();
        mClipPath.reset();
        float radius = 0;
        if (mAsCircle) {
            // 圆形
            radius = mRectF.width() < mRectF.height() ? mRectF.width() * 0.5f : mRectF.height() * 0.5f;
            mTempPath.addCircle(mRectF.centerX(), mRectF.centerY(), radius, Path.Direction.CW);
        } else {
            mTempPath.addRoundRect(mRectF, mRadius, Path.Direction.CW);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            // 9.0开始透明区域不再绘制，Xfermode无法通过透明像素裁剪或者拼接边界，这里用反选的方式（即选中目标外的区域再去混合）
            mClipPath.addRect(0, 0, mViewWidth, mViewHeight, Path.Direction.CW);
            mClipPath.op(mTempPath, Path.Op.DIFFERENCE);
            mClipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        } else {
            if (mAsCircle) {
                // 圆形
                mClipPath.addCircle(mRectF.centerX(), mRectF.centerY(), radius, Path.Direction.CW);
            } else {
                mClipPath.addRoundRect(mRectF, mRadius, Path.Direction.CW);
            }
            mClipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        }
    }

    public void clipBackgroundDraw(Canvas canvas) {
        canvas.drawPath(mClipPath, mClipPaint);
    }

    public void clipDraw(Canvas canvas) {
        if (mStrokeWidth > 0 && mStrokeColor != 0) {
            // 画描边
            canvas.drawPath(mTempPath, mStrokePaint);
        }
        canvas.drawPath(mClipPath, mClipPaint);
    }

    @Override
    public void setRadius(float radius) {
        for (int i = 0; i < mRadius.length; i++) {
            mRadius[i] = radius;
        }
        refreshClipPath();
    }

    @Override
    public void setTopLeftRadius(float radius) {
        mRadius[0] = radius;
        mRadius[1] = radius;
        refreshClipPath();
    }

    @Override
    public void setTopRightRadius(float radius) {
        mRadius[2] = radius;
        mRadius[3] = radius;
        refreshClipPath();
    }

    @Override
    public void setBottomLeftRadius(float radius) {
        mRadius[6] = radius;
        mRadius[7] = radius;
        refreshClipPath();
    }

    @Override
    public void setBottomRightRadius(float radius) {
        mRadius[4] = radius;
        mRadius[5] = radius;
        refreshClipPath();
    }

    @Override
    public void setStrokeWidth(float strokeWidth) {
        mStrokeWidth = strokeWidth * 2;
        mStrokePaint.setStrokeWidth(mStrokeWidth);
    }

    @Override
    public void setStrokeColor(int strokeColor) {
        mStrokeColor = strokeColor;
        mStrokePaint.setColor(mStrokeColor);
    }

    @Override
    public void setAsCircle(boolean asCircle) {
        mAsCircle = asCircle;
        refreshClipPath();
    }
}