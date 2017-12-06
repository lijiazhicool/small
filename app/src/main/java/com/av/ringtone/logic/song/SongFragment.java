package com.av.ringtone.logic.song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import com.av.ringtone.UserDatas;
import com.av.ringtone.base.BaseFragment;
import com.av.ringtone.logic.MainActivity;
import com.av.ringtone.model.CutterModel;
import com.av.ringtone.model.RecordModel;
import com.av.ringtone.model.SongModel;
import com.av.ringtone.model.VoiceModel;
import com.example.ad.ADManager;
import com.facebook.ads.NativeAd;
import com.music.ringtonemaker.ringtone.cutter.maker.R;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 我的音乐
 */
public class SongFragment extends BaseFragment implements UserDatas.DataChangedListener {
    private RecyclerView mRecyclerView;
    private SongsAdapter mAdapter;
    private TextView mEmptyTv;

    private boolean mSortReverseByName = true;
    private boolean mSortReverseByDate = true;
    private boolean mSortReverseByTrack = true;
    private boolean mSortReverseByArtist = true;
    private boolean mSortReverseByAlbum = true;

    private int mSortType = 0;
    private boolean mIsInit = false;

    private List<SongModel> mDataLists;

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
    }

    @Override
    protected void initData() {
        mIsInit = true;
        ADManager.getInstance().getNativeAdlist(new ADManager.ADNumListener() {
            @Override
            public void onLoadedSuccess(List<NativeAd> list, boolean needGif) {
                if (list.size()>0&&null!=mAdapter) {
                    mAdapter.upateDatas(mDataLists);
                }
            }
        });
    }

    @Override
    protected void initListener() {
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && mIsInit) {
            ((MainActivity) getActivity()).stop();
            UserDatas.getInstance().resetSongs();
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
        if (mIsInit) {
            UserDatas.getInstance().resetSongs();
            ((MainActivity) getActivity()).stop();
        }
    }

    @Override
    public void updateSongs(List<SongModel> list) {
        mDataLists = list;
        mAdapter = new SongsAdapter((MainActivity) getActivity(), list);
        if (mAdapter.getDatas().size() == 0) {
            mEmptyTv.setVisibility(View.VISIBLE);
        } else {
            mEmptyTv.setVisibility(View.GONE);
            mRecyclerView.setAdapter(mAdapter);
        }
        if (mSortType == 0) {
            sortByName_fresh();
        } else if (mSortType == 1) {
            sortByDate_fresh();
        } else if (mSortType == 2) {
            sortByTrack_fresh();
        } else if (mSortType == 3) {
            sortByArtist_fresh();
        } else if (mSortType == 4) {
            sortByAlbum_fresh();
        }
    }

    @Override
    public void updateRecords(List<RecordModel> list) {

    }

    @Override
    public void updateCutters(List<CutterModel> list) {

    }

    @Override
    public void updatePlayStatus(VoiceModel model) {
        if (model.catorytype != 1) {
            return;
        }
        mAdapter.updatePlayStatus(model);
    }

    @Override
    public void resetPlayStatus(int catorytype) {
        if (catorytype != 1) {
            return;
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void sortByName(int sortType, boolean isNeedRevers) {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        if (sortType != UserDatas.SORT_SONG) {
            return;
        }
        if (mSortReverseByName) {
            Collections.sort(mDataLists, new Comparator<SongModel>() {

                /*
                 * int compare(Student o1, Student o2) 返回一个基本类型的整型， 返回负数表示：o1 小于o2， 返回0 表示：o1和o2相等， 返回正数表示：o1大于o2。
                 */
                public int compare(SongModel o1, SongModel o2) {
                    if (o1.title.compareTo(o2.title) < 0) {
                        return 1;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(mDataLists, new Comparator<SongModel>() {

                /*
                 * int compare(Student o1, Student o2) 返回一个基本类型的整型， 返回负数表示：o1 小于o2， 返回0 表示：o1和o2相等， 返回正数表示：o1大于o2。
                 */
                public int compare(SongModel o1, SongModel o2) {
                    if (o1.title.compareTo(o2.title) > 0) {
                        return 1;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(mDataLists);
        if (isNeedRevers) {
            mSortReverseByName = !mSortReverseByName;
        }
        mSortType = 0;
    }

    @Override
    public void sortByDate(int sortType, boolean isNeedRevers) {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        if (sortType != UserDatas.SORT_SONG) {
            return;
        }
        if (mSortReverseByDate) {
            Collections.sort(mDataLists, new Comparator<SongModel>() {
                public int compare(SongModel o1, SongModel o2) {
                    if (o1.date < o2.date) {
                        return 1;
                    }
                    if (o1.date == o2.date) {
                        return 0;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(mDataLists, new Comparator<SongModel>() {
                public int compare(SongModel o1, SongModel o2) {
                    // 按照学生的年龄进行倒序排列
                    if (o1.date > o2.date) {
                        return 1;
                    }
                    if (o1.date == o2.date) {
                        return 0;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(mDataLists);
        if (isNeedRevers) {
            mSortReverseByDate = !mSortReverseByDate;
        }
        mSortType = 1;
    }

    @Override
    public void sortByTrack(int sortType, boolean isNeedRevers) {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        if (sortType != UserDatas.SORT_SONG) {
            return;
        }
        if (mSortReverseByTrack) {
            Collections.sort(mDataLists, new Comparator<SongModel>() {
                public int compare(SongModel o1, SongModel o2) {
                    if (o1.trackNumber < o2.trackNumber) {
                        return 1;
                    }
                    if (o1.trackNumber == o2.trackNumber) {
                        return 0;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(mDataLists, new Comparator<SongModel>() {
                public int compare(SongModel o1, SongModel o2) {
                    // 按照学生的年龄进行倒序排列
                    if (o1.trackNumber > o2.trackNumber) {
                        return 1;
                    }
                    if (o1.trackNumber == o2.trackNumber) {
                        return 0;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(mDataLists);
        if (isNeedRevers) {
            mSortReverseByTrack = !mSortReverseByTrack;
        }
        mSortType = 2;
    }

    @Override
    public void sortByArtist(int sortType, boolean isNeedRevers) {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        if (sortType != UserDatas.SORT_SONG) {
            return;
        }
        if (mSortReverseByArtist) {
            Collections.sort(mDataLists, new Comparator<SongModel>() {

                /*
                 * int compare(Student o1, Student o2) 返回一个基本类型的整型， 返回负数表示：o1 小于o2， 返回0 表示：o1和o2相等， 返回正数表示：o1大于o2。
                 */
                public int compare(SongModel o1, SongModel o2) {
                    if (o1.artist.compareTo(o2.artist) < 0) {
                        return 1;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(mDataLists, new Comparator<SongModel>() {

                /*
                 * int compare(Student o1, Student o2) 返回一个基本类型的整型， 返回负数表示：o1 小于o2， 返回0 表示：o1和o2相等， 返回正数表示：o1大于o2。
                 */
                public int compare(SongModel o1, SongModel o2) {
                    if (o1.artist.compareTo(o2.artist) > 0) {
                        return 1;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(mDataLists);
        if (isNeedRevers) {
            mSortReverseByArtist = !mSortReverseByArtist;
        }
        mSortType = 3;
    }

    @Override
    public void sortByAlbum(int sortType, boolean isNeedRevers) {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        if (sortType != UserDatas.SORT_SONG) {
            return;
        }
        if (mSortReverseByAlbum) {
            Collections.sort(mDataLists, new Comparator<SongModel>() {

                /*
                 * int compare(Student o1, Student o2) 返回一个基本类型的整型， 返回负数表示：o1 小于o2， 返回0 表示：o1和o2相等， 返回正数表示：o1大于o2。
                 */
                public int compare(SongModel o1, SongModel o2) {
                    if (o1.albumName.compareTo(o2.albumName) < 0) {
                        return 1;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(mDataLists, new Comparator<SongModel>() {

                /*
                 * int compare(Student o1, Student o2) 返回一个基本类型的整型， 返回负数表示：o1 小于o2， 返回0 表示：o1和o2相等， 返回正数表示：o1大于o2。
                 */
                public int compare(SongModel o1, SongModel o2) {
                    if (o1.albumName.compareTo(o2.albumName) > 0) {
                        return 1;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(mDataLists);
        if (isNeedRevers) {
            mSortReverseByAlbum = !mSortReverseByAlbum;
        }
        mSortType = 4;
    }

    private void sortByName_fresh() {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        if (!mSortReverseByName) {
            Collections.sort(mDataLists, new Comparator<SongModel>() {

                /*
                 * int compare(Student o1, Student o2) 返回一个基本类型的整型， 返回负数表示：o1 小于o2， 返回0 表示：o1和o2相等， 返回正数表示：o1大于o2。
                 */
                public int compare(SongModel o1, SongModel o2) {
                    if (o1.title.compareTo(o2.title) < 0) {
                        return 1;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(mDataLists, new Comparator<SongModel>() {

                /*
                 * int compare(Student o1, Student o2) 返回一个基本类型的整型， 返回负数表示：o1 小于o2， 返回0 表示：o1和o2相等， 返回正数表示：o1大于o2。
                 */
                public int compare(SongModel o1, SongModel o2) {
                    if (o1.title.compareTo(o2.title) > 0) {
                        return 1;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(mDataLists);
        mSortType = 0;
    }

    private void sortByDate_fresh() {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        if (!mSortReverseByDate) {
            Collections.sort(mDataLists, new Comparator<SongModel>() {
                public int compare(SongModel o1, SongModel o2) {
                    if (o1.date < o2.date) {
                        return 1;
                    }
                    if (o1.date == o2.date) {
                        return 0;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(mDataLists, new Comparator<SongModel>() {
                public int compare(SongModel o1, SongModel o2) {
                    // 按照学生的年龄进行倒序排列
                    if (o1.date > o2.date) {
                        return 1;
                    }
                    if (o1.date == o2.date) {
                        return 0;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(mDataLists);
        mSortType = 1;
    }

    private void sortByTrack_fresh() {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        if (!mSortReverseByTrack) {
            Collections.sort(mDataLists, new Comparator<SongModel>() {
                public int compare(SongModel o1, SongModel o2) {
                    if (o1.trackNumber < o2.trackNumber) {
                        return 1;
                    }
                    if (o1.trackNumber == o2.trackNumber) {
                        return 0;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(mDataLists, new Comparator<SongModel>() {
                public int compare(SongModel o1, SongModel o2) {
                    if (o1.trackNumber > o2.trackNumber) {
                        return 1;
                    }
                    if (o1.trackNumber == o2.trackNumber) {
                        return 0;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(mDataLists);
        mSortType = 2;
    }

    private void sortByArtist_fresh() {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        if (!mSortReverseByArtist) {
            Collections.sort(mDataLists, new Comparator<SongModel>() {
                public int compare(SongModel o1, SongModel o2) {
                    if (o1.artist.compareTo(o2.artist) < 0) {
                        return 1;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(mDataLists, new Comparator<SongModel>() {
                public int compare(SongModel o1, SongModel o2) {
                    if (o1.artist.compareTo(o2.artist) > 0) {
                        return 1;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(mDataLists);
        mSortType = 3;
    }

    private void sortByAlbum_fresh() {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        if (!mSortReverseByAlbum) {
            Collections.sort(mDataLists, new Comparator<SongModel>() {
                public int compare(SongModel o1, SongModel o2) {
                    if (o1.albumName.compareTo(o2.albumName) < 0) {
                        return 1;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(mDataLists, new Comparator<SongModel>() {
                public int compare(SongModel o1, SongModel o2) {
                    if (o1.albumName.compareTo(o2.albumName) > 0) {
                        return 1;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(mDataLists);
        mSortType = 4;
    }
}
