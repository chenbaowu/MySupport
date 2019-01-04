package com.cbw.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.util.TypedValue;

import java.util.List;

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

    /**
     * 获取当前进程名
     */
    public static String GetProcessName(Context context)
    {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if(runningApps == null)
        {
            return null;
        }
        for(ActivityManager.RunningAppProcessInfo proInfo : runningApps)
        {
            if(proInfo.pid == android.os.Process.myPid())
            {
                if(proInfo.processName != null)
                {
                    return proInfo.processName;
                }
            }
        }
        return null;
    }
}
