package com.example.test.logic;

import java.util.ArrayList;
import java.util.List;

import com.example.test.R;
import com.example.test.UserDatas;
import com.example.test.base.BaseActivity;
import com.example.test.logic.home.HomeFragment;
import com.example.test.logic.song.SongFragment;
import com.example.test.logic.record.RecordFragment;
import com.example.test.logic.ringtone.CutteredFragment;
import com.example.test.utils.NavigationUtils;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

public class MainActivity extends BaseActivity implements HomeFragment.onHomeListener {
    private List<Fragment> mFragments = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>();
    private FragmentPagerAdapter mAdpter;
    private ViewPager mViewPager;
    private SmartTabLayout mTabLayout;
    private ImageView mSearchIv, mMoreMenu;

    private LinearLayout mSearchll;
    private ImageView mSearchBackiv;
    private EditText mSearchev;

    public static void launch(Context context) {
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    @Override
    protected int getLayoutId() {
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
        mMoreMenu = findView(R.id.menu);
        mSearchll = findView(R.id.search_ll);
        mSearchBackiv = findView(R.id.search_back);
        mSearchev = findView(R.id.search_ev);
    }

    @Override
    protected void initListeners() {
        mSearchBackiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchll.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) mSearchev.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
        });
        mSearchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchll.setVisibility(View.VISIBLE);
                mSearchev.requestFocus();
                InputMethodManager imm = (InputMethodManager) mSearchev.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
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
                            case R.id.menu_about:
                                NavigationUtils.goToAbout(MainActivity.this);
                                break;
                            case R.id.menu_rate:
                                break;
                            case R.id.menu_invite:
                                break;
                            case R.id.menu_help:
                                break;
                        }
                        return false;
                    }
                });
                menu.inflate(R.menu.popup_main);
                menu.show();
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        freshMediaDB();
        initViewPages();
        mTabLayout.setViewPager(mViewPager);
        UserDatas.getInstance().setContext(this);
        UserDatas.getInstance().loadDatas();
    }

    private void freshMediaDB() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            // Android4.4中拒绝发送Intent.ACTION_MEDIA_MOUNTED扫描SD卡的广播
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath())));
        } else {
            // http://www.tuicool.com/articles/vyYZny
            MediaScannerConnection.scanFile(this,
                new String[] { Environment.getExternalStorageDirectory().getAbsolutePath() }, null, null);
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
}
