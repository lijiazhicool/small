package com.av.ringtone;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.av.ringtone.logic.song.SongLoader;
import com.av.ringtone.model.CutterModel;
import com.av.ringtone.model.RateModel;
import com.av.ringtone.model.RecordModel;
import com.av.ringtone.model.SongModel;
import com.av.ringtone.utils.modelcache.LightModelCache;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by LiJiaZhi on 16/12/22.
 */

public class UserDatas {

    private static volatile UserDatas sUserDatas;

    // 裁剪次数
    private int mCutCount = 0;
    // app启动次数
    private int mAppStart = 0;

    private List<SongModel> mSongs;
    private List<RecordModel> mRecords;
    private List<CutterModel> mCuttereds;

    private List<RateModel> mRateds;

    private List<DataChangedListener> mListenerList = new ArrayList<>();
    private DataCountChangedListener mCountListener;
    private GotoFragmentListener mGotoFragmentListener;
    private Context mContext;

    // songs
    private AsyncTask<String, Void, String> mTask;

    // records
    private LightModelCache mLightModelCache;
    private static final String CUT_COUNT_KEY = "cut_count_key";
    private static final String APP_START_KEY = "app_start_key";
    private static final String RECORD_S_KEY = "record_s_key";
    private static final String CUTTERED_S_KEY = "cuttered_s_key";

    private static final String RATED_S_KEY = "rated_s_key";

    public static final int SORT_SONG = 1;
    public static final int SORT_RECORD = 2;
    public static final int SORT_CUT = 3;

    // cutters

    public static UserDatas getInstance() {
        if (sUserDatas == null) {
            synchronized (UserDatas.class) {
                if (sUserDatas == null) {
                    sUserDatas = new UserDatas();
                }
            }
        }
        return sUserDatas;
    }

    public void setContext(Context context) {
        mContext = context;

        mLightModelCache = new LightModelCache(mContext, "UserData");
        mLightModelCache.setGson(new Gson());
    }

    public void loadDatas() {
        mCutCount = mLightModelCache.getIntValue(CUT_COUNT_KEY, 0);
        mAppStart = mLightModelCache.getIntValue(APP_START_KEY, 0);

        TypeToken type = new TypeToken<List<RateModel>>() {
        };
        mRateds = mLightModelCache.getModelList(RATED_S_KEY, type);

        loadMusics();
        loadReords();
        loadCutters();
    }

    public int getCutCount() {
        return mCutCount;
    }

    public void addCutCount() {
        this.mCutCount++;
        mLightModelCache.putInt(CUT_COUNT_KEY, mCutCount);

        if (null != mCountListener) {
            mCountListener.updateCutCount(mCutCount);
        }
    }

    public int getAppStart() {
        return mAppStart;
    }

    public void addAppStart() {
        this.mAppStart++;
        mLightModelCache.putInt(APP_START_KEY, mAppStart);
    }

    public void register(DataChangedListener listener) {
        mListenerList.add(listener);
        listener.updateSongs(getSongs());
        listener.updateRecords(getRecords());
        listener.updateCutters(getCuttereds());
    }

    public void unregister(DataChangedListener listener) {
        mListenerList.remove(listener);
    }

    public void register(DataCountChangedListener listener) {
        mCountListener = listener;
        mCountListener.updatecount(getSongs().size(), getRecords().size(), getCuttereds().size());
    }

    public void unregister(DataCountChangedListener listener) {
        mCountListener = null;
    }

    public void register(GotoFragmentListener listener) {
        mGotoFragmentListener = listener;
    }

    public void unregister(GotoFragmentListener listener) {
        mGotoFragmentListener = null;
    }

    public void gotoIndex(int index) {
        if (null != mGotoFragmentListener) {
            mGotoFragmentListener.gotoIndex(index);
        }
    }

    public List<RateModel> getRateds() {
        if (null == mRateds) {
            mRateds = new ArrayList<>();
        }
        return mRateds;
    }

    public void addRated(int versionCode) {
        if (null == mRateds) {
            mRateds = new ArrayList<>();
        }
        mRateds.add(new RateModel(versionCode));
        mLightModelCache.putModelList(RATED_S_KEY, mRateds);
    }

    public boolean isRated(int versionCode) {
        if (getRateds().contains(new RateModel(versionCode))) {
            return true;
        }
        return false;
    }

    public List<SongModel> getSongs() {
        if (null == mSongs) {
            mSongs = new ArrayList<>();
        }
        return mSongs;
    }

    public void setSongs(List<SongModel> mSongs) {
        this.mSongs = mSongs;
        if (null != mCountListener) {
            mCountListener.updatecount(getSongs().size(), getRecords().size(), getCuttereds().size());
        }
    }

    public List<RecordModel> getRecords() {
        if (mRecords == null) {
            mRecords = new ArrayList<>();
        }
        return mRecords;
    }

