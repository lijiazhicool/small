package com.av.ringtone;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.example.ad.ADConstants;
import com.google.android.gms.ads.MobileAds;

/**
 * @author：LiJiaZhi on 2017/12/4
 * @des：ToDo
 * @org mtime.com
 */
public class App extends Application{
    private static App instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //初始化google广告
        MobileAds.initialize(this, ADConstants.GOOGLE_APP_ID);
    }

    public static App getAppContext() {
        return instance;
    }

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public  int getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
