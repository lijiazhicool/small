package com.example.test.model;

import com.example.test.utils.modelcache.IBaseCacheModel;

import java.io.Serializable;

/**
 * Created by LiJiaZhi on 16/12/19.
 * record
 */

public class RecordModel extends BaseModel implements Serializable, IBaseCacheModel {
    public  String title;//文件名字
    public  String path; // 音乐文件的路径
    public int duration;//时间长度

    public RecordModel(String title, String path, int duration) {
        this.title = title;
        this.path = path;
        this.duration = duration;
    }
}
