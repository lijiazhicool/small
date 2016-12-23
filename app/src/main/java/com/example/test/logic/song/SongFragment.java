package com.example.test.logic.song;

import com.example.test.R;
import com.example.test.UserDatas;
import com.example.test.base.BaseActivity;
import com.example.test.base.BaseFragment;
import com.example.test.model.CutterModel;
import com.example.test.model.RecordModel;
import com.example.test.model.SongModel;
import com.example.test.utils.ToastUtils;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

/**
 * 我的音乐
 */
public class SongFragment extends BaseFragment implements UserDatas.DataChangedListener {
    private RecyclerView mRecyclerView;
    private SongsAdapter mAdapter;
    private LinearLayout mEmptyll;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_music;
    }

    @Override
    protected void initView(View parentView, Bundle savedInstanceState) {
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
}
