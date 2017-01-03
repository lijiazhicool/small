package com.av.ringtone.model;

import com.facebook.ads.NativeAd;

/**
 * Created by LiJiaZhi on 16/12/19.
 */

public class HomeModel extends BaseModel {
    public int type;//1 music;   2 record;   3 cutterd;     4 ad
    public int resId;//image
    public String title;
    public String subtitle;

    public NativeAd ad = null;

    public HomeModel(int type, int resId, String title, String subtitle) {
        this.type = type;
        this.resId = resId;
        this.title = title;
        this.subtitle = subtitle;
    }

    public HomeModel(int type, NativeAd ad) {
        this.ad = ad;
    }
}
