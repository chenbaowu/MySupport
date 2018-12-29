package com.cbw.mysupport;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.cbw.base.BaseActivity;
import com.cbw.mediaCodec.decode.VideoDecode;
import com.cbw.mediaCodec.encode.VideoEncode;
import com.cbw.player.MediaPlayerHelper;
import com.cbw.utils.OnAnimatorTouchListener;
import com.cbw.utils.PathUtil;

/**
 * Created by cbw on 2018/11/13.
 * <p>
 * 编解码demo，只做基本测试不考虑逻辑完整性
 */
public class MediaCodecActivity extends BaseActivity {

    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_mediacode);
        init();
    }

    private HandlerThread mHandlerThread;
    private Handler mThreadHandler;
    private Handler mMainHandler;
    private boolean isStarted;
    private String mInputPath;
    private String mOutputPath;

    private VideoDecode mVideoDecode;
    private VideoEncode mVideoEncode;
    private SurfaceView surfaceView;

    private void init() {

        mHandlerThread = new HandlerThread("VideoCodec");
        mHandlerThread.start();
        mThreadHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                int what = msg.what;
                switch (what) {
                    case 0: //  解码到 SurfaceView
                        mVideoDecode.prepare();
                        mVideoDecode.seekTo(30000, true);
                        mVideoDecode.start();
                        break;
                    case 1: // 解码到编码器重新编码
                        mVideoEncode.prepare();
                        MediaPlayerHelper mediaPlayerHelper = new MediaPlayerHelper(mContext);
                        mediaPlayerHelper.play(mInputPath,true);
                        mediaPlayerHelper.setSurface(mVideoEncode.getInputSurface());
                        mVideoEncode.start();
                        break;
                }
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        isStarted = false;
                        Log.i("bbb", "finish: ");
                    }
                });
            }
        };
        mMainHandler = new Handler(Looper.getMainLooper());

        findViewById(R.id.tv_startDecode).setOnTouchListener(animatorTouchListener);
        surfaceView = this.findViewById(R.id.surfaceView);

        mInputPath = PathUtil.GetAppPath(this) + "test.mp4";
        mVideoDecode = new VideoDecode(mInputPath);

        mOutputPath = PathUtil.GetAppPath(this) + "encode.mp4";
        mVideoEncode = new VideoEncode(mOutputPath, 540, 960);

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mVideoDecode.setSurface(holder.getSurface());

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mVideoDecode.release();
            }
        });
    }

    private OnAnimatorTouchListener animatorTouchListener = new OnAnimatorTouchListener() {
        @Override
        public void onActionClick(View v) {
            if (isStarted) {
                return;
            }
            isStarted = true;
            mThreadHandler.sendEmptyMessage(0);
        }
    };
}
