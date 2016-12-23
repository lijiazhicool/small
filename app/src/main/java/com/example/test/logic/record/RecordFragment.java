package com.example.test.logic.record;

import static android.app.Activity.RESULT_OK;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.example.test.R;
import com.example.test.UserDatas;
import com.example.test.base.BaseActivity;
import com.example.test.base.BaseFragment;
import com.example.test.model.CutterModel;
import com.example.test.model.RecordModel;
import com.example.test.model.SongModel;
import com.example.test.utils.ToastUtils;
import com.example.test.views.FloatingActionButton;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 录音
 */
public class RecordFragment extends BaseFragment implements UserDatas.DataChangedListener {
    private static final int RECORD_AUDIO = 1001;
    private RecyclerView mRecyclerView;
    private RecordsAdapter mAdapter;
    private LinearLayout mEmptyll;
    private FloatingActionButton mButton;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_record;
    }

    @Override
    protected void initView(View parentView, Bundle savedInstanceState) {
        mEmptyll = findViewById(R.id.emptyll);
        mRecyclerView = findViewById(R.id.recyclerView);
        mButton = findViewById(R.id.button);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void initData() {
        // TODO: 16/12/22 6.0权限
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
        mButton.setOnClickListener(new View.OnClickListener() {
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

                        Cursor cursor = mActivity.getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();
                        String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
                        String title = cursor.getString(cursor.getColumnIndex("title"));
                        int duration = 0;
                        try {
                            duration = getAmrDuration(new File(filePath));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        UserDatas.getInstance().addRecord(new RecordModel(title, filePath, duration));
                        mAdapter.setDatas(UserDatas.getInstance().getRecords());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
            }
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
        int[] packedSize = {12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0};
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
}
