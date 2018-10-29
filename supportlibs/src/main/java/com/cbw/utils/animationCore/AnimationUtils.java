package com.cbw.utils.animationCore;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Created by cbw on 2017/6/6.
 */

public class AnimationUtils {

    private static View mANIMview;

    // 以下是补间动画

    public static void scaleANIM(View view, float from, float to, long time, boolean fillAfter, Animation.AnimationListener animationListener) {

        ScaleAnimation scaleAnimation = new ScaleAnimation(
                from, to, from, to,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(time);
        scaleAnimation.setFillAfter(fillAfter);
        scaleAnimation.setInterpolator(new LinearInterpolator());
        if (animationListener != null) {
            scaleAnimation.setAnimationListener(animationListener);
        }
        view.startAnimation(scaleAnimation);
    }

    public static void scaleANIM(View view, float from, float to, float pivotXValue, float pivotYValue, long time, boolean fillAfter, Animation.AnimationListener animationListener) {

        ScaleAnimation scaleAnimation = new ScaleAnimation(
                from, to, from, to,
                Animation.RELATIVE_TO_SELF, pivotXValue,
                Animation.RELATIVE_TO_SELF, pivotYValue);
        scaleAnimation.setDuration(time);
        scaleAnimation.setFillAfter(fillAfter);
        scaleAnimation.setInterpolator(new LinearInterpolator());
        if (animationListener != null) {
            scaleAnimation.setAnimationListener(animationListener);
        }
        view.startAnimation(scaleAnimation);
    }

    public static void rotateANIM(View view) {
        view.clearAnimation();
        RotateAnimation rotateAnimation = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setRepeatMode(Animation.RESTART);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        view.startAnimation(rotateAnimation);
    }

    public static void alphaANIM(final View view, long time, long startOffset, float s, float end, boolean fillAfter, Animation.AnimationListener animationListener) {
        AlphaAnimation aAnima = new AlphaAnimation(s, end);
        aAnima.setDuration(time);
        aAnima.setStartOffset(startOffset);
        aAnima.setFillAfter(fillAfter);
        if (animationListener != null) {
            aAnima.setAnimationListener(animationListener);
        }
        view.startAnimation(aAnima);
    }

    public static void translateANIM(View view, long time, float fx, float tox, float fy, float toy, boolean fillAfter, Animation.AnimationListener animationListener) {
        Animation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, fx,
                Animation.RELATIVE_TO_SELF, tox,
                Animation.RELATIVE_TO_SELF, fy,
                Animation.RELATIVE_TO_SELF, toy);
        translateAnimation.setInterpolator(new LinearInterpolator());
        translateAnimation.setFillAfter(fillAfter);
        translateAnimation.setDuration(time);
        if (animationListener != null) {
            translateAnimation.setAnimationListener(animationListener);
        }
        view.startAnimation(translateAnimation);
    }

