package com.example.test.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.example.test.R;
import com.example.test.UserDatas;
import com.example.test.base.BaseActivity;
import com.example.test.logic.home.HomeFragment;
import com.example.test.logic.song.SongFragment;
import com.example.test.logic.record.RecordFragment;
import com.example.test.logic.ringtone.CutteredFragment;
import com.example.test.logic.song.SongsAdapter;
import com.example.test.model.SongModel;
import com.example.test.utils.NavigationUtils;
import com.example.test.utils.ShareUtils;
import com.example.test.utils.ToastUtils;
import com.example.test.views.HelpDialog;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

public class MainActivity extends BaseActivity implements HomeFragment.onHomeListener {
    private List<Fragment> mFragments = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>();
    private FragmentPagerAdapter mAdpter;
    private ViewPager mViewPager;
    private SmartTabLayout mTabLayout;
    private ImageView mSearchIv, mMoreMenu;

    // 全屏幕搜索
    private LinearLayout mSearchll;
    private ImageView mSearchBackiv;
    private EditText mSearchev;
    private RecyclerView mSearchRecyclerView;

    private List<SongModel> filteredDataList = new ArrayList<>();
    private CommonAdapter mAdapter;

    private int mCurrentPage = 0;

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
        mSearchRecyclerView = findView(R.id.search_list);
//        mAdapter = new CommonAdapter();
//        mSearchRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initListeners() {
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
                            case R.id.menu_about:
                                NavigationUtils.goToAbout(MainActivity.this);
                                break;
                            case R.id.menu_rate:
                                String appPackageName = getPackageName();
                                launchAppDetail(appPackageName, "com.android.vending");
                                break;
                            case R.id.menu_invite:
                                ShareUtils.shareText(MainActivity.this);
                                break;
                            case R.id.menu_help:
                                HelpDialog dialog = new HelpDialog();
                                dialog.setSendlListener(new HelpDialog.SendlListener() {
                                    @Override
                                    public void onSendClick(View v, String text) {
                                        ShareUtils.adviceEmail(MainActivity.this, text);
                                    }
                                });
                                dialog.show(getFragmentManager(), "HelpDialog");
                                break;
                        }
                        return false;
                    }
                });
                menu.inflate(R.menu.popup_main);
                menu.show();
            }
        });
        mSearchev.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mViewPager.getCurrentItem()==1){
                    filteredDataList = filter(UserDatas.getInstance().getSongs(), s.toString());
                    SongsAdapter adapter = new SongsAdapter(MainActivity.this, filteredDataList);
                    mSearchRecyclerView.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
                } else if (mViewPager.getCurrentItem()==2){
                    filteredDataList = filter(UserDatas.getInstance().getSongs(), s.toString());
                    SongsAdapter adapter = new SongsAdapter(MainActivity.this, filteredDataList);
                    mSearchRecyclerView.setAdapter(adapter);
                } else if (mViewPager.getCurrentItem()==3){
                    filteredDataList = filter(UserDatas.getInstance().getSongs(), s.toString());
                    SongsAdapter adapter = new SongsAdapter(MainActivity.this, filteredDataList);
                    mSearchRecyclerView.setAdapter(adapter);
                }
//                filteredDataList = filter(UserDatas.getInstance().getSongs(), s.toString());
//                mAdapter.notifyDataSetChanged();
                if (filteredDataList == null || filteredDataList.size() == 0) {
                    mSearchRecyclerView.setVisibility(View.GONE);
                } else {
                    mSearchRecyclerView.setVisibility(View.VISIBLE);
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
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private List<SongModel> filter(List<SongModel> dataList, String newText) {
        newText = newText.toLowerCase();
        String text;
        filteredDataList = new ArrayList<>();
        for (SongModel dataFromDataList : dataList) {
            text = dataFromDataList.title.toLowerCase();

            if (text.contains(newText)) {
                filteredDataList.add(dataFromDataList);
            }
        }

        return filteredDataList;
    }

    /**
     * 启动到应用商店app详情界面 http://www.jianshu.com/p/a4a806567368
     *
     * @param appPkg 目标App的包名
     * @param marketPkg 应用商店包名 ,如果为""则由系统弹出应用商店列表供用户选择,否则调转到目标市场的应用详情界面，某些应用商店可能会失败 例如com.android.vending Google Play
     */
    public void launchAppDetail(String appPkg, String marketPkg) {
        try {
            if (TextUtils.isEmpty(appPkg))
                return;

            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        freshMediaDB();
        initViewPages();
        mTabLayout.setViewPager(mViewPager);

        MyLayoutManager linearLayoutManager = new MyLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSearchRecyclerView.setLayoutManager(linearLayoutManager);

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

    class CommonAdapter extends RecyclerView.Adapter<CommonAdapter.ItemHolder> {

        public CommonAdapter() {
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song, null);
            ItemHolder ml = new ItemHolder(v);
            return ml;
        }

        @Override
        public void onBindViewHolder(ItemHolder itemHolder, int i) {
            SongModel localItem = filteredDataList.get(i);
            itemHolder.type.setImageResource(R.drawable.ic_music_small);
            itemHolder.title.setText(localItem.title);
            itemHolder.artist
                .setText(getDuration(localItem.duration / 1000) + " " + localItem.artist + " " + localItem.path);
            setOnPopupMenuListener(itemHolder, i);
        }

        private String getDuration(int d) {
            return d / 60 + ":" + d % 60;
        }

        @Override
        public int getItemCount() {
            return (null != filteredDataList ? filteredDataList.size() : 0);
        }

        private void setOnPopupMenuListener(ItemHolder itemHolder, final int position) {
            itemHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu menu = new PopupMenu(MainActivity.this, v);
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.popup_song_edit:
                                    NavigationUtils.goToCutter(MainActivity.this, filteredDataList.get(position));
                                    break;
                                case R.id.popup_song_delete:
                                    File file = new File(filteredDataList.get(position).path);
                                    if (file.exists()) {
                                        if (file.delete()) {
                                            ToastUtils.makeToastAndShow(MainActivity.this,
                                                file.getPath() + MainActivity.this.getString(R.string.delete_success));
                                        } else {
                                            ToastUtils.makeToastAndShow(MainActivity.this,
                                                file.getPath() + MainActivity.this.getString(R.string.delete_failed));
                                        }
                                        String params[] = new String[] { file.getPath() };
                                        MainActivity.this.getContentResolver().delete(
                                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                            MediaStore.Images.Media.DATA + " LIKE ?", params);
                                        filteredDataList.remove(position);
                                        notifyItemRemoved(position);
                                    }
                                    break;
                            }
                            return false;
                        }
                    });
                    menu.inflate(R.menu.popup_song);
                    menu.show();
                }
            });
        }

        public class ItemHolder extends RecyclerView.ViewHolder {
            protected TextView title, artist;
            protected ImageView type, popupMenu;

            public ItemHolder(View view) {
                super(view);

                this.type = (ImageView) view.findViewById(R.id.type_iv);
                this.title = (TextView) view.findViewById(R.id.song_title);
                this.artist = (TextView) view.findViewById(R.id.song_detail);
                this.popupMenu = (ImageView) view.findViewById(R.id.popup_menu);
            }
        }
    }
}
