package com.cbw.utils;

import android.graphics.Paint;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

/**
 * Created by cbw on 2018/10/29.
 */
public class ShapeDrawableUtil {

    public static ShapeDrawable GetRectShapeDrawable(int color, int cornerRadius) {
        RectShape rectShape = new RectShape();
        ShapeDrawable shape = new ShapeDrawable(rectShape);
        shape.getPaint().setColor(color);
        shape.getPaint().setStyle(Paint.Style.STROKE);
        shape.getPaint().setStrokeWidth(cornerRadius);
        return shape;
    }

    public static ShapeDrawable GetRoundRectShapeDrawable(int color, int cornerRadius) {
        PaintDrawable paintDrawable = new PaintDrawable(color);
        paintDrawable.setCornerRadius(cornerRadius);
        return paintDrawable;
    }
}
