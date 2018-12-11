package com.cbw.mysupport;

import android.media.MediaFormat;
import android.os.Bundle;

import com.cbw.mediaCodec.base.AudioCodec;
import com.cbw.utils.PathUtil;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AudioCodec audioCodec = new AudioCodec();
        audioCodec.setEncodeType(MediaFormat.MIMETYPE_AUDIO_AAC);
        String inputPath = PathUtil.GetAppPath(this) + "test.mp4";
        String outputPath = PathUtil.GetAppPath(this) + "0.mp3";
        audioCodec.setIOPath(inputPath, outputPath);
        audioCodec.prepare();
        audioCodec.startAsync();
    }
}
