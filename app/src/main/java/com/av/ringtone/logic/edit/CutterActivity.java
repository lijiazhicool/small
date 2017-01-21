
package com.av.ringtone.logic.edit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

import com.av.ringtone.Constants;
import com.av.ringtone.R;
import com.av.ringtone.UserDatas;
import com.av.ringtone.base.BaseActivity;
import com.av.ringtone.logic.SaveSuccessActivity;
import com.av.ringtone.model.BaseModel;
import com.av.ringtone.model.CutterModel;
import com.av.ringtone.soundfile.CheapSoundFile;
import com.av.ringtone.utils.FileUtils;
import com.av.ringtone.utils.SharePreferenceUtil;
import com.av.ringtone.utils.ToastUtils;
import com.av.ringtone.views.FileSaveDialog;
import com.av.ringtone.views.HintDialog;
import com.av.ringtone.views.MarkerView;
import com.av.ringtone.views.WaveformView;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.av.ringtone.Constants.FILE_KIND_ALARM;
import static com.av.ringtone.Constants.FILE_KIND_MUSIC;
import static com.av.ringtone.Constants.FILE_KIND_NOTIFICATION;
import static com.av.ringtone.Constants.FILE_KIND_RINGTONE;

public class CutterActivity extends BaseActivity implements MarkerView.MarkerListener, WaveformView.WaveformListener {

    public static final String INTENT_IN_MODEL = "intent_in_model";
    private BaseModel mFileModel;
    private ImageView mBackIv;
    private TextView mTotaltv;
    private LinearLayout mAdll;

    private AdView adView;
    private final static String EVENT_AD_TYPE = "AdView_Click";
    private final static String EVENT_AD_NAME = "AdView";
    private final static String EVENT_AD_ID = "AdView_ID";

    private long mLoadingLastUpdateTime;
    private boolean mLoadingKeepGoing;
    private ProgressDialog mProgressDialog;
    private CheapSoundFile mSoundFile;
    private File mFile;
    private String mFilename;
    private String mDstFilename;
    private String mArtist;
    private String mAlbum;
    private String mGenre;
    private String mTitle;
    private int mYear;
    private String mExtension;
    private int mNewFileKind;
    private WaveformView mWaveformView;
    private MarkerView mStartMarker;
    private MarkerView mEndMarker;
    private EditText mStartText;
    private EditText mEndText;
    private TextView mInfo;
    private ImageButton mPlayButton;
     private ImageButton mRewindButton;
     private ImageButton mFfwdButton;
     private ImageView mZoomInButton;
     private ImageView mZoomOutButton;
    private ImageButton mSaveButton;
    private boolean mKeyDown;
    private String mCaption = "";
    private int mWidth;
    private int mMaxPos;
    private int mStartPos;
    private int mEndPos;
    private int mTotalPos;
    private boolean mStartVisible;
    private boolean mEndVisible;
    private int mLastDisplayedStartPos;
    private int mLastDisplayedEndPos;
    private int mOffset;
    private int mOffsetGoal;
    private int mFlingVelocity;
    private int mPlayStartMsec;
    private int mPlayStartOffset;
    private int mPlayEndMsec;
    private Handler mHandler;
    private boolean mIsPlaying;
    private MediaPlayer mPlayer;
    private boolean mCanSeekAccurately;
    private boolean mTouchDragging;
    private float mTouchStart;
    private int mTouchInitialOffset;
    private int mTouchInitialStartPos;
    private int mTouchInitialEndPos;
    private long mWaveformTouchStartMsec;
    private float mDensity;
    private int mMarkerLeftInset;
    private int mMarkerRightInset;
    private int mMarkerTopOffset;
    private int mMarkerBottomOffset;

    // Menu commands
    private static final int CMD_SAVE = 1;
    private static final int CMD_RESET = 2;
    private static final int CMD_ABOUT = 3;

    // Result codes
    // private static final int REQUEST_CODE_RECORD = 1;
    private static final int REQUEST_CODE_CHOOSE_CONTACT = 2;

    /**
     * This is a special intent action that means "edit a sound file".
     */
    // public static final String EDIT =
    // "com.ringdroid.action.EDIT";


    SharePreferenceUtil mSharePreferenceUtil;
    private String KEY="is_check_hint";
    /**
     * Preference names
     */
    public static final String PREF_SUCCESS_COUNT = "success_count";

    private int mIndex = 0;

    @Override
    protected int getLayoutId() {
        initState();
        return R.layout.activity_cutter;
    }

    @Override
    protected void initBundleExtra() {
        mFileModel = (BaseModel) getIntent().getSerializableExtra(INTENT_IN_MODEL);
        mFilename = mFileModel.path;
    }

    @Override
    protected void findViewById() {
        mBackIv = findView(R.id.back);
        mTotaltv = findView(R.id.total_tv);
        mAdll = findView(R.id.ad_ll);
        loadGui();
    }

