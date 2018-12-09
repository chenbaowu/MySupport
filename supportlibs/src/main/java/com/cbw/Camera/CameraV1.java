package com.cbw.Camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.support.v4.math.MathUtils;
import android.util.SparseArray;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cbw on 2018/12/4.
 */
public class CameraV1 implements ICamera, ICamera.OnCameraCallback {

    private Context mContext;

    private Camera mCamera;
    private int mCameraNumber;
    private SparseArray<Camera.CameraInfo> mCameraInfoMap = new SparseArray();
    private SurfaceHolder mSurfaceHolder;
    private OnCameraCallback mOnCameraCallback;

    /**
     * 当前镜头id
     */
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    /**
     * 当前镜头的预览角度
     */
    private int mDisplayOrientation = -1;

    /**
     * 照片的角度
     */
    private int mPictureRotation = -1;

    /**
     * 预览Size
     */
    private int mPreviewWith = 1440, mPreviewHeight = 1080;

    /**
     * 图片Size
     */
    private int mPictureWith = 1440, mPictureHeight = 1080;

    /**
     * size选择类型
     * 0 选择最合适
     * 1 选择最大
     * 2 选择最小
     */
    private int mPreviewSizeType = 0, mPictureSizeType = 0;

    public CameraV1(Context context) {
        mContext = context;

        getCameraInfo();
    }

    @Override
    public void setCameraCallback(OnCameraCallback onCameraCallback) {
        mOnCameraCallback = onCameraCallback;
    }

    public void setSurface(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
    }

    /**
     * Set preview size
     *
     * @param with
     * @param height
     * @param sizeType {@link #mPreviewSizeType}
     */
    public void setPreviewSize(int with, int height, int sizeType) {
        mPreviewWith = with;
        mPreviewHeight = height;
        mPreviewSizeType = sizeType;
    }

    /**
     * Set picture size
     *
     * @param with
     * @param height
     * @param sizeType {@link #mPictureSizeType}
     */
    public void setPictureSize(int with, int height, int sizeType) {
        mPreviewWith = with;
        mPreviewHeight = height;
        mPictureSizeType = sizeType;
    }

    public Camera getCamera() {
        return mCamera;
    }

