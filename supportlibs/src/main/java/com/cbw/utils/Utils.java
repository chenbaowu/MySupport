package com.cbw.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by cbw on 2017/6/6.
 */

public class Utils {

    /**
     * 获取2点间的距离
     *
     * @param dx
     * @param dy
     */
    public static float Spacing(float dx, float dy) {
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 支持把所有的单位换算到px
     *
     * @param context
     * @param unit    TypedValue.COMPLEX_UNIT_DIP , TypedValue.COMPLEX_UNIT_SP ... {@link TypedValue}
     * @param value
     */
    public static float ApplyDimension(Context context, int unit, float value) {
        if (context == null) {
            return 0;
        }
        return TypedValue.applyDimension(unit, value, context.getResources().getDisplayMetrics());
    }

}
