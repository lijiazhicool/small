package com.av.ringtone.model;

import java.io.Serializable;

import com.av.ringtone.utils.modelcache.IBaseCacheModel;

/**
 * Created by LiJiaZhi on 16/12/19.
 *  song record saved
 */

public class VoiceModel extends BaseModel implements IBaseCacheModel, Serializable {
    public int playStatus = 0;//0 无效、1播放、2停止
    public int catorytype;//1:song    2:record    3:saved
    public int progress = 0;//播放进度
}
