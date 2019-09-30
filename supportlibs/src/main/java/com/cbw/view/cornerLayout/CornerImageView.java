package com.cbw.view.cornerLayout;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by cbw on 2019/7/11
 * 圆角ImageView
 */
public class CornerImageView extends AppCompatImageView implements ICornerApi {

    CornerLayoutHelper mCornerLayoutHelper;

    public CornerImageView(Context context) {
        super(context);
        init(null);
    }

    public CornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mCornerLayoutHelper = new CornerLayoutHelper();
        mCornerLayoutHelper.init(getContext(), attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCornerLayoutHelper.onSizeChanged(w, h, this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        super.onDraw(canvas);
        mCornerLayoutHelper.clipDraw(canvas);
        canvas.restore();
    }

    @Override
    public void setRadius(float radius) {
        mCornerLayoutHelper.setRadius(radius);
        invalidate();
    }

    @Override
    public void setTopLeftRadius(float radius) {
        mCornerLayoutHelper.setTopLeftRadius(radius);
        invalidate();
    }

    @Override
    public void setTopRightRadius(float radius) {
        mCornerLayoutHelper.setTopRightRadius(radius);
        invalidate();
    }

    @Override
    public void setBottomLeftRadius(float radius) {
        mCornerLayoutHelper.setBottomLeftRadius(radius);
        invalidate();
    }

    @Override
    public void setBottomRightRadius(float radius) {
        mCornerLayoutHelper.setBottomRightRadius(radius);
        invalidate();
    }

    @Override
    public void setStrokeWidth(float strokeWidth) {
        mCornerLayoutHelper.setStrokeWidth(strokeWidth);
        invalidate();
    }

    @Override
    public void setStrokeColor(int strokeColor) {
        mCornerLayoutHelper.setStrokeColor(strokeColor);
        invalidate();
    }

    @Override
    public void setAsCircle(boolean asCircle) {
        mCornerLayoutHelper.setAsCircle(asCircle);
        invalidate();
    }
}
