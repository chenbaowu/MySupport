package com.cbw.utils.animationCore;

import android.animation.Animator;
import android.animation.ValueAnimator;

/**
 * Created by cbw on 2017/12/12.
 */

public interface AnimaCallback {

    void onAnimationStart(Animator animation);

    void onAnimationEnd(Animator animation);

    void onAnimationCancel(Animator animation);

    void onAnimationRepeat(Animator animation);

    void onAnimationUpdate(ValueAnimator animation); // ！！！ 比onAnimationStart先执行第一个回调值
}
