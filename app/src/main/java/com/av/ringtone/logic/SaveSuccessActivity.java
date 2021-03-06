package com.av.ringtone.logic;

import java.util.ArrayList;
import java.util.List;

import com.av.ringtone.UserDatas;
import com.av.ringtone.base.BaseActivity;
import com.av.ringtone.utils.ShareUtils;
import com.av.ringtone.utils.ToastUtils;
import com.av.ringtone.views.CommonDialog;
import com.example.ad.ADConstants;
import com.example.ad.ADManager;
import com.example.ad.NativeAD;
import com.example.ad.StatisticsManager;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.music.ringtonemaker.ringtone.cutter.maker.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 保存成功
 */
public class SaveSuccessActivity extends BaseActivity {
    private static final String INTENT_IN_URI = "intent_in_uri";
    private ImageView mBackIv;
    private LinearLayout mRingtonell, mNotificationll, mAlarmll, mShareButton;
    private LinearLayout mNativeAdContainer;
    private TextView mAdHintTv;
    private Uri mUri;

    private LinearLayout mSharell;
    private TextView mSharetv;
    private Animation mTranstionAnim;

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
        mShareButton = findView(R.id.share);
        mNativeAdContainer = findView(R.id.ad_ll);
        mAdHintTv = findView(R.id.ad_hint_tv);

        mSharell = (LinearLayout) findViewById(R.id.share_ll);
        mSharetv = (TextView) findViewById(R.id.cutcount_tv);

    }

    @Override
    protected void initListeners() {
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mRingtonell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatisticsManager.submit(SaveSuccessActivity.this,StatisticsManager.EVENT_SAVE, "ringtone",null,null);
                CommonDialog dialog = new CommonDialog(SaveSuccessActivity.this, getString(R.string.set_ringtone_title),
                    getString(R.string.set_ringtone_content), "", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RingtoneManager.setActualDefaultRingtoneUri(SaveSuccessActivity.this,
                                RingtoneManager.TYPE_RINGTONE, mUri);
                            ToastUtils.makeToastAndShowLong(SaveSuccessActivity.this,
                                getString(R.string.set_ringtone_success));
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
                StatisticsManager.submit(SaveSuccessActivity.this,StatisticsManager.EVENT_SAVE, "notification",null,null);
                CommonDialog dialog =
                    new CommonDialog(SaveSuccessActivity.this, getString(R.string.set_notification_title),
                        getString(R.string.set_notification_content), "", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RingtoneManager.setActualDefaultRingtoneUri(SaveSuccessActivity.this,
                                    RingtoneManager.TYPE_NOTIFICATION, mUri);
                                ToastUtils.makeToastAndShowLong(SaveSuccessActivity.this,
                                    getString(R.string.set_notification_success));
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
                StatisticsManager.submit(SaveSuccessActivity.this,StatisticsManager.EVENT_SAVE, "alarm",null,null);
                CommonDialog dialog = new CommonDialog(SaveSuccessActivity.this, getString(R.string.set_alarm_title),
                    getString(R.string.set_alarm_content), "", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RingtoneManager.setActualDefaultRingtoneUri(SaveSuccessActivity.this,
                                RingtoneManager.TYPE_ALARM, mUri);
                            ToastUtils.makeToastAndShowLong(SaveSuccessActivity.this,
                                getString(R.string.set_alarm_success));
                            onBackPressed();
                        }
                    });
                dialog.setCancelable(true);
                dialog.show();
            }
        });
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatisticsManager.submit(SaveSuccessActivity.this,StatisticsManager.EVENT_SAVE, "share",null,null);
                ShareUtils.shareFile(SaveSuccessActivity.this, mUri);
            }
        });
        mSharell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.shareHomeSavedText(SaveSuccessActivity.this);
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mTranstionAnim = AnimationUtils.loadAnimation(this, R.anim.anim_bottom_in);
        randomShow();
    }

    private void randomShow() {
        mSharell.setVisibility(View.GONE);
        showFacebookAd();
//        // 1-2
//        int index = new Random().nextInt(2) + 1;
//        if (index % 2 == 0) {
//            mSharell.setVisibility(View.VISIBLE);
//            mSharetv.setText(String.format(getString(R.string.home_cut_count), UserDatas.getInstance().getCutCount()));
//            mAdHintTv.setVisibility(View.GONE);
//            mNativeAdContainer.setVisibility(View.GONE);
//            mSharell.startAnimation(mTranstionAnim);
//        } else {
//            mSharell.setVisibility(View.GONE);
//            showFacebookAd();
//        }
    }

    private void showFacebookAd() {
        new NativeAD().loadAD(this, ADManager.AD_Facebook, ADConstants.facebook_savesuccess_native, new NativeAD.ADListener() {
            @Override
            public void onLoadedSuccess(NativeAd nativeAd, String adId) {
                mNativeAdContainer.setVisibility(View.VISIBLE);
                mAdHintTv.setVisibility(View.VISIBLE);
                LayoutInflater inflater = LayoutInflater.from(SaveSuccessActivity.this);
                RelativeLayout adView = (RelativeLayout) inflater.inflate(R.layout.layout_big_ad, mNativeAdContainer, false);
                mNativeAdContainer.removeAllViews();
                mNativeAdContainer.addView(adView);

                // Create native UI using the ad_front metadata.
                ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.native_ad_icon);
                TextView nativeAdTitle = (TextView) adView.findViewById(R.id.native_ad_title);
                MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.native_ad_media);
                // TextView nativeAdSocialContext = (TextView) adView.findViewById(R.id.native_ad_social_context);
                TextView nativeAdBody = (TextView) adView.findViewById(R.id.native_ad_body);
                Button nativeAdCallToAction = (Button) adView.findViewById(R.id.native_ad_call_to_action);

                // Set the Text.
                nativeAdTitle.setText(nativeAd.getAdTitle());
                // nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                nativeAdBody.setText(nativeAd.getAdBody());
                nativeAdCallToAction.setText(nativeAd.getAdCallToAction());

                // Download and display the ad_front icon.
                NativeAd.Image adIcon = nativeAd.getAdIcon();
                NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

                // Download and display the cover image.
                nativeAdMedia.setNativeAd(nativeAd);

                // Add the AdChoices icon
                LinearLayout adChoicesContainer = (LinearLayout) findViewById(R.id.ad_choices_container);
                AdChoicesView adChoicesView = new AdChoicesView(SaveSuccessActivity.this, nativeAd, true);
                adChoicesContainer.addView(adChoicesView);

                // Register the Title and CTA button to listen for clicks.
                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(nativeAdTitle);
                clickableViews.add(nativeAdCallToAction);
                nativeAd.registerViewForInteraction(mNativeAdContainer, clickableViews);

                mNativeAdContainer.startAnimation(mTranstionAnim);
            }

            @Override
            public void onLoadedFailed(String msg, String adId, int errorcode) {

            }

            @Override
            public void onAdClick() {

            }

            @Override
            public void onAdImpression(NativeAd ad, String adId) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        UserDatas.getInstance().gotoIndex(2);
        super.onBackPressed();
    }
}