    @Override
    protected void initListeners() {
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mSharePreferenceUtil = new SharePreferenceUtil(this, "CutterActivity");
        mPlayer = null;
        mIsPlaying = false;

        mSoundFile = null;
        mKeyDown = false;

        mHandler = new Handler();
        mHandler.postDelayed(mTimerRunnable, 100);

        loadBanner();
        loadFromFile();

        if (!mSharePreferenceUtil.getBooleanValue(KEY, false)){
            HintDialog dialog =
                    new HintDialog(this, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean isChecked = (boolean)v.getTag();
                            if (isChecked){
                                mSharePreferenceUtil.putBoolean(KEY, true);
                            }
                        }
                    });
            dialog.setCancelable(true);
            dialog.show();
        }
    }

    protected void loadBanner() {
        // Instantiate an AdView view
        adView = new AdView(this, Constants.AD_PLACE_CUT_BANNER, AdSize.BANNER_HEIGHT_50);
        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                adView.destroy();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (null != mAdll) {
                    mAdll.addView(adView);
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, EVENT_AD_ID);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, EVENT_AD_NAME);
                mFirebaseAnalytics.logEvent(EVENT_AD_TYPE, bundle);
            }
        });

        // Request to load an ad
        adView.loadAd();
    }

    /** Called with the activity is finally destroyed. */
    @Override
    protected void onDestroy() {
        Log.i("Ringdroid", "CutterActivity OnDestroy");

        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mPlayer = null;

        if (null != adView) {
            adView.destroy();
        }

        super.onDestroy();
    }

    /**
     * Called when the orientation changes and/or the keyboard is shown or hidden. We don't need to recreate the whole
     * activity in this case, but we do need to redo our layout somewhat.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        final int saveZoomLevel = mWaveformView.getZoomLevel();
        super.onConfigurationChanged(newConfig);

        loadGui();
         enableZoomButtons();

        mHandler.postDelayed(new Runnable() {
            public void run() {
                mStartMarker.requestFocus();
                markerFocus(mStartMarker);

                mWaveformView.setZoomLevel(saveZoomLevel);
                mWaveformView.recomputeHeights(mDensity);

                updateDisplay();
            }
        }, 500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
//        MenuItem item;

        // item = menu.add(0, CMD_SAVE, 0, R.string.menu_save);
        // item.setIcon(R.drawable.menu_save);
        //
        // item = menu.add(0, CMD_RESET, 0, R.string.menu_reset);
        // item.setIcon(R.drawable.menu_reset);
        //
        // item = menu.add(0, CMD_ABOUT, 0, R.string.menu_about);
        // item.setIcon(R.drawable.menu_about);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(CMD_SAVE).setVisible(true);
        menu.findItem(CMD_RESET).setVisible(true);
        menu.findItem(CMD_ABOUT).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CMD_SAVE:
                onSave();
                return true;
            case CMD_RESET:
                resetPositions();
                mOffsetGoal = 0;
                updateDisplay();
                return true;
            case CMD_ABOUT:
                onAbout(this);
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            onPlay(mStartPos);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    //
    // WaveformListener
    //

    /**
     * Every time we get a message that our waveform drew, see if we need to animate and trigger another redraw.
     */
    public void waveformDraw() {
        mWidth = mWaveformView.getMeasuredWidth();
        if (mOffsetGoal != mOffset && !mKeyDown)
            updateDisplay();
        else if (mIsPlaying) {
            updateDisplay();
        } else if (mFlingVelocity != 0) {
            updateDisplay();
        }
    }

    public void waveformTouchStart(float x) {
        mTouchDragging = true;
        mTouchStart = x;
        mTouchInitialOffset = mOffset;
        mFlingVelocity = 0;
        mWaveformTouchStartMsec = System.currentTimeMillis();
    }

    public void waveformTouchMove(float x) {
        mOffset = trap((int) (mTouchInitialOffset + (mTouchStart - x)));
        updateDisplay();
    }

    public void waveformTouchEnd() {
        mTouchDragging = false;
        mOffsetGoal = mOffset;

        long elapsedMsec = System.currentTimeMillis() - mWaveformTouchStartMsec;
        if (elapsedMsec < 300) {
            if (mIsPlaying) {
                int seekMsec = mWaveformView.pixelsToMillisecs((int) (mTouchStart + mOffset));
                if (seekMsec >= mPlayStartMsec && seekMsec < mPlayEndMsec) {
                    mPlayer.seekTo(seekMsec - mPlayStartOffset);
                } else {
                    handlePause();
                }
            } else {
                onPlay((int) (mTouchStart + mOffset));
            }
        }
    }

    public void waveformFling(float vx) {
        mTouchDragging = false;
        mOffsetGoal = mOffset;
        mFlingVelocity = (int) (-vx);
        updateDisplay();
    }

    //
    // MarkerListener
    //

    public void markerDraw() {
    }

    public void markerTouchStart(MarkerView marker, float x) {
        mTouchDragging = true;
        mTouchStart = x;
        mTouchInitialStartPos = mStartPos;
        mTouchInitialEndPos = mEndPos;
    }

    public void markerTouchMove(MarkerView marker, float x) {
        float delta = x - mTouchStart;

        if (marker == mStartMarker) {
            mStartPos = trap((int) (mTouchInitialStartPos + delta));
            mEndPos = trap((int) (mTouchInitialEndPos + delta));
        } else {
            mEndPos = trap((int) (mTouchInitialEndPos + delta));
            if (mEndPos < mStartPos)
                mEndPos = mStartPos;
        }

        updateDisplay();
    }

    public void markerTouchEnd(MarkerView marker) {
        mTouchDragging = false;
        if (marker == mStartMarker) {
            setOffsetGoalStart();
        } else {
            setOffsetGoalEnd();
        }
    }

    public void markerLeft(MarkerView marker, int velocity) {
        mKeyDown = true;

        if (marker == mStartMarker) {
            int saveStart = mStartPos;
            mStartPos = trap(mStartPos - velocity);
            mEndPos = trap(mEndPos - (saveStart - mStartPos));
            setOffsetGoalStart();
        }

        if (marker == mEndMarker) {
            if (mEndPos == mStartPos) {
                mStartPos = trap(mStartPos - velocity);
                mEndPos = mStartPos;
            } else {
                mEndPos = trap(mEndPos - velocity);
            }

            setOffsetGoalEnd();
        }

        updateDisplay();
    }

    public void markerRight(MarkerView marker, int velocity) {
        mKeyDown = true;

        if (marker == mStartMarker) {
            int saveStart = mStartPos;
            mStartPos += velocity;
            if (mStartPos > mMaxPos)
                mStartPos = mMaxPos;
            mEndPos += (mStartPos - saveStart);
            if (mEndPos > mMaxPos)
                mEndPos = mMaxPos;

            setOffsetGoalStart();
        }

        if (marker == mEndMarker) {
            mEndPos += velocity;
            if (mEndPos > mMaxPos)
                mEndPos = mMaxPos;

            setOffsetGoalEnd();
        }

        updateDisplay();
    }

    public void markerEnter(MarkerView marker) {
    }

    public void markerKeyUp() {
        mKeyDown = false;
        updateDisplay();
    }

    public void markerFocus(MarkerView marker) {
        mKeyDown = false;
        if (marker == mStartMarker) {
            setOffsetGoalStartNoUpdate();
        } else {
            setOffsetGoalEndNoUpdate();
        }

        // Delay updaing the display because if this focus was in
        // response to a touch event, we want to receive the touch
        // event too before updating the display.
        mHandler.postDelayed(new Runnable() {
            public void run() {
                updateDisplay();
            }
        }, 100);
    }

    //
    // Static About dialog method, also called from RingdroidSelectActivity
    //

    public static void onAbout(final Activity activity) {
        // new AlertDialog.Builder(activity)
        // .setTitle(R.string.about_title)
        // .setMessage(R.string.about_text)
        // .setPositiveButton(R.string.alert_ok_button, null)
        // .setCancelable(false)
        // .show();
    }

    //
    // Internal methods
    //

    /**
     * Called from both onCreate and onConfigurationChanged (if the user switched layouts)
     */
    private void loadGui() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDensity = metrics.density;

        mMarkerLeftInset = (int) (46 * mDensity);
        mMarkerRightInset = (int) (48 * mDensity);
        mMarkerTopOffset = (int) (10 * mDensity);
        mMarkerBottomOffset = (int) (10 * mDensity);

        mStartText = (EditText) findViewById(R.id.starttext);
        mStartText.addTextChangedListener(mTextWatcher);
        mEndText = (EditText) findViewById(R.id.endtext);
        mEndText.addTextChangedListener(mTextWatcher);

        mPlayButton = (ImageButton) findViewById(R.id.play);
        mPlayButton.setOnClickListener(mPlayListener);
         mRewindButton = (ImageButton)findViewById(R.id.rew);
         mRewindButton.setOnClickListener(mRewindListener);
         mFfwdButton = (ImageButton)findViewById(R.id.ffwd);
         mFfwdButton.setOnClickListener(mFfwdListener);
         mZoomInButton = (ImageView)findViewById(R.id.zoom_in);
         mZoomInButton.setOnClickListener(mZoomInListener);
         mZoomOutButton = (ImageView)findViewById(R.id.zoom_out);
         mZoomOutButton.setOnClickListener(mZoomOutListener);
        mSaveButton = (ImageButton) findViewById(R.id.save);
        mSaveButton.setOnClickListener(mSaveListener);

        TextView markStartButton = (TextView) findViewById(R.id.mark_start);
        markStartButton.setOnClickListener(mMarkStartListener);
        TextView markEndButton = (TextView) findViewById(R.id.mark_end);
        markEndButton.setOnClickListener(mMarkStartListener);

        enableDisableButtons();

        mWaveformView = (WaveformView) findViewById(R.id.waveform);
        mWaveformView.setListener(this);

        mInfo = (TextView) findViewById(R.id.info);
        mInfo.setText(mCaption);

        mMaxPos = 0;
        mLastDisplayedStartPos = -1;
        mLastDisplayedEndPos = -1;

        if (mSoundFile != null) {
            mWaveformView.setSoundFile(mSoundFile);
            mWaveformView.recomputeHeights(mDensity);
            mMaxPos = mWaveformView.maxPos();
        }

        mStartMarker = (MarkerView) findViewById(R.id.startmarker);
        mStartMarker.setListener(this);
        mStartMarker.setAlpha(255);
        mStartMarker.setFocusable(true);
        mStartMarker.setFocusableInTouchMode(true);
        mStartVisible = true;

        mEndMarker = (MarkerView) findViewById(R.id.endmarker);
        mEndMarker.setListener(this);
        mEndMarker.setAlpha(255);
        mEndMarker.setFocusable(true);
        mEndMarker.setFocusableInTouchMode(true);
        mEndVisible = true;

        updateDisplay();
    }

    private void loadFromFile() {
        mFile = new File(mFilename);
        try {
            mExtension = getExtensionFromFilename(mFilename);
        } catch (Exception e){
            ToastUtils.makeToastAndShow(this, "File Error!");
            finish();
            return;
        }

        SongMetadataReader metadataReader = new SongMetadataReader(this, mFilename);
        mTitle = metadataReader.mTitle;
        mArtist = metadataReader.mArtist;
        mAlbum = metadataReader.mAlbum;
        mYear = metadataReader.mYear;
        mGenre = metadataReader.mGenre;

        String titleLabel = mTitle;
        if (mArtist != null && mArtist.length() > 0) {
            titleLabel += " - " + mArtist;
        }
        setTitle(titleLabel);

        mLoadingLastUpdateTime = System.currentTimeMillis();
        mLoadingKeepGoing = true;
        mProgressDialog = new ProgressDialog(CutterActivity.this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle(R.string.progress_dialog_loading);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                mLoadingKeepGoing = false;
            }
        });
        mProgressDialog.show();

        final CheapSoundFile.ProgressListener listener = new CheapSoundFile.ProgressListener() {
            public boolean reportProgress(double fractionComplete) {
                long now = System.currentTimeMillis();
                if (now - mLoadingLastUpdateTime > 100) {
                    mProgressDialog.setProgress((int) (mProgressDialog.getMax() * fractionComplete));
                    mLoadingLastUpdateTime = now;
                }
                return mLoadingKeepGoing;
            }
        };

        // Create the MediaPlayer in a background thread
        mCanSeekAccurately = false;
        new Thread() {
            public void run() {
                mCanSeekAccurately = SeekTest.CanSeekAccurately(getPreferences(Context.MODE_PRIVATE));

                System.out.println("Seek test done, creating media player.");
                try {
                    MediaPlayer player = new MediaPlayer();
                    player.setDataSource(new FileInputStream(mFile.getAbsolutePath()).getFD());
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.prepare();
                    mPlayer = player;
                } catch (final Exception e) {
                    Runnable runnable = new Runnable() {
                        public void run() {
                            handleFatalError("ReadError", getResources().getText(R.string.read_error), e);
                        }
                    };
                    mHandler.post(runnable);
                }
            }
        }.start();

        // Load the sound file in a background thread
        new Thread() {
            public void run() {
                try {
                    mSoundFile = CheapSoundFile.create(mFile.getAbsolutePath(), listener);

                    if (mSoundFile == null) {
                        mProgressDialog.dismiss();
                        String name = mFile.getName().toLowerCase();
                        String[] components = name.split("\\.");
                        String err;
                        if (components.length < 2) {
                            err = getResources().getString(R.string.no_extension_error);
                        } else {
                            err = getResources().getString(R.string.bad_extension_error) + " "
                                + components[components.length - 1];
                        }
                        final String finalErr = err;
                        Runnable runnable = new Runnable() {
                            public void run() {
                                handleFatalError("UnsupportedExtension", finalErr, new Exception());
                            }
                        };
                        mHandler.post(runnable);
                        return;
                    }
                } catch (final Exception e) {
                    mProgressDialog.dismiss();
                    e.printStackTrace();
                    mInfo.setText(e.toString());

                    Runnable runnable = new Runnable() {
                        public void run() {
                            handleFatalError("ReadError", getResources().getText(R.string.read_error), e);
                        }
                    };
                    mHandler.post(runnable);
                    return;
                }
                mProgressDialog.dismiss();
                if (mLoadingKeepGoing)

                {
                    Runnable runnable = new Runnable() {
                        public void run() {
                            finishOpeningSoundFile();
                        }
                    };
                    mHandler.post(runnable);
                } else {
                    CutterActivity.this.finish();
                }
            }
        }.start();
    }

    private void finishOpeningSoundFile() {
        mWaveformView.setSoundFile(mSoundFile);
        mWaveformView.recomputeHeights(mDensity);

        mMaxPos = mWaveformView.maxPos();
        mLastDisplayedStartPos = -1;
        mLastDisplayedEndPos = -1;

        mTouchDragging = false;

        mOffset = 0;
        mOffsetGoal = 0;
        mFlingVelocity = 0;
        resetPositions();
        if (mEndPos > mMaxPos)
            mEndPos = mMaxPos;

        mCaption =
            mSoundFile.getFiletype() + ", " + mSoundFile.getSampleRate() + " Hz, " + mSoundFile.getAvgBitrateKbps()
                + " kbps, " + formatTime(mMaxPos) + " " + getResources().getString(R.string.time_seconds);
        mInfo.setText(mCaption);

        updateDisplay();
    }

    private synchronized void updateDisplay() {
        if (mIsPlaying) {
            int now = mPlayer.getCurrentPosition() + mPlayStartOffset;
            int frames = mWaveformView.millisecsToPixels(now);
            mWaveformView.setPlayback(frames);
            setOffsetGoalNoUpdate(frames - mWidth / 2);
            if (now >= mPlayEndMsec) {
                handlePause();
            }
        }

        if (!mTouchDragging) {
            int offsetDelta;

            if (mFlingVelocity != 0) {
                float saveVel = mFlingVelocity;

                offsetDelta = mFlingVelocity / 30;
                if (mFlingVelocity > 80) {
                    mFlingVelocity -= 80;
                } else if (mFlingVelocity < -80) {
                    mFlingVelocity += 80;
                } else {
                    mFlingVelocity = 0;
                }

                mOffset += offsetDelta;

                if (mOffset + mWidth / 2 > mMaxPos) {
                    mOffset = mMaxPos - mWidth / 2;
                    mFlingVelocity = 0;
                }
                if (mOffset < 0) {
                    mOffset = 0;
                    mFlingVelocity = 0;
                }
                mOffsetGoal = mOffset;
            } else {
                offsetDelta = mOffsetGoal - mOffset;

                if (offsetDelta > 10)
                    offsetDelta = offsetDelta / 10;
                else if (offsetDelta > 0)
                    offsetDelta = 1;
                else if (offsetDelta < -10)
                    offsetDelta = offsetDelta / 10;
                else if (offsetDelta < 0)
                    offsetDelta = -1;
                else
                    offsetDelta = 0;

                mOffset += offsetDelta;
            }
        }

        mWaveformView.setParameters(mStartPos, mEndPos, mOffset);
        mWaveformView.invalidate();

        mStartMarker.setContentDescription(getResources().getText(R.string.start_marker) + " " + formatTime(mStartPos));
        mEndMarker.setContentDescription(getResources().getText(R.string.end_marker) + " " + formatTime(mEndPos));

        int startX = mStartPos - mOffset - mMarkerLeftInset;
        if (startX + mStartMarker.getWidth() >= 0) {
            if (!mStartVisible) {
                // Delay this to avoid flicker
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        mStartVisible = true;
                        mStartMarker.setAlpha(255);
                    }
                }, 0);
            }
        } else {
            if (mStartVisible) {
                mStartMarker.setAlpha(0);
                mStartVisible = false;
            }
            startX = 0;
        }

        int endX = mEndPos - mOffset - mEndMarker.getWidth() + mMarkerRightInset;
        if (endX + mEndMarker.getWidth() >= 0) {
            if (!mEndVisible) {
                // Delay this to avoid flicker
                mHandler.postDelayed(new Runnable() {

                    public void run() {
                        mEndVisible = true;
                        mEndMarker.setAlpha(255);
                    }
                }, 0);
            }
        } else {
            if (mEndVisible) {
                mEndMarker.setAlpha(0);
                mEndVisible = false;
            }
            endX = 0;
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(
                startX,
                mMarkerTopOffset,
                -mStartMarker.getWidth(),
                -mStartMarker.getHeight());
        mStartMarker.setLayoutParams(params);

        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(
                endX,
                mWaveformView.getMeasuredHeight() - mEndMarker.getHeight() - mMarkerBottomOffset,
                -mStartMarker.getWidth(),
                -mStartMarker.getHeight());
        mEndMarker.setLayoutParams(params);
    }

    private Runnable mTimerRunnable = new Runnable() {
        public void run() {
            // Updating an EditText is slow on Android. Make sure
            // we only do the update if the text has actually changed.
            if (mStartPos != mLastDisplayedStartPos && !mStartText.hasFocus()) {
                mStartText.setText(formatTime(mStartPos));
                mTotaltv.setText(formatTime(mEndPos - mStartPos));
                mTotalPos = mEndPos - mStartPos;
                mLastDisplayedStartPos = mStartPos;
            }

            if (mEndPos != mLastDisplayedEndPos && !mEndText.hasFocus()) {
                mEndText.setText(formatTime(mEndPos));
                mLastDisplayedEndPos = mEndPos;
                mTotaltv.setText(formatTime(mEndPos - mStartPos));
                mTotalPos = mEndPos - mStartPos;
            }

            mHandler.postDelayed(mTimerRunnable, 100);
        }
    };

    private void enableDisableButtons() {
        if (mIsPlaying) {
            mPlayButton.setBackgroundResource(R.drawable.ic_pause_red);
            mPlayButton.setContentDescription(getResources().getText(R.string.stop));
        } else {
            mPlayButton.setBackgroundResource(R.drawable.ic_play);
            mPlayButton.setContentDescription(getResources().getText(R.string.play));
        }
    }

    private void resetPositions() {
        mStartPos = mWaveformView.secondsToPixels(0.0);
        mEndPos = mWaveformView.secondsToPixels(15.0);
    }

    private int trap(int pos) {
        if (pos < 0)
            return 0;
        if (pos > mMaxPos)
            return mMaxPos;
        return pos;
    }

    private void setOffsetGoalStart() {
        setOffsetGoal(mStartPos - mWidth / 2);
    }

    private void setOffsetGoalStartNoUpdate() {
        setOffsetGoalNoUpdate(mStartPos - mWidth / 2);
    }

    private void setOffsetGoalEnd() {
        setOffsetGoal(mEndPos - mWidth / 2);
    }

    private void setOffsetGoalEndNoUpdate() {
        setOffsetGoalNoUpdate(mEndPos - mWidth / 2);
    }

    private void setOffsetGoal(int offset) {
        setOffsetGoalNoUpdate(offset);
        updateDisplay();
    }

    private void setOffsetGoalNoUpdate(int offset) {
        if (mTouchDragging) {
            return;
        }

        mOffsetGoal = offset;
        if (mOffsetGoal + mWidth / 2 > mMaxPos)
            mOffsetGoal = mMaxPos - mWidth / 2;
        if (mOffsetGoal < 0)
            mOffsetGoal = 0;
    }

    private String formatTime(int pixels) {
        if (mWaveformView != null && mWaveformView.isInitialized()) {
            return formatDecimal(mWaveformView.pixelsToSeconds(pixels));
        } else {
            return "";
        }
    }

    private String formatDecimal(double x) {
        int xWhole = (int) x;
        int xFrac = (int) (100 * (x - xWhole) + 0.5);

        if (xFrac >= 100) {
            xWhole++; // Round up
            xFrac -= 100; // Now we need the remainder after the round up
            if (xFrac < 10) {
                xFrac *= 10; // we need a fraction that is 2 digits long
            }
        }

        if (xFrac < 10)
            return xWhole + ".0" + xFrac;
        else
            return xWhole + "." + xFrac;
    }

    private synchronized void handlePause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        mWaveformView.setPlayback(-1);
        mIsPlaying = false;
        enableDisableButtons();
    }

    private synchronized void onPlay(int startPosition) {
        if (mIsPlaying) {
            handlePause();
            return;
        }

        if (mPlayer == null) {
            // Not initialized yet
            return;
        }

        try {
            mPlayStartMsec = mWaveformView.pixelsToMillisecs(startPosition);
            if (startPosition < mStartPos) {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mStartPos);
            } else if (startPosition > mEndPos) {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mMaxPos);
            } else {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mEndPos);
            }

            mPlayStartOffset = 0;

            int startFrame = mWaveformView.secondsToFrames(mPlayStartMsec * 0.001);
            int endFrame = mWaveformView.secondsToFrames(mPlayEndMsec * 0.001);
            int startByte = mSoundFile.getSeekableFrameOffset(startFrame);
            int endByte = mSoundFile.getSeekableFrameOffset(endFrame);
            if (mCanSeekAccurately && startByte >= 0 && endByte >= 0) {
                try {
                    mPlayer.reset();
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    FileInputStream subsetInputStream = new FileInputStream(mFile.getAbsolutePath());
                    mPlayer.setDataSource(subsetInputStream.getFD(), startByte, endByte - startByte);
                    mPlayer.prepare();
                    mPlayStartOffset = mPlayStartMsec;
                } catch (Exception e) {
                    System.out.println("Exception trying to play file subset");
                    mPlayer.reset();
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mPlayer.setDataSource(mFile.getAbsolutePath());
                    mPlayer.prepare();
                    mPlayStartOffset = 0;
                }
            }

            mPlayer.setOnCompletionListener(new OnCompletionListener() {
                public synchronized void onCompletion(MediaPlayer arg0) {
                    handlePause();
                }
            });
            mIsPlaying = true;

            if (mPlayStartOffset == 0) {
                mPlayer.seekTo(mPlayStartMsec);
            }
            mPlayer.start();
            updateDisplay();
            enableDisableButtons();
        } catch (Exception e) {
            showFinalAlert(e, R.string.play_error);
            return;
        }
    }

    /**
     * Show a "final" alert dialog that will exit the activity after the user clicks on the OK button. If an exception
     * is passed, it's assumed to be an error condition, and the dialog is presented as an error, and the stack trace is
     * logged. If there's no exception, it's a success message.
     */
    private void showFinalAlert(Exception e, CharSequence message) {
        CharSequence title;
        if (e != null) {
            Log.e("Ringdroid", "Error: " + message);
            Log.e("Ringdroid", getStackTrace(e));
            title = getResources().getText(R.string.alert_title_failure);
            setResult(RESULT_CANCELED, new Intent());
        } else {
            Log.i("Ringdroid", "Success: " + message);
            title = getResources().getText(R.string.alert_title_success);
        }

        new AlertDialog.Builder(CutterActivity.this).setTitle(title).setMessage(message)
            .setPositiveButton(R.string.alert_ok_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    finish();
                }
            }).setCancelable(false).show();
    }

    private void showFinalAlert(Exception e, int messageResourceId) {
        showFinalAlert(e, getResources().getText(messageResourceId));
    }

    private String makeRingtoneFilename(CharSequence title, String extension) {
        String parentdir;
        switch (mNewFileKind) {
            default:
            case FILE_KIND_MUSIC:
                parentdir = "/sdcard/media/audio/music";
                break;
            case FILE_KIND_ALARM:
                parentdir = "/sdcard/media/audio/alarms";
                break;
            case FILE_KIND_NOTIFICATION:
                parentdir = "/sdcard/media/audio/notifications";
                break;
            case FILE_KIND_RINGTONE:
                parentdir = "/sdcard/media/audio/ringtones";
                break;
        }

        // Create the parent directory
        File parentDirFile = new File(parentdir);
        parentDirFile.mkdirs();

        // If we can't write to that special path, try just writing
        // directly to the sdcard
        if (!parentDirFile.isDirectory()) {
            parentdir = "/sdcard";
        }

        // Turn the title into a filename
        String filename = "";
        for (int i = 0; i < title.length(); i++) {
            if (Character.isLetterOrDigit(title.charAt(i))) {
                filename += title.charAt(i);
            }
        }

        // Try to make the filename unique
        String path = null;
        for (int i = 0; i < 100; i++) {
            String testPath;
            if (i > 0)
                testPath = parentdir + "/" + filename + i + extension;
            else
                testPath = parentdir + "/" + filename + extension;

            try {
                RandomAccessFile f = new RandomAccessFile(new File(testPath), "r");
            } catch (Exception e) {
                // Good, the file didn't exist
                path = testPath;
                break;
            }
        }

        return path;
    }

    private void saveRingtone(final CharSequence title) {
        final String outPath = makeRingtoneFilename(title, mExtension);

        if (outPath == null) {
            showFinalAlert(new Exception(), R.string.no_unique_filename);
            return;
        }

        mDstFilename = outPath;

        double startTime = mWaveformView.pixelsToSeconds(mStartPos);
        double endTime = mWaveformView.pixelsToSeconds(mEndPos);
        final int startFrame = mWaveformView.secondsToFrames(startTime);
        final int endFrame = mWaveformView.secondsToFrames(endTime);
        final int duration = (int) (endTime - startTime + 0.5);

        // Create an indeterminate progress dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle(R.string.progress_dialog_saving);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        // Save the sound file in a background thread
        new Thread() {
            public void run() {
                final File outFile = new File(outPath);
                try {
                    // Write the new file
                    mSoundFile.WriteFile(outFile, startFrame, endFrame - startFrame);

                    // Try to load the new file to make sure it worked
                    final CheapSoundFile.ProgressListener listener = new CheapSoundFile.ProgressListener() {
                        public boolean reportProgress(double frac) {
                            // Do nothing - we're not going to try to
                            // estimate when reloading a saved sound
                            // since it's usually fast, but hard to
                            // estimate anyway.
                            return true; // Keep going
                        }
                    };
                    CheapSoundFile.create(outPath, listener);
                } catch (Exception e) {
                    mProgressDialog.dismiss();

                    CharSequence errorMessage;
                    if (e.getMessage().equals("No space left on device")) {
                        errorMessage = getResources().getText(R.string.no_space_error);
                        e = null;
                    } else {
                        errorMessage = getResources().getText(R.string.write_error);
                    }

                    final CharSequence finalErrorMessage = errorMessage;
                    final Exception finalException = e;
                    Runnable runnable = new Runnable() {
                        public void run() {
                            handleFatalError("WriteError", finalErrorMessage, finalException);
                        }
                    };
                    mHandler.post(runnable);
                    return;
                }

                mProgressDialog.dismiss();

                Runnable runnable = new Runnable() {
                    public void run() {
                        afterSavingRingtone(title, outPath, outFile, duration);
                    }
                };
                mHandler.post(runnable);
            }
        }.start();
    }

    private void afterSavingRingtone(CharSequence title, String outPath, File outFile, int duration) {
        long length = outFile.length();
        if (length <= 512) {
            outFile.delete();
            new AlertDialog.Builder(this).setTitle(R.string.alert_title_failure).setMessage(R.string.too_small_error)
                .setPositiveButton(R.string.alert_ok_button, null).setCancelable(false).show();
            return;
        }

        // Create the database record, pointing to the existing file path

        long fileSize = outFile.length();
        String mimeType = "audio/mpeg";

        String artist = "" + getResources().getText(R.string.artist_name);

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, outPath);
        values.put(MediaStore.MediaColumns.TITLE, title.toString());
        values.put(MediaStore.MediaColumns.SIZE, fileSize);
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);

        values.put(MediaStore.Audio.Media.ARTIST, artist);
        values.put(MediaStore.Audio.Media.DURATION, duration);

        values.put(MediaStore.Audio.Media.IS_RINGTONE, mNewFileKind == FILE_KIND_RINGTONE);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, mNewFileKind == FILE_KIND_NOTIFICATION);
        values.put(MediaStore.Audio.Media.IS_ALARM, mNewFileKind == FILE_KIND_ALARM);
        values.put(MediaStore.Audio.Media.IS_MUSIC, mNewFileKind == FILE_KIND_MUSIC);

        // 存到自己目录下一份－－不然分享不出去
