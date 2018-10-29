package com.cbw.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;

import com.cbw.bean.AlbumBean;

import java.util.ArrayList;

/**
 * Created by cbw on 2017/12/20.
 */

public class AlbumUtils {

    public static ArrayList<AlbumBean> mAlbumBeans = new ArrayList<>();

    public static void InitAlbumData(Context context) {
        ContentResolver contentResolver = null;
        Cursor cursor = null;
        try {
            contentResolver = context.getContentResolver();
            if (contentResolver != null) {
                cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cursor != null) {

            while (cursor.moveToNext()) {
                AlbumBean albumBean = new AlbumBean();
                albumBean.mImageID = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                albumBean.mImagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                albumBean.mImageName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                albumBean.mImageDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
                albumBean.mImagedDesc = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DESCRIPTION));
                albumBean.mImagedRotation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION));
                albumBean.mImagedSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                mAlbumBeans.add(albumBean);
            }

            Log.i("bbb", "album size: " + mAlbumBeans.size());

            cursor.close();
        }
    }

    /**
     * 根据id获取缩略图
     */
    public static Bitmap GetThumb(Context context, int id) {
        return MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
    }

}
