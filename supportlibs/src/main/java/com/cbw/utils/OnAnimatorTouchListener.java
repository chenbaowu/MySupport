package com.cbw.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by cbw on 2018/3/3.
 */

public abstract class OnAnimatorTouchListener implements View.OnTouchListener {

    public OnAnimatorTouchListener() {
        init();
    }

    private boolean UiLock = false;

    public void setUiLock(boolean uiLock) {
        UiLock = uiLock;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        if (UiLock || isInReverseAnim) {
            return true;
        }

        if (m_touch_view != view && m_isDown) {
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                m_touch_view = view;
                OnDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                OnMove(event);
                break;
            case MotionEvent.ACTION_UP:
                OnUp(event);
                break;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
                OnCancel(event);
                break;
        }
        return true;
    }

    private View m_touch_view;
    private View m_animator_view;
    private boolean m_isDown;
    private boolean isNeeDoAnim; // 是否需要动画
    private boolean isInReverseAnim; // 手指离开的还原动画

    public boolean getIsDown() {
        return m_isDown;
    }

    /**
     * 设置做动画的 view,如果不设置则使用 m_touch_view 做动画,仅处理一次就置空
     *
     * @param m_animator_view
     */
    public void setAnimatorView(View m_animator_view) {
        this.m_animator_view = m_animator_view;
    }

    private void OnDown(MotionEvent event) {

        m_isDown = true;
        m_animator_view = null;
        m_touch_view.setPressed(true);
        m_view_Rect.right = m_touch_view.getWidth();
        m_view_Rect.bottom = m_touch_view.getHeight();

        isNeeDoAnim = onActionDown(m_touch_view, event);

        if (isNeeDoAnim) {
            m_touchAnimator.start();
        }
    }

    private void OnMove(MotionEvent event) {
    }

    private boolean isClickInView = false;

    private void OnUp(MotionEvent event) {

        m_isDown = false;
        m_touch_view.setPressed(false);

        if (m_view_Rect.contains(event.getX(), event.getY())) {
            isClickInView = true;
        } else {
            isClickInView = false;
            onActionCancel(m_touch_view, event);
        }

        if (isNeeDoAnim) {
            isInReverseAnim = true;
            m_touchAnimator.reverse();
        } else {
            animationEnd();
        }
    }

    private void OnCancel(MotionEvent event) {
        onActionCancel(m_touch_view, event);
        isClickInView = false;
        m_isDown = false;
        m_touch_view.setPressed(false);
        if (isNeeDoAnim) {
            isInReverseAnim = true;
            m_touchAnimator.reverse();
        } else {
            animationEnd();
        }
    }

    private ValueAnimator m_touchAnimator; // 触摸动画
    protected RectF m_view_Rect; // view 的触发范围

    private void init() {

        m_view_Rect = new RectF(0f, 0f, 0f, 0f);

        m_touchAnimator = new ValueAnimator();
        m_touchAnimator.setFloatValues(1, m_animator_scale);
        m_touchAnimator.setInterpolator(new LinearInterpolator());
        m_touchAnimator.setDuration(m_animator_duration);
        m_touchAnimator.addUpdateListener(m_updateListener);
        m_touchAnimator.addListener(m_animatorListenerAdapter);
        m_touchAnimator.addUpdateListener(m_updateListener);
    }

    private ValueAnimator.AnimatorUpdateListener m_updateListener = new ValueAnimator.AnimatorUpdateListener() {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float scale = (Float) animation.getAnimatedValue();

            if (m_animator_view == null) {
                m_touch_view.setScaleX(scale);
                m_touch_view.setScaleY(scale);
            } else {
                m_animator_view.setScaleX(scale);
                m_animator_view.setScaleY(scale);
            }
        }
    };

    private AnimatorListenerAdapter m_animatorListenerAdapter = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            animationEnd();
            isInReverseAnim = false;
        }
    };

    private void animationEnd() {
        if (isClickInView) {
            onActionClick(m_touch_view);
        }
        isClickInView = false;
    }

    protected float m_animator_scale = 0.80f;

    public void setAnimatorScale(float scale) {
        this.m_animator_scale = scale;
        m_touchAnimator.setFloatValues(1, m_animator_scale);
    }

    protected int m_animator_duration = 80;

    public void setAnimatorDuration(int duration) {
        this.m_animator_duration = duration;
        m_touchAnimator.setDuration(duration);
    }

    abstract public void onActionClick(View v);

    /**
     * @param v
     * @param event
     * @return 返回值 false 不做触摸动画
     */
    public boolean onActionDown(View v, MotionEvent event) {
        return true;
    }

    public void onActionCancel(View v, MotionEvent event) {
    }
}
