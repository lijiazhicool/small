package com.example.ad;

import java.util.ArrayList;
import java.util.List;

import com.facebook.ads.NativeAd;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

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
        mAdIdList.clear();
        mAdIdList.add(ADConstants.facebook_feed_native1);
        mAdIdList.add(ADConstants.facebook_feed_native2);
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
    class NativeWrapper {
        public String adId;
        public NativeAd nativeAd;
        public boolean isShown = false;
        public int errorcode = 0;//0是成功

        public NativeWrapper(String adId, NativeAd nativeAd, int errorcode) {
            this.adId = adId;
            this.nativeAd = nativeAd;
            this.errorcode = errorcode;
        }
    }
    public interface ADNumListener {
        void onLoadedSuccess(List<NativeAd> list, boolean needGif);//是否需要展示小动画
    }

    //准备好的，给上层的数据
    private List<NativeWrapper> mReadyQueue = new ArrayList<>();
    private List<String> mAdIdList = new ArrayList<>();
    private int mIndex = 0;
    private int mFinished = 0;

    private Context mContext;
    private List<ADNumListener> mListeners = new ArrayList<>();
    public Interstitial mInterstitial;

    /**
     * 开始加载广告
     */
    public void startLoadAD(Context context) {
        mContext = context;
        mReadyQueue.clear();
        //初始化加载三个
        mFinished = 0;
        for (int i = 0; i < mAdIdList.size(); i++) {
            loadAD();
        }
    }

    /**
     * 获取广告
     *
     * @return
     */
    public void getNativeAdlist(ADNumListener listener) {
        if(!mListeners.contains(listener)){
            mListeners.add(listener);
        }
        callbackAD(false);
    }

    /**
     * feed流广告个数
     *
     * @return
     */
    public List<NativeAd> getFeeds() {
        List<com.facebook.ads.NativeAd> tempList = new ArrayList<>();
        for (int i = 0; i < mReadyQueue.size(); i++) {
            if (null != mReadyQueue.get(i).nativeAd/**&& !mReadyQueue.get(i).isShown*/) {
                tempList.add(mReadyQueue.get(i).nativeAd);
            }
        }
        return tempList;
    }

    int mAdIndex = 0;
    public NativeAd getNextAD(){
        NativeAd temp = getFeeds().get(mAdIndex%getFeeds().size());
        mAdIndex++;
        return temp;
    }


    //回调给上层广告数组
    protected void callbackAD(boolean needGif) {
        Log.e("ADManager", "callbackAD " + mReadyQueue.size() + " " + needGif);
            List<com.facebook.ads.NativeAd> tempList = new ArrayList<>();
            for (int i = 0; i < mReadyQueue.size(); i++) {
                if (null != mReadyQueue.get(i).nativeAd) {
                    tempList.add(mReadyQueue.get(i).nativeAd);
                }
            }
        for (int j = 0;j<mListeners.size();j++){
            mListeners.get(j).onLoadedSuccess(tempList, needGif);
        }
    }

    /**
     * 获取下一个广告位
     *
     * @return
     */
    private String getNextAdId() {
        String adID = mAdIdList.get(mIndex % mAdIdList.size());
        mIndex++;
        return adID;
    }
    private void loadAD() {
        if (sLevel == Level_None) {
            return;
        }
        String adId = getNextAdId();
        if (!TextUtils.isEmpty(adId)) {
            new NativeAD().loadAD(mContext, ADManager.AD_Facebook, adId, new NativeAD.ADListener() {
                @Override
                public void onLoadedSuccess(com.facebook.ads.NativeAd ad, String adId) {
                    mFinished++;
                    Log.e("ADManager", "onLoadedSuccess " + adId);
                    if (null != ad) {
                        mReadyQueue.add(new NativeWrapper(adId, ad, 0));
                        if (mReadyQueue.size() == 1) {//只要有了一个广告成功就通知上层展示
                            callbackAD(false);
                        }
                        if (mFinished == mAdIdList.size()) {
                            callbackAD(true);
                        }
                    }
                }

                @Override
                public void onLoadedFailed(String msg, String adId, int errorcode) {
                    Log.e("ADManager", "onLoadedFailed ");
                    mReadyQueue.add(new NativeWrapper(adId, null, errorcode));
                    mFinished++;
                    if (mFinished == mAdIdList.size()) {
                        int failedCount = 0;
                        for (int i = 0; i < mAdIdList.size(); i++) {
                            if (null == mReadyQueue.get(i).nativeAd) {
                                failedCount++;
                            }
                        }
                        if (failedCount == mAdIdList.size()) {
                            //三个都失败了
                            loadInterstitial();
                        } else {
                            callbackAD(false);
                        }
                    }
                }

                @Override
                public void onAdClick() {

                }

                @Override
                public void onAdImpression(NativeAd ad, String adId) {
                    Log.e("ADManager", "onAdImpression ");
                    for (int i = 0; i < mReadyQueue.size(); i++) {
                        if (mReadyQueue.get(i).adId.equals(adId)) {
                            mReadyQueue.get(i).isShown = true;
                            return;
                        }
                    }
                }
            });
        }
    }

    private void loadInterstitial() {
        mInterstitial = new Interstitial();
        mInterstitial.loadAD(mContext, ADManager.AD_Google, ADConstants.google_gif_interstitial, new Interstitial.ADListener() {
            @Override
            public void onLoadedSuccess() {
                Log.e("ADManager", "loadInterstitial success");
                callbackAD(true);
            }

            @Override
            public void onLoadedFailed() {
                Log.e("ADManager", "loadInterstitial failed");
            }

            @Override
            public void onAdDisplayed() {
            }

            @Override
            public void onAdClose() {

            }
        });
    }

}
