package com.cbw.view.fingerMove;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.cbw.utils.Utils;
import com.cbw.utils.animationCore.AnimaCallback;
import com.cbw.utils.animationCore.AnimationUtils;

import java.util.ArrayList;

/**
 * Created by cbw on 2017/6/6.
 * <p>
 * 基于旋转 ->缩放 ->移动的基类
 * 有移动 、 缩放 等基础动画
 */

public abstract class BaseView extends View {

    public boolean mUILock;
    protected Context mContext;
    private int W, H; // 记录初始化控件的宽高

    protected float mDownX, mDownY;
    protected float mMoveX, mMoveY;
    protected float mUpX, mUpY;

    protected float mDownX1, mDownY1;
    protected float mDownX2, mDownY2;

    protected boolean m_isTouch = false; // 触摸屏幕的时候 true，有任何手离开的时候 false
    protected boolean isNeedReInitOpData = false; // 用于多手触控重新初始化移动数据
    protected int mPointerCount;

    protected float mOldX, mOldY;
    protected float mOldPx, mOldPy; // 记录上一次的缩放中心点
    private float mDeltaDist;
    private float mDeltaDegrees;
    protected Matrix mGlobalMatrix;  // 控制整体的矩阵
    private Matrix mOldMatrix; // 操作前的记录矩阵
    protected Shape mTarget;

    public ArrayList<Shape> mShapeList = new ArrayList<Shape>();
    public int mCurShapeSel = -1; //当前选中index

    public BaseView(Context context) {
        super(context);
        mContext = context;
        InitData();
    }

