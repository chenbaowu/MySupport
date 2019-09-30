package com.cbw.mysupport;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Outline;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cbw.Camera.CameraV1;
import com.cbw.Camera.ICamera;
import com.cbw.base.BaseActivity;
import com.cbw.utils.OnAnimatorTouchListener;

/**
 * Created by cbw on 2018/12/3.
 */
public class CameraActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.activity_test);

        keepScreenWakeup(true);
        init();
    }

    private TextView tv_show;
    private SurfaceView surfaceView;
    private ImageView iv_pic;

    public class TextureVideoViewOutlineProvider extends ViewOutlineProvider {
        private float mRadius;

        public TextureVideoViewOutlineProvider(float radius) {
            this.mRadius = radius;
        }

        @Override
        public void getOutline(View view, Outline outline) {
            final int margin = Math.min(view.getWidth(), view.getHeight()) / 10;
            outline.setRoundRect(margin, margin, view.getWidth() - margin, view.getHeight() - margin, mRadius);
        }
    }

    private void init() {
        this.findViewById(R.id.btn_add).setOnTouchListener(animatorTouchListener);
        this.findViewById(R.id.btn_delete).setOnTouchListener(animatorTouchListener);
        this.findViewById(R.id.btn_update).setOnTouchListener(animatorTouchListener);
        this.findViewById(R.id.btn_find).setOnTouchListener(animatorTouchListener);
        iv_pic = findViewById(R.id.iv_pic);
        tv_show = this.findViewById(R.id.tv_show);
        tv_show.setOnTouchListener(animatorTouchListener);
        surfaceView = this.findViewById(R.id.surfaceView);
        surfaceView.setOnTouchListener(animatorTouchListener);

        surfaceView.setOutlineProvider(new TextureVideoViewOutlineProvider(100));
        surfaceView.setClipToOutline(true);

        int screenOrientation = mContext.getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            mSurfaceWith = 1440;
            mSurfaceHeight = 1080;
        } else {
            mSurfaceWith = 1080;
            mSurfaceHeight = 1440;
        }
        surfaceView.getLayoutParams().width = mSurfaceWith;
        surfaceView.getLayoutParams().height = mSurfaceHeight;

        surfaceView.setVisibility(View.VISIBLE);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (camera1 == null) {
                    camera1 = new CameraV1(mContext);
                    camera1.setCameraCallback(new ICamera.OnCameraCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            Log.i("bbb", "onAutoFocus: " + success);
                        }

                        @Override
                        public void onError(int error, Camera camera) {
                            Log.i("bbb", "onError: " + error);
                        }

                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            iv_pic.setImageBitmap(bitmap);
                            iv_pic.setRotation(camera1.getPictureDegrees(mScreenRotation));
                            iv_pic.setVisibility(View.VISIBLE);
                            Log.i("bbb", "onPictureTaken: " + bitmap.getWidth() + "," + bitmap.getHeight() + " ,mScreenRotation: " + mScreenRotation);
                        }

                        @Override
                        public void onPreviewFrame(byte[] data, Camera camera) {

                        }

                        @Override
                        public void onShutter() {

                        }
                    });
                }
                camera1.setSurface(holder);
                Log.i("bbb", "surfaceCreated: ");
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (camera1 != null) {
                    camera1.setPreviewSize(width, height, 0);
                    camera1.setDisplayOrientation(-1);
                }
                Log.i("bbb", "surfaceChanged: " + width + "," + height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                camera1.releaseCamera();
            }
        });


        mOrientationListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {

                if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) { //手机平放时，检测不到有效的角度
                    return;
                }

                if (orientation > 350 || orientation < 10) { //0度
                    mScreenRotation = 0;
                } else if (orientation > 80 && orientation < 100) { //90度
                    mScreenRotation = 90;
                } else if (orientation > 170 && orientation < 190) { //180度
                    mScreenRotation = 180;
                } else if (orientation > 260 && orientation < 280) { //270度
                    mScreenRotation = 270;
                }
            }
        };

        mOrientationListener.enable();
    }

    private int mScreenRotation;
    private CameraV1 camera1;
    private OrientationEventListener mOrientationListener;
    private int mSurfaceWith, mSurfaceHeight;

    private OnAnimatorTouchListener animatorTouchListener = new OnAnimatorTouchListener() {
        @Override
        public void onActionClick(View v) {

            switch (v.getId()) {
                case R.id.btn_add:
                    if (camera1 != null) {
                        camera1.setExposureValue(6);
                        camera1.setCameraZoom(10);
                        camera1.setPictureFormat(ImageFormat.RGB_565);
                    }
                    break;
                case R.id.btn_delete:
                    if (camera1 != null) {
//                        camera1.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                        camera1.setShutterSound(true);
                        camera1.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    }
                    break;
                case R.id.btn_update:
                    iv_pic.setVisibility(View.GONE);
                    if (camera1 != null) {
                        camera1.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
                    }
                    break;
                case R.id.btn_find:
                    iv_pic.setVisibility(View.GONE);
                    if (camera1 != null) {
                        camera1.openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    }
                    break;
                case R.id.tv_show:
                    if (camera1 != null) {
                        camera1.takePicture();
                    }
                    break;
            }
        }

        @Override
        public boolean onActionDown(View v, MotionEvent event) {
            if (v == surfaceView) {
                iv_pic.setVisibility(View.GONE);
                if (camera1 != null && camera1.getCamera() != null) {
                    camera1.setFocusAndMeteringArea(event.getX(), event.getY(), event.getX(), event.getY());
                }
                return false;
            }
            return super.onActionDown(v, event);
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("bbb", "onConfigurationChanged: " + newConfig.orientation);
        if (surfaceView != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                surfaceView.getLayoutParams().width = 1440;
                surfaceView.getLayoutParams().height = 1080;
            } else {
                surfaceView.getLayoutParams().width = 1080;
                surfaceView.getLayoutParams().height = 1440;
            }
            surfaceView.requestLayout();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void keepScreenWakeup(boolean on) {
        if (on) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

}
