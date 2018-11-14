package com.cbw.player;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.view.Surface;

import com.cbw.bean.BaseVideoInfo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cbw on 2018/3/23.
 * TODO {@link SimpleExoPlayerImpl} need test
 */

public class MediaPlayerHelper {

    private Context mContext;

    /**
     * @see MediaPlayerImpl
     * @see SimpleExoPlayerImpl
     */
    public IPlayer mMediaPlayer;

    /**
     * 地址
     *
     * @see IPlayer.DataSourceType
     */
    private String mFilePath = "";

    /**
     * 播放位置, 如果未准备好，则在播放的是时候 {@link IPlayer#seekTo(long)}
     */
    private int seeTime = -1;

    /**
     * 播放速度
     */
    private float mSpeed = 1;

    /**
     * 播放音调
     */
    private float mPitch = 1;

    /**
     * 设置速度、音调失败
     */
    private boolean mSetPlayerError = false;

    /**
     * 失败次数
     */
    private int mErrorCount = 0;

    /**
     * 是否准备中
     */
    private boolean isPrepareIng = false;

    /**
     * 是否准备好
     */
    private boolean isReady = false;

    /**
     * 是否开始播放
     */
    private boolean isReadyToPlay = false;

    public MediaPlayerHelper(Context context) {
        mContext = context;
        initMediaPlayer();
    }

    public MediaPlayerHelper(Context context, @PlayerType int type) {
        mContext = context;
        if (type == PlayerType.MediaPlayer) {
            mMediaPlayer = new MediaPlayerImpl(mContext);
        } else if (type == PlayerType.SimpleExoPlayer) {
            mMediaPlayer = new SimpleExoPlayerImpl(mContext);
        }
        mMediaPlayer.setOnPlayListener(mOnPlayListener);
    }

    public MediaPlayerHelper(Context context, PlayerFactory playerFactory) {
        mContext = context;
        mMediaPlayer = playerFactory.CreatePlyer(context);
        mMediaPlayer.setOnPlayListener(mOnPlayListener);
    }

    private void initMediaPlayer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mMediaPlayer = new MediaPlayerImpl(mContext);
        } else {
            mMediaPlayer = new SimpleExoPlayerImpl(mContext);
        }
        mMediaPlayer.setOnPlayListener(mOnPlayListener);
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void play(String path, boolean isLoop) {
        if (!mFilePath.equals(path)) {
            setDataSource(path);
            isReadyToPlay = true;
        } else {
            seeTime = -1;
            start(isLoop);
        }
    }

    public void setSurface(Surface surface) {
        mMediaPlayer.setSurface(surface);
    }

    public void setDataSource(String path) {
//        if (!FileUtil.IsFileExists(path)) {
//            Log.e("player", "setDataSource: file unFind");
//            return;
//        }
        mFilePath = path;
        resetStatus();
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setLooping(true);
                if (mFilePath.startsWith("/") || mFilePath.startsWith("http")) {
                    mMediaPlayer.setDataSource(mFilePath, IPlayer.DataSourceType.sd);
                } else if (path.startsWith("android.resource")) {
                    mMediaPlayer.setDataSource(mFilePath, IPlayer.DataSourceType.raw);
                } else {
                    mMediaPlayer.setDataSource(mFilePath, IPlayer.DataSourceType.asset);
                }
                prepare();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private void resetStatus() {
        isPrepareIng = false;
        isReady = false;
        isReadyToPlay = false;
        mSetPlayerError = false;
        seeTime = -1;
    }

    private void prepare() {
        if (mMediaPlayer != null && !isPrepareIng) {
            try {
                mMediaPlayer.prepare();
                isPrepareIng = true;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void start(boolean isLoop) {
        if (mMediaPlayer != null) {
            isReadyToPlay = true;
            mMediaPlayer.setLooping(isLoop);
            if (isReady) {
                start();
            } else {
                prepare();
            }
        }
    }

    private void start() {
        if (seeTime != -1) {
            setSeeTo(seeTime);
        }
        mMediaPlayer.setVolume(1f);
        if (mSetPlayerError) {
            setPlayerSpeedAndPitch(mSpeed, mPitch);
        }
        mMediaPlayer.start();
    }

    public void setPlayerSpeedAndPitch(float speed, float Pitch) {
        this.mSpeed = speed;
        this.mPitch = Pitch;
        try {
            mMediaPlayer.setPlayerSpeedAndPitch(speed, Pitch);
            mSetPlayerError = false;
        } catch (Throwable throwable) {
            mSetPlayerError = true;
            throwable.printStackTrace();
        }

    }

    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    public void pause() {
        isReadyToPlay = false;
        releaseTimer();
        if (isPlaying()) {
            mMediaPlayer.setVolume(0f); // 为了解决系统bug: pause之后还有声音
            mMediaPlayer.pause();
//            Log.i("bbb", "pause: " + mMediaPlayer.getCurrentPosition());
        }
    }

    public void stop() {
        isReady = false;
        isReadyToPlay = false;
        if (isPlaying()) {
            mMediaPlayer.stop();
        }
    }

    public void setSeeTo(int time) {

        if (isReady) {
            seeTime = -1;
            try {
                mMediaPlayer.seekTo(time);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } else {
            seeTime = time;
        }
    }

    public void release() {
        releaseTimer();
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void releaseTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    private Timer mTimer;
    private TimerTask mTimerTask;

    /**
     * 倒计时播放
     *
     * @param duration  播放时长
     * @param startTime 开始位置
     */
    public void executeCountDownTimerPlay(final int duration, final int startTime) {

        start(false);

        mTimer = new Timer();

        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (mMediaPlayer == null) {
                    releaseTimer();
                    return;
                }
                int time = (int) (mMediaPlayer.getCurrentPosition() - (duration + startTime));
                if (time >= -20) {
                    pause();
                }
            }
        };

        mTimer.schedule(mTimerTask, 0, 20);
    }

    private IPlayer.OnPlayListener mOnPlayListener = new IPlayer.OnPlayListener() {

        @Override
        public void onPrepared(IPlayer player) {
            isPrepareIng = false;
            isReady = true;
            if (isReadyToPlay) {
                start();
            }
        }

        @Override
        public void onCompletion(IPlayer player) {

        }

        @Override
        public void onSeekComplete(IPlayer player) {

        }

        @Override
        public void onError(IPlayer player) {

            if (mErrorCount > 10) {
                return;
            }
            mErrorCount++;
            isPrepareIng = false;
            prepare();
        }
    };

    public BaseVideoInfo getVideoInfo(String videoPath) {
        BaseVideoInfo info = null;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String width, height, duration, rotation;
        try {
            mmr.setDataSource(videoPath);
            width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            rotation = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);

            info = new BaseVideoInfo();
            info.path = videoPath;
            info.width = Integer.valueOf(width);
            info.height = Integer.valueOf(height);
            info.duration = Long.valueOf(duration);
            info.rotation = Integer.valueOf(rotation);
        } catch (Exception e) {
            e.printStackTrace();
            info = null;
        } finally {
            mmr.release();
        }

        return info;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayerType {
        /**
         * @see MediaPlayerImpl
         */
        int MediaPlayer = 0;

        /**
         * @see SimpleExoPlayerImpl
         */
        int SimpleExoPlayer = 1;
    }
}
