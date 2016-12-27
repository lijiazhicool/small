package com.example.test.model;

import java.io.Serializable;

import com.example.test.utils.modelcache.IBaseCacheModel;

/**
 * Created by LiJiaZhi on 16/12/19.
 * cutter
 */

public class CutterModel extends BaseModel implements IBaseCacheModel, Serializable {
    public  int type;
    public long fileSize;
    public  String localPath; // 本地自己存储的路径

    public CutterModel(int type, String title, String path, String artisit, int duration, long fileSize,String localPath) {
        this.type = type;
        this.title = title;
        this.path = path;
        this.artist = artisit;
        this.duration = duration;
        this.fileSize = fileSize;
        this.localPath = localPath;
    }
}
