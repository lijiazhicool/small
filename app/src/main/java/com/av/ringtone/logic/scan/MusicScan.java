package com.av.ringtone.logic.scan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

/**
 * Created by LiJiaZhi
 */
public class MusicScan extends Thread {
    private String[] filesStr;
    private Handler handler;
    public static final int NOW_SCAN_FOLDER = 2001;
    public static final int FIND_FILE = 2002;
    public static final int NOT_FOUNT_SDCARD = 2003;
    public static final int FIND_FINISH = 2004;

    public MusicScan(String[] name, Handler handler) {
        filesStr = name;
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();
        File sdDir;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
            searchFile(sdDir.getPath());
        } else {
            Message msg = new Message();
            msg.what = NOT_FOUNT_SDCARD;
            handler.sendMessage(msg);
            return;
        }
        Message msg = new Message();
        msg.what = FIND_FINISH;
        handler.sendMessage(msg);

    }

    private void searchFile(String filePath) {
        File file = new File(filePath);
        Message msg = new Message();
        msg.obj = file;
        msg.what = NOW_SCAN_FOLDER;
        handler.sendMessage(msg);
        List<File> folderList = new ArrayList<File>();
        if (file.isDirectory()) {
            if (file.listFiles() != null) {
                for (File childFile : file.listFiles()) {
                    if (childFile.isDirectory()) {
                        folderList.add(childFile);
                    } else {
                        checkChild(childFile);
                    }
                }
            }
        } else {
            checkChild(file);
        }
        for (File folder : folderList) {
            searchFile(folder.getPath());
        }
    }

    private void checkChild(File file) {
        if (file.isFile()) {
            int dot = file.getName().lastIndexOf(".");
            if (dot > -1 && dot < file.getName().length()) {
                String extriName = file.getName().substring(dot, file.getName().length());// 得到文件的扩展名
                if (isRight(extriName)) {
                    Message msg = new Message();
                    msg.obj = file;
                    msg.what = FIND_FILE;
                    handler.sendMessage(msg);
                }
            }
        }
    }

    public boolean isRight(String targetValue) {
        for (String s : filesStr) {
            if (s.equals(targetValue))
                return true;
        }
        return false;
    }
}
