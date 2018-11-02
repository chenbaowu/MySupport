package com.cbw.mediaCodec.decode;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import com.cbw.mediaCodec.base.MediaCodeUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by cbw on 2018/10/30.
 */
public class VideoDecode {

    private static final int TIMEOUT_USEC = 10000;

    private MediaCodec mDecoder;
    private MediaExtractor mExtractor;
    private MediaFormat mFormat;

    private String mInputPath;
    private int mWidth;
    private int mHeight;
    private int mColorFormat;
    private int mPixelFormat;

    public VideoDecode(String inputPath) {
        mInputPath = inputPath;
    }

    /**
     * 准备
     */
    public void prepare() {

        try {
            mExtractor = new MediaExtractor();
            mExtractor.setDataSource(mInputPath);
            int trackIndex = MediaCodeUtils.SelectTrack(mExtractor, "video/");
            mExtractor.selectTrack(trackIndex);
            mFormat = mExtractor.getTrackFormat(trackIndex);

            mColorFormat = mFormat.getInteger(MediaFormat.KEY_COLOR_FORMAT);
            mWidth = mFormat.getInteger(MediaFormat.KEY_WIDTH);
            mHeight = mFormat.getInteger(MediaFormat.KEY_HEIGHT);

            String mime = mFormat.getString(MediaFormat.KEY_MIME);
            mDecoder = MediaCodec.createDecoderByType(mime);
            mDecoder.configure(mFormat, null, null, 0);
            mDecoder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean mInputDone;
    private boolean mOutputDone;
    private boolean mStarted;

    /**
     * 开始
     */
    public void start() {

        mStarted = true;
        ByteBuffer[] decoderInputBuffers = mDecoder.getInputBuffers();
        ByteBuffer[] decoderOutputBuffers = mDecoder.getOutputBuffers();
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

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
                } else if (decoderStatus < 0) {

                } else {
                    if (bufferInfo.presentationTimeUs >= 0) {
                        ByteBuffer buffer = decoderOutputBuffers[decoderStatus];


                        mDecoder.releaseOutputBuffer(decoderStatus, false);
                        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            mOutputDone = true;
                        }
                    } else {
                        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            mOutputDone = true;
                        }
                    }

                    mDecoder.releaseOutputBuffer(decoderStatus, false);
                }
            }
        }
    }

    /**
     * 释放
     */
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
