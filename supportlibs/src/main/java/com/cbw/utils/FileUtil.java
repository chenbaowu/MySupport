package com.cbw.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by cbw on 2018/9/25.
 */

public class FileUtil {

    /**
     * 文件是否存在
     */
    public static boolean IsFileExists(String path) {
        return !(IsNullOrEmpty(path) || !new File(path).exists() || !new File(path).isFile());
    }

    /**
     * 地址是否空
     */
    public static boolean IsNullOrEmpty(String content) {
        return content == null || content.trim().isEmpty();
    }

    public static void MakeFolder(String path) {
        try {
            if (path != null) {
                File file = new File(path);
                if (!(file.exists() && file.isDirectory())) {
                    file.mkdirs();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用文件通道的方式复制文件
     *
     * @param s 源文件
     * @param t 复制到的新文件
     */
    public static void FileChannelCopy(File s, File t) {
        FileChannel in = null;
        FileChannel out = null;
        try {
            in = new FileInputStream(s).getChannel();
            out = new FileOutputStream(t).getChannel();
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除文件
     *
     * @param path       文件路径
     * @param deleteSelf 是否删除自己
     * @return 删除是否成功
     */
    public static boolean DeleteFiles(String path, boolean deleteSelf) {

        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (!file.exists()) {
                return false;
            }

            boolean result = false;
            if (file.isDirectory()) {
                String[] filePaths = file.list();
                if (filePaths != null) {
                    for (String filePath : filePaths) {
                        result = DeleteFile(path + "/" + filePath);
                    }
                }
            }

            if (deleteSelf) {
                return file.delete();
            }

            return result;
        }

        return false;
    }

    /**
     * 删除指定文件，如果指定文件时目录，需要先删除该目录下的所有文件才能删除该目录
     *
     * @param path 文件路径
     * @return 删除是否成功
     */
    public static boolean DeleteFile(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (!file.exists()) {
                return false;
            }

            if (file.isDirectory()) {
                String[] filePaths = file.list();
                if (filePaths != null) {
                    for (String filePath : filePaths) {
                        DeleteFile(path + "/" + filePath);
                    }
                }
            }

            return file.delete();
        }

        return false;
    }


}
