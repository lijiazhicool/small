package com.av.ringtone.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.av.ringtone.ADManager;
import com.av.ringtone.Constants;
import com.av.ringtone.R;
import com.av.ringtone.UserDatas;
import com.av.ringtone.base.BaseActivity;
import com.av.ringtone.logic.home.HomeFragment;
import com.av.ringtone.logic.record.RecordFragment;
import com.av.ringtone.logic.record.RecordsAdapter;
import com.av.ringtone.logic.ringtone.CutteredFragment;
import com.av.ringtone.logic.ringtone.CuttersAdapter;
import com.av.ringtone.logic.scan.ScanActivity;
import com.av.ringtone.logic.song.SongFragment;
import com.av.ringtone.logic.song.SongsAdapter;
import com.av.ringtone.model.BaseModel;
import com.av.ringtone.model.CutterModel;
import com.av.ringtone.model.RecordModel;
import com.av.ringtone.model.SongModel;
import com.av.ringtone.model.VoiceModel;
import com.av.ringtone.utils.NavigationUtils;
import com.av.ringtone.utils.SharePreferenceUtil;
import com.av.ringtone.utils.ShareUtils;
import com.av.ringtone.utils.ToastUtils;
import com.av.ringtone.views.RateDialog;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements MediaListener, UserDatas.GotoFragmentListener {
    private List<Fragment> mFragments = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>();
    private FragmentPagerAdapter mAdpter;
    private ViewPager mViewPager;
    private SmartTabLayout mTabLayout;
    private ImageView mSearchIv, mrRfreshIv, mMoreMenu;

    // 全屏幕搜索
    private LinearLayout mSearchll;
    private ImageView mSearchBackiv;
    private EditText mSearchev;
    private RecyclerView mSearchRecyclerView;

    private int mCurrentPage = 0;

    private boolean mIsPlaying;
    private MediaPlayer mPlayer;

    protected Timer UPDATE_PROGRESS_TIMER;// 更新进度条的定时器
    protected ProgressTimerTask mProgressTimerTask;

    private VoiceModel currentModel = null;

    SharePreferenceUtil mSharePreferenceUtil;
    private String KEY = "is_check_hint";

    public static void launch(Context context) {
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected int getLayoutId() {
        initState();
        return R.layout.activity_main;
    }

    @Override
    protected void initBundleExtra() {
        if (getIntent() != null) {
            String action = getIntent().getAction();
            if (Intent.ACTION_VIEW.equals(action)) {
                BaseModel model = new BaseModel();
                model.path = getIntent().getData().getPath();
                NavigationUtils.goToCutter(MainActivity.this, model);
            }
        }
    }

    @Override
    protected void findViewById() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(4);
        mTabLayout = (SmartTabLayout) findViewById(R.id.viewpagertab);
        mTabLayout.setCustomTabView(R.layout.custom_tab, R.id.custom_text);
        mTabLayout.setDividerColors(getResources().getColor(R.color.transparent));
        mSearchIv = findView(R.id.search);
        mrRfreshIv = findView(R.id.refresh);
        mMoreMenu = findView(R.id.menu);

        mSearchll = findView(R.id.search_ll);
        mSearchBackiv = findView(R.id.search_back);
        mSearchev = findView(R.id.search_ev);
        mSearchRecyclerView = findView(R.id.search_list);
    }

    @Override
    protected void initListeners() {
        mrRfreshIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UserDatas.getInstance().loadMusics();
                // freshMediaDB();
                startActivity(new Intent(MainActivity.this, ScanActivity.class));
            }
        });
        mSearchll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPage == 1){
                    UserDatas.getInstance().resetSongs();
                }else if (mCurrentPage == 2){
                    UserDatas.getInstance().resetCutteds();
                } else {
                    UserDatas.getInstance().resetRecords();
                }
                mSearchll.setVisibility(View.GONE);
                mSearchev.setText("");
                stop();
                InputMethodManager imm =
                    (InputMethodManager) mSearchev.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
        });
        mSearchBackiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPage == 1){
                    UserDatas.getInstance().resetSongs();
                }else if (mCurrentPage == 2){
                    UserDatas.getInstance().resetCutteds();
                } else {
                    UserDatas.getInstance().resetRecords();
                }
                mSearchll.setVisibility(View.GONE);
                mSearchev.setText("");
                stop();
                InputMethodManager imm =
                    (InputMethodManager) mSearchev.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
        });
        mSearchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchll.setVisibility(View.VISIBLE);
                mSearchev.requestFocus();
                InputMethodManager imm =
                    (InputMethodManager) mSearchev.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);

            }
        });
        mMoreMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu menu = new PopupMenu(MainActivity.this, v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_sort_name:
                                UserDatas.getInstance().sortByName(mCurrentPage);
                                break;
                            case R.id.menu_sort_date:
                                UserDatas.getInstance().sortByDate(mCurrentPage);
                                break;
                            case R.id.menu_sort_track:
                                UserDatas.getInstance().sortByDate(mCurrentPage);
                                break;
                            case R.id.menu_sort_artist:
                                UserDatas.getInstance().sortByDate(mCurrentPage);
                                break;
                            case R.id.menu_sort_album:
                                UserDatas.getInstance().sortByDate(mCurrentPage);
                                break;
                            case R.id.menu_about:
                                NavigationUtils.goToAbout(MainActivity.this);
                                break;
                            case R.id.menu_rate:
                                String appPackageName = getPackageName();
                                launchAppDetail(appPackageName, "com.android.vending");
                                break;
                            case R.id.menu_invite:
                                ShareUtils.shareAppText(MainActivity.this);
                                break;
                            case R.id.menu_help:
                                ShareUtils.adviceEmail(MainActivity.this);
                                break;
                        }
                        return false;
                    }
                });
                menu.getMenuInflater().inflate(R.menu.popup_main, menu.getMenu());
                if (mCurrentPage == 0) {
                    menu.getMenu().setGroupVisible(R.id.menu_group_sort, false);
                } else {
                    menu.getMenu().setGroupVisible(R.id.menu_group_sort, true);
                    if (mCurrentPage == 1) {
                        menu.getMenu().getItem(0).getSubMenu().getItem(2).setVisible(true);
                        menu.getMenu().getItem(0).getSubMenu().getItem(3).setVisible(true);
                        menu.getMenu().getItem(0).getSubMenu().getItem(4).setVisible(true);
                    } else {
                        menu.getMenu().getItem(0).getSubMenu().getItem(2).setVisible(false);
                        menu.getMenu().getItem(0).getSubMenu().getItem(3).setVisible(false);
                        menu.getMenu().getItem(0).getSubMenu().getItem(4).setVisible(false);
                    }
                }
                menu.show();
            }
        });
        mSearchev.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mViewPager.getCurrentItem() == 1) {
                    List<SongModel> filteredDataList = filterSongs(UserDatas.getInstance().getSongs(), s.toString());
                    SongsAdapter adapter = new SongsAdapter(MainActivity.this, filteredDataList);
                    mSearchRecyclerView.setAdapter(adapter);
                    if (filteredDataList == null || filteredDataList.size() == 0) {
                        mSearchRecyclerView.setVisibility(View.GONE);
                    } else {
                        mSearchRecyclerView.setVisibility(View.VISIBLE);
                    }
                } else if (mViewPager.getCurrentItem() == 2) {
                    List<RecordModel> filteredDataList =
                        filterRecords(UserDatas.getInstance().getRecords(), s.toString());
                    RecordsAdapter adapter = new RecordsAdapter(MainActivity.this, filteredDataList);
                    mSearchRecyclerView.setAdapter(adapter);
                    if (filteredDataList == null || filteredDataList.size() == 0) {
                        mSearchRecyclerView.setVisibility(View.GONE);
                    } else {
                        mSearchRecyclerView.setVisibility(View.VISIBLE);
                    }
                } else if (mViewPager.getCurrentItem() == 3) {
                    List<CutterModel> filteredDataList =
                        filterCutters(UserDatas.getInstance().getCuttereds(), s.toString());
                    CuttersAdapter adapter = new CuttersAdapter(MainActivity.this, filteredDataList);
                    mSearchRecyclerView.setAdapter(adapter);
                    if (filteredDataList == null || filteredDataList.size() == 0) {
                        mSearchRecyclerView.setVisibility(View.GONE);
                    } else {
                        mSearchRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPage = position;
                if (mCurrentPage == 0) {
                    mSearchIv.setVisibility(View.GONE);
                } else {
                    mSearchIv.setVisibility(View.VISIBLE);
                }
                if (mCurrentPage == 1) {
                    mrRfreshIv.setVisibility(View.VISIBLE);
                } else {
                    mrRfreshIv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        UserDatas.getInstance().register(this);
    }

    private List<SongModel> filterSongs(List<SongModel> dataList, String newText) {
        if (null == newText) {
            return new ArrayList<>();
        }
        newText = newText.toLowerCase();
        if (TextUtils.isEmpty(newText)) {
            return new ArrayList<>();
        }
        String text;
        List<SongModel> filteredDataList = new ArrayList<>();
        for (SongModel dataFromDataList : dataList) {
            text = dataFromDataList.title.toLowerCase();

            if (text.contains(newText)) {
                filteredDataList.add(dataFromDataList);
            }
        }

        return filteredDataList;
    }

    private List<RecordModel> filterRecords(List<RecordModel> dataList, String newText) {
        if (null == newText) {
            return new ArrayList<>();
        }
        newText = newText.toLowerCase();
        if (TextUtils.isEmpty(newText)) {
            return new ArrayList<>();
        }
        String text;
        List<RecordModel> filteredDataList = new ArrayList<>();
        for (RecordModel dataFromDataList : dataList) {
            text = dataFromDataList.title.toLowerCase();

            if (text.contains(newText)) {
                filteredDataList.add(dataFromDataList);
            }
        }

        return filteredDataList;
    }

    private List<CutterModel> filterCutters(List<CutterModel> dataList, String newText) {
        if (null == newText) {
            return new ArrayList<>();
        }
        newText = newText.toLowerCase();
        if (TextUtils.isEmpty(newText)) {
            return new ArrayList<>();
        }
        String text;
        List<CutterModel> filteredDataList = new ArrayList<>();
        for (CutterModel dataFromDataList : dataList) {
            text = dataFromDataList.title.toLowerCase();

            if (text.contains(newText)) {
                filteredDataList.add(dataFromDataList);
            }
        }

        return filteredDataList;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mSharePreferenceUtil = new SharePreferenceUtil(this, "MainActivity");
        mPlayer = null;
        mIsPlaying = false;
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        freshMediaDB();
        initViewPages();
        mTabLayout.setViewPager(mViewPager);

        MyLayoutManager linearLayoutManager = new MyLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSearchRecyclerView.setLayoutManager(linearLayoutManager);

        UserDatas.getInstance().setContext(this);
        UserDatas.getInstance().loadDatas();

        if (!UserDatas.getInstance().isRated(getVersionCode())) {
            if (UserDatas.getInstance().getAppStart() != 0 && UserDatas.getInstance().getAppStart() % 7 == 0) {
                RateDialog dialog = new RateDialog(this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String appPackageName = getPackageName();
                        launchAppDetail(appPackageName, "com.android.vending");
                        UserDatas.getInstance().addRated(getVersionCode());
                    }
                });
                dialog.setCancelable(true);
                dialog.show();
            }
        }

        UserDatas.getInstance().addAppStart();
        // 缓存广告
        if (Constants.Ad_type == Constants.AD_FACEBOOK) {
            ADManager.getInstance().loadSaveSuccessAD(this);
            ADManager.getInstance().loadHomeAD(this);
            ADManager.getInstance().loadScanSuccessAD(this);
        }
    }

    private void freshMediaDB() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            // Android4.4中拒绝发送Intent.ACTION_MEDIA_MOUNTED扫描SD卡的广播
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath())));
        } else {
            // http://www.tuicool.com/articles/vyYZny
            MediaScannerConnection.scanFile(this,
                new String[] { Environment.getExternalStorageDirectory().getAbsolutePath() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
//                        UserDatas.getInstance().loadMusics();
                    }
                });
        }
    }

    private void initViewPages() {
        // 初始化四个布局
        HomeFragment tab01 = new HomeFragment();
        SongFragment tab02 = new SongFragment();
        CutteredFragment tab03 = new CutteredFragment();
        RecordFragment tab04 = new RecordFragment();

        mFragments.add(tab01);
        mFragments.add(tab02);
        mFragments.add(tab03);
        mFragments.add(tab04);
        mTitles.add(getString(R.string.tab_one));
        mTitles.add(getString(R.string.tab_two));
        mTitles.add(getString(R.string.tab_three));
        mTitles.add(getString(R.string.tab_four));
        // 初始化Adapter这里使用FragmentPagerAdapter
        mAdpter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {

                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles.get(position);
            }
        };
        mViewPager.setAdapter(mAdpter);
    }

    @Override
    public void gotoIndex(int index) {
        mViewPager.setCurrentItem(index);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                break;
            case 1:
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void play(final VoiceModel model) {
        currentModel = model;
        mIsPlaying = false;
        if (mPlayer.isPlaying()){
            Log.d("progress ", "isPlaying");
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        mPlayer.reset();
        try {
            mPlayer.setDataSource(MainActivity.this, Uri.fromFile(new File(model.path)));
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d("progress ", "onCompletion");
                    cancelProgressTimer();
                    currentModel.playStatus = 0;
                    currentModel.progress = 0;
                    UserDatas.getInstance().updatePlayStatus(model);
                }
            });
            mPlayer.prepare();
            mPlayer.start();
            Log.d("progress ", "play");
            currentModel.playStatus = 1;
            startProgressTimer();
        } catch (Exception e) {
            Log.d("progress ", e.getLocalizedMessage());
            e.printStackTrace();
            mIsPlaying = false;
            cancelProgressTimer();
        }
        mIsPlaying = true;
    }

    @Override
    public void pause() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            mIsPlaying = false;
            cancelProgressTimer();
        }
    }

    @Override
    public void stop() {
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
            mIsPlaying = false;
            cancelProgressTimer();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
            mIsPlaying = false;
            if (currentModel != null) {
                currentModel.playStatus = 0;
                UserDatas.getInstance().updatePlayStatus(currentModel);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserDatas.getInstance().unregister(this);
        if (mPlayer != null) {
            mPlayer.release();
            mIsPlaying = false;
        }
        mPlayer = null;
    }

    private long mPressedTime = 0;

    @Override
    public void onBackPressed() {
        if (!UserDatas.getInstance().isRated(getVersionCode())) {
            RateDialog dialog = new RateDialog(this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (int) v.getTag();
                    if (tag == 1) {
                        MainActivity.this.finish();
                        System.exit(0);
                    } else {
                        String appPackageName = getPackageName();
                        launchAppDetail(appPackageName, "com.android.vending");
                        UserDatas.getInstance().addRated(getVersionCode());
                        MainActivity.this.finish();
                        System.exit(0);
                    }
                }
            });
            dialog.setCancelable(true);
            dialog.show();
            return;
        }

        long mNowTime = System.currentTimeMillis();// 获取第一次按键时间
        if ((mNowTime - mPressedTime) > 2000) {// 比较两次按键时间差
            ToastUtils.makeToastAndShow(this, " Press back key again to exit!");
            mPressedTime = mNowTime;
        } else {// 退出程序
            this.finish();
            System.exit(0);
        }
    }

    /**
     * 获取版本号
     * 
     * @return 当前应用的版本号
     */
    public int getVersionCode() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    protected void startProgressTimer() {
        cancelProgressTimer();
        UPDATE_PROGRESS_TIMER = new Timer();
        mProgressTimerTask = new ProgressTimerTask();
        UPDATE_PROGRESS_TIMER.schedule(mProgressTimerTask, 0, 300);
        Log.d("progress ", "startProgressTimer");
    }

    protected void cancelProgressTimer() {
        Log.d("progress ", "cancelProgressTimer");
        if (UPDATE_PROGRESS_TIMER != null) {
            UPDATE_PROGRESS_TIMER.cancel();
        }
        if (mProgressTimerTask != null) {
            mProgressTimerTask.cancel();
        }

    }

    protected class ProgressTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (!mPlayer.isPlaying()){
                    return;
                }
                int position = mPlayer.getCurrentPosition();
                int duration = mPlayer.getDuration();
                int progress = position * 100 / (duration == 0 ? 1 : duration);
                Log.d("progress ", "" + progress);
                currentModel.progress = progress;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UserDatas.getInstance().updatePlayStatus(currentModel);
                    }
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1005:
                if(data==null)
                {
                    return;
                }
                //处理返回的data,获取选择的联系人信息
                Uri uri=data.getData();
                setPhoneContacts(uri);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void setPhoneContacts(Uri dataUri){
        //得到ContentResolver对象
        ContentResolver cr = getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor=cr.query(dataUri,null,null,null,null);
        if(cursor!=null)
        {
            cursor.moveToFirst();
            int dataIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(dataIndex);

            dataIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
            String displayName = cursor.getString(dataIndex);
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId);

            ContentValues values = new ContentValues();
            values.put(ContactsContract.Contacts.CUSTOM_RINGTONE, UserDatas.getInstance().getAssignContactUri());
            getContentResolver().update(uri, values, null, null);

            String message = getResources().getText(R.string.success_contact_ringtone) + " " + displayName;

            Toast.makeText(this, message, Toast.LENGTH_SHORT)
                    .show();

            cursor.close();
            return;
        }
        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
    }
}