    public BaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        InitData();
    }

    protected void InitData() {
        mTarget = new Shape();
        mGlobalMatrix = new Matrix();
        mOldMatrix = new Matrix();
    }

    public void setUILock(boolean UILock) {
        this.mUILock = UILock;
    }

    protected float mGlobalScale = 1f; // 全局缩放参数

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        if (H == 0) {
            this.W = w;
            this.H = h;
        } else {
            if (H == h) {
                mGlobalScale = 1f;
            } else {
                mGlobalScale = h * 1.0f / oldh;
            }
            mGlobalMatrix.setScale(mGlobalScale, mGlobalScale, w / 2f, 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mUILock) {
            return true;
        }

        mPointerCount = event.getPointerCount();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                mDownX = event.getX();
                mDownY = event.getY();
                SingleDown(event);
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                mDownX1 = event.getX(0);
                mDownY1 = event.getY(0);
                mDownX2 = event.getX(1);
                mDownY2 = event.getY(1);
                TwoDown(event);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (event.getPointerCount() == 1) {
                    mMoveX = event.getX();
                    mMoveY = event.getY();
                    SingleMove(event);
                } else {
                    TwoMove(event);
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                mUpX = event.getX();
                mUpY = event.getY();
                SingleUp(event);
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                TwoUp(event);
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE: {
                if (event.getPointerCount() == 1) {
                    SingleUp(event);
                } else if (event.getPointerCount() > 1) {
                    TwoUp(event);
                }
                break;
            }
        }

        return true;
    }

    protected void UpdateUI() {
        this.invalidate();
    }

    /**
     * 初始化移动
     */
    protected void Init_M_Data(Shape target, float x, float y) {
        mOldMatrix.set(target.m_matrix);
        init_M_Data(x, y);
    }

    protected void init_M_Data(float x, float y) {
        mOldX = x;
        mOldY = y;
    }

    /**
     * 移动计算
     */
    protected void Run_M_OP(Shape target, float x, float y) {
        target.m_matrix.postTranslate(x - mOldX, y - mOldY);
    }

    protected void Run_M(Shape target, float x, float y) {
        target.m_matrix.set(mOldMatrix);
        Run_M_OP(target, x, y);
    }

    /**
     * 初始化旋转
     */
    protected void Init_R_Data(Shape target, float x1, float y1, float x2, float y2) {
        mOldMatrix.set(target.m_matrix);
        init_R_Data(x1, y1, x2, y2);
    }

    protected void init_R_Data(float x1, float y1, float x2, float y2) {
        if (x1 - x2 == 0) {
            if (y1 >= y2) {
                mDeltaDegrees = 90;
            } else {
                mDeltaDegrees = -90;
            }
        } else if (y1 - y2 != 0) {
            mDeltaDegrees = (float) Math.toDegrees(Math.atan(((double) (y1 - y2)) / (x1 - x2)));
            if (x1 < x2) {
                mDeltaDegrees += 180;
            }
        } else {
            if (x1 >= x2) {
                mDeltaDegrees = 0;
            } else {
                mDeltaDegrees = 180;
            }
        }
    }

    /**
     * 旋转计算
     */
    protected void Run_R_OP(Shape target, float x1, float y1, float x2, float y2) {
        float tempAngle;
        if (x1 - x2 == 0) {
            if (y1 >= y2) {
                tempAngle = 90;
            } else {
                tempAngle = -90;
            }
        } else if (y1 - y2 != 0) {
            tempAngle = (float) Math.toDegrees(Math.atan(((double) (y1 - y2)) / (x1 - x2)));
            if (x1 < x2) {
                tempAngle += 180;
            }
        } else {
            if (x1 >= x2) {
                tempAngle = 0;
            } else {
                tempAngle = 180;
            }
        }
        target.m_matrix.postRotate(tempAngle - mDeltaDegrees, (mDownX1 + mDownX2) / 2f, (mDownY1 + mDownY2) / 2f);
    }

    protected void Run_Z(Shape target, float x1, float y1, float x2, float y2) {
        target.m_matrix.set(mOldMatrix);
        Run_Z_OP(target, x1, y1, x2, y2);
    }

    /**
     * 初始化缩放
     */
    protected void Init_Z_Data(Shape target, float x1, float y1, float x2, float y2) {
        mOldMatrix.set(target.m_matrix);
        init_Z_Data(x1, y1, x2, y2);
    }

    protected void init_Z_Data(float x1, float y1, float x2, float y2) {
        mDeltaDist = Utils.Spacing(x1 - x2, y1 - y2);
    }

    /**
     * 缩放计算 (有最大最小值限制)
     */
    protected void Run_Z_OP(Shape target, float x1, float y1, float x2, float y2) {
        float tempDist = Utils.Spacing(x1 - x2, y1 - y2);
        if (tempDist > 10) {
            float scale = tempDist / mDeltaDist;
            if (target.isLimitScale) {
                if (scale * GetScaleXY(target)[0] >= target.MAX_SCALE && scale > 1) {
                    scale = target.MAX_SCALE / GetScaleXY(target)[0];
                } else if (scale * GetScaleXY(target)[0] <= target.MIN_SCALE && scale < 1) {
                    scale = target.MIN_SCALE / GetScaleXY(target)[0];
                }
            }
            mOldPx = (mDownX1 + mDownX2) / 2f;
            mOldPy = (mDownY1 + mDownY2) / 2f;
            target.m_matrix.postScale(scale, scale, mOldPx, mOldPy);
        }
    }

    protected void Run_R(Shape target, float x1, float y1, float x2, float y2) {
        target.m_matrix.set(mOldMatrix);
        Run_R_OP(target, x1, y1, x2, y2);
    }

    protected void Init_ZM_Data(Shape target, float x1, float y1, float x2, float y2) {
        mOldMatrix.set(target.m_matrix);
        init_Z_Data(x1, y1, x2, y2);
        init_M_Data((x1 + x2) / 2f, (y1 + y2) / 2f);
    }

    protected void Run_ZM(Shape target, float x1, float y1, float x2, float y2) {
        target.m_matrix.set(mOldMatrix);
        Run_Z_OP(target, x1, y1, x2, y2);
        Run_M_OP(target, (x1 + x2) / 2f, (y1 + y2) / 2f);
    }

    protected void Init_RZM_Data(Shape target, float x1, float y1, float x2, float y2) {
        mOldMatrix.set(target.m_matrix);
        init_R_Data(x1, y1, x2, y2);
        init_Z_Data(x1, y1, x2, y2);
        init_M_Data((x1 + x2) / 2f, (y1 + y2) / 2f);
    }

    protected void Run_RZM(Shape target, float x1, float y1, float x2, float y2) {
        target.m_matrix.set(mOldMatrix);
        Run_R_OP(target, x1, y1, x2, y2);
        Run_Z_OP(target, x1, y1, x2, y2);
        Run_M_OP(target, (x1 + x2) / 2f, (y1 + y2) / 2f);
    }

    protected int GetSelectIndex(float x, float y) {
        int index = -1;
        float[] dst = new float[2];
        float[] src = new float[]{x, y};
        for (int i = mShapeList.size() - 1; i >= 0; i--) {
            Shape shape = mShapeList.get(i);

            Matrix tempMatrix = new Matrix();

            tempMatrix.postConcat(mGlobalMatrix);
            tempMatrix.postConcat(shape.m_matrix);

//            Canvas canvas = new Canvas();
//            canvas.concat(mGlobalMatrix);
//            canvas.concat(shape.m_matrix);
//            canvas.getMatrix(tempMatrix);

            Matrix m2 = new Matrix();
            tempMatrix.invert(m2);
            m2.mapPoints(dst, src);
            if (dst[0] > 0 && dst[0] < shape.m_bmp.getWidth() && dst[1] > 0 && dst[1] < shape.m_bmp.getHeight()) {
                index = i;
                break;
            }
        }
        return index;
    }

    protected Shape GetSelectShapeByIndex(int index) {
        Shape out = null;
        if (index >= 0 && mShapeList.size() > index) {
            out = mShapeList.get(index);
        }
        return out;
    }

    protected Shape GetSelectShape(float x, float y) {
        Shape out = null;
        int index = GetSelectIndex(x, y);
        out = GetSelectShapeByIndex(index);
        return out;
    }

    /**
     * 获取显示矩形
     */
    protected RectF GetShowRect(Shape shape) {

        RectF rectF = new RectF(0, 0, shape.m_w, shape.m_h);
        Matrix tempMatrix = new Matrix();
        tempMatrix.postConcat(mGlobalMatrix);
        tempMatrix.postConcat(shape.m_matrix);
        tempMatrix.mapRect(rectF);

        return rectF;
    }

    /**
     * 获取shape的x和y的缩放值
     */
    protected float[] GetScaleXY(Shape shape) {
        float[] s = new float[2];
        if (shape != null && shape.m_matrix != null) {
            RectF rectF = GetShowRect(shape);
            s[0] = rectF.width() / shape.m_w;
            s[1] = rectF.height() / shape.m_h;
        }

//        Log.i("bbb", "ScaleXY: " + s[0] + " , " + s[1]);
        return s;
    }

    /**
     * 获取shape的中心点的xy
     */
    protected float[] GetCenterXY(Shape shape) {
        float[] c = new float[2];
        if (shape != null && shape.m_matrix != null) {
            RectF rectF = GetShowRect(shape);
            c[0] = rectF.centerX();
            c[1] = rectF.centerY();
        }

//        Log.i("bbb", "CenterXY: " + c[0] + " , " + c[1]);
        return c;
    }

    public boolean isInAnm = false;

    /**
     * 将中心点移动到指定的位置动画
     *
     * @param dstX 移动到指定x
     * @param dstY 移动到指定y
     * @param time
     */
    public void DoTranslateANM(float dstX, float dstY, int time) {

        if (mTarget == null) {
            return;
        }

        float cx = GetCenterXY(mTarget)[0];
        float cy = GetCenterXY(mTarget)[1];

        final float dx = dstX - cx;
        final float dy = dstY - cy;

        if (dx == 0 && dy == 0) {
            return;
        }

        AnimationUtils.valueAnimaFloat(time, 0, 1, new AnimaCallback() {
            @Override
            public void onAnimationStart(Animator animation) {

                isInAnm = true;
                setUILock(true);
                mOldMatrix.set(mTarget.m_matrix);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                if (mTarget != null) {
                    mTarget.m_matrix.set(mOldMatrix);
                    mTarget.m_matrix.postTranslate(dx, dy);
                    UpdateUI();
                }
                setUILock(false);
                isNeedReInitOpData = true;
                isInAnm = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float value = (float) animation.getAnimatedValue();

                if (mTarget != null) {
                    mTarget.m_matrix.set(mOldMatrix);
                    float x = value * dx;
                    float y = value * dy;
                    mTarget.m_matrix.postTranslate(x, y);
                    UpdateUI();
                }
            }
        });
    }

    /**
     * 缩放动画
     *
     * @param dstScale 缩放值(以当前的缩放为1)
     * @param px,py    缩放中心点
     * @param time
     */
    public void DoScaleANM(final float dstScale, final float px, final float py, int time) {

        AnimationUtils.valueAnimaFloat(time, 1, dstScale, new AnimaCallback() {
            @Override
            public void onAnimationStart(Animator animation) {

                isInAnm = true;
                setUILock(true);
                mOldMatrix.set(mTarget.m_matrix);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                if (mTarget != null) {
                    mTarget.m_matrix.set(mOldMatrix);
                    mTarget.m_matrix.postScale(dstScale, dstScale, px, py);
                    UpdateUI();
                }

                setUILock(false);
                isNeedReInitOpData = true;
                isInAnm = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float value = (float) animation.getAnimatedValue();

                if (mTarget != null) {
                    mTarget.m_matrix.set(mOldMatrix);
                    mTarget.m_matrix.postScale(value, value, px, py);

                    UpdateUI();
                }
            }
        });
    }

    /**
     * 将中心点移动到指定的位置动画
     * 同时以中心点缩放
     *
     * @param dstX     移动到指定x
     * @param dstY     移动到指定y
     * @param dstScale 缩放值(以当前的缩放为1)
     * @param px,py    缩放中心点
     * @param time
     */
    public void DoTranslateAndScaleANM(float dstX, float dstY, final float dstScale, final float px, final float py, int time) {

        if (mTarget == null) {
            return;
        }

        final float cx = GetCenterXY(mTarget)[0];
        final float cy = GetCenterXY(mTarget)[1];

        final float dx = dstX - cx;
        final float dy = dstY - cy;

        if (dx == 0 && dy == 0 && dstScale == 1) {
            isInAnm = false;
            return;
        }

        AnimationUtils.valueAnimaFloat(time, 0, 1, new AnimaCallback() {
            @Override
            public void onAnimationStart(Animator animation) {

                isInAnm = true;
                setUILock(true);
                mOldMatrix.set(mTarget.m_matrix);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                if (mTarget != null) {
                    mTarget.m_matrix.set(mOldMatrix);
                    if (dstScale != 1) {
                        mTarget.m_matrix.postScale(dstScale, dstScale, px, py);
                    }
                    mTarget.m_matrix.postTranslate(dx, dy);
                    UpdateUI();
                }
                setUILock(false);
                isNeedReInitOpData = true;
                isInAnm = false;

                Log.i("bbb", "w: " + GetShowRect(mTarget).width());
//                Log.i("bbb", "left: " + GetShowRect(mTarget).left);
                Log.i("bbb", "scale: " + GetScaleXY(mTarget)[0]);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float value = (float) animation.getAnimatedValue();

                if (mTarget != null && value != 0) {
                    mTarget.m_matrix.set(mOldMatrix);
                    float x = value * dx;
                    float y = value * dy;
                    float scale = 1 + value * (dstScale - 1);

//                    Log.i("bbb", "value: " + scale);

                    if (dstScale != 1) {
                        mTarget.m_matrix.postScale(scale, scale, px, py);
                    }
                    mTarget.m_matrix.postTranslate(x, y);
                    UpdateUI();
                }
            }
        });
    }

    protected abstract void SingleDown(MotionEvent event);

    protected abstract void SingleMove(MotionEvent event);

    protected abstract void SingleUp(MotionEvent event);

    protected abstract void TwoDown(MotionEvent event);

    protected abstract void TwoMove(MotionEvent event);

    protected abstract void TwoUp(MotionEvent event);

    public static class Shape implements Cloneable {

        private static int SOLE_NUM = 0x0001;
        public int m_soleId; //当前类的唯一id(用于区分不同对象)

        protected int GetSoleId() {
            return ++SOLE_NUM;
        }

        public Shape() {
            m_soleId = GetSoleId();
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        public enum Flip {
            NONE(0),
            HORIZONTAL(1),
            VERTICAL(2),;

            private final int m_value;

            Flip(int value) {
                m_value = value;
            }

            public int GetValue() {
                return m_value;
            }
        }

        public Matrix m_matrix = new Matrix();
        public Paint m_paint = new Paint();
        public Bitmap m_bmp;
        public Object m_ex;
        public Object m_info;

        public float MAX_SCALE = 2f;
        public float DEF_SCALE = 1f;
        public float MIN_SCALE = 0.5f;
        public boolean isLimitScale = true;

        public float m_w = 0, m_h = 0;
        public float m_x = 0, m_y = 0;
        public float m_degree = 0f;
        public float m_scaleX = 1f, m_scaleY = 1f;
        public Flip m_flip = Flip.NONE;
    }

}
