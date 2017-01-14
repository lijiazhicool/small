package com.av.ringtone.logic.record;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.av.ringtone.R;
import com.av.ringtone.UserDatas;
import com.av.ringtone.base.BaseActivity;
import com.av.ringtone.base.BaseFragment;
import com.av.ringtone.model.CutterModel;
import com.av.ringtone.model.RecordModel;
import com.av.ringtone.model.SongModel;
import com.av.ringtone.utils.FileUtils;
import com.av.ringtone.utils.NavigationUtils;
import com.av.ringtone.utils.ToastUtils;
import com.av.ringtone.views.RecordingDialog;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.app.Activity.RESULT_OK;

/**
 * 录音
 */
public class RecordFragment extends BaseFragment implements UserDatas.DataChangedListener {
    private static final int RECORD_AUDIO = 1001;
    private RecyclerView mRecyclerView;
    private RecordsAdapter mAdapter;
    private LinearLayout mEmptyll;
    private ImageView mFab;

    private final static int FLAG_WAV = 0;
    private final static int FLAG_AMR = 1;
    private int mState = -1; // -1:没再录制，0：录制wav，1：录制amr

    private UIHandler uiHandler = new UIHandler();
    private UIThread uiThread;

    private TextView mTimerTextView;
    private Dialog mDialog;

    private String mRecordFilePath = "";
    private int mRecordingTime = 0;

