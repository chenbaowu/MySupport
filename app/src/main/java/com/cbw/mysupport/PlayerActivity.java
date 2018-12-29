package com.cbw.mysupport;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.FrameLayout;

import com.cbw.base.BaseActivity;
import com.cbw.bean.BaseVideoInfo;
import com.cbw.player.MediaPlayerHelper;
import com.cbw.utils.PathUtil;

/**
 * 播放器测试demo
 */
public class PlayerActivity extends BaseActivity implements SurfaceHolder.Callback, TextureView.SurfaceTextureListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);

        init();
    }

    private SurfaceView surfaceView;
    private TextureView textureView;
    private MediaPlayerHelper mediaPlayerHelper, mediaPlayerHelper2;

    private void init() {
        String mFilePath = "http://www.pearvideo.com/video_1443809";
        mFilePath = PathUtil.GetAppPath(this) + "/test.mp4";
//        mFilePath = "test.mp4";
//        mFilePath = "android.resource://" + this.getPackageName() + "/" + R.raw.test;

        surfaceView = this.findViewById(R.id.surfaceView);
        mediaPlayerHelper = new MediaPlayerHelper(this, MediaPlayerHelper.PlayerType.SimpleExoPlayer);
        mediaPlayerHelper.setDataSource(mFilePath);
        BaseVideoInfo baseVideoInfo = mediaPlayerHelper.getVideoInfo(mFilePath);
        if (baseVideoInfo != null) {
            int w, h;
            if (baseVideoInfo.rotation % 180 != 0) {
                w = baseVideoInfo.height;
                h = baseVideoInfo.width;
            } else {
                w = baseVideoInfo.width;
                h = baseVideoInfo.height;
            }

            FrameLayout.LayoutParams flp = (FrameLayout.LayoutParams) surfaceView.getLayoutParams();
            flp.width = (int) (w * 0.6f);
            flp.height = (int) (h * 0.6f);

        }
        surfaceView.getHolder().addCallback(this);

        /* 使用 textureView */
        textureView = findViewById(R.id.textureView);
        mediaPlayerHelper2 = new MediaPlayerHelper(this);
        mFilePath = "android.resource://" + this.getPackageName() + "/" + R.raw.test;
        mediaPlayerHelper2.setDataSource(mFilePath);
        baseVideoInfo = mediaPlayerHelper.getVideoInfo(mFilePath);
        if (baseVideoInfo != null) {
            int w, h;
            if (baseVideoInfo.rotation % 180 != 0) {
                w = baseVideoInfo.height;
                h = baseVideoInfo.width;
            } else {
                w = baseVideoInfo.width;
                h = baseVideoInfo.height;
            }

            FrameLayout.LayoutParams flp = (FrameLayout.LayoutParams) textureView.getLayoutParams();
            flp.width = (int) (w * 0.6f);
            flp.height = (int) (h * 0.6f);
        }
        textureView.setSurfaceTextureListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textureView.animate().rotation(180).setDuration(1000).start();
                surfaceView.animate().rotation(360).setDuration(1000).start();
            }
        }, 3000);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaPlayerHelper.setSurface(holder.getSurface());
        mediaPlayerHelper.start(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("bbb", "surfaceChanged: " + width + " , " + height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayerHelper.pause();
        mediaPlayerHelper2.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayerHelper.start(true);
        mediaPlayerHelper2.start(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayerHelper.release();
        mediaPlayerHelper2.release();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mediaPlayerHelper2.setSurface(new Surface(surface));
        mediaPlayerHelper2.start(true);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
