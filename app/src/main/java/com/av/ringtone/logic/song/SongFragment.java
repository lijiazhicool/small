package com.av.ringtone.logic.song;

import com.av.ringtone.Constants;
import com.av.ringtone.R;
import com.av.ringtone.UserDatas;
import com.av.ringtone.base.BaseFragment;
import com.av.ringtone.logic.MainActivity;
import com.av.ringtone.model.CutterModel;
import com.av.ringtone.model.RecordModel;
import com.av.ringtone.model.SongModel;
import com.av.ringtone.utils.ToastUtils;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 我的音乐
 */
public class SongFragment extends BaseFragment implements UserDatas.DataChangedListener {
    private RecyclerView mRecyclerView;
    private SongsAdapter mAdapter;
    private TextView mEmptyTv;
    private LinearLayout mAdll;

    private AdView adView;
    private final static String EVENT_AD_TYPE = "Fragment_AdView_Click";
    private final static String EVENT_AD_NAME = "Fragment_AdView";
    private final static String EVENT_AD_ID = "Fragment_AdView_ID";

    private boolean mSortReverseByName = true;
    private boolean mSortReverseByDate = true;
    private boolean mSortReverseByTrack = true;
    private boolean mSortReverseByArtist = true;
    private boolean mSortReverseByAlbum = true;

    private int mSortType = 0;
    private boolean mIsInit = false;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_music;
    }

    @Override
    protected void initView(View parentView, Bundle savedInstanceState) {
        mEmptyTv = findViewById(R.id.empty_tv);
        mRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdll = findViewById(R.id.ad_ll);
    }

    @Override
    protected void initData() {
        loadBanner();
        mIsInit = true;
    }

    @Override
    protected void initListener() {
    }

    protected void loadBanner() {
        // Instantiate an AdView view
        adView = new AdView(getActivity(), Constants.AD_PLACE_FRAGMENT_BANNER, AdSize.BANNER_HEIGHT_50);
        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                adView.destroy();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (null != mAdll) {
                    mAdll.setVisibility(View.VISIBLE);
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
            mEmptyTv.setVisibility(View.VISIBLE);
        } else {
            mEmptyTv.setVisibility(View.GONE);
            mRecyclerView.setAdapter(mAdapter);
        }
        if (mSortType ==  0){
            sortByName_fresh();
        } else if (mSortType ==  1){
            sortByDate_fresh();
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
        mSortType = 1;
    }

    @Override
    public void sortByTrack(int sortType, boolean isNeedRevers) {
        if (sortType!= UserDatas.SORT_SONG){
            return;
        }
        List<SongModel> list = mAdapter.getDatas();
        if (mSortReverseByTrack){
            Collections.sort(list, new Comparator<SongModel>(){
                public int compare(SongModel o1, SongModel o2) {
                    if(o1.trackNumber < o2.trackNumber){
                        return 1;
                    }
                    if(o1.trackNumber == o2.trackNumber){
                        return 0;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(list, new Comparator<SongModel>(){
                public int compare(SongModel o1, SongModel o2) {
                    //按照学生的年龄进行倒序排列
                    if(o1.trackNumber > o2.trackNumber){
                        return 1;
                    }
                    if(o1.trackNumber == o2.trackNumber){
                        return 0;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(list);
        if (isNeedRevers) {
            mSortReverseByTrack = !mSortReverseByTrack;}
        mSortType = 2;
    }

    @Override
    public void sortByArtist(int sortType, boolean isNeedRevers) {
        if (sortType!= UserDatas.SORT_SONG){
            return;
        }
        List<SongModel> list = mAdapter.getDatas();
        if (mSortReverseByArtist){
            Collections.sort(list, new Comparator<SongModel>(){

                /*
                 * int compare(Student o1, Student o2) 返回一个基本类型的整型，
                 * 返回负数表示：o1 小于o2，
                 * 返回0 表示：o1和o2相等，
                 * 返回正数表示：o1大于o2。
                 */
                public int compare(SongModel o1, SongModel o2) {
                    if(o1.artist.compareTo(o2.artist)<0){
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
                    if(o1.artist.compareTo(o2.artist)>0){
                        return 1;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(list);
        if (isNeedRevers) {
            mSortReverseByArtist = !mSortReverseByArtist;
        }
        mSortType = 3;
    }

    @Override
    public void sortByAlbum(int sortType, boolean isNeedRevers) {
        if (sortType!= UserDatas.SORT_SONG){
            return;
        }
        List<SongModel> list = mAdapter.getDatas();
        if (mSortReverseByAlbum){
            Collections.sort(list, new Comparator<SongModel>(){

                /*
                 * int compare(Student o1, Student o2) 返回一个基本类型的整型，
                 * 返回负数表示：o1 小于o2，
                 * 返回0 表示：o1和o2相等，
                 * 返回正数表示：o1大于o2。
                 */
                public int compare(SongModel o1, SongModel o2) {
                    if(o1.albumName.compareTo(o2.albumName)<0){
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
                    if(o1.albumName.compareTo(o2.albumName)>0){
                        return 1;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(list);
        if (isNeedRevers) {
            mSortReverseByAlbum = !mSortReverseByAlbum;
        }
        mSortType = 4;
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
        mSortType = 1;
    }
    private void sortByTrack_fresh() {
        List<SongModel> list = mAdapter.getDatas();
        if (!mSortReverseByTrack){
            Collections.sort(list, new Comparator<SongModel>(){
                public int compare(SongModel o1, SongModel o2) {
                    if(o1.trackNumber < o2.trackNumber){
                        return 1;
                    }
                    if(o1.trackNumber == o2.trackNumber){
                        return 0;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(list, new Comparator<SongModel>(){
                public int compare(SongModel o1, SongModel o2) {
                    if(o1.trackNumber > o2.trackNumber){
                        return 1;
                    }
                    if(o1.trackNumber == o2.trackNumber){
                        return 0;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(list);
        mSortType = 2;
    }
    private void sortByArtist_fresh() {
        List<SongModel> list = mAdapter.getDatas();
        if (!mSortReverseByArtist){
            Collections.sort(list, new Comparator<SongModel>(){
                public int compare(SongModel o1, SongModel o2) {
                    if(o1.artist.compareTo(o2.artist)<0){
                        return 1;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(list, new Comparator<SongModel>(){
                public int compare(SongModel o1, SongModel o2) {
                    if(o1.artist.compareTo(o2.artist)>0){
                        return 1;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(list);
        mSortType = 3;
    }
    private void sortByAlbum_fresh() {
        List<SongModel> list = mAdapter.getDatas();
        if (!mSortReverseByAlbum){
            Collections.sort(list, new Comparator<SongModel>(){
                public int compare(SongModel o1, SongModel o2) {
                    if(o1.albumName.compareTo(o2.albumName)<0){
                        return 1;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(list, new Comparator<SongModel>(){
                public int compare(SongModel o1, SongModel o2) {
                    if(o1.albumName.compareTo(o2.albumName)>0){
                        return 1;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(list);
        mSortType = 4;
    }
}
