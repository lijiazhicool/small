package com.av.ringtone.ad;

import com.av.ringtone.Constants;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.NativeAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by LiJiaZhi on 17/2/6. 广告管理
 */

public class ADManager {

    //广告平台   1：google   2:facebook
    public static final long AD_Google = 1;
    public static final long AD_Facebook = 2;
    public static long sPlatForm = AD_Facebook;

    //广告级别
    public static final long Level_None = 0; //无广告
    public static final long Level_Little = 1;//只有feed流和pause的native
    public static final long Level_Normal = 2;//加上插屏
    public static final long Level_Big = 3;//加上banner
    public static long sLevel = Level_Normal;

    private static volatile ADManager instance;
    private ADManager() {

    }

    public static ADManager getInstance() {
        if (instance == null) {
            synchronized (ADManager.class) {
                if (instance == null) {
                    instance = new ADManager();
                }
            }
        }
        return instance;
    }

}
