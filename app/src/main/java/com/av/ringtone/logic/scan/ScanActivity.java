package com.av.ringtone.logic.scan;

import android.app.Activity;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.av.ringtone.ADManager;
import com.av.ringtone.R;
import com.av.ringtone.UserDatas;
import com.av.ringtone.base.BaseActivity;
import com.av.ringtone.model.SongModel;
import com.av.ringtone.views.WaterRadarView;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScanActivity extends BaseActivity {
    private ImageView mBackIv;
    private TextView mHintTv, mFileTv;
    private WaterRadarView mWaterRadarView;
    private LinearLayout nativeAdContainer;

    private MusicScan mFileScan;
    private List<SongModel> mSongModelList;
    private List<File> list = new ArrayList<>();
    private String[] str = new String[] { ".mp3", ".m4a", ".wav", ".wma" };// aiff,flac,dsf

    private boolean isFinished = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MusicScan.FIND_FILE:
                    File file = (File) msg.obj;
                    list.add(file);
                    if (list.size() > 0) {
                        mHintTv.setText(String.valueOf(list.size()) + " musics found");
                    }
                    if (!mSongModelList.contains(new SongModel(file.getAbsolutePath()))) {
                        MediaScannerConnection.scanFile(ScanActivity.this, new String[] { file.getAbsolutePath() },
                            null, null);
                    }
                    break;
                case MusicScan.NOT_FOUNT_SDCARD:
                    mHintTv.setText("Error, Not found SDCard");
                    break;
                case MusicScan.NOW_SCAN_FOLDER:
                    mFileTv.setText(""+msg.obj);
                    break;
                case MusicScan.FIND_FINISH:
                    mFileTv.setText("");
                    mHintTv.setText(String.valueOf(list.size()) + " musics found");
                    mWaterRadarView.updateBitmap(R.drawable.scanned);
                    mWaterRadarView.stop();
                    isFinished = true;
                    break;
            }
        }
    };

    private void sendBroadcast() {
        // if (file1.getName().equals("a.mp3") || file1.getName().equals("b.mp3")) {
        // MediaScannerConnection.scanFile(ScanActivity.this, new String[] { file1.getAbsolutePath() },
        // null, null);
        // }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan;
    }

    @Override
    protected void initBundleExtra() {

    }

    @Override
    protected void findViewById() {
        mBackIv = findView(R.id.back);
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mWaterRadarView = (WaterRadarView) findViewById(R.id.wrv);
        mHintTv = (TextView) findViewById(R.id.hint_tv);
        mFileTv = (TextView) findViewById(R.id.file_tv);
        nativeAdContainer = (LinearLayout) findViewById(R.id.home_ad_ll);
    }

    @Override
    protected void initListeners() {
        // mWaterRadarView.setOnClickListener(new View.OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // if (mWaterRadarView.isPlaying()){
        // mWaterRadarView.stop();
        // } else {
        // mWaterRadarView.start();
        // if (mFileScan.getState() != Thread.State.NEW) {
        // mFileScan = null;
        // mFileScan = new MusicScan(str, handler);
        // }
        // mFileScan.start();
        // }
        // }
        // });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mSongModelList = UserDatas.getInstance().getSongs();
        mFileScan = new MusicScan(str, handler);
        mFileScan.start();
        // mWaterRadarView.performClick();
        mWaterRadarView.start();
        showNativeAd();
    }

    @Override
    public void onBackPressed() {
        if (isFinished) {
            setResult(Activity.RESULT_OK);
        }
        super.onBackPressed();
    }

    private void showNativeAd() {
        NativeAd nativeAd = ADManager.getInstance().getScanAd();
        if (null == nativeAd) {
            nativeAdContainer.setVisibility(View.GONE);
            return;
        }

        nativeAdContainer.setVisibility(View.VISIBLE);

        LayoutInflater inflater = LayoutInflater.from(ScanActivity.this);
        RelativeLayout adView = (RelativeLayout) inflater.inflate(R.layout.layout_big_ad, nativeAdContainer, false);
        nativeAdContainer.removeAllViews();
        nativeAdContainer.addView(adView);

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
        AdChoicesView adChoicesView = new AdChoicesView(ScanActivity.this, nativeAd, true);
        adChoicesContainer.addView(adChoicesView);

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeAd.registerViewForInteraction(nativeAdContainer, clickableViews);
    }
}
