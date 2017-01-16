package com.av.ringtone.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Map;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Header;

/**
 * Created by LiJiaZhi on 17/1/3.
 */

public class FileUtils {

    public static final String app_dir="/mp3cut";

    // 文件夹名
    public static String getFileDir(String path) {
        String fpath = path.trim();
        String temp[] = fpath.split("/");
        if (temp.length < 2) {
            return "";
        }
        String fileName = temp[temp.length - 2];
        return fileName;

    }

    // 文件名
    public static String getFileName(String pathandname) {
        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return pathandname.substring(start + 1, end);
        } else {
            return null;
        }
    }

    /**
     * 得到amr的时长
     *
     * @param file
     * @return amr文件时间长度
     * @throws IOException
     */
    public static int getAmrDuration(File file) throws IOException {
        long duration = -1;
        int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0 };
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            long length = file.length();// 文件的长度
            int pos = 6;// 设置初始位置
            int frameCount = 0;// 初始帧数
            int packedPos = -1;

            byte[] datas = new byte[1];// 初始数据值
            while (pos <= length) {
                randomAccessFile.seek(pos);
                if (randomAccessFile.read(datas, 0, 1) != 1) {
                    duration = length > 0 ? ((length - 6) / 650) : 0;
                    break;
                }
                packedPos = (datas[0] >> 3) & 0x0F;
                pos += packedSize[packedPos] + 1;
                frameCount++;
            }

            duration += frameCount * 20;// 帧数*20
        } finally {
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        }
        return (int) ((duration / 1000) + 1);
    }

    /**
     * 判断是否有外部存储设备sdcard
     *
     * @return true | false
     */
    public static boolean isSdcardExit() {
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 获取sd卡路径
     * 
     * @return
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        }
        return sdDir.toString();
    }

    /**
     * 获取应用路径
     * @param activity
     * @return
     */
    public static String getAppDir(Context activity){
        String path = getSDPath();
        if (TextUtils.isEmpty(path)) {
            path = activity.getFilesDir().getPath();
        }
        return path+app_dir;
    }

    public static String getMusicPath(Context activity) {
        String musicPath = getAppDir(activity) + "/music/";
        if (!new File(musicPath).exists()) {
            new File(musicPath).mkdirs();
        }
        return musicPath;
    }

    public static String getRingtonePath(Context activity) {
        String ringtonePath = getAppDir(activity) + "/ringtone/";
        if (!new File(ringtonePath).exists()) {
            new File(ringtonePath).mkdirs();
        }
        return ringtonePath;
    }

    public static String getNotificationPath(Context activity) {
        String NotificationPath = getAppDir(activity)+ "/notification/";
        if (!new File(NotificationPath).exists()) {
            new File(NotificationPath).mkdirs();
        }
        return NotificationPath;
    }

    public static String getAlarmPath(Context activity) {
        String alarmPath = getAppDir(activity)+ "/alarm/";
        if (!new File(alarmPath).exists()) {
            new File(alarmPath).mkdirs();
        }
        return alarmPath;
    }

    public static String getRecordPath(Context activity) {
        String recordPath = getAppDir(activity) + "/record/";
        if (!new File(recordPath).exists()) {
            new File(recordPath).mkdirs();
        }
        return recordPath;
    }

    // public static int getMp3TrackLength(File mp3File) {
    //// try {
    //// MP3File f = (MP3File) AudioFileIO.read(mp3File);
    //// MP3AudioHeader audioHeader = (MP3AudioHeader)f.getAudioHeader();
    //// return audioHeader.getTrackLength();
    //// } catch(Exception e) {
    //// return -1;
    //// }
    // return 0;
    // }
    /**
     * 获取音频时长
     */
    public static int getMp3TrackLength(File file) {
        FileInputStream fis;
        int time = 0;
        try {
            fis = new FileInputStream(file);
            int b = fis.available();
            Bitstream bt = new Bitstream(fis);
            Header h = bt.readFrame();
            time = (int) h.total_ms(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BitstreamException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        int i = time / 1000; // s
        return i;
    }
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { // 文件不存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    public static synchronized String createtFileName() {
        java.util.Date dt = new java.util.Date(System.currentTimeMillis());
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fileName = fmt.format(dt);
        fileName = fileName + ".amr";
        return fileName;
    }
}
