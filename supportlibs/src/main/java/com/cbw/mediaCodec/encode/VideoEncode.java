package com.cbw.mediaCodec.encode;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by cbw on 2018/10/30.
 */
public class VideoEncode {

    private static final String TAG = "VideoEncode";
    private static final int TIMEOUT_USEC = 10000;
    private static final String MIME_TYPE = "video/avc";
    private static final int FRAME_RATE = 30;
    private static final int I_FRAME_INTERVAL = 2;
    private static final float BPP = 0.25f;
    private static int mColorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface;

    private MediaCodec mEncoder;
    private MediaCodec.BufferInfo mBufferInfo;
    private MediaMuxer mMuxer;
    private Surface mInputSurface;
    public boolean mMuxerStarted;
    private int mTrackIndex;

    private String mOutputPath;
    private int mVideoWidth;
    private int mVideoHeight;

    public VideoEncode(String outputPath, int videoWidth, int videoHeight) {
        mOutputPath = outputPath;
        mVideoWidth = videoWidth;
        mVideoHeight = videoHeight;
    }

    public void prepare() {

        mBufferInfo = new MediaCodec.BufferInfo();

        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mVideoWidth, mVideoHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, mColorFormat);
        format.setInteger(MediaFormat.KEY_BIT_RATE, calculateBitRate());
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, I_FRAME_INTERVAL);

        try {
            mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                mEncoder = MediaCodec.createByCodecName("H264/AVC");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        try {
            mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mInputSurface = mEncoder.createInputSurface();
            mEncoder.start();
        }catch (Exception e){ // 错误打印不出来？
            e.printStackTrace();
        }

        try {
            mMuxer = new MediaMuxer(mOutputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mTrackIndex = -1;
        mMuxerStarted = false;
    }

    private int calculateBitRate() {
        final int bitrate = (int) (BPP * FRAME_RATE * mVideoWidth * mVideoHeight);
        return bitrate;
    }

    /**
     * Returns the encoder's input surface.
     */
    public Surface getInputSurface() {
        return mInputSurface;
    }

    public void start() {

        ByteBuffer[] encoderInputBuffers = mEncoder.getInputBuffers();
        ByteBuffer[] encoderOutputBuffers = mEncoder.getOutputBuffers();

        boolean inputDone = false;
        boolean outputDone = false;

        while (!outputDone) {

           /* if (!inputDone) {
                int inputBufIndex = mEncoder.dequeueInputBuffer(TIMEOUT_USEC);
                if (inputBufIndex >= 0) {
                    ByteBuffer inputBuf = encoderInputBuffers[inputBufIndex];
                    inputBuf.clear();
                    inputBuf.put(frameData); //  放入数据
                    mEncoder.queueInputBuffer(inputBufIndex, 0, frameData.length, ptsUsec, 0);
                } else {
                    // input buffer not available
                }
            }*/

            if (!outputDone) {
                int encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
                if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // no output available yet
                    Log.d(TAG, "no output from encoder available");
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    // not expected for an encoder
                    encoderOutputBuffers = mEncoder.getOutputBuffers();
                    Log.d(TAG, "encoder output buffers changed");
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    // should happen before receiving buffers, and should only happen once
                    MediaFormat newFormat = mEncoder.getOutputFormat();
                    Log.d(TAG, "encoder output format changed: " + newFormat);

                    if (mMuxerStarted) {
                        throw new RuntimeException("format changed twice");
                    }
                    mTrackIndex = mMuxer.addTrack(newFormat);
                    mMuxer.start();
                    mMuxerStarted = true;
                } else if (encoderStatus < 0) {
                    // let's ignore it
                    Log.d(TAG, "unexpected result from encoder.dequeueOutputBuffer: " + encoderStatus);
                } else {
                    ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                    if (encodedData == null) {
                        Log.d(TAG, "encoderOutputBuffer " + encoderStatus + " was null");
                    }

                    if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        // The codec config data was pulled out and fed to the muxer when we got
                        // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                        Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
                        mBufferInfo.size = 0;
                    }

                    if (mBufferInfo.size != 0) {
                        if (!mMuxerStarted) {
                            throw new RuntimeException("muxer hasn't started");
                        }

                        // adjust the ByteBuffer values to match BufferInfo (not needed?)
                        encodedData.position(mBufferInfo.offset);
                        encodedData.limit(mBufferInfo.offset + mBufferInfo.size);
//                        mBufferInfo.presentationTimeUs = ;

                        mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                        Log.d(TAG, "sent " + mBufferInfo.size + " bytes to muxer, ts=" + mBufferInfo.presentationTimeUs);
                        mEncoder.releaseOutputBuffer(encoderStatus, false);
                    }

                    if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        outputDone = false;
                        break;
                    }
                }
            }
        }
    }

    public void release() {

        if (mEncoder != null) {
            try {
                mEncoder.stop();
                mEncoder.release();
                mEncoder = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mMuxer != null) {
            try {
                mMuxer.stop();
                mMuxer.release();
                mMuxer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        release();
        super.finalize();
    }
}
