package com.av.ringtone.logic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.av.ringtone.R;
import com.av.ringtone.UserDatas;
import com.av.ringtone.base.BaseActivity;
import com.av.ringtone.logic.home.HomeFragment;
import com.av.ringtone.logic.record.RecordFragment;
import com.av.ringtone.logic.record.RecordsAdapter;
import com.av.ringtone.logic.ringtone.CutteredFragment;
import com.av.ringtone.logic.ringtone.CuttersAdapter;
import com.av.ringtone.logic.song.SongFragment;
import com.av.ringtone.logic.song.SongsAdapter;
import com.av.ringtone.model.CutterModel;
import com.av.ringtone.model.RecordModel;
import com.av.ringtone.model.SongModel;
import com.av.ringtone.utils.NavigationUtils;
import com.av.ringtone.utils.SharePreferenceUtil;
import com.av.ringtone.utils.ShareUtils;
import com.av.ringtone.utils.ToastUtils;
import com.av.ringtone.views.RateDialog;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

public class MainActivity extends BaseActivity
    implements CuttersAdapter.MediaListener, UserDatas.GotoFragmentListener {
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

    private CutterModel currentModel = null;


    SharePreferenceUtil mSharePreferenceUtil;
    private String KEY="is_check_hint";

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
                UserDatas.getInstance().loadMusics();
            }
        });
        mSearchll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchll.setVisibility(View.GONE);
                InputMethodManager imm =
                    (InputMethodManager) mSearchev.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
        });
        mSearchBackiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchll.setVisibility(View.GONE);
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
                            case R.id.menu_sort_length:
                                UserDatas.getInstance().sortByLength(mCurrentPage);
                                break;
                            case R.id.menu_sort_date:
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
                menu.inflate(R.menu.popup_main);
                if (mCurrentPage == 0) {
                    menu.getMenu().setGroupVisible(R.id.menu_group_sort, false);
                } else {
                    menu.getMenu().setGroupVisible(R.id.menu_group_sort, true);
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
    }

    private List<SongModel> filterSongs(List<SongModel> dataList, String newText) {
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

        if (!UserDatas.getInstance().isRated(getVersionCode())){
            if (UserDatas.getInstance().getAppStart()!=0 && UserDatas.getInstance().getAppStart()%7==0){
                RateDialog dialog =
                        new RateDialog(this, new View.OnClickListener() {
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
                        UserDatas.getInstance().loadMusics();
                    }
                });
        }

    }

    private void initViewPages() {
        // 初始化四个布局
        HomeFragment tab01 = new HomeFragment();
        SongFragment tab02 = new SongFragment();
        RecordFragment tab03 = new RecordFragment();
        CutteredFragment tab04 = new CutteredFragment();

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
    public void play(CutterModel model) {
        currentModel = model;
        mIsPlaying = false;
        mPlayer.reset();
        try {
            mPlayer.setDataSource(MainActivity.this, Uri.fromFile(new File(model.localPath)));
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    currentModel.playStatus = 0;
                    UserDatas.getInstance().updateCutters();
                }
            });
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            mIsPlaying = false;
        }
        mIsPlaying = true;
    }

    @Override
    public void pause() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            mIsPlaying = false;
        }
    }

    @Override
    public void stop() {
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
            mIsPlaying = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        UserDatas.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
            mIsPlaying = false;
            if (currentModel != null) {
                currentModel.playStatus = 0;
                UserDatas.getInstance().updateCutters();
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
        long mNowTime = System.currentTimeMillis();// 获取第一次按键时间
        if ((mNowTime - mPressedTime) > 2000) {// 比较两次按键时间差
            ToastUtils.makeToastAndShow(this, "Press it again to return key to exit!");
            mPressedTime = mNowTime;
        } else {// 退出程序
            this.finish();
            System.exit(0);
        }
    }
    /**
     * 获取版本号
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

}
