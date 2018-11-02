package com.cbw.mediaCodec.base;

import android.media.MediaExtractor;
import android.media.MediaFormat;

/**
 * Created by cbw on 2018/10/30.
 */
public class MediaCodeUtils {

    public static int SelectTrack(MediaExtractor extractor ,String type) {
        int numTracks = extractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith(type)) {
                return i;
            }
        }
        return -1;
    }
}
