package com.example.test;

import java.util.ArrayList;
import java.util.List;

import com.example.test.logic.song.SongLoader;
import com.example.test.model.CutterModel;
import com.example.test.model.RecordModel;
import com.example.test.model.SongModel;
import com.example.test.utils.modelcache.LightModelCache;
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

    private List<SongModel> mSongs;
    private List<RecordModel> mRecords;
    private List<CutterModel> mCuttereds;

    private List<DataChangedListener> mListenerList = new ArrayList<>();
    private DataCountChangedListener mCountListener;
    private Context mContext;

    // songs
    private AsyncTask<String, Void, String> mTask;

    // records
    private LightModelCache mLightModelCache;
    private static final String CUT_COUNT_KEY = "cut_count_key";
    private static final String RECORD_S_KEY = "record_s_key";
    private static final String CUTTERED_S_KEY = "cuttered_s_key";

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

        addCutCount();
    }

    private void loadMusics() {
        if (null != mTask && mTask.isCancelled()) {
            mTask.cancel(true);
        }
        mTask = new loadSongs().execute("");
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
    }

    public interface DataCountChangedListener {
        void updatecount(int isong, int irecord, int icutter);
        void updateCutCount(int count);
    }

}
