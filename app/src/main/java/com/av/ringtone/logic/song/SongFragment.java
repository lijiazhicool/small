package com.av.ringtone.logic.song;

import com.av.ringtone.R;
import com.av.ringtone.UserDatas;
import com.av.ringtone.base.BaseActivity;
import com.av.ringtone.base.BaseFragment;
import com.av.ringtone.logic.MainActivity;
import com.av.ringtone.model.CutterModel;
import com.av.ringtone.model.RecordModel;
import com.av.ringtone.model.SongModel;
import com.av.ringtone.utils.ToastUtils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
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
//    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mRecyclerView;
    private SongsAdapter mAdapter;
    private LinearLayout mEmptyll;

    private boolean mSortReverseByName = true;
    private boolean mSortReverseByLength = true;
    private boolean mSortReverseByDate = true;

    private int mSortType = 0;
    private boolean mIsInit = false;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_music;
    }

    @Override
    protected void initView(View parentView, Bundle savedInstanceState) {
        mEmptyll = findViewById(R.id.empty_tv);
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
        mIsInit = true;
    }

    @Override
    protected void initListener() {
//        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                UserDatas.getInstance().loadMusics();
//            }
//        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && mIsInit) {
            UserDatas.getInstance().resetSongs();
            ((MainActivity)getActivity()).stop();
        }
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
        mAdapter = new SongsAdapter((MainActivity) getActivity(), list);
        if (mAdapter.getDatas().size() == 0) {
            mEmptyll.setVisibility(View.VISIBLE);
        } else {
            mEmptyll.setVisibility(View.GONE);
            mRecyclerView.setAdapter(mAdapter);
        }
        if (mSortType ==  0){
            sortByName_fresh();
        } else if (mSortType ==  1){
            sortByLength_fresh();
        } else if (mSortType == 2){
            sortByDate_fresh();
        }
    }

    @Override
    public void updateRecords(List<RecordModel> list) {

    }

    @Override
    public void updateCutters(List<CutterModel> list) {

    }

    @Override
    public void updatePlayStatus(int mainType) {
        if (mainType != 1){
            return;
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void sortByName(int sortType, boolean isNeedRevers) {
        if (sortType!= UserDatas.SORT_SONG){
            return;
        }
        List<SongModel> list = mAdapter.getDatas();
        if (mSortReverseByName){
            Collections.sort(list, new Comparator<SongModel>(){

                /*
                 * int compare(Student o1, Student o2) 返回一个基本类型的整型，
                 * 返回负数表示：o1 小于o2，
                 * 返回0 表示：o1和o2相等，
                 * 返回正数表示：o1大于o2。
                 */
                public int compare(SongModel o1, SongModel o2) {
                    if(o1.title.compareTo(o2.title)<0){
                        return 1;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(list, new Comparator<SongModel>(){

                /*
                 * int compare(Student o1, Student o2) 返回一个基本类型的整型，
                 * 返回负数表示：o1 小于o2，
                 * 返回0 表示：o1和o2相等，
                 * 返回正数表示：o1大于o2。
                 */
                public int compare(SongModel o1, SongModel o2) {
                    if(o1.title.compareTo(o2.title)>0){
                        return 1;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(list);
        if (isNeedRevers) {
            mSortReverseByName = !mSortReverseByName;
        }
        mSortType = 0;
    }

    @Override
    public void sortByLength(int sortType, boolean isNeedRevers) {
        if (sortType!= UserDatas.SORT_SONG){
            return;
        }
        List<SongModel> list = mAdapter.getDatas();

        if (mSortReverseByLength){
            Collections.sort(list, new Comparator<SongModel>(){
                public int compare(SongModel o1, SongModel o2) {
                    if(o1.duration < o2.duration){
                        return 1;
                    }
                    if(o1.duration == o2.duration){
                        return 0;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(list, new Comparator<SongModel>(){
                public int compare(SongModel o1, SongModel o2) {
                    if(o1.duration > o2.duration){
                        return 1;
                    }
                    if(o1.duration == o2.duration){
                        return 0;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(list);
        if (isNeedRevers) {
            mSortReverseByLength = !mSortReverseByLength;
        }
        mSortType = 1;
    }

    @Override
    public void sortByDate(int sortType, boolean isNeedRevers) {
        if (sortType!= UserDatas.SORT_SONG){
            return;
        }
        List<SongModel> list = mAdapter.getDatas();
        if (mSortReverseByDate){
            Collections.sort(list, new Comparator<SongModel>(){
                public int compare(SongModel o1, SongModel o2) {
                    if(o1.date < o2.date){
                        return 1;
                    }
                    if(o1.date == o2.date){
                        return 0;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(list, new Comparator<SongModel>(){
                public int compare(SongModel o1, SongModel o2) {
                    //按照学生的年龄进行倒序排列
                    if(o1.date > o2.date){
                        return 1;
                    }
                    if(o1.date == o2.date){
                        return 0;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(list);
        if (isNeedRevers) {
        mSortReverseByDate = !mSortReverseByDate;}
        mSortType = 2;
    }

    private void sortByName_fresh() {
        List<SongModel> list = mAdapter.getDatas();
        if (!mSortReverseByName){
            Collections.sort(list, new Comparator<SongModel>(){

                /*
                 * int compare(Student o1, Student o2) 返回一个基本类型的整型，
                 * 返回负数表示：o1 小于o2，
                 * 返回0 表示：o1和o2相等，
                 * 返回正数表示：o1大于o2。
                 */
                public int compare(SongModel o1, SongModel o2) {
                    if(o1.title.compareTo(o2.title)<0){
                        return 1;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(list, new Comparator<SongModel>(){

                /*
                 * int compare(Student o1, Student o2) 返回一个基本类型的整型，
                 * 返回负数表示：o1 小于o2，
                 * 返回0 表示：o1和o2相等，
                 * 返回正数表示：o1大于o2。
                 */
                public int compare(SongModel o1, SongModel o2) {
                    if(o1.title.compareTo(o2.title)>0){
                        return 1;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(list);
        mSortType = 0;
    }

    private void sortByLength_fresh() {
        List<SongModel> list = mAdapter.getDatas();

        if (!mSortReverseByLength){
            Collections.sort(list, new Comparator<SongModel>(){
                public int compare(SongModel o1, SongModel o2) {
                    if(o1.duration < o2.duration){
                        return 1;
                    }
                    if(o1.duration == o2.duration){
                        return 0;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(list, new Comparator<SongModel>(){
                public int compare(SongModel o1, SongModel o2) {
                    if(o1.duration > o2.duration){
                        return 1;
                    }
                    if(o1.duration == o2.duration){
                        return 0;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(list);
        mSortType = 1;
    }

    private void sortByDate_fresh() {
        List<SongModel> list = mAdapter.getDatas();
        if (!mSortReverseByDate){
            Collections.sort(list, new Comparator<SongModel>(){
                public int compare(SongModel o1, SongModel o2) {
                    if(o1.date < o2.date){
                        return 1;
                    }
                    if(o1.date == o2.date){
                        return 0;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(list, new Comparator<SongModel>(){
                public int compare(SongModel o1, SongModel o2) {
                    //按照学生的年龄进行倒序排列
                    if(o1.date > o2.date){
                        return 1;
                    }
                    if(o1.date == o2.date){
                        return 0;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(list);
        mSortType = 2;
    }
}
