package com.example.ad;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * @author：LiJiaZhi on 2017/11/28
 * @des：ToDo
 * @org mtime.com
 */
public class StatisticsManager {

    // 广告事件
    public static final String EVENT_AD = "ad";
    // Interstitial
    public static final String ITEM_AD_INTERSTITIAL_REQUEST = "interstitial_request_";
    public static final String ITEM_AD_INTERSTITIAL_IMPRESSION = "interstitial_impression_";
    public static final String ITEM_AD_INTERSTITIAL_FAILED = "interstitial_failed_";
    public static final String ITEM_AD_INTERSTITIAL_LOADED = "interstitial_loaded_";
    public static final String ITEM_AD_INTERSTITIAL_SHOW = "interstitial_show_";// 主动调用show函数
    // banner
    public static final String ITEM_AD_BANNER_REQUEST = "banner_request_";
    public static final String ITEM_AD_BANNER_IMPRESSION = "banner_impression_";
    public static final String ITEM_AD_BANNER_FAILED = "banner_failed_";
    public static final String ITEM_AD_BANNER_LOADED = "banner_loaded_";
    // native
    public static final String ITEM_AD_NATIVE_FAILED = "feed_failed_";
    public static final String ITEM_AD_NATIVE_IMPRESSION = "feed_impression_";
    public static final String ITEM_AD_NATIVE_LOADED = "feed_loaded_";
    public static final String ITEM_AD_NATIVE_REQUEST = "feed_request_";
    public static final String ITEM_AD_NATIVE_CLICK = "feed_click_";

    //gif
    //gif
    public static final String ITEM_AD_MAIN_GIF ="main_gif_";

    //
    public static final String  EVENT_SEARCH= "search";
    public static final String  EVENT_RATE_MENU= "ratemenu";
    public static final String  EVENT_INVITE= "invite";
    public static final String  EVENT_HOME_GRID= "home_grid";
    public static final String  EVENT_RECORD= "record";
    public static final String  EVENT_SCAN= "scan";


    public static final String  EVENT_CUT= "cut";
    public static final String  EVENT_SAVE= "save";

    public static final String EVENT_RATE= "rate";
    public static final String ITEM_RATE_DISLIKE = "dislike";
    public static final String ITEM_RATE_CANCEL = "cancel";
    public static final String ITEM_RATE_STAR = "fivestar";

    /**
     * 上报广告
     *
     * @param context
     * @param type
     */
    public static void submitAd(Context context, String type) {
        if (null == context) {
            return;
        }
        submit(context, EVENT_AD, type, null, null);
    }

    /**
     * 上报
     * 
     * @param context
     * @param event
     * @param type
     * @param itemId
     * @param itemName
     */
    public static void submit(Context context, String event, String type, String itemId, String itemName) {
        if (null == context) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, itemId);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName);
        FirebaseAnalytics.getInstance(context).logEvent(event, bundle);
    }
}