    public void setRecords(List<RecordModel> mRecords) {
        this.mRecords = mRecords;
        mLightModelCache.putModelList(RECORD_S_KEY, mRecords);
        if (null != mCountListener) {
            mCountListener.updatecount(getSongs().size(), getRecords().size(), getCuttereds().size());
        }
    }

    public void addRecord(RecordModel record) {
        getRecords().add(record);
        mLightModelCache.putModelList(RECORD_S_KEY, mRecords);
        if (null != mCountListener) {
            mCountListener.updatecount(getSongs().size(), getRecords().size(), getCuttereds().size());
        }
    }

    public List<CutterModel> getCuttereds() {
        if (null == mCuttereds) {
            mCuttereds = new ArrayList<>();
        }
        return mCuttereds;
    }

    public void setCuttereds(List<CutterModel> mCuttereds) {
        this.mCuttereds = mCuttereds;
        mLightModelCache.putModelList(CUTTERED_S_KEY, mCuttereds);
        if (null != mCountListener) {
            mCountListener.updatecount(getSongs().size(), getRecords().size(), getCuttereds().size());
        }
    }

    public void addCuttereds(CutterModel cutter) {
        getCuttereds().add(cutter);
        mLightModelCache.putModelList(CUTTERED_S_KEY, mCuttereds);
        if (null != mCountListener) {
            mCountListener.updatecount(getSongs().size(), getRecords().size(), getCuttereds().size());
        }
        for (DataChangedListener listener : mListenerList) {
            if (null != listener) {
                listener.updateCutters(getCuttereds());
            }
        }

        addCutCount();
    }

    public void loadMusics() {
        if (null != mTask && mTask.isCancelled()) {
            mTask.cancel(true);
        }
        LinkedBlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<Runnable>();
        ExecutorService exec = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, blockingQueue);
        mTask = new loadSongs().executeOnExecutor(exec);
        // mTask = new loadSongs().execute(String.valueOf(taskcount++));
    }

    private void loadReords() {
        TypeToken type = new TypeToken<List<RecordModel>>() {
        };
        mRecords = mLightModelCache.getModelList(RECORD_S_KEY, type);
        for (DataChangedListener listener : mListenerList) {
            if (null != listener) {
                listener.updateRecords(mRecords);
            }
        }
        if (null != mCountListener) {
            mCountListener.updatecount(getSongs().size(), getRecords().size(), getCuttereds().size());
        }
    }

    private void loadCutters() {
        TypeToken type = new TypeToken<List<CutterModel>>() {
        };
        mCuttereds = mLightModelCache.getModelList(CUTTERED_S_KEY, type);
        for (DataChangedListener listener : mListenerList) {
            if (null != listener) {
                listener.updateCutters(mCuttereds);
            }
        }
        if (null != mCountListener) {
            mCountListener.updatecount(getSongs().size(), getRecords().size(), getCuttereds().size());
        }
    }

    public void updateCutters() {
        for (DataChangedListener listener : mListenerList) {
            if (null != listener) {
                listener.updateCutters();
            }
        }
        mLightModelCache.putModelList(CUTTERED_S_KEY, mCuttereds);
    }

    public void sortByName(int sortType) {
        for (DataChangedListener listener : mListenerList) {
            if (null != listener) {
                listener.sortByName(sortType, true);
            }
        }
    }

    public void sortByLength(int sortType) {
        for (DataChangedListener listener : mListenerList) {
            if (null != listener) {
                listener.sortByLength(sortType, true);
            }
        }
    }

    public void sortByDate(int sortType) {
        for (DataChangedListener listener : mListenerList) {
            if (null != listener) {
                listener.sortByDate(sortType, true);
            }
        }
    }

    private class loadSongs extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String...params) {
            mSongs = SongLoader.getAllSongs(mContext);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            for (DataChangedListener listener : mListenerList) {
                if (null != listener) {
                    listener.updateSongs(mSongs);
                }
            }
            if (null != mCountListener) {
                mCountListener.updatecount(getSongs().size(), getRecords().size(), getCuttereds().size());
            }
        }

        @Override
        protected void onPreExecute() {
        }
    }

    public interface DataChangedListener {
        void updateSongs(List<SongModel> list);

        void updateRecords(List<RecordModel> list);

        void updateCutters(List<CutterModel> list);

        void updateCutters();

        void sortByName(int sortType, boolean isNeedRevers);

        void sortByLength(int sortType, boolean isNeedRevers);

        void sortByDate(int sortType, boolean isNeedRevers);
    }

    public interface DataCountChangedListener {
        void updatecount(int isong, int irecord, int icutter);

        void updateCutCount(int count);
    }

    public interface GotoFragmentListener {
        void gotoIndex(int index);
    }

}
