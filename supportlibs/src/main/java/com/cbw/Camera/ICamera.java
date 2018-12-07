package com.cbw.Camera;

import android.hardware.Camera;
import android.view.SurfaceHolder;

/**
 * Created by cbw on 2018/12/3.
 */
public interface ICamera {

    void setCameraCallback(OnCameraCallback onCameraCallback);

    void setSurface(SurfaceHolder surfaceHolder);

    void setPreviewSize(int with, int height, int sizeType);

    void setPictureSize(int with, int height, int sizeType);

    void openCamera(int id);

    void takePicture();

    void setFlashMode(String flashMode);

    void setFocusMode(String focusMode);

    void setFocusAndMeteringArea(float focusX, float focusY, float meteringX, float meteringY, float ratio);

    void setDisplayOrientation(int orientation);

    void setShutterSound(boolean enabled);

    void setExposureValue(int exposureValue);

    void setCameraZoom(int value);

    void releaseCamera();

    int getDisplayOrientation();

    int getPictureDegrees();

    interface OnCameraCallback extends Camera.AutoFocusCallback, Camera.ShutterCallback, Camera.PreviewCallback, Camera.PictureCallback, Camera.ErrorCallback {
    }
}
