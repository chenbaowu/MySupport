package com.cbw.player;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;

/**
 * Created by cbw on 2018/9/10.
 */

public class MediaPlayerImpl implements IPlayer {

    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private OnPlayListener mOnPlayListener;

    public MediaPlayerImpl(Context context) {

        mContext = context;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mOnPlayListener == null) return;
                mOnPlayListener.onPrepared(MediaPlayerImpl.this);
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mOnPlayListener == null) return;
                mOnPlayListener.onCompletion(MediaPlayerImpl.this);
            }
        });
        mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                if (mOnPlayListener == null) return;
                mOnPlayListener.onSeekComplete(MediaPlayerImpl.this);
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (mOnPlayListener == null) return false;
                mOnPlayListener.onError(MediaPlayerImpl.this);
                return false;
            }
        });
        mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            }
        });
    }

    @Override
    public void setSurface(Surface surface) {
        mMediaPlayer.setSurface(surface);
    }

    @Override
    public void setDataSource(String dataSource) throws Exception {
        if (dataSource.startsWith("/")) {
            mMediaPlayer.setDataSource(dataSource);
        }
    }

    @Override
    public void setDataSource(Object dataSource, @DataSourceType int type) throws Exception {
        if (type == DataSourceType.sd) {
            mMediaPlayer.setDataSource((String) dataSource);
        } else if (type == DataSourceType.asset) {
            AssetManager assetManager = mContext.getAssets();
            AssetFileDescriptor fileDescriptor = assetManager.openFd((String) dataSource);
            mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
        } else if (type == DataSourceType.raw) {
/*
            AssetFileDescriptor fileDescriptor = mContext.getResources().openRawResourceFd((Integer) dataSource);
            mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
*/
            Uri uri = Uri.parse((String) dataSource);
            mMediaPlayer.setDataSource(mContext, uri);
        }
    }

    @Override
    public void setVolume(float volume) {

        mMediaPlayer.setVolume(volume, volume);
    }

    @Override
    public void setLooping(boolean looping) {
        mMediaPlayer.setLooping(looping);
    }

    private float mSpeed = 1;
    private float mPitch = 1;

    /**
     * @param speed
     * @param Pitch setSpeed must before setPitch
     * @throws IllegalStateException    if the internal player engine has not been
     *                                  initialized or has been released.
     * @throws IllegalArgumentException if params is not supported.
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void setPlayerSpeedAndPitch(float speed, float Pitch) {
        this.mSpeed = speed;
        this.mPitch = Pitch;
        mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setSpeed(speed));
        mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setPitch(Pitch));
    }

    @Override
    public void prepare() {
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void start() {
        mMediaPlayer.start();
    }

    @Override
    public void pause() {
        mMediaPlayer.pause();
    }

    @Override
    public void stop() {
        mMediaPlayer.stop();
    }

    @Override
    public void seekTo(long millisecond) {
        mMediaPlayer.seekTo((int) millisecond);
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public long getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getDurations() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public void reset() {
        mMediaPlayer.reset();
    }

    @Override
    public void release() {
        mMediaPlayer.release();
    }

    @Override
    public void setOnPlayListener(OnPlayListener onPlayListener) {
        mOnPlayListener = onPlayListener;
    }
}
