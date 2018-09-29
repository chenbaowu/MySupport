package com.cbw.utils;

import java.io.File;

/**
 * Created by cbw on 2018/9/25.
 */

public class FileUtil {

    /**
     * 文件是否存在
     */
    public static boolean IsFileExists(String path) {
        return !(isNullOrEmpty(path) || !new File(path).exists() || !new File(path).isFile());
    }

    /**
     * 地址是否空
     */
    private static boolean isNullOrEmpty(String content) {
        return content == null || content.trim().isEmpty();
    }

}
