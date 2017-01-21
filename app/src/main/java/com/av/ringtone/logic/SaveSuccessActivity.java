package com.av.ringtone.logic;

import com.av.ringtone.Constants;
import com.av.ringtone.R;
import com.av.ringtone.UserDatas;
import com.av.ringtone.base.BaseActivity;
import com.av.ringtone.utils.ShareUtils;
import com.av.ringtone.utils.ToastUtils;
import com.av.ringtone.views.CommonDialog;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 保存成功
 */
public class SaveSuccessActivity extends BaseActivity {
    private static final String INTENT_IN_URI = "intent_in_uri";
    private ImageView mBackIv;
    private LinearLayout mRingtonell, mNotificationll, mAlarmll, mSharell;
    private MediaView mAdIv;

    private NativeAd mNativeAd;

    private final static String EVENT_AD_TYPE = "Save_Success_NativeAd_Click";
    private final static String EVENT_AD_NAME = "Save_Success_NativeAd";
    private final static String EVENT_AD_ID = "Save_Success_NativeAd_ID";

    private Uri mUri;

    // 启动
    public static void launch(Context context, Uri uri) {
        Intent launcher = new Intent(context, SaveSuccessActivity.class);
        launcher.putExtra(INTENT_IN_URI, uri);
        if (context instanceof Activity) {
        } else {
            launcher.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(launcher);
    }

    @Override
    protected int getLayoutId() {
        initState();
        return R.layout.activity_save_success;
    }

    @Override
    protected void initBundleExtra() {
        Intent intent = getIntent();
        mUri = intent.getParcelableExtra(INTENT_IN_URI);
    }

    @Override
    protected void findViewById() {
        mBackIv = findView(R.id.back);
        mRingtonell = findView(R.id.ringtone);
        mNotificationll = findView(R.id.notification);
        mAlarmll = findView(R.id.alarm);
        mSharell = findView(R.id.share);
        mAdIv = findView(R.id.ad_iv);
    }

    @Override
    protected void initListeners() {
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRingtonell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonDialog dialog =
                        new CommonDialog(SaveSuccessActivity.this, getString(R.string.set_ringtone_hint),"",new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RingtoneManager.setActualDefaultRingtoneUri(SaveSuccessActivity.this, RingtoneManager.TYPE_RINGTONE,
                                        mUri);
                                ToastUtils.makeToastAndShowLong(SaveSuccessActivity.this, "Set Ringtone Success!");
                                onBackPressed();
                            }
                        });
                dialog.setCancelable(true);
                dialog.show();
            }
        });
        mNotificationll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonDialog dialog =
                        new CommonDialog(SaveSuccessActivity.this, getString(R.string.set_notification_hint),"",new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RingtoneManager.setActualDefaultRingtoneUri(SaveSuccessActivity.this, RingtoneManager.TYPE_NOTIFICATION,
                                        mUri);
                                ToastUtils.makeToastAndShowLong(SaveSuccessActivity.this, "Set Notification Success!");
                                onBackPressed();
                            }
                        });
                dialog.setCancelable(true);
                dialog.show();

            }
        });
        mAlarmll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonDialog dialog =
                        new CommonDialog(SaveSuccessActivity.this, getString(R.string.set_alarm_hint),"",new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RingtoneManager.setActualDefaultRingtoneUri(SaveSuccessActivity.this, RingtoneManager.TYPE_ALARM, mUri);
                                ToastUtils.makeToastAndShowLong(SaveSuccessActivity.this, "Set Alarm Success!");
                                onBackPressed();
                            }
                        });
                dialog.setCancelable(true);
                dialog.show();
            }
        });
        mSharell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.shareFile(SaveSuccessActivity.this, mUri);
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        showBigNativeAd();
    }

    private void showBigNativeAd() {
        mNativeAd = new NativeAd(this, Constants.AD_PLACE_SAVE);
        mNativeAd.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError error) {
                // Ad error callback
                System.err.println("onError " + error.getErrorCode() + " " + error.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Download and display the cover image.
                mAdIv.setNativeAd(mNativeAd);
                // Register the Title and CTA button to listen for clicks.
                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(mAdIv);
                mNativeAd.registerViewForInteraction(mAdIv, clickableViews);
            }

            @Override
            public void onAdClicked(Ad ad) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, EVENT_AD_ID);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, EVENT_AD_NAME);
                mFirebaseAnalytics.logEvent(EVENT_AD_TYPE, bundle);
            }
        });
        // Request an ad
        mNativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);
    }

    @Override
    public void onBackPressed() {
        UserDatas.getInstance().gotoIndex(3);
        super.onBackPressed();
    }
}