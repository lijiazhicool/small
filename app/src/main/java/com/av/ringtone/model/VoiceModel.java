package com.av.ringtone.model;

import java.io.Serializable;
import java.lang.annotation.Native;

import com.av.ringtone.utils.modelcache.IBaseCacheModel;
import com.facebook.ads.NativeAd;

/**
 * Created by LiJiaZhi on 16/12/19.
 *  song record saved
 */

public class VoiceModel extends BaseModel implements IBaseCacheModel, Serializable {
    public static final int NORMAL = 0;
    public static final int AD = 1;



    public int playStatus = 0;//0 无效、1播放、2停止
    public int catorytype;//1:song    2:record    3:saved  4：广告
    public int progress = 0;//播放进度
}
