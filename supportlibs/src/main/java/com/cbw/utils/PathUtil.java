package com.cbw.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by cbw on 2018/10/9.
 */
public class PathUtil {

    /*sdcard*/
    private static final String PATH_SD = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;

    /*APP 文件夹*/
    private static String PATH_APP;

    /*临时文件*/
    private static String PATH_TEMP;

    /**
     * 获取APP路径，带'/'
     */
    public static String GetAppPath(Context context) {

        if (PATH_APP == null) {
            String packageName = context.getPackageName();
            PATH_APP = GetSDPath() + packageName.substring(packageName.lastIndexOf(".") + 1) + File.separator;
        }

        FileUtil.MakeFolder(PATH_APP);
        return PATH_APP;
    }

    /**
     * 获取APP路径，带'/'
     */
    public static String GetTempPath() {

        if (PATH_TEMP == null) {
            PATH_TEMP = PATH_APP + "/temp/";
        }

        FileUtil.MakeFolder(PATH_TEMP);
        return PATH_TEMP;
    }

    /**
     * 获取SD卡根目录，带'/'
     */
    public static String GetSDPath() {
        return PATH_SD;
    }

}
