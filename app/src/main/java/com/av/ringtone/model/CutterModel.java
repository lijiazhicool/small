package com.av.ringtone.model;

/**
 * Created by LiJiaZhi on 16/12/19. cutter
 */

public class CutterModel extends VoiceModel {
    public int type;
    public long fileSize;
    // public String localPath; // 本地自己存储的路径
    public boolean isNew;// 是否是新保存的

    public CutterModel(int type, String title, String path, String artisit, int duration, long fileSize, long date,
        boolean isNew) {
        this.type = type;
        this.title = title;
        this.path = path;
        this.artist = artisit;
        this.duration = duration;
        this.fileSize = fileSize;
        this.date = date;
        this.isNew = isNew;

        catorytype = 3;
    }

    public CutterModel(int type, String title, String path, String artisit, int duration, long fileSize, long date) {
        this.type = type;
        this.title = title;
        this.path = path;
        this.artist = artisit;
        this.duration = duration;
        this.fileSize = fileSize;
        this.date = date;
        this.isNew = false;

        catorytype = 3;
    }
}