    private boolean mSortReverseByName = true;
    private boolean mSortReverseByLength = true;
    private boolean mSortReverseByDate = true;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_record;
    }

    @Override
    protected void initView(View parentView, Bundle savedInstanceState) {
        mEmptyll = findViewById(R.id.empty_tv);
        mRecyclerView = findViewById(R.id.recyclerView);
        mFab = findViewById(R.id.fab);
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
    public void onDestroy() {
        super.onDestroy();

        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    protected void initListener() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // andoird 7.0 不能调用系统录音
                if (Build.VERSION.SDK_INT >= 24) {
                    mDialog = new RecordingDialog(mActivity, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            stop();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            stop();
                            String fileName = FileUtils.getFileName(mRecordFilePath);
                            RecordModel tempModel = new RecordModel(fileName, mRecordFilePath, mRecordingTime / 1000, new File(mRecordFilePath).lastModified());
                            UserDatas.getInstance().addRecord(tempModel);
                            mAdapter.setDatas(UserDatas.getInstance().getRecords());

                            NavigationUtils.goToCutter(mActivity, tempModel);
                        }
                    });
                    mDialog.setCancelable(false);
                    mDialog.show();
                    mTimerTextView = (TextView) mDialog.findViewById(R.id.record_audio_timer);
                    record(FLAG_AMR);
                } else {
                    Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    startActivityForResult(intent, RECORD_AUDIO);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RECORD_AUDIO:
                    try {
                        Uri uri = data.getData();
                        String filePath = uri.getPath();
                        String fileName = FileUtils.getFileName(filePath);
                        int duration = 0;
                        try {
                            duration = FileUtils.getAmrDuration(new File(filePath));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        RecordModel tempModel = new RecordModel(fileName, filePath, duration, new File(filePath).lastModified());
                        UserDatas.getInstance().addRecord(tempModel);
                        mAdapter.setDatas(UserDatas.getInstance().getRecords());
                        NavigationUtils.goToCutter(mActivity, tempModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public void updateSongs(List<SongModel> list) {

    }

    @Override
    public void updateRecords(List<RecordModel> list) {
        if (list == null || list.size() == 0) {
            mEmptyll.setVisibility(View.VISIBLE);
        } else {
            mEmptyll.setVisibility(View.GONE);
        }
        mAdapter = new RecordsAdapter((BaseActivity) getActivity(), list);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void updateCutters(List<CutterModel> list) {

    }

    @Override
    public void updateCutters() {

    }

    @Override
    public void sortByName(int sortType) {
        if (sortType!= UserDatas.SORT_RECORD){
            return;
        }
        List<RecordModel> list = mAdapter.getDatas();
        if (mSortReverseByName){
            Collections.sort(list, new Comparator<RecordModel>(){

                /*
                 * int compare(Student o1, Student o2) 返回一个基本类型的整型，
                 * 返回负数表示：o1 小于o2，
                 * 返回0 表示：o1和o2相等，
                 * 返回正数表示：o1大于o2。
                 */
                public int compare(RecordModel o1, RecordModel o2) {
                    if(o1.title.compareTo(o2.title)<0){
                        return 1;
                    }
                    return -1;
                }
            });
        } else {
            Collections.sort(list, new Comparator<RecordModel>(){

                /*
                 * int compare(Student o1, Student o2) 返回一个基本类型的整型，
                 * 返回负数表示：o1 小于o2，
                 * 返回0 表示：o1和o2相等，
                 * 返回正数表示：o1大于o2。
                 */
                public int compare(RecordModel o1, RecordModel o2) {
                    if(o1.title.compareTo(o2.title)>0){
                        return 1;
                    }
                    return -1;
                }
            });
        }
        mAdapter.upateDatas(list);

        mSortReverseByName = !mSortReverseByName;
    }

    @Override
    public void sortByLength(int sortType) {
        if (sortType!= UserDatas.SORT_RECORD){
            return;
        }
        List<RecordModel> list = mAdapter.getDatas();

        if (mSortReverseByLength){
            Collections.sort(list, new Comparator<RecordModel>(){
                public int compare(RecordModel o1, RecordModel o2) {
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
            Collections.sort(list, new Comparator<RecordModel>(){
                public int compare(RecordModel o1, RecordModel o2) {
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
        mSortReverseByLength = !mSortReverseByLength;
    }

    @Override
    public void sortByDate(int sortType) {
        if (sortType!= UserDatas.SORT_RECORD){
            return;
        }
        List<RecordModel> list = mAdapter.getDatas();
        if (mSortReverseByDate){
            Collections.sort(list, new Comparator<RecordModel>(){
                public int compare(RecordModel o1, RecordModel o2) {
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
            Collections.sort(list, new Comparator<RecordModel>(){
                public int compare(RecordModel o1, RecordModel o2) {
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
        mSortReverseByDate = !mSortReverseByDate;
    }

    /**
     * 开始录音
     *
     * @param mFlag，0：录制wav格式，1：录音amr格式
     */
    private void record(int mFlag) {
        mRecordFilePath = AudioFileFunc.getAMRFilePath(mActivity);
        if (mState != -1) {
            Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putInt("cmd", CMD_RECORDFAIL);
            b.putInt("msg", ErrorCode.E_STATE_RECODING);
            msg.setData(b);

            uiHandler.sendMessage(msg); // 向Handler发送消息,更新UI
            return;
        }
        int mResult = -1;
        switch (mFlag) {
            case FLAG_WAV:
                AudioRecordFunc mRecord_1 = AudioRecordFunc.getInstance();
                mResult = mRecord_1.startRecordAndFile();
                break;
            case FLAG_AMR:
                MediaRecordFunc mRecord_2 = MediaRecordFunc.getInstance();
                mResult = mRecord_2.startRecordAndFile(mRecordFilePath);
                break;
        }
        if (mResult == ErrorCode.SUCCESS) {
            uiThread = new UIThread();
            new Thread(uiThread).start();
            mState = mFlag;
        } else {
            Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putInt("cmd", CMD_RECORDFAIL);
            b.putInt("msg", mResult);
            msg.setData(b);

            uiHandler.sendMessage(msg); // 向Handler发送消息,更新UI
        }
    }

    /**
     * 停止录音
     */
    private void stop() {
        if (mState != -1) {
            switch (mState) {
                case FLAG_WAV:
                    AudioRecordFunc mRecord_1 = AudioRecordFunc.getInstance();
                    mRecord_1.stopRecordAndFile();
                    break;
                case FLAG_AMR:
                    MediaRecordFunc mRecord_2 = MediaRecordFunc.getInstance();
                    mRecord_2.stopRecordAndFile();
                    break;
            }
            if (uiThread != null) {
                uiThread.stopThread();
            }
            if (uiHandler != null)
                uiHandler.removeCallbacks(uiThread);
            Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putInt("cmd", CMD_STOP);
            b.putInt("msg", mState);
            msg.setData(b);
            uiHandler.sendMessageDelayed(msg, 1000); // 向Handler发送消息,更新UI
            mState = -1;
        }
    }

    private final static int CMD_RECORDING_TIME = 2000;
    private final static int CMD_RECORDFAIL = 2001;
    private final static int CMD_STOP = 2002;

    class UIHandler extends Handler {
        public UIHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle b = msg.getData();
            int vCmd = b.getInt("cmd");
            switch (vCmd) {
                case CMD_RECORDING_TIME:
                    int vTime = b.getInt("msg");
                    mRecordingTime = vTime;
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int min = (int) (mRecordingTime / 60 / 1000);
                            float sec = (float) (mRecordingTime - 60 * min * 1000) / 1000;
                            mTimerTextView.setText(String.format("%d:%05.2f", min, sec));
                            // int min = mRecordingTime / 60;
                            // int sec = mRecordingTime - 60 * min;
                            // mTimerTextView.setText(String.format("%02d:%02d", min, sec));
                        }
                    });
                    // Log.e("TAG","正在录音中，已录制：" + vTime + " s");
                    break;
                case CMD_RECORDFAIL:
                    int vErrorCode = b.getInt("msg");
                    String vMsg = ErrorCode.getErrorInfo(mActivity, vErrorCode);
                    // Log.e("TAG","录音失败：" + vMsg);
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    ToastUtils.makeToastAndShow(mActivity, "Record error " + vMsg);
                    break;
                case CMD_STOP:
                    // int vFileType = b.getInt("msg");
                    // switch (vFileType) {
                    // case FLAG_WAV:
                    // AudioRecordFunc mRecord_1 = AudioRecordFunc.getInstance();
                    // long mSize = mRecord_1.getRecordFileSize();
                    // Log.e("TAG","录音已停止.录音文件:" + AudioFileFunc.getWavFilePath() + "\n文件大小：" + mSize);
                    // break;
                    // case FLAG_AMR:
                    // MediaRecordFunc mRecord_2 = MediaRecordFunc.getInstance();
                    // mSize = mRecord_2.getRecordFileSize();
                    // Log.e("TAG","录音已停止.录音文件:" + AudioFileFunc.getAMRFilePath() + "\n文件大小：" + mSize);
                    // break;
                    // }
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    class UIThread implements Runnable {
        int mTimeMill = 0;
        boolean vRun = true;

        public void stopThread() {
            vRun = false;
        }

        public void run() {
            while (vRun) {
                try {
                    Thread.sleep(86);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mTimeMill++;
                Message msg = new Message();
                Bundle b = new Bundle();// 存放数据
                b.putInt("cmd", CMD_RECORDING_TIME);
                b.putInt("msg", mTimeMill * 86);
                msg.setData(b);

                uiHandler.sendMessage(msg); // 向Handler发送消息,更新UI
            }

        }
    }
}
