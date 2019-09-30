package com.cbw.view.cornerLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.cbw.supportlibs.R;


/**
 * Created by cbw on 2019/8/2
 * 圆角遮罩
 */
public class CornerMask extends View implements ICornerApi {

    private int mViewWidth, mViewHeight;
    private Paint mPaint;
    private Path mPath;
    private Path mTempPath;
    private RectF mRectF;
    private float[] mRadius;
    private int mMaskColor;
    public float mMaskWidth;

    public CornerMask(Context context) {
        this(context, null);
    }

    public CornerMask(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @Override
    public void setRadius(float radius) {
        for (int i = 0; i < mRadius.length; i++) {
            mRadius[i] = radius;
        }
        refreshPath();
        invalidate();
    }

    @Override
    public void setTopLeftRadius(float radius) {
        mRadius[0] = radius;
        mRadius[1] = radius;
        refreshPath();
        invalidate();
    }

    @Override
    public void setTopRightRadius(float radius) {
        mRadius[2] = radius;
        mRadius[3] = radius;
        refreshPath();
        invalidate();
    }

    @Override
    public void setBottomLeftRadius(float radius) {
        mRadius[6] = radius;
        mRadius[7] = radius;
        refreshPath();
        invalidate();
    }

    @Override
    public void setBottomRightRadius(float radius) {
        mRadius[4] = radius;
        mRadius[5] = radius;
        refreshPath();
        invalidate();
    }

    @Override
    public void setStrokeWidth(float strokeWidth) {
        if (strokeWidth != mMaskWidth) {
            mMaskWidth = strokeWidth;
            refreshPath();
            invalidate();
        }
    }

    @Override
    public void setStrokeColor(int strokeColor) {
        if (strokeColor != mMaskColor) {
            mMaskColor = strokeColor;
            mPaint.setColor(strokeColor);
            invalidate();
        }
    }

    @Override
    public void setAsCircle(boolean asCircle) {

    }

    private void init(AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mMaskColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPath = new Path();
        mTempPath = new Path();
        mRectF = new RectF();
        mRadius = new float[8];
        initAttrs(getContext(), attrs);
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
            typedArray.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        refreshPath();
    }

    private void refreshPath() {
        if (mViewWidth == 0 || mViewHeight == 0) return;
        mTempPath.reset();
        mPath.reset();
        mRectF.left = this.getPaddingLeft() + mMaskWidth;
        mRectF.top = this.getPaddingTop();
        mRectF.right = mViewWidth - this.getPaddingRight() - mMaskWidth;
        mRectF.bottom = mViewHeight - this.getPaddingBottom();
        mTempPath.addRoundRect(mRectF, mRadius, Path.Direction.CW);
        mPath.addRect(0, 0, mViewWidth, mViewHeight, Path.Direction.CW);
        mPath.op(mTempPath, Path.Op.DIFFERENCE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(mPath, mPaint);
    }
}
