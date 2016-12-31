package com.av.ringtone.model;

import java.io.Serializable;

/**
 * Created by LiJiaZhi on 16/12/19.
 */

public class BaseModel implements Serializable {
    public  String title;
    public  String path; // 音乐文件的路径
    public  String artist;
    public int duration;
}
