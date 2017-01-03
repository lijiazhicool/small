package com.av.ringtone.utils;

/**
 * Created by LiJiaZhi on 17/1/3.
 */

public class FileUtils {

    // 文件夹名
    public static String getFileDir(String path) {
        String fpath = path.trim();
        String temp[] = fpath.split("/");
        if (temp.length<2){
            return "";
        }
        String fileName = temp[temp.length - 2];
        return fileName;

    }

    // 文件名
    public static String getFileName(String path) {
        String fName = path.trim();
        int start = fName.lastIndexOf("/");
        if (start != -1) {
            return fName.substring(start + 1);
        } else {
            return null;
        }

    }
}
