package com.av.ringtone.model;

import static com.av.ringtone.Constants.FILE_KIND_RECORD;

import java.io.Serializable;

import com.av.ringtone.utils.modelcache.IBaseCacheModel;

/**
 * Created by LiJiaZhi on 16/12/19. record
 */

public class RecordModel extends VoiceModel {
    public int type = FILE_KIND_RECORD;

    public RecordModel(String title, String path, int duration, long date) {
        this.title = title;
        this.path = path;
        this.duration = duration;
        this.date = date;
        catorytype = 2;
    }
}
