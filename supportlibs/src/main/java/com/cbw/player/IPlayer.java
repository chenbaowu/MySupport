package com.cbw.player;

import android.view.Surface;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by cbw on 2018/9/10.
 */

public interface IPlayer {

    void setSurface(Surface surface);

    /**
     * @param dataSource must be {@link DataSourceType}
     * @throws Exception
     */
    void setDataSource(String dataSource) throws Exception;

    void setDataSource(Object dataSource, @DataSourceType int type) throws Exception;

    void setVolume(float volume);

    void setLooping(boolean looping);

    /**
     * @param speed 速度
     * @param Pitch 音调（<1 老牛 ; >1 小孩）
     */
    void setPlayerSpeedAndPitch(float speed, float Pitch) throws Exception;

    void prepare() throws Exception;

    void start();

    void pause();

    void stop();

    void seekTo(long millisecond);

    boolean isPlaying();

    long getCurrentPosition();

    long getDurations();

    void reset();

    void release();

    void setOnPlayListener(OnPlayListener onPlayListener);

    interface OnPlayListener {

        void onPrepared(IPlayer player);

        void onCompletion(IPlayer player);

        void onSeekComplete(IPlayer player);

        void onError(IPlayer player);
    }

    @Retention(RetentionPolicy.SOURCE)
    @interface DataSourceType {
        int sd = 0;    // SD卡完整路径
        int asset = 1; // 文件名
        int raw = 2;   // uri
    }
}