//        String newPath = getExternalCacheDir().getPath() + "/" + outFile.getName();
        String newPath = "";
        if (mNewFileKind == FILE_KIND_MUSIC){
            newPath = FileUtils.getMusicPath(CutterActivity.this) + outFile.getName();
        } else if (mNewFileKind == FILE_KIND_RINGTONE){
            newPath = FileUtils.getRingtonePath(CutterActivity.this)+ outFile.getName();
        }else if (mNewFileKind == FILE_KIND_NOTIFICATION){
            newPath = FileUtils.getNotificationPath(CutterActivity.this) + outFile.getName();
        }else if (mNewFileKind == FILE_KIND_ALARM){
            newPath = FileUtils.getAlarmPath(CutterActivity.this) + outFile.getName();
        }

        FileUtils.copyFile(outPath, newPath);

        // save data
        UserDatas.getInstance().addCuttereds(
            new CutterModel(mNewFileKind, title.toString(), outPath, artist, duration, fileSize, newPath,outFile.lastModified()));

        // Insert it into the database
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(outPath);
        final Uri newUri = getContentResolver().insert(uri, values);
        setResult(RESULT_OK, new Intent().setData(newUri));

        // Update a preference that counts how many times we've
        // successfully saved a ringtone or other audio
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        int successCount = prefs.getInt(PREF_SUCCESS_COUNT, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putInt(PREF_SUCCESS_COUNT, successCount + 1);
        prefsEditor.commit();

        SaveSuccessActivity.launch(CutterActivity.this, newUri);
        finish();

//        // There's nothing more to do with music or an alarm. Show a
//        // success message and then quit.
//        if (mNewFileKind == FILE_KIND_MUSIC || mNewFileKind == FILE_KIND_ALARM) {
//            Toast.makeText(this, R.string.save_success_message, Toast.LENGTH_SHORT).show();
//            // sendStatsToServerIfAllowedAndFinish();
//            return;
//        }
//
//        // If it's a notification, give the user the option of making
//        // this their default notification. If they say no, we're finished.
//        if (mNewFileKind == FILE_KIND_NOTIFICATION) {
//            new AlertDialog.Builder(CutterActivity.this).setTitle(R.string.alert_title_success)
//                .setMessage(R.string.set_default_notification)
//                .setPositiveButton(R.string.alert_yes_button, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        RingtoneManager.setActualDefaultRingtoneUri(CutterActivity.this,
//                            RingtoneManager.TYPE_NOTIFICATION, newUri);
//                        // sendStatsToServerIfAllowedAndFinish();
//                    }
//                }).setNegativeButton(R.string.alert_no_button, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        // sendStatsToServerIfAllowedAndFinish();
//                    }
//                }).setCancelable(false).show();
//            return;
//        }
//
//        // If we get here, that means the type is a ringtone. There are
//        // three choices: make this your default ringtone, assign it to a
//        // contact, or do nothing.
//
//        final Handler handler = new Handler() {
//
//            public void handleMessage(Message response) {
//                int actionId = response.arg1;
//                switch (actionId) {
//                    case R.id.button_make_default:
//                        RingtoneManager.setActualDefaultRingtoneUri(CutterActivity.this, RingtoneManager.TYPE_RINGTONE,
//                            newUri);
//                        ToastUtils.makeToastAndShow(CutterActivity.this, "Setting Success!");
//                        break;
//                    case R.id.button_choose_contact:
//                        // chooseContactForRingtone(newUri);
//                        break;
//                    default:
//                    case R.id.button_do_nothing:
//                        break;
//                }
//            }
//        };
//
//        Message message = Message.obtain(handler);
//        AfterSaveActionDialog dlog = new AfterSaveActionDialog(this, message);
//        dlog.show();
    }

    private void chooseContactForRingtone(Uri uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_EDIT, uri);
            intent.setClassName("com.ringdroid", "com.ringdroid.ChooseContactActivity");
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_CONTACT);
        } catch (Exception e) {
            Log.e("Ringdroid", "Couldn't open Choose Contact window");
        }
    }

    private void handleFatalError(final CharSequence errorInternalName, final CharSequence errorString,
        final Exception exception) {
        ToastUtils.makeToastAndShow(CutterActivity.this, "Error: "+errorInternalName +" "+errorString);

        // SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        // int failureCount = prefs.getInt(PREF_ERROR_COUNT, 0);
        // final SharedPreferences.Editor prefsEditor = prefs.edit();
        // prefsEditor.putInt(PREF_ERROR_COUNT, failureCount + 1);
        // prefsEditor.commit();
        //
        // // Check if we already have a pref for whether or not we can
        // // contact the server.
        // int serverAllowed = prefs.getInt(PREF_ERR_SERVER_ALLOWED,
        // SERVER_ALLOWED_UNKNOWN);
        //
        // if (serverAllowed == SERVER_ALLOWED_NO) {
        // Log.i("Ringdroid", "ERR: SERVER_ALLOWED_NO");
        //
        // // Just show a simple "write error" message
        // showFinalAlert(exception, errorString);
        // return;
        // }
        //
        // if (serverAllowed == SERVER_ALLOWED_YES) {
        // Log.i("Ringdroid", "SERVER_ALLOWED_YES");
        //
        // new AlertDialog.Builder(CutterActivity.this)
        // .setTitle(R.string.alert_title_failure)
        // .setMessage(errorString)
        // .setPositiveButton(
        // R.string.alert_ok_button,
        // new DialogInterface.OnClickListener() {
        // public void onClick(DialogInterface dialog,
        // int whichButton) {
        // sendErrToServerAndFinish(errorInternalName,
        // exception);
        // return;
        // }
        // })
        // .setCancelable(false)
        // .show();
        // return;
        // }
        //
        // // The number of times the user must have had a failure before
        // // we'll ask them. Defaults to 1, and each time they click "Later"
        // // we double and add 1.
        // final int allowServerCheckIndex =
        // prefs.getInt(PREF_ERR_SERVER_CHECK, 1);
        // if (failureCount < allowServerCheckIndex) {
        // Log.i("Ringdroid", "failureCount " + failureCount +
        // " is less than " + allowServerCheckIndex);
        // // Just show a simple "write error" message
        // showFinalAlert(exception, errorString);
        // return;
        // }
        //
        // final SpannableString message = new SpannableString(
        // errorString + ". " +
        // getResources().getText(R.string.error_server_prompt));
        // Linkify.addLinks(message, Linkify.ALL);
        //
        // AlertDialog dialog = new AlertDialog.Builder(this)
        // .setTitle(R.string.alert_title_failure)
        // .setMessage(message)
        // .setPositiveButton(
        // R.string.server_yes,
        // new DialogInterface.OnClickListener() {
        // public void onClick(DialogInterface dialog,
        // int whichButton) {
        // prefsEditor.putInt(PREF_ERR_SERVER_ALLOWED,
        // SERVER_ALLOWED_YES);
        // prefsEditor.commit();
        // sendErrToServerAndFinish(errorInternalName,
        // exception);
        // }
        // })
        // .setNeutralButton(
        // R.string.server_later,
        // new DialogInterface.OnClickListener() {
        // public void onClick(DialogInterface dialog,
        // int whichButton) {
        // prefsEditor.putInt(PREF_ERR_SERVER_CHECK,
        // 1 + allowServerCheckIndex * 2);
        // Log.i("Ringdroid",
        // "Won't check again until " +
        // (1 + allowServerCheckIndex * 2) +
        // " errors.");
        // prefsEditor.commit();
        // finish();
        // }
        // })
        // .setNegativeButton(
        // R.string.server_never,
        // new DialogInterface.OnClickListener() {
        // public void onClick(DialogInterface dialog,
        // int whichButton) {
        // prefsEditor.putInt(PREF_ERR_SERVER_ALLOWED,
        // SERVER_ALLOWED_NO);
        // prefsEditor.commit();
        // finish();
        // }
        // })
        // .setCancelable(false)
        // .show();
        //
        // // Make links clicky
        // ((TextView)dialog.findViewById(android.R.id.message))
        // .setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void onSave() {
        if (mIsPlaying) {
            handlePause();
        }

        final Handler handler = new Handler() {
            public void handleMessage(Message response) {
                CharSequence newTitle = (CharSequence) response.obj;
                mNewFileKind = response.arg1;
                saveRingtone(newTitle);
            }
        };
        Message message = Message.obtain(handler);
        FileSaveDialog dlog = new FileSaveDialog(this, getResources(), mTitle, message);
        dlog.show();
    }

    private void enableZoomButtons() {
        mZoomInButton.setEnabled(mWaveformView.canZoomIn());
        mZoomOutButton.setEnabled(mWaveformView.canZoomOut());
    }

    private OnClickListener mSaveListener = new OnClickListener() {
        public void onClick(View sender) {
            if (mStartPos > mEndPos) {
                ToastUtils.makeToastAndShow(CutterActivity.this, "start must be smaller than end！");
                return;
            }
            onSave();
        }
    };

    private OnClickListener mPlayListener = new OnClickListener() {
        public void onClick(View sender) {
            onPlay(mStartPos);
        }
    };

    private OnClickListener mZoomInListener = new OnClickListener() {
        public void onClick(View sender) {
            mWaveformView.zoomIn();
            mStartPos = mWaveformView.getStart();
            mEndPos = mWaveformView.getEnd();
            mMaxPos = mWaveformView.maxPos();
            mOffset = mWaveformView.getOffset();
            mOffsetGoal = mOffset;
            enableZoomButtons();
            updateDisplay();
        }
    };

    private OnClickListener mZoomOutListener = new OnClickListener() {
        public void onClick(View sender) {
            mWaveformView.zoomOut();
            mStartPos = mWaveformView.getStart();
            mEndPos = mWaveformView.getEnd();
            mMaxPos = mWaveformView.maxPos();
            mOffset = mWaveformView.getOffset();
            mOffsetGoal = mOffset;
            enableZoomButtons();
            updateDisplay();
        }
    };

    private OnClickListener mRewindListener = new OnClickListener() {
        public void onClick(View sender) {
            if (mIsPlaying) {
                int newPos = mPlayer.getCurrentPosition() - 5000;
                if (newPos < mPlayStartMsec)
                    newPos = mPlayStartMsec;
                mPlayer.seekTo(newPos);
            } else {
                mStartMarker.requestFocus();
                markerFocus(mStartMarker);
            }
        }
    };

    private OnClickListener mFfwdListener = new OnClickListener() {
        public void onClick(View sender) {
            if (mIsPlaying) {
                int newPos = 5000 + mPlayer.getCurrentPosition();
                if (newPos > mPlayEndMsec)
                    newPos = mPlayEndMsec;
                mPlayer.seekTo(newPos);
            } else {
                mEndMarker.requestFocus();
                markerFocus(mEndMarker);
            }
        }
    };

    private OnClickListener mMarkStartListener = new OnClickListener() {
        public void onClick(View sender) {
            if (mIsPlaying) {
                mStartPos = mWaveformView.millisecsToPixels(mPlayer.getCurrentPosition() + mPlayStartOffset);
                updateDisplay();
            }
        }
    };

    private OnClickListener mMarkEndListener = new OnClickListener() {
        public void onClick(View sender) {
            if (mIsPlaying) {
                mEndPos = mWaveformView.millisecsToPixels(mPlayer.getCurrentPosition() + mPlayStartOffset);
                updateDisplay();
                handlePause();
            }
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            String temp = s.toString();
            int posDot = temp.indexOf(".");
            if (posDot > 0 && temp.length() - posDot - 1 > 2) {
                s.delete(posDot + 3, posDot + 4);
            }

            if (mStartText.hasFocus()) {
                try {
                    mStartPos = mWaveformView.secondsToPixels(Double.parseDouble(mStartText.getText().toString()));
                    if (mStartPos > mMaxPos) {
                        mStartPos = 0;
                        mStartText.setText("0.00");
                        mStartText.setSelection(mEndText.getText().toString().length());
                        ToastUtils.makeToastAndShow(CutterActivity.this,
                            "Start time must be less than the maximum time!");
                    }
                    if (mStartPos > mEndPos) {
                        mEndPos = mStartPos + mTotalPos;
                        if (mEndPos > mMaxPos) {
                            mEndPos = mMaxPos;
                            mEndText.setText(String.valueOf(mWaveformView.pixelsToSeconds(mMaxPos)));
                            mEndText.setSelection(mEndText.getText().toString().length());
                        }
                        ToastUtils.makeToastAndShow(CutterActivity.this, "Start time must be less than the end time!");

                    }
                    updateDisplay();
                } catch (NumberFormatException e) {
                }
            }
            if (mEndText.hasFocus()) {
                try {
                    mEndPos = mWaveformView.secondsToPixels(Double.parseDouble(mEndText.getText().toString()));
                    if (mEndPos > mMaxPos) {
                        mEndPos = mMaxPos;
                        mEndText.setText(String.valueOf(mWaveformView.pixelsToSeconds(mMaxPos)));
                        mEndText.setSelection(mEndText.getText().toString().length());
                        ToastUtils.makeToastAndShow(CutterActivity.this,
                            "End time must be less than the maximum time!");
                    }
                    updateDisplay();
                } catch (NumberFormatException e) {
                }
            }
        }
    };

    private String getStackTrace(Exception e) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(stream, true);
        e.printStackTrace(writer);
        return stream.toString();
    }

    /**
     * Return extension including dot, like ".mp3"
     */
    private String getExtensionFromFilename(String filename) {
        return filename.substring(filename.lastIndexOf('.'), filename.length());
    }

    private String getFilenameFromUri(Uri uri) {
        Cursor c = managedQuery(uri, null, "", null, null);
        if (c.getCount() == 0) {
            return null;
        }
        c.moveToFirst();
        int dataIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

        return c.getString(dataIndex);
    }

    // private void sendStatsToServerIfAllowedAndFinish() {
    // Log.i("Ringdroid", "sendStatsToServerIfAllowedAndFinish");
    //
    // final SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
    //
    // // Check if we already have a pref for whether or not we can
    // // contact the server.
    // int serverAllowed = prefs.getInt(PREF_STATS_SERVER_ALLOWED,
    // SERVER_ALLOWED_UNKNOWN);
    // if (serverAllowed == SERVER_ALLOWED_NO) {
    // Log.i("Ringdroid", "SERVER_ALLOWED_NO");
    // finish();
    // return;
    // }
    //
    // if (serverAllowed == SERVER_ALLOWED_YES) {
    // Log.i("Ringdroid", "SERVER_ALLOWED_YES");
    // sendStatsToServerAndFinish();
    // return;
    // }
    //
    // // Number of times the user has successfully saved a sound.
    // int successCount = prefs.getInt(PREF_SUCCESS_COUNT, 0);
    //
    // // The number of times the user must have successfully saved
    // // a sound before we'll ask them. Defaults to 2, and doubles
    // // each time they click "Later".
    // final int allowServerCheckIndex =
    // prefs.getInt(PREF_STATS_SERVER_CHECK, 2);
    // if (successCount < allowServerCheckIndex) {
    // Log.i("Ringdroid", "successCount " + successCount +
    // " is less than " + allowServerCheckIndex);
    // finish();
    // return;
    // }
    //
    // showServerPrompt(false);
    // }

    // void showServerPrompt(final boolean userInitiated) {
    // final SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
    //
    // final SpannableString message = new SpannableString(
    // getResources().getText(R.string.server_prompt));
    // Linkify.addLinks(message, Linkify.ALL);
    //
    // final AlertDialog dialog = new AlertDialog.Builder(CutterActivity.this)
    // .setTitle(R.string.server_title)
    // .setMessage(message)
    // .setPositiveButton(
    // R.string.server_yes,
    // new DialogInterface.OnClickListener() {
    // public void onClick(DialogInterface dialog,
    // int whichButton) {
    // SharedPreferences.Editor prefsEditor = prefs.edit();
    // prefsEditor.putInt(PREF_STATS_SERVER_ALLOWED,
    // SERVER_ALLOWED_YES);
    // prefsEditor.commit();
    // if (userInitiated) {
    // finish();
    // } else {
    // sendStatsToServerAndFinish();
    // }
    // }
    // })
    // .setNeutralButton(
    // R.string.server_later,
    // new DialogInterface.OnClickListener() {
    // public void onClick(DialogInterface dialog,
    // int whichButton) {
    // int allowServerCheckIndex =
    // prefs.getInt(PREF_STATS_SERVER_CHECK, 2);
    // int successCount = prefs.getInt(PREF_SUCCESS_COUNT, 0);
    // SharedPreferences.Editor prefsEditor = prefs.edit();
    // if (userInitiated) {
    // prefsEditor.putInt(PREF_STATS_SERVER_CHECK,
    // successCount + 2);
    //
    // } else {
    // prefsEditor.putInt(PREF_STATS_SERVER_CHECK,
    // allowServerCheckIndex * 2);
    // }
    // prefsEditor.commit();
    // finish();
    // }
    // })
    // .setNegativeButton(
    // R.string.server_never,
    // new DialogInterface.OnClickListener() {
    // public void onClick(DialogInterface dialog,
    // int whichButton) {
    // SharedPreferences.Editor prefsEditor = prefs.edit();
    // prefsEditor.putInt(PREF_STATS_SERVER_ALLOWED,
    // SERVER_ALLOWED_NO);
    // if (userInitiated) {
    // // If the user initiated, err on the safe side and disable
    // // sending crash reports too. There's no way to turn them
    // // back on now aside from clearing data from this app, but
    // // it doesn't matter, we don't need error reports from every
    // // user ever.
    // prefsEditor.putInt(PREF_ERR_SERVER_ALLOWED,
    // SERVER_ALLOWED_NO);
    // }
    // prefsEditor.commit();
    // finish();
    // }
    // })
    // .setCancelable(false)
    // .show();
    //
    // // Make links clicky
    // ((TextView)dialog.findViewById(android.R.id.message))
    // .setMovementMethod(LinkMovementMethod.getInstance());
    // }
    //
    // void sendStatsToServerAndFinish() {
    // Log.i("Ringdroid", "sendStatsToServerAndFinish");
    // new Thread() {
    // public void run() {
    // sendToServer(STATS_SERVER_URL, null, null);
    // }
    // }.start();
    // Log.i("Ringdroid", "sendStatsToServerAndFinish calling finish");
    // finish();
    // }
    //
    // void sendErrToServerAndFinish(final CharSequence errType,
    // final Exception exception) {
    // Log.i("Ringdroid", "sendErrToServerAndFinish");
    // new Thread() {
    // public void run() {
    // sendToServer(ERR_SERVER_URL, errType, exception);
    // }
    // }.start();
    // Log.i("Ringdroid", "sendErrToServerAndFinish calling finish");
    // finish();
    // }
    //
    // /**
    // * Nothing nefarious about this; the purpose is just to
    // * uniquely identify each user so we don't double-count the same
    // * ringtone - without actually identifying the actual user.
    // */
    // long getUniqueId() {
    // SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
    // long uniqueId = prefs.getLong(PREF_UNIQUE_ID, 0);
    // if (uniqueId == 0) {
    // uniqueId = new Random().nextLong();
    //
    // SharedPreferences.Editor prefsEditor = prefs.edit();
    // prefsEditor.putLong(PREF_UNIQUE_ID, uniqueId);
    // prefsEditor.commit();
    // }
    //
    // return uniqueId;
    // }
    //
    // /**
    // * If the exception is not null, will send the stack trace.
    // */
    // void sendToServer(String serverUrl,
    // CharSequence errType,
    // Exception exception) {
    // if (mTitle == null)
    // return;
    //
    // Log.i("Ringdroid", "sendStatsToServer");
    //
    // boolean isSuccess = (exception == null);
    //
    // StringBuilder postMessage = new StringBuilder();
    // String ringdroidVersion = "unknown";
    // try {
    // ringdroidVersion =
    // getPackageManager().getPackageInfo(getPackageName(), -1)
    // .versionName;
    // } catch (android.content.pm.PackageManager.NameNotFoundException e) {
    // }
    // postMessage.append("ringdroid_version=");
    // postMessage.append(URLEncoder.encode(ringdroidVersion));
    //
    // postMessage.append("&android_version=");
    // postMessage.append(URLEncoder.encode(Build.VERSION.RELEASE));
    //
    // postMessage.append("&unique_id=");
    // postMessage.append(getUniqueId());
    //
    // postMessage.append("&accurate_seek=");
    // postMessage.append(mCanSeekAccurately);
    //
    // if (isSuccess) {
    // postMessage.append("&title=");
    // postMessage.append(URLEncoder.encode(mTitle));
    // if (mArtist != null) {
    // postMessage.append("&artist=");
    // postMessage.append(URLEncoder.encode(mArtist));
    // }
    // if (mAlbum != null) {
    // postMessage.append("&album=");
    // postMessage.append(URLEncoder.encode(mAlbum));
    // }
    // if (mGenre != null) {
    // postMessage.append("&genre=");
    // postMessage.append(URLEncoder.encode(mGenre));
    // }
    // postMessage.append("&year=");
    // postMessage.append(mYear);
    //
    // postMessage.append("&filename=");
    // postMessage.append(URLEncoder.encode(mFilename));
    //
    // // The user's real location is not actually sent, this is just
    // // vestigial code from an old experiment.
    // double latitude = 0.0;
    // double longitude = 0.0;
    // postMessage.append("&user_lat=");
    // postMessage.append(URLEncoder.encode("" + latitude));
    // postMessage.append("&user_lon=");
    // postMessage.append(URLEncoder.encode("" + longitude));
    //
    // SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
    // int successCount = prefs.getInt(PREF_SUCCESS_COUNT, 0);
    // postMessage.append("&success_count=");
    // postMessage.append(URLEncoder.encode("" + successCount));
    //
    // postMessage.append("&bitrate=");
    // postMessage.append(URLEncoder.encode(
    // "" + mSoundFile.getAvgBitrateKbps()));
    //
    // postMessage.append("&channels=");
    // postMessage.append(URLEncoder.encode(
    // "" + mSoundFile.getChannels()));
    //
    // String md5;
    // try {
    // md5 = mSoundFile.computeMd5OfFirst10Frames();
    // } catch (Exception e) {
    // md5 = "";
    // }
    // postMessage.append("&md5=");
    // postMessage.append(URLEncoder.encode(md5));
    //
    // } else {
    // // Error case
    //
    // postMessage.append("&err_type=");
    // postMessage.append(errType);
    // postMessage.append("&err_str=");
    // postMessage.append(URLEncoder.encode(getStackTrace(exception)));
    //
    // postMessage.append("&src_filename=");
    // postMessage.append(URLEncoder.encode(mFilename));
    //
    // if (mDstFilename != null) {
    // postMessage.append("&dst_filename=");
    // postMessage.append(URLEncoder.encode(mDstFilename));
    // }
    // }
    //
    // if (mSoundFile != null) {
    // double framesToSecs = 0.0;
    // double sampleRate = mSoundFile.getSampleRate();
    // if (sampleRate > 0.0) {
    // framesToSecs = mSoundFile.getSamplesPerFrame()
    // * 1.0 / sampleRate;
    // }
    //
    // double songLen = framesToSecs * mSoundFile.getNumFrames();
    // postMessage.append("&songlen=");
    // postMessage.append(URLEncoder.encode("" + songLen));
    //
    // postMessage.append("&sound_type=");
    // postMessage.append(URLEncoder.encode(mSoundFile.getFiletype()));
    //
    // double clipStart = mStartPos * framesToSecs;
    // double clipLen = (mEndPos - mStartPos) * framesToSecs;
    // postMessage.append("&clip_start=");
    // postMessage.append(URLEncoder.encode("" + clipStart));
    // postMessage.append("&clip_len=");
    // postMessage.append(URLEncoder.encode("" + clipLen));
    // }
    //
    // String fileKindName = FileSaveDialog.KindToName(mNewFileKind);
    // postMessage.append("&clip_kind=");
    // postMessage.append(URLEncoder.encode(fileKindName));
    //
    // Log.i("Ringdroid", postMessage.toString());
    //
    // try {
    // int TIMEOUT_MILLISEC = 10000; // = 10 seconds
    // HttpParams httpParams = new BasicHttpParams();
    // HttpConnectionParams.setConnectionTimeout(httpParams,
    // TIMEOUT_MILLISEC);
    // HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
    // HttpClient client = new DefaultHttpClient(httpParams);
    //
    // HttpPost request = new HttpPost(serverUrl);
    // request.setEntity(new ByteArrayEntity(
    // postMessage.toString().getBytes("UTF8")));
    //
    // Log.i("Ringdroid", "Executing request");
    // HttpResponse response = client.execute(request);
    //
    // Log.i("Ringdroid", "Response: " + response.toString());
    //
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    /** Called with an Activity we started with an Intent returns. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        if (requestCode == REQUEST_CODE_CHOOSE_CONTACT) {
            // The user finished saving their ringtone and they're
            // just applying it to a contact. When they return here,
            // they're done.
            ToastUtils.makeToastAndShow(CutterActivity.this, "Setting Success!");
            return;
        }
    }
}
