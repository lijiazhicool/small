package com.av.ringtone.logic.scan;

import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
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
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScanActivity extends BaseActivity {
    private ImageView mBackIv;
    private TextView mHintTv;
    private TextView mFileTv;
    private TextView mScanTv, mDoneTv;
    private LinearLayout nativeAdContainer;

    private List<SongModel> mSongModelList;
    private List<String> mFilePathList = new ArrayList<>();
    private String[] filesStr = new String[] { ".mp3", ".m4a", ".wav", ".wma" };// aiff,flac,dsf

    public static final int NOW_SCAN_FOLDER = 2001;
    public static final int FIND_FILE = 2002;
    public static final int NOT_FOUNT_SDCARD = 2003;
    public static final int FIND_FINISH = 2004;

    private boolean isFinished = false;
    private boolean isNew = false;

    private HandlerThread mCheckMsgThread;
    private Handler mHandler;

    private boolean iStopRunning = false;

    private int mStart = 0;// 是否开始扫描 0:未开始 1：正在 2：完成

    private Runnable MusicScanRunnable = new Runnable() {
        @Override
        public void run() {
            File sdDir;
            boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
            if (sdCardExist) {
                sdDir = Environment.getExternalStorageDirectory();
                searchFile(sdDir.getPath());
            } else {
                Message msg = new Message();
                msg.what = NOT_FOUNT_SDCARD;
                mHandler.sendMessage(msg);
                return;
            }
            Message msg = new Message();
            msg.what = FIND_FINISH;
            mHandler.sendMessage(msg);
        }
    };

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
        mHintTv = (TextView) findViewById(R.id.hint_tv);
        mFileTv = (TextView) findViewById(R.id.file_tv);
        mScanTv = (TextView) findViewById(R.id.scan_btn);
        mDoneTv = (TextView) findViewById(R.id.done_tv);

        nativeAdContainer = (LinearLayout) findViewById(R.id.home_ad_ll);
    }

    @Override
    protected void initListeners() {
        mScanTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStart == 0) {
                    // 开始
                    mHandler.post(MusicScanRunnable);
                    mScanTv.setText("Cancel");
                    mHintTv.setText("0 musics found");
                    mStart = 1;
                } else if (mStart == 1) {
                    // 取消
                    iStopRunning = true;
                    mHandler.removeCallbacks(MusicScanRunnable);
                    mCheckMsgThread.quit();
                    mScanTv.setText("Done");
                    mDoneTv.setVisibility(View.VISIBLE);
                    mFileTv.setVisibility(View.INVISIBLE);
                    mStart = 2;
                } else {
                    // 完成
                    onBackPressed();
                }
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mSongModelList = UserDatas.getInstance().getSongs();
        mCheckMsgThread = new HandlerThread("handler_thread");
        mCheckMsgThread.start();
        mHandler = new Handler(mCheckMsgThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.e("Tag", "Message " + msg.obj + "thread " + Thread.currentThread().getId() + " "
                    + Thread.currentThread().getName());
                switch (msg.what) {
                    case FIND_FILE:
                        // final String filePath = (String) msg.obj;
                        // runOnUiThread(new Runnable() {
                        // @Override
                        // public void run() {
                        // mFilePathList.add(filePath);
                        // if (mFilePathList.size() > 0) {
                        // mHintTv.setText(String.valueOf(mFilePathList.size()) + " musics found");
                        // }
                        // if (!mSongModelList.contains(new SongModel(filePath))) {
                        // MediaScannerConnection.scanFile(ScanActivity.this, new String[] { filePath }, null,
                        // null);
                        // isNew = true;
                        // }
                        // }
                        // });
                        break;
                    case NOT_FOUNT_SDCARD:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mHintTv.setText("Error, Not found SDCard");
                            }
                        });
                        break;
                    // case NOW_SCAN_FOLDER:
                    // mFileTv.setText("" + msg.obj);
                    // break;
                    case FIND_FINISH:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mFileTv.setText("");
                                mHintTv.setText(String.valueOf(mFilePathList.size()) + " musics found");
                                iStopRunning = true;
                                mHandler.removeCallbacks(MusicScanRunnable);
                                mCheckMsgThread.quit();
                                mScanTv.setText("Done");
                                mDoneTv.setVisibility(View.VISIBLE);
                                mFileTv.setVisibility(View.INVISIBLE);
                                mStart = 2;
                                isFinished = true;
                            }
                        });
                        break;
                }
            }
        };
        showNativeAd();
    }

    private void searchFile(final String filePath) {
        File file = new File(filePath);
        if (iStopRunning) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("Tag", "searchFile " + "thread " + Thread.currentThread().getId() + " "
                    + Thread.currentThread().getName());
                mFileTv.setText("" + filePath);
            }
        });
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<File> folderList = new ArrayList<File>();
        if (file.isDirectory()) {
            if (file.listFiles() != null) {
                for (File childFile : file.listFiles()) {
                    if (childFile.isDirectory()) {
                        folderList.add(childFile);
                    } else {
                        checkChild(childFile);
                    }
                }
            }
        } else {
            checkChild(file);
        }
        for (File folder : folderList) {
            // 不扫描隐藏文件
            if (!folder.getName().contains(".")) {
                searchFile(folder.getPath());
            }
        }
    }

    private void checkChild(File file) {
        if (file.isFile()) {
            final String filePath = file.getAbsolutePath();
            int dot = file.getName().lastIndexOf(".");
            if (dot > -1 && dot < file.getName().length()) {
                String extriName = file.getName().substring(dot, file.getName().length());// 得到文件的扩展名
                if (isRight(extriName)) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mFilePathList.add(filePath);
                            if (mFilePathList.size() > 0) {
                                mHintTv.setText(String.valueOf(mFilePathList.size()) + " musics found");
                            }
                            if (!mSongModelList.contains(new SongModel(filePath))) {
                                MediaScannerConnection.scanFile(ScanActivity.this, new String[] { filePath }, null,
                                    null);
                                isNew = true;
                            }
                        }
                    });
                    // Message msg = new Message();
//                    Message msg = mHandler.obtainMessage();
//                    msg.obj = file.getAbsolutePath();
//                    msg.what = FIND_FILE;
//                    mHandler.sendMessage(msg);
                }
            }
        }
    }

    public boolean isRight(String targetValue) {
        for (String s : filesStr) {
            if (s.equals(targetValue))
                return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iStopRunning = true;
        mHandler.removeCallbacks(MusicScanRunnable);
        mCheckMsgThread.quit();
    }

    @Override
    public void onBackPressed() {
        if (isFinished && isNew) {
            UserDatas.getInstance().loadMusics();
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
