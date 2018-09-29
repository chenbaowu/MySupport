package com.cbw.player;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;

/**
 * Created by cbw on 2018/9/10.
 */

public class SimpleExoPlayerImpl implements IPlayer {

    private Context mContext;
    private SimpleExoPlayer mSimpleExoPlayer;
    private OnPlayListener mOnPlayListener;

    /**
     * The factor by which playback will be sped up.
     */
    private float mSpeed = 1;

    /**
     * The factor by which the audio pitch will be scaled.
     */
    private float mPitch = 1;

    public SimpleExoPlayerImpl(Context context) {

        mContext = context;
        mUserAgent = Util.getUserAgent(this.mContext, this.mContext.getApplicationContext().getPackageName());
        mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());
        mSimpleExoPlayer.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if (mOnPlayListener == null) return;

                if (playbackState == Player.STATE_READY) {
                    mOnPlayListener.onPrepared(SimpleExoPlayerImpl.this);
                } else if (playWhenReady && playbackState == Player.STATE_ENDED) {
                    mOnPlayListener.onCompletion(SimpleExoPlayerImpl.this);
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                if (mOnPlayListener == null) return;
                mOnPlayListener.onError(SimpleExoPlayerImpl.this);
            }

        });
        mSimpleExoPlayer.addVideoListener(new VideoListener() {
            @Override
            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

            }

            @Override
            public void onRenderedFirstFrame() {

            }
        });
    }

    @Override
    public void setSurface(Surface surface) {
        mSimpleExoPlayer.setVideoSurface(surface);
    }

    private MediaSource mMediaSource;
    private String mUserAgent;

    @Override
    public void setDataSource(String dataSource) {
        DataSource.Factory dataFactory = new DefaultDataSourceFactory(mContext, mUserAgent);
        Uri parse = Uri.parse(dataSource);
        mMediaSource = new ExtractorMediaSource.Factory(dataFactory)
                .setExtractorsFactory(new DefaultExtractorsFactory())
                .setMinLoadableRetryCount(5)
                .setCustomCacheKey(parse.getPath())
                .createMediaSource(parse);
    }

    @Deprecated
    @Override
    public void setDataSource(Object dataSource, @DataSourceType int type){

        DataSource.Factory dataFactory = new DefaultDataSourceFactory(mContext, mUserAgent);
        Uri parse = Uri.parse((String) dataSource);
        mMediaSource = new ExtractorMediaSource.Factory(dataFactory)
                .setExtractorsFactory(new DefaultExtractorsFactory())
                .setMinLoadableRetryCount(5)
                .setCustomCacheKey(parse.getPath())
                .createMediaSource(parse);

        if (type == DataSourceType.sd) {

        } else if (type == DataSourceType.asset) {

        } else if (type == DataSourceType.raw) {

        }
    }

    @Override
    public void setVolume(float volume) {
        mSimpleExoPlayer.setVolume(volume);
    }

    @Override
    public void setLooping(boolean looping) {
        mSimpleExoPlayer.setRepeatMode(looping ? Player.REPEAT_MODE_ALL : Player.REPEAT_MODE_OFF);
    }

    @Override
    public void setPlayerSpeedAndPitch(float speed, float Pitch) {
        this.mSpeed = speed;
        this.mPitch = Pitch;
        PlaybackParameters playbackParams = new PlaybackParameters(mSpeed, mPitch);
        mSimpleExoPlayer.setPlaybackParameters(playbackParams);
    }

    @Override
    public void prepare() {
        if (mMediaSource == null) return;
        mSimpleExoPlayer.prepare(mMediaSource, true, true);
    }

    @Override
    public void start() {
        mSimpleExoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void pause() {
        mSimpleExoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void stop() {
        mSimpleExoPlayer.stop();
    }

    @Override
    public void seekTo(long millisecond) {
        mSimpleExoPlayer.seekTo(millisecond);
    }

    @Override
    public boolean isPlaying() {
        return mSimpleExoPlayer.getPlaybackState() != Player.STATE_IDLE && mSimpleExoPlayer.getPlaybackState() != Player.STATE_BUFFERING && mSimpleExoPlayer.getPlayWhenReady();
    }

    @Override
    public long getCurrentPosition() {
        return mSimpleExoPlayer.getCurrentPosition();
    }

    @Override
    public long getDurations() {
        return mSimpleExoPlayer.getDuration();
    }

    @Override
    public void reset() {
        mSimpleExoPlayer.seekTo(0, 0);
    }

    @Override
    public void release() {
        mSimpleExoPlayer.release();
        mMediaSource.releaseSource(null);
        mMediaSource = null;
    }

    @Override
    public void setOnPlayListener(OnPlayListener onPlayListener) {
        mOnPlayListener = onPlayListener;
    }
}
