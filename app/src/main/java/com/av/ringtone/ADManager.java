package com.av.ringtone;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.av.ringtone.logic.SaveSuccessActivity;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiJiaZhi on 17/2/6. 广告管理
 */

public class ADManager {
    private static volatile ADManager instance;

    public NativeAd mSaveSuccessAD = null;
    private final static String EVENT_AD_TYPE = "Save_Success_NativeAd_Click";
    private final static String EVENT_AD_NAME = "Save_Success_NativeAd";
    private final static String EVENT_AD_ID = "Save_Success_NativeAd_ID";

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

    public void loadSaveSuccessAD(final Context context) {
        mSaveSuccessAD = new NativeAd(context, Constants.AD_PLACE_SAVE);
        mSaveSuccessAD.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError error) {
                // Ad error callback
                System.err.println("onError " + error.getErrorCode() + " " + error.getErrorMessage());
                mSaveSuccessAD = null;
            }

            @Override
            public void onAdLoaded(Ad ad) {
            }

            @Override
            public void onAdClicked(Ad ad) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, EVENT_AD_ID);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, EVENT_AD_NAME);
                FirebaseAnalytics.getInstance(context).logEvent(EVENT_AD_TYPE, bundle);

                //广告点击后，请求新的广告缓存
                loadSaveSuccessAD(context);
            }
        });
        // Request an ad
        mSaveSuccessAD.loadAd(NativeAd.MediaCacheFlag.ALL);
    }
}
