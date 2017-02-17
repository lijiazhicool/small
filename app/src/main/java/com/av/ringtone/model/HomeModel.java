package com.av.ringtone.model;

import com.facebook.ads.NativeAd;

/**
 * Created by LiJiaZhi on 16/12/19.
 */

public class HomeModel extends BaseModel {
    public int type;//1 music;   2 cutterd;   3 record;     4 ad_front
    public int resId;//image
    public String title;
    public String subtitle;

    public NativeAd ad_front = null;//最先加载的广告
    public NativeAd ad_back = null;//第二次加载的广告

    public HomeModel(int type, int resId, String title, String subtitle) {
        this.type = type;
        this.resId = resId;
        this.title = title;
        this.subtitle = subtitle;
    }

    public HomeModel(int type) {
        this.type = type;
    }

    public void addAd(NativeAd ad){
        if (ad_front == null) {
            this.ad_front = ad;
        } else {
            if (ad_back ==null){
                ad_back = ad;
            } else {
                ad_front = ad_back;
                ad_back = ad;
            }
        }
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
