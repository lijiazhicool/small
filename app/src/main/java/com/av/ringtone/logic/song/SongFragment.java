package com.av.ringtone.logic.song;

import com.av.ringtone.R;
import com.av.ringtone.UserDatas;
import com.av.ringtone.base.BaseActivity;
import com.av.ringtone.base.BaseFragment;
import com.av.ringtone.model.CutterModel;
import com.av.ringtone.model.RecordModel;
import com.av.ringtone.model.SongModel;
import com.av.ringtone.utils.ToastUtils;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 我的音乐
 */
public class SongFragment extends BaseFragment implements UserDatas.DataChangedListener {
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mRecyclerView;
    private SongsAdapter mAdapter;
    private LinearLayout mEmptyll;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_music;
    }

    @Override
    protected void initView(View parentView, Bundle savedInstanceState) {
        mSwipeLayout = findViewById(R.id.id_swipe_ly);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
            android.R.color.holo_orange_light, android.R.color.holo_red_light);

        mEmptyll = findViewById(R.id.emptyll);
        mRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void initData() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            ToastUtils.makeToastAndShow(mActivity, getString(R.string.sdcard_readonly));
            return;
        }
        if (status.equals(Environment.MEDIA_SHARED)) {
            ToastUtils.makeToastAndShow(mActivity, getString(R.string.sdcard_shared));
            return;
        }
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            ToastUtils.makeToastAndShow(mActivity, getString(R.string.no_sdcard));
            return;
        }
    }

    @Override
    protected void initListener() {
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                UserDatas.getInstance().loadMusics();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        UserDatas.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        UserDatas.getInstance().unregister(this);
    }

    @Override
    public void updateSongs(List<SongModel> list) {
        mSwipeLayout.setRefreshing(false);
        mAdapter = new SongsAdapter((BaseActivity) getActivity(), list);
        if (mAdapter.getDatas().size() == 0) {
            mEmptyll.setVisibility(View.VISIBLE);
        } else {
            mEmptyll.setVisibility(View.GONE);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void updateRecords(List<RecordModel> list) {

    }

    @Override
    public void updateCutters(List<CutterModel> list) {

    }

    @Override
    public void updateCutters() {

    }

    @Override
    public void sortByName(int sortType) {
        if (sortType != UserDatas.SORT_SONG) {
            return;
        }
        List<SongModel> list = mAdapter.getDatas();
        Collections.sort(list, new Comparator<SongModel>() {

            /*
             * int compare(Student o1, Student o2) 返回一个基本类型的整型， 返回负数表示：o1 小于o2， 返回0 表示：o1和o2相等， 返回正数表示：o1大于o2。
             */
            public int compare(SongModel o1, SongModel o2) {

                // 按照学生的年龄进行升序排列
                if (o1.title.compareTo(o2.title) > 0) {
                    return 1;
                }
                return -1;
            }
        });
        mAdapter.upateDatas(list);
    }

    @Override
    public void sortByLength(int sortType) {
        if (sortType != UserDatas.SORT_SONG) {
            return;
        }
        List<SongModel> list = mAdapter.getDatas();
        Collections.sort(list, new Comparator<SongModel>() {
            public int compare(SongModel o1, SongModel o2) {

                // 按照学生的年龄进行升序排列
                if (o1.duration > o2.duration) {
                    return 1;
                }
                if (o1.duration == o2.duration) {
                    return 0;
                }
                return -1;
            }
        });
        mAdapter.upateDatas(list);
    }

    @Override
    public void sortByDate(int sortType) {
        if (sortType != UserDatas.SORT_SONG) {
            return;
        }
        List<SongModel> list = mAdapter.getDatas();
        Collections.sort(list, new Comparator<SongModel>() {
            public int compare(SongModel o1, SongModel o2) {

                // 按照学生的年龄进行升序排列
                if (o1.date > o2.date) {
                    return 1;
                }
                if (o1.date == o2.date) {
                    return 0;
                }
                return -1;
            }
        });
        mAdapter.upateDatas(list);
    }
}
