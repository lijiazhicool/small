package com.example.test.logic.ringtone;

import com.example.test.R;
import com.example.test.UserDatas;
import com.example.test.base.BaseActivity;
import com.example.test.base.BaseFragment;
import com.example.test.logic.song.SongsAdapter;
import com.example.test.model.CutterModel;
import com.example.test.model.RecordModel;
import com.example.test.model.SongModel;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

/**
 * cuttered
 */
public class CutteredFragment extends BaseFragment implements UserDatas.DataChangedListener {
    private RecyclerView mRecyclerView;
    private CuttersAdapter mAdapter;
    private LinearLayout mEmptyll;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cuttered;
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

    }

    @Override
    public void updateRecords(List<RecordModel> list) {

    }

    @Override
    public void updateCutters(List<CutterModel> list) {
        mAdapter = new CuttersAdapter((BaseActivity) getActivity(), list);
        if (mAdapter.getDatas().size() == 0) {
            mEmptyll.setVisibility(View.VISIBLE);
        } else {
            mEmptyll.setVisibility(View.GONE);
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}
