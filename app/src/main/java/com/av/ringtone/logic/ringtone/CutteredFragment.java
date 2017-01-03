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

import java.util.Collections;
import java.util.Comparator;
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
    public void updateCutters() {
        mAdapter.notifyDataSetChanged();
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
    @Override
    public void sortByName(int sortType) {
        if (sortType!= UserDatas.SORT_CUT){
            return;
        }
        List<CutterModel> list = mAdapter.getDatas();
        Collections.sort(list, new Comparator<CutterModel>(){

            /*
             * int compare(Student o1, Student o2) 返回一个基本类型的整型，
             * 返回负数表示：o1 小于o2，
             * 返回0 表示：o1和o2相等，
             * 返回正数表示：o1大于o2。
             */
            public int compare(CutterModel o1, CutterModel o2) {

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
        if (sortType!= UserDatas.SORT_CUT){
            return;
        }
        List<CutterModel> list = mAdapter.getDatas();
        Collections.sort(list, new Comparator<CutterModel>(){
            public int compare(CutterModel o1, CutterModel o2) {

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
        if (sortType!= UserDatas.SORT_CUT){
            return;
        }
        List<CutterModel> list = mAdapter.getDatas();
        Collections.sort(list, new Comparator<CutterModel>(){
            public int compare(CutterModel o1, CutterModel o2) {

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