    private void getCameraInfo() {
        mCameraNumber = Camera.getNumberOfCameras();

        for (int i = 0; i < mCameraNumber; i++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            try {
                Camera.getCameraInfo(i, cameraInfo);
                mCameraInfoMap.put(i, cameraInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void openCamera(int id) {

        mCameraId = id;
        if (id >= mCameraNumber) {
            mCameraId = 0;
        }
        releaseCamera();

        try {
            mCamera = Camera.open(mCameraId);
            mCamera.setErrorCallback(this);
            Camera.Parameters mCameraParameters = mCamera.getParameters();

            setOptimalPreviewSize(mPreviewWith, mPreviewHeight);
            setOptimalPictureSize(mPictureWith, mPictureHeight);

            setDisplayOrientation(-1);
            startPreview();

        } catch (Exception e) {
            e.printStackTrace();
            if (mOnCameraCallback != null) {
                mOnCameraCallback.onError(ICamera.CAMERA_ERROR_OPEN_FAIL, null);
            }
        }
    }

    @Override
    public void takePicture() {

        if (checkErrorStatus()) return;

        try {
            mCamera.takePicture(mEnableShutterSound ? this : null, null, this);
        } catch (Exception e) {
            e.printStackTrace();
            stopPreview();
            startPreview();
        }
    }

    /**
     * Set picture format
     *
     * @see android.graphics.ImageFormat
     * @see Camera.Parameters#getSupportedPictureFormats()
     */
    @Override
    public void setPictureFormat(int pixel_format) {

        if (checkErrorStatus()) return;

        try {
            Camera.Parameters mCameraParameters = mCamera.getParameters();
            mCameraParameters.setPictureFormat(pixel_format);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String mFlashMode;

    /**
     * Set flashMode
     *
     * @param flashMode
     * @see Camera.Parameters#FOCUS_MODE_AUTO
     */
    public void setFlashMode(String flashMode) {

        if (checkErrorStatus()) return;
        Camera.Parameters mCameraParameters = mCamera.getParameters();

        List<String> mSupportedFlashModes = mCameraParameters.getSupportedFlashModes();
        if (mSupportedFlashModes != null && mSupportedFlashModes.contains(flashMode)) {
            mCameraParameters.setFlashMode(flashMode);
            mCamera.setParameters(mCameraParameters);
            mFlashMode = flashMode;
        }
    }

    private String mFocusMode;

    /**
     * Set focusMode
     *
     * @param focusMode
     * @see Camera.Parameters#FOCUS_MODE_INFINITY
     */
    public void setFocusMode(String focusMode) {

        if (checkErrorStatus()) return;
        Camera.Parameters mCameraParameters = mCamera.getParameters();

        List<String> mSupportedFocusModes = mCameraParameters.getSupportedFocusModes();
        if (mSupportedFocusModes != null && mSupportedFocusModes.contains(focusMode)) {
            mCameraParameters.setFocusMode(focusMode);
            mCamera.setParameters(mCameraParameters);
            mFocusMode = focusMode;
        }
    }

    /**
     * 设置对焦区域和测光区域<br/>
     * List<Camera.Area>中的每一个Area的范围是（-1000，-1000）到（1000， 1000）<br/>
     * 也就是说屏幕中心为原点，左上角为（-1000，-1000），右下角为（1000，1000）
     *
     * @param focusX
     * @param focusY
     * @param meteringX
     * @param meteringY
     * @param ratio     镜头预览尺寸与UI尺寸的比例
     */
    public void setFocusAndMeteringArea(float focusX, float focusY, float meteringX, float meteringY, float ratio) {

        if (checkErrorStatus()) return;
        Camera.Parameters mCameraParameters = mCamera.getParameters();

        boolean mFocusAreaSupported = mCameraParameters.getMaxNumFocusAreas() > 0;
        if (focusX != -1 && focusY != -1 && mFocusAreaSupported) {
            List<Camera.Area> focusAreas = new ArrayList<>();
            Rect focusRect = calculateCameraArea(true, 300, 1f, focusX, focusY, ratio);
            if (focusRect != null) {
                mCamera.cancelAutoFocus();

                /*当闪光灯模式为 FLASH_MODE_ON 或 FLASH_MODE_AUTO 时，调用autoFocus闪光灯会闪*/
                String flashMode = mCameraParameters.getFlashMode();
                if (Camera.Parameters.FLASH_MODE_ON.equals(flashMode) || Camera.Parameters.FLASH_MODE_AUTO.equals(flashMode)) {
                    mCameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                }

                mFocusMode = mCameraParameters.getFocusMode();
                if (!mFocusMode.equals(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }

                focusAreas.add(new Camera.Area(focusRect, 1000));
                mCameraParameters.setFocusAreas(focusAreas);
                mCamera.autoFocus(mOnCameraCallback);
            }
        }

        boolean mMeteringSupported = mCameraParameters.getMaxNumMeteringAreas() > 0;
        if (meteringX != -1 && meteringY != -1 && mMeteringSupported) {
            List<Camera.Area> meteringAreas = new ArrayList<>();
            Rect meteringRect = calculateCameraArea(false, 300, 1.5f, meteringX, meteringY, ratio);
            if (meteringRect != null) {
                meteringAreas.add(new Camera.Area(meteringRect, 1000));
                mCameraParameters.setMeteringAreas(meteringAreas);
            }
        }

        mCamera.setParameters(mCameraParameters);
    }

    /**
     * Set the clockwise rotation of preview display in degrees
     *
     * @param orientation 角度 , 如果是 -1 则计算默认值
     */
    public void setDisplayOrientation(int orientation) {

        if (checkErrorStatus()) return;

        int result;
        if (orientation == -1) {
            Camera.CameraInfo cameraInfo = mCameraInfoMap.get(mCameraId);
            if (cameraInfo == null) {
                return;
            }

            int rotation = ((Activity) mContext).getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break;
                case Surface.ROTATION_90:
                    degrees = 90;
                    break;
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;
            }

            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (cameraInfo.orientation + degrees) % 360;
                result = (360 - result) % 360;  // compensate the mirror
            } else {  // back-facing
                result = (cameraInfo.orientation - degrees + 360) % 360;
            }
        } else {
            result = orientation;
        }

        try {
            mCamera.setDisplayOrientation(result);
            mDisplayOrientation = result;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The camera driver may set orientation in the EXIF header without rotating the picture
     *
     * @param screenRotation 屏幕角度
     */
    public void setPictureRotation(int screenRotation) {

        if (checkErrorStatus()) return;

        try {
            int result = getPictureDegrees(screenRotation);
            Camera.Parameters mCameraParameters = mCamera.getParameters();
            mCameraParameters.setRotation(result);
            mPictureRotation = result;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 有些机型需要在takePicture时将ShutterCallback置为null，拍照才没声音
     * 某些厂商可能会在framework层写死不可修改 {@link android.hardware.Camera.CameraInfo#canDisableShutterSound}
     */
    private boolean mEnableShutterSound;

    /**
     * 设置是否开启快门声音
     *
     * @param enabled
     */
    public void setShutterSound(boolean enabled) {

        if (checkErrorStatus()) return;

        try {
            mCamera.enableShutterSound(enabled);
            mEnableShutterSound = enabled;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置曝光值
     *
     * @param exposureValue 亮度
     */
    public void setExposureValue(int exposureValue) {

        if (checkErrorStatus()) return;

        Camera.Parameters mCameraParameters = mCamera.getParameters();
        if (exposureValue < mCameraParameters.getMinExposureCompensation()) {
            exposureValue = mCameraParameters.getMinExposureCompensation();
        } else if (exposureValue > mCameraParameters.getMaxExposureCompensation()) {
            exposureValue = mCameraParameters.getMaxExposureCompensation();
        }
        mCameraParameters.setExposureCompensation(exposureValue);
        mCamera.setParameters(mCameraParameters);
    }

    /**
     * 设置焦距
     *
     * @param value 焦距
     */
    public void setCameraZoom(int value) {

        if (checkErrorStatus()) return;

        Camera.Parameters mCameraParameters = mCamera.getParameters();
        if (mCameraParameters.isZoomSupported()) {
            value = MathUtils.clamp(value, 0, mCameraParameters.getMaxZoom());
            mCameraParameters.setZoom(value);
            mCamera.setParameters(mCameraParameters);
        }
    }

    /**
     * 设置预览size，在开始预览之前设置
     */
    private void setOptimalPreviewSize(int width, int height) {
        
        Camera.Parameters mCameraParameters = mCamera.getParameters();
        List<Camera.Size> mSupportedPreviewSizes = sortCameraSizes(mCameraParameters.getSupportedPreviewSizes(), true);

        Camera.Size mPreviewSize = null;

        if (mPreviewSizeType == 0) {
            int max = width > height ? width : height;
            int min = width > height ? height : width;
            for (Camera.Size size : mSupportedPreviewSizes) {
                if (size.width == max && size.height == min) {
                    mPreviewSize = size;
                }
            }
        }

        if (mPreviewSize == null) {
            mPreviewSize = getOptimalSize(mSupportedPreviewSizes, width, height, mPreviewSizeType);
        }

        if (mPreviewSize != null) {
            mPreviewWith = mPreviewSize.width;
            mPreviewHeight = mPreviewSize.height;
        }
        mCameraParameters.setPreviewSize(mPreviewWith, mPreviewHeight);
        mCamera.setParameters(mCameraParameters);
    }

    /**
     * 设置图片size，在开始预览之前设置
     */
    private void setOptimalPictureSize(int width, int height) {
        
        Camera.Parameters mCameraParameters = mCamera.getParameters();
        List<Camera.Size> mSupportedPictureSizes = sortCameraSizes(mCameraParameters.getSupportedPictureSizes(), true);

        Camera.Size mPictureSize = getOptimalSize(mSupportedPictureSizes, width, height, mPictureSizeType);

        if (mPictureSize != null) {
            mPictureWith = mPictureSize.width;
            mPictureHeight = mPictureSize.height;
        }
        mCameraParameters.setPictureSize(mPictureWith, mPictureHeight);
        mCamera.setParameters(mCameraParameters);
    }

    /**
     * @param sizes 支持的size
     * @param desc  降序
     * @return
     */
    private List<Camera.Size> sortCameraSizes(List<Camera.Size> sizes, final boolean desc) {
        if (sizes == null) {
            return sizes;
        }
        Collections.sort(sizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size size1, Camera.Size size2) {
                int areaOne = size1.height * size1.width;
                int areaTwo = size2.height * size2.width;
                if (areaOne > areaTwo) {
                    return desc ? -1 : 1;
                } else if (areaTwo == areaOne) {
                    int big1 = Math.max(size1.height, size1.width);
                    int big2 = Math.max(size2.height, size2.width);
                    if (big1 > big2) {
                        return desc ? -1 : 1;
                    } else if (big1 == big2) {
                        int small1 = Math.min(size1.height, size1.width);
                        int small2 = Math.min(size2.height, size2.width);
                        if (small1 > small2) {
                            return desc ? -1 : 1;
                        } else if (small1 == small2) {
                            return 0;
                        } else {
                            return desc ? 1 : -1;
                        }
                    } else {
                        return desc ? 1 : -1;
                    }
                } else {
                    return desc ? 1 : -1;
                }
            }
        });
        return sizes;
    }

    /**
     * @param mSupportedSizes
     * @param width
     * @param height
     * @param sizeType        {@link #mPreviewSizeType#mPictureSizeType }
     * @return
     */
    private Camera.Size getOptimalSize(List<Camera.Size> mSupportedSizes, int width, int height, int sizeType) {
        Camera.Size mSize = null;

        float targetRatio;
        int targetHeight;
        if (width > height) {
            targetRatio = (float) width / height;
            targetHeight = height;
        } else {
            targetRatio = (float) height / width;
            targetHeight = width;
        }

        List<Camera.Size> tempSizes = null;
        for (Camera.Size size : mSupportedSizes) {
            float ratio = size.width / (float) size.height;
            if (Math.abs(ratio - targetRatio) <= 0.005f) {
                if (tempSizes == null) {
                    tempSizes = new ArrayList<>();
                }
                tempSizes.add(size);
            }
        }

        if (tempSizes != null && !tempSizes.isEmpty()) {
            for (Camera.Size size : tempSizes) {
                float ratio = size.width / (float) size.height;
                if (targetRatio == ratio) {
                    mSize = size;
                    break;
                }
            }
        }

        return mSize;
    }

    /**
     * Convert touch position x:y to {@link Camera.Area} position -1000:-1000 to 1000:1000.
     *
     * @param areaBaseSize 对焦框（测光框）基本大小
     * @param coefficient  设置对焦框（测光框）的大小，作为百分比进行调节。
     * @param ratio        镜头预览尺寸与UI尺寸的比例
     * @return
     */
    private Rect calculateCameraArea(boolean isFocusRect, float areaBaseSize, float coefficient, float x, float y, float ratio) {
        if (mPreviewWith < 0 || mPreviewHeight < 0 || x < 0 || y < 0) {
            return null;
        }
        int areaSize = (int) (areaBaseSize * coefficient);
        if (ratio <= 0) {
            ratio = 1.0f;
        }
        int centerX = (int) (y * ratio / mPreviewWith * 2000 - 1000);
        int centerY = (int) (x * ratio / mPreviewHeight * 2000 - 1000);

        int left = MathUtils.clamp(centerX - areaSize / 2, -1000, 1000);
        int right = MathUtils.clamp(left + areaSize, -1000, 1000);
        int top = MathUtils.clamp(centerY - areaSize / 2, -1000, 1000);
        int bottom = MathUtils.clamp(top + areaSize, -1000, 1000);

        Rect result;
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            result = new Rect(left, -bottom, right, -top);
        } else {
            if (isFocusRect) {
                result = new Rect(left, top, right, bottom);
            } else {
                result = new Rect(-right, -bottom, -left, -top);
            }
        }
        return result;
    }

    private void startPreview() {
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopPreview() {
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
        }
        mCamera = null;
        mDisplayOrientation = -1;
        mPictureRotation = -1;

    }

    private boolean checkErrorStatus() {
        return mCamera == null;
    }

    public int getDisplayOrientation() {
        return mDisplayOrientation;
    }

    /**
     * 获取照片角度
     *
     * @param screenRotation 屏幕角度
     * @return
     */
    public int getPictureDegrees(int screenRotation) {

        Camera.CameraInfo cameraInfo = mCameraInfoMap.get(mCameraId);
        if (cameraInfo == null) {
            return 0;
        }

        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mPictureRotation = (cameraInfo.orientation - screenRotation + 360) % 360;
        } else {  // back-facing camera
            mPictureRotation = (cameraInfo.orientation + screenRotation) % 360;
        }
        return mPictureRotation;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {

        Camera.Parameters mCameraParameters = mCamera.getParameters();
        if (mFlashMode != null && !mCameraParameters.getFlashMode().equals(mFlashMode)) {
            setFlashMode(mFlashMode);
        }
        if (mFocusMode != null && !mCameraParameters.getFocusMode().equals(mFocusMode)) {
            setFocusMode(mFocusMode);
        }
        if (mOnCameraCallback != null) {
            mOnCameraCallback.onAutoFocus(success, camera);
        }
    }

    /**
     * Callback for camera errors.
     *
     * @param error  error code:
     *               <ul>
     *               <li>{@link Camera#CAMERA_ERROR_UNKNOWN}
     *               <li>{@link Camera#CAMERA_ERROR_SERVER_DIED}
     *               </ul>
     * @param camera the Camera service object
     */
    @Override
    public void onError(int error, Camera camera) {
        switch (error) {
            case ICamera.CAMERA_ERROR_OPEN_FAIL:
                break;
            case Camera.CAMERA_ERROR_EVICTED:
                break;
            case Camera.CAMERA_ERROR_SERVER_DIED:
                break;
            case Camera.CAMERA_ERROR_UNKNOWN:
                break;
            default:
                break;
        }
        if (mOnCameraCallback != null) {
            mOnCameraCallback.onError(error, camera);
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        if (mOnCameraCallback != null) {
            mOnCameraCallback.onPictureTaken(data, camera);
        }
        startPreview();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mOnCameraCallback != null) {
            mOnCameraCallback.onPreviewFrame(data, camera);
        }
    }

    @Override
    public void onShutter() {
        if (mOnCameraCallback != null) {
            mOnCameraCallback.onShutter();
        }
    }
}
