package com.cbw.mediaCodec.decode;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.Surface;

import com.cbw.mediaCodec.base.MediaCodeUtils;
import com.cbw.utils.PathUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by cbw on 2018/10/30.
 */
public class VideoDecode {

    private static final String TAG = "VideoDecode";
    private static final int TIMEOUT_USEC = 10000;

    private MediaCodec mDecoder;
    private MediaExtractor mExtractor;
    private MediaFormat mFormat;

    private String mInputPath;
    private int mWidth;
    private int mHeight;
    private int mRotation;
    private int mColorFormat;
    private int mPixelFormat;

    public VideoDecode(String inputPath) {
        mInputPath = inputPath;
    }

    private Surface mSurface;

    public void setSurface(Surface surface) {
        mSurface = surface;
    }

    public void prepare() {

        try {
            reset();
            mExtractor = new MediaExtractor();
            mExtractor.setDataSource(mInputPath);
            int trackIndex = MediaCodeUtils.SelectTrack(mExtractor, "video/");
            mExtractor.selectTrack(trackIndex);
            mFormat = mExtractor.getTrackFormat(trackIndex);

            mWidth = mFormat.getInteger(MediaFormat.KEY_WIDTH);
            mHeight = mFormat.getInteger(MediaFormat.KEY_HEIGHT);

            if (mFormat.containsKey("rotation-degrees")) { // 如果 true 视频的纹理矩阵加了角度处理 ，否则要自己处理角度
                mRotation = mFormat.getInteger("rotation-degrees");
            } else {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                try {
                    retriever.setDataSource(mInputPath);
                    String s = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                    mRotation = Integer.valueOf(s);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    retriever.release();
                }
            }

            if (mRotation % 180 != 0) {
                int temp = mWidth;
                mWidth = mHeight;
                mHeight = temp;
            }

            String mime = mFormat.getString(MediaFormat.KEY_MIME);
            mDecoder = MediaCodec.createDecoderByType(mime);
            mDecoder.configure(mFormat, mSurface, null, 0);
            mDecoder.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        mInputDone = false;
        mOutputDone = false;
        mStarted = false;
        mStartTime = 0;
        release();
    }

    /**
     * seek到指定时间
     *
     * @param timeMs   时间戳,单位毫秒
     * @param keyFrame 是否关键帧,如果是false会比较慢需要遍历时间戳
     */
    public void seekTo(long timeMs, boolean keyFrame) {
        if (keyFrame) {
            mExtractor.seekTo(timeMs * 1000, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
            mStartTime = 0;
        } else {
            mStartTime = timeMs * 1000;
        }
    }

    private boolean mInputDone;
    private boolean mOutputDone;
    private boolean mStarted;
    private long mStartTime;
    private int outputFrameCount;

    public void start() {

        mStarted = true;
        ByteBuffer[] decoderInputBuffers = mDecoder.getInputBuffers();
        ByteBuffer[] decoderOutputBuffers = mDecoder.getOutputBuffers();
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        outputFrameCount = 0;

        while (!mOutputDone) {

            if (!mInputDone) {
                int inputBufIndex = mDecoder.dequeueInputBuffer(TIMEOUT_USEC);
                if (inputBufIndex >= 0) {
                    ByteBuffer inputBuf = decoderInputBuffers[inputBufIndex];
                    int chunkSize = mExtractor.readSampleData(inputBuf, 0);
                    if (chunkSize < 0) {
                        mDecoder.queueInputBuffer(inputBufIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        mInputDone = true;
                    } else {
                        long presentationTimeUs = mExtractor.getSampleTime();
                        mDecoder.queueInputBuffer(inputBufIndex, 0, chunkSize, presentationTimeUs, 0);
                        mExtractor.advance();
                    }
                }
            }

            if (!mOutputDone) {
                int decoderStatus = mDecoder.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
                if (decoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {

                } else if (decoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {

                    decoderOutputBuffers = mDecoder.getOutputBuffers();

                } else if (decoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    mFormat = mDecoder.getOutputFormat();
//                    int strideWidth = mWidth;
//                    int sliceHeight = mHeight;
//                    if (mFormat.containsKey(MediaFormat.KEY_STRIDE)) {
//                        strideWidth = mFormat.getInteger(MediaFormat.KEY_STRIDE);
//                    }
//                    if (mFormat.containsKey(MediaFormat.KEY_SLICE_HEIGHT)) {
//                        sliceHeight = mFormat.getInteger(MediaFormat.KEY_SLICE_HEIGHT);
//                    }
//                    if (strideWidth > 0 && sliceHeight > 0) {
//                        mCropRight = strideWidth - mWidth;
//                        mCropBottom = sliceHeight - mHeight;
//                        mWidth = strideWidth;
//                        mHeight = sliceHeight;
//                    }
//                    if (mFormat.containsKey("crop-left") && mFormat.containsKey("crop-right")) {
//                        mCropLeft = mFormat.getInteger("crop-left");
//                        mCropRight = mWidth - mFormat.getInteger("crop-right") - 1;
//                    }
//                    if (mFormat.containsKey("crop-top") && mFormat.containsKey("crop-bottom")) {
//                        mCropTop = mFormat.getInteger("crop-top");
//                        mCropBottom = mHeight - mFormat.getInteger("crop-bottom") - 1;
//                    }
                    if (mFormat.containsKey(MediaFormat.KEY_COLOR_FORMAT)) {
                        mColorFormat = mFormat.getInteger(MediaFormat.KEY_COLOR_FORMAT);
                    }
                } else if (decoderStatus < 0) {

                } else {
                    if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        mOutputDone = true;
                    }
                    if (bufferInfo.presentationTimeUs >= mStartTime) {
                        outputFrameCount++;
//                        ByteBuffer buffer = decoderOutputBuffers[decoderStatus];

//                        getOutputImage(decoderStatus);
                        mDecoder.releaseOutputBuffer(decoderStatus, mSurface != null);
                    } else {
                        mDecoder.releaseOutputBuffer(decoderStatus, false);
                    }
                }
            }
        }
    }

    private static final int COLOR_FormatI420 = 1;
    private static final int COLOR_FormatNV21 = 2;

    public static final int FILE_TypeI420 = 1;
    public static final int FILE_TypeNV21 = 2;
    public static final int FILE_TypeJPEG = 3;

    private int outputImageFileType = FILE_TypeI420;

    private void getOutputImage(int outputBufferId) {
        Image image = mDecoder.getOutputImage(outputBufferId);

        String fileName;
        switch (outputImageFileType) {
            case FILE_TypeI420:
                fileName = PathUtil.GetTempPath() + String.format("frame_%05d_I420_%dx%d.yuv", outputFrameCount, mWidth, mHeight);
                dumpFile(fileName, getDataFromImage(image, COLOR_FormatI420));
                break;
            case FILE_TypeNV21:
                fileName = PathUtil.GetTempPath() + String.format("frame_%05d_NV21_%dx%d.yuv", outputFrameCount, mWidth, mHeight);
                dumpFile(fileName, getDataFromImage(image, COLOR_FormatNV21));
                break;
            case FILE_TypeJPEG:
                fileName = PathUtil.GetTempPath() + outputFrameCount + ".jpg";
                compressToJpeg(fileName, image);
                break;
        }
        image.close();
    }

    private static void dumpFile(String fileName, byte[] data) {
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(fileName);
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to create output file " + fileName, ioe);
        }
        try {
            outStream.write(data);
            outStream.close();
        } catch (IOException ioe) {
            throw new RuntimeException("failed writing data to file " + fileName, ioe);
        }
    }

    private void compressToJpeg(String fileName, Image image) {
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(fileName);
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to create output file " + fileName, ioe);
        }
        Rect rect = image.getCropRect();
        YuvImage yuvImage = new YuvImage(getDataFromImage(image, COLOR_FormatNV21), ImageFormat.NV21, rect.width(), rect.height(), null);
        yuvImage.compressToJpeg(rect, 100, outStream);
    }

    public static byte[] getDataFromImage(Image image, int colorFormat) {
        if (colorFormat != COLOR_FormatI420 && colorFormat != COLOR_FormatNV21) {
            throw new IllegalArgumentException("only support COLOR_FormatI420 " + "and COLOR_FormatNV21");
        }
        if (!isImageFormatSupported(image)) {
            throw new RuntimeException("can't convert Image to byte array, format " + image.getFormat());
        }
        Rect crop = image.getCropRect();
        int format = image.getFormat();
        int width = crop.width();
        int height = crop.height();
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];

        int channelOffset = 0;
        int outputStride = 1;
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
                case 0:
                    channelOffset = 0;
                    outputStride = 1;
                    break;
                case 1:
                    if (colorFormat == COLOR_FormatI420) {
                        channelOffset = width * height;
                        outputStride = 1;
                    } else if (colorFormat == COLOR_FormatNV21) {
                        channelOffset = width * height;
                        outputStride = 2;
                    }
                    break;
                case 2:
                    if (colorFormat == COLOR_FormatI420) {
                        channelOffset = (int) (width * height * 1.25);
                        outputStride = 1;
                    } else if (colorFormat == COLOR_FormatNV21) {
                        channelOffset = width * height + 1;
                        outputStride = 2;
                    }
                    break;
            }
            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();

//            Log.v(TAG, "pixelStride " + pixelStride);
//            Log.v(TAG, "rowStride " + rowStride);
//            Log.v(TAG, "width : " + width + ", height :" + height);
//            Log.v(TAG, "buffer size " + buffer.remaining());

            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;
            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
            for (int row = 0; row < h; row++) {
                int length;
                if (pixelStride == 1 && outputStride == 1) {
                    length = w;
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
            Log.v(TAG, "Finished reading data from plane " + i);
        }
        return data;
    }

    private static boolean isImageFormatSupported(Image image) {
        int format = image.getFormat();
        switch (format) {
            case ImageFormat.YUV_420_888:
            case ImageFormat.NV21:
            case ImageFormat.YV12:
                return true;
        }
        return false;
    }

    public void release() {

        if (mDecoder != null) {
            if (mStarted) {
                try {
                    mDecoder.stop();
                    mStarted = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mDecoder.release();
            mDecoder = null;
        }

        if (mExtractor != null) {
            mExtractor.release();
            mExtractor = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        release();
        super.finalize();
    }
}
