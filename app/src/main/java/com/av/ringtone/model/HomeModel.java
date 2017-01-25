package com.av.ringtone.model;

import com.facebook.ads.NativeAd;

/**
 * Created by LiJiaZhi on 16/12/19.
 */

public class HomeModel extends BaseModel {
    public int type;//1 music;   2 cutterd;   3 record;     4 ad
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
        this.type = type;
        this.ad = ad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HomeModel homeModel = (HomeModel) o;

        return type == homeModel.type;

    }

    @Override
    public int hashCode() {
        return type;
    }
}
