package com.av.ringtone.logic.ringtone;

import com.av.ringtone.R;
import com.av.ringtone.UserDatas;
import com.av.ringtone.base.BaseFragment;
import com.av.ringtone.logic.MainActivity;
import com.av.ringtone.model.CutterModel;
import com.av.ringtone.model.RecordModel;
import com.av.ringtone.model.SongModel;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

/**
 * cuttered
 */
public class CutteredFragment extends BaseFragment implements UserDatas.DataChangedListener{
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
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void updateSongs(List<SongModel> list) {

    }

    @Override
    public void updateRecords(List<RecordModel> list) {

    }

    @Override
    public void updateCutters(List<CutterModel> list) {
        mAdapter = new CuttersAdapter((MainActivity) getActivity(), list);
        if (mAdapter.getDatas().size() == 0) {
            mEmptyll.setVisibility(View.VISIBLE);
        } else {
            mEmptyll.setVisibility(View.GONE);
            mRecyclerView.setAdapter(mAdapter);
        }
    }


}
