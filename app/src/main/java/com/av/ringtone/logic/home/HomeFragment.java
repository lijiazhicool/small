package com.av.ringtone.logic.home;

import com.av.ringtone.R;
import com.av.ringtone.UserDatas;
import com.av.ringtone.base.BaseFragment;
import com.av.ringtone.model.HomeModel;
import com.av.ringtone.utils.ShareUtils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends BaseFragment implements UserDatas.DataCountChangedListener {

    private GridView mGridView;
    private List<HomeModel> mDatas = new ArrayList<>();
    private onHomeListener mListener;
    private MyAdapter mAdapter;

    private LinearLayout mSharell;
    private TextView mSharetv;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View parentView, Bundle savedInstanceState) {
        mGridView = findViewById(R.id.gv);
        mSharell = findViewById(R.id.share_ll);
        mSharetv = findViewById(R.id.cutcount_tv);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (onHomeListener) activity;
    }

    @Override
    protected void initData() {
        mDatas.clear();
        mDatas.add(new HomeModel(1, R.drawable.ic_music, getString(R.string.tab_two),
            String.format(getString(R.string.home_music_count), 0)));
        mDatas.add(new HomeModel(2, R.drawable.ic_record, getString(R.string.tab_three),
            String.format(getString(R.string.home_record_count), 0)));
        mDatas.add(new HomeModel(3, R.drawable.ic_tones, getString(R.string.tab_four),
            String.format(getString(R.string.home_cuttered_count), 0)));
        mDatas.add(new HomeModel(4, R.drawable.ic_tones, "广告",
                "广告"));
        mAdapter = new MyAdapter(getActivity());
        mGridView.setAdapter(mAdapter);
        loadAds();
    }

    @Override
    public void onStart() {
        super.onStart();
        UserDatas.getInstance().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        UserDatas.getInstance().unregister(this);
    }

    @Override
    protected void initListener() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 3 && mListener != null) {
                    mListener.gotoIndex(position + 1);
                }
            }
        });
        mSharell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.shareText(mActivity);
            }
        });
    }

    private void loadAds() {
        // add data
        // TODO: 16/12/19
    }

    @Override
    public void updatecount(int isong, int irecord, int icutter) {
        if (null==mDatas || mDatas.size()==0){
            return;
        }
        mDatas.get(0).subtitle = String.format(getString(R.string.home_music_count), isong);
        mDatas.get(1).subtitle = String.format(getString(R.string.home_record_count), irecord);
        mDatas.get(2).subtitle = String.format(getString(R.string.home_cuttered_count), icutter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateCutCount(int count) {
        if (count > 3) {
            mSharell.setVisibility(View.VISIBLE);
        } else {
            mSharell.setVisibility(View.GONE);
        }
        mSharetv.setText(String.format(getString(R.string.home_cut_count), count));
    }

    // 自定义适配器
    class MyAdapter extends BaseAdapter {
        // 上下文对象
        private Context context;

        MyAdapter(Context context) {
            this.context = context;
        }

        public int getCount() {
            return mDatas.size();
        }

        public Object getItem(int item) {
            return item;
        }

        public long getItemId(int id) {
            return id;
        }

        // 创建View方法
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_home, null);
            CircleImageView image = (CircleImageView) convertView.findViewById(R.id.image);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView subtitle = (TextView) convertView.findViewById(R.id.subtitle);
            image.setImageResource(mDatas.get(position).resId);
            title.setText(mDatas.get(position).title);
            subtitle.setText(mDatas.get(position).subtitle);
            return convertView;
        }
    }

    public interface onHomeListener {
        void gotoIndex(int index);
    }
}
