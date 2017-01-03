package com.av.ringtone.logic.record;

import static android.app.Activity.RESULT_OK;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
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
import com.av.ringtone.utils.ToastUtils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 录音
 */
public class RecordFragment extends BaseFragment implements UserDatas.DataChangedListener {
    private static final int RECORD_AUDIO = 1001;
    private RecyclerView mRecyclerView;
    private RecordsAdapter mAdapter;
    private LinearLayout mEmptyll;
    private ImageView mFab;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_record;
    }

    @Override
    protected void initView(View parentView, Bundle savedInstanceState) {
        mEmptyll = findViewById(R.id.emptyll);
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
    public void onDestroy() {
        super.onDestroy();
        UserDatas.getInstance().unregister(this);
    }

    @Override
    protected void initListener() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                startActivityForResult(intent, RECORD_AUDIO);
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
                        String fileName = getFileName(filePath);
                        int duration = 0;
                        try {
                            duration = getAmrDuration(new File(filePath));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        UserDatas.getInstance().addRecord(new RecordModel(fileName, filePath, duration));
                        mAdapter.setDatas(UserDatas.getInstance().getRecords());
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.makeToastAndShow(mActivity, "Recording error");
                    }
                    break;
            }
        }
    }
    public String getFileName(String pathandname){

        int start=pathandname.lastIndexOf("/");
        int end=pathandname.lastIndexOf(".");
        if(start!=-1 && end!=-1){
            return pathandname.substring(start+1,end);
        }else{
            return null;
        }

    }
    /**
     * 得到amr的时长
     *
     * @param file
     * @return amr文件时间长度
     * @throws IOException
     */
    public int getAmrDuration(File file) throws IOException {
        long duration = -1;
        int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0 };
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            long length = file.length();// 文件的长度
            int pos = 6;// 设置初始位置
            int frameCount = 0;// 初始帧数
            int packedPos = -1;

            byte[] datas = new byte[1];// 初始数据值
            while (pos <= length) {
                randomAccessFile.seek(pos);
                if (randomAccessFile.read(datas, 0, 1) != 1) {
                    duration = length > 0 ? ((length - 6) / 650) : 0;
                    break;
                }
                packedPos = (datas[0] >> 3) & 0x0F;
                pos += packedSize[packedPos] + 1;
                frameCount++;
            }

            duration += frameCount * 20;// 帧数*20
        } finally {
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        }
        return (int) ((duration / 1000) + 1);
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
        Collections.sort(list, new Comparator<RecordModel>(){

            /*
             * int compare(Student o1, Student o2) 返回一个基本类型的整型，
             * 返回负数表示：o1 小于o2，
             * 返回0 表示：o1和o2相等，
             * 返回正数表示：o1大于o2。
             */
            public int compare(RecordModel o1, RecordModel o2) {

                //按照学生的年龄进行升序排列
                if(o1.title.compareTo(o2.title)>0){
                    return 1;
                }
                return -1;
            }
        });
        mAdapter.upateDatas(list);
    }

    @Override
    public void sortByLength(int sortType) {
        if (sortType!= UserDatas.SORT_RECORD){
            return;
        }
        List<RecordModel> list = mAdapter.getDatas();
        Collections.sort(list, new Comparator<RecordModel>(){
            public int compare(RecordModel o1, RecordModel o2) {

                //按照学生的年龄进行升序排列
                if(o1.duration > o2.duration){
                    return 1;
                }
                if(o1.duration == o2.duration){
                    return 0;
                }
                return -1;
            }
        });
        mAdapter.upateDatas(list);
    }

    @Override
    public void sortByDate(int sortType) {
        if (sortType!= UserDatas.SORT_RECORD){
            return;
        }
        List<RecordModel> list = mAdapter.getDatas();
        Collections.sort(list, new Comparator<RecordModel>(){
            public int compare(RecordModel o1, RecordModel o2) {

                //按照学生的年龄进行升序排列
                if(o1.date > o2.date){
                    return 1;
                }
                if(o1.date == o2.date){
                    return 0;
                }
                return -1;
            }
        });
        mAdapter.upateDatas(list);
    }
}
