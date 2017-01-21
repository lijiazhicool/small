package com.av.ringtone.model;

import com.av.ringtone.utils.modelcache.IBaseCacheModel;

/**
 * Created by LiJiaZhi on 16/12/19.
 *
 * 是否评分
 */

public class RateModel extends BaseModel implements IBaseCacheModel{
    public int versionCode;

    public RateModel(int versionCode) {
        this.versionCode = versionCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RateModel rateModel = (RateModel) o;

        return versionCode == rateModel.versionCode;

    }

    @Override
    public int hashCode() {
        return versionCode;
    }
}