    public static void jumpANIM(View view) {
        view.clearAnimation();
        int time1 = 170;
        int time2 = 130;
        int time3 = 150;
        int time4 = 150;
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setInterpolator(new LinearInterpolator());
        ScaleAnimation scaleAnimation1 = new ScaleAnimation(
                1.0f, 1.1f, 1.0f, 1.1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation1.setDuration(time1);
        scaleAnimation1.setFillAfter(true);
        animationSet.addAnimation(scaleAnimation1);
        ScaleAnimation scaleAnimation2 = new ScaleAnimation(
                1.0f, 1.0f / 1.1f, 1.0f, 1.0f / 1.1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation2.setDuration(time2);
        scaleAnimation2.setStartOffset(time1);
        scaleAnimation2.setFillAfter(true);
        animationSet.addAnimation(scaleAnimation2);
//        ScaleAnimation scaleAnimation3 = new ScaleAnimation(
//                1.0f, 1.1f, 1.0f, 1.1f,
//                Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF, 0.5f);
//        scaleAnimation3.setDuration(time3);
//        scaleAnimation3.setStartOffset(time1 + time2);
//        scaleAnimation3.setFillAfter(true);
//        animationSet.addAnimation(scaleAnimation3);
//        ScaleAnimation scaleAnimation4 = new ScaleAnimation(
//                1.0f, 1.0f / 1.1f, 1.0f, 1.0f / 1.1f,
//                Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF, 0.5f);
//        scaleAnimation4.setDuration(time4);
//        scaleAnimation4.setStartOffset(time1 + time2 + time3);
//        scaleAnimation4.setFillAfter(true);
//        animationSet.addAnimation(scaleAnimation4);
        view.startAnimation(animationSet);
    }

    public static void YoYiYaoANIM(View view, Animation.AnimationListener animationListener) {
        int time1 = 100;
        int time2 = 100;
        int time3 = 50;
        int time4 = 100;
        int time5 = 100;
        int time6 = 50;
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setFillAfter(true);
        animationSet.setInterpolator(new LinearInterpolator());
        animationSet.setStartOffset(200);

        RotateAnimation rotateAnimation1 = new RotateAnimation(
                0, -10,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 1f);
        rotateAnimation1.setFillAfter(true);
        rotateAnimation1.setDuration(time1);
        animationSet.addAnimation(rotateAnimation1);
        RotateAnimation rotateAnimation2 = new RotateAnimation(
                0, 15,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 1f);
        rotateAnimation2.setFillAfter(true);
        rotateAnimation2.setStartOffset(time1);
        rotateAnimation2.setDuration(time2);
        animationSet.addAnimation(rotateAnimation2);
        RotateAnimation rotateAnimation3 = new RotateAnimation(
                0, -5,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 1f);
        rotateAnimation3.setFillAfter(true);
        rotateAnimation3.setStartOffset(time1 + time2);
        rotateAnimation3.setDuration(time3);
        animationSet.addAnimation(rotateAnimation3);

        RotateAnimation rotateAnimation4 = new RotateAnimation(
                0, -10,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 1f);
        rotateAnimation4.setFillAfter(true);
        rotateAnimation4.setStartOffset(time1 + time2 + time3);
        rotateAnimation4.setDuration(time4);
        animationSet.addAnimation(rotateAnimation4);
        RotateAnimation rotateAnimation5 = new RotateAnimation(
                0, 15,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 1f);
        rotateAnimation5.setFillAfter(true);
        rotateAnimation5.setStartOffset(time1 + time2 + time3 + time4);
        rotateAnimation5.setDuration(time5);
        animationSet.addAnimation(rotateAnimation5);
        RotateAnimation rotateAnimation6 = new RotateAnimation(
                0, -5,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 1f);
        rotateAnimation6.setFillAfter(true);
        rotateAnimation6.setStartOffset(time1 + time2 + time3 + time4 + time5);
        rotateAnimation6.setDuration(time6);
        animationSet.addAnimation(rotateAnimation6);
        if (animationListener != null) {
            animationSet.setAnimationListener(animationListener);
        }
        view.startAnimation(animationSet);
    }

    public static void showToast(final View view, long stime, float sfx, float stox, int atiem, long startOffset, float fromAlpha, float toAlpha, Animation.AnimationListener animationListener) {

        AnimationSet animationSet = new AnimationSet(false);

        ScaleAnimation scaleAnimation = new ScaleAnimation(
                sfx, stox, sfx, stox,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(stime);
//        scaleAnimation.setInterpolator(new BounceInterpolator());
        animationSet.addAnimation(scaleAnimation);

        AlphaAnimation aAnima = new AlphaAnimation(fromAlpha, toAlpha);
        aAnima.setDuration(atiem);
        aAnima.setStartOffset(startOffset);
        animationSet.addAnimation(aAnima);

        if (animationListener != null) {
            animationSet.setAnimationListener(animationListener);
        }
        view.startAnimation(animationSet);
    }

    // 边缩放，边旋转，边移动，边改边透明度 (改为先移动)
    public static void showANIM(View view, long time, float sfx, float stox, float pivotXValue, float pivotYValue,
                                float fromDegrees, float toDegrees,
                                float fromXDelta, float toXDelta, float fromYDelta, float toYDelta,
                                float fromAlpha, float toAlpha,
                                boolean fillAfter,
                                Animation.AnimationListener animationListener) {

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setFillAfter(fillAfter);
//        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());

        if (sfx != stox) {
            ScaleAnimation scaleAnimation = new ScaleAnimation(
                    sfx, stox, sfx, stox,
                    Animation.RELATIVE_TO_SELF, pivotXValue,
                    Animation.RELATIVE_TO_SELF, pivotYValue);
            scaleAnimation.setDuration(time);
            animationSet.addAnimation(scaleAnimation);
        }

        if (fromDegrees != toDegrees) {
            RotateAnimation rotateAnimation = new RotateAnimation(
                    fromDegrees, toDegrees,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(time);
            animationSet.addAnimation(rotateAnimation);
        }

        if (fromXDelta != toXDelta || fromYDelta != toYDelta) {
            TranslateAnimation translateAnimation = new TranslateAnimation(
                    fromXDelta,
                    toXDelta,
                    fromYDelta,
                    toYDelta);
            translateAnimation.setDuration(time);
            animationSet.addAnimation(translateAnimation);
        }

        if (fromAlpha != toAlpha) {
            AlphaAnimation aAnima = new AlphaAnimation(fromAlpha, toAlpha);
            aAnima.setDuration(time);
            animationSet.addAnimation(aAnima);
        }

        if (animationListener != null) {
            animationSet.setAnimationListener(animationListener);
        }
        animationSet.setDuration(time);
        view.startAnimation(animationSet);
    }

    // 以下是属性动画

    public static void valueAnimaFloat(long time, float from, float to, ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {

        ValueAnimator va;
        va = ValueAnimator.ofFloat(from, to);
        va.addUpdateListener(animatorUpdateListener);
        va.setDuration(time);
        va.start();
    }

    public static ValueAnimator valueAnimaFloat(long time, float from, float to, final AnimaCallback animaCallback) {

        ValueAnimator va;
        va = ValueAnimator.ofFloat(from, to);
        va.setDuration(time);
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (animaCallback != null) {
                    animaCallback.onAnimationStart(animation);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (animaCallback != null) {
                    animaCallback.onAnimationEnd(animation);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (animaCallback != null) {
                    animaCallback.onAnimationCancel(animation);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                if (animaCallback != null) {
                    animaCallback.onAnimationRepeat(animation);
                }
            }
        });
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animaCallback != null) {
                    animaCallback.onAnimationUpdate(animation);
                }
            }
        });
        va.start();

        return va;
    }

    public static void valueAnimaInt(long time, int from, int to, ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {

        ValueAnimator va;
        va = ValueAnimator.ofInt(from, to);
        va.setInterpolator(new BounceInterpolator());
        va.addUpdateListener(animatorUpdateListener);
        va.setDuration(time);
        va.start();
    }

    public static void pageANM(View view1, long time, float fromAlpha, float toAlpha, float fy, float ty, Animator.AnimatorListener animatorListener) {

        AnimatorSet setANM = new AnimatorSet();
        setANM.setDuration(time);
        ObjectAnimator objANM1 = ObjectAnimator.ofFloat(view1, "translationY", fy, ty);
        ObjectAnimator objANM2 = ObjectAnimator.ofFloat(view1, "alpha", fromAlpha, toAlpha);
        setANM.playTogether(objANM1, objANM2);
        if (animatorListener != null) {
            setANM.addListener(animatorListener);
        }
        setANM.start();

    }

    // 暂不使用
    public void clearAllANIM() {
        if (mANIMview != null) {
            mANIMview.clearAnimation();
            mANIMview = null;
        }
    }
}
