package com.cbw.Camera;

import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by cbw on 2018/12/3.
 */
public interface ICamera {

    int CAMERA_ERROR_OPEN_FAIL = -1;

    void setCameraCallback(OnCameraCallback onCameraCallback);

    void setSurface(SurfaceHolder surfaceHolder);

    void setPreviewSize(int with, int height, int sizeType);

    void setPictureSize(int with, int height, int sizeType);

    void openCamera(int id);

    void takePicture();

    void setPictureFormat(int pixel_format);

    void setFlashMode(String flashMode);

    void setFocusMode(String focusMode);

    void setFocusAndMeteringArea(float focusX, float focusY, float meteringX, float meteringY);

    void setDisplayOrientation(int orientation);

    void setShutterSound(boolean enabled);

    void setExposureValue(int exposureValue);

    void setCameraZoom(int value);

    void releaseCamera();

    int getDisplayOrientation();

    int getPictureDegrees(int screenRotation);

    @Retention(RetentionPolicy.SOURCE)
    @interface CameraState {
        int CAMERA_IDLE = 1;
        int CAMERA_WAIT_OPEN = 2;
        int CAMERA_OPEN = 3;
        int CAMERA_WAIT_PREVIEW = 4;
        int CAMERA_PREVIEW = 5;
        int CAMERA_DOFOCUS = 6;
        int CAMERA_TAKEPICTURE = 7;
    }

    interface OnCameraCallback extends Camera.AutoFocusCallback, Camera.ShutterCallback, Camera.PreviewCallback, Camera.PictureCallback, Camera.ErrorCallback {
    }
}
