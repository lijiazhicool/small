package com.av.ringtone.logic.home;

import com.av.ringtone.Constants;
import com.av.ringtone.R;
import com.av.ringtone.UserDatas;
import com.av.ringtone.base.BaseFragment;
import com.av.ringtone.model.HomeModel;
import com.av.ringtone.utils.ShareUtils;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.google.firebase.analytics.FirebaseAnalytics;

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
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends BaseFragment implements UserDatas.DataCountChangedListener {

    private GridView mGridView;
    private MediaView mBigAdIv;
    private List<HomeModel> mDatas = new ArrayList<>();
    private MyAdapter mAdapter;

    private LinearLayout mSharell, mInfoll;
    private TextView mSharetv;

    private NativeAd mSmallNativeAd;
    private NativeAd mBigNativeAd;

    private final static String EVENT_Big_AD_TYPE = "Home_Big_NativeAd_Click";
    private final static String EVENT_Big_AD_NAME = "Home_Big_NativeAd";
    private final static String EVENT_Big_AD_ID = "Home_Big_NativeAd_ID";
    private final static String EVENT_Small_AD_TYPE = "Home_Small_NativeAd_Click";
    private final static String EVENT_Small_AD_NAME = "Home_Small_NativeAd";
    private final static String EVENT_Small_AD_ID = "Home_Small_NativeAd_ID";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View parentView, Bundle savedInstanceState) {
        mGridView = findViewById(R.id.gv);
        mSharell = findViewById(R.id.share_ll);
        mInfoll = findViewById(R.id.info_ll);
        mSharetv = findViewById(R.id.cutcount_tv);
        mBigAdIv = findViewById(R.id.home_ad_iv);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    protected void initData() {
        mDatas.clear();
        mDatas.add(new HomeModel(1, R.drawable.ic_music, getString(R.string.tab_two),
            String.format(getString(R.string.home_music_count), 0)));
        mDatas.add(new HomeModel(2, R.drawable.ic_tones, getString(R.string.tab_four),
            String.format(getString(R.string.home_cuttered_count), 0)));
        mDatas.add(new HomeModel(3, R.drawable.icon_record, getString(R.string.tab_three),
                String.format(getString(R.string.home_record_count), 0)));
        mAdapter = new MyAdapter(getActivity());
        mGridView.setAdapter(mAdapter);
        loadAds();

        if (UserDatas.getInstance().getCutCount() < 3) {
            mSharell.setVisibility(View.GONE);
            mBigAdIv.setVisibility(View.GONE);
            mInfoll.setVisibility(View.VISIBLE);
        } else {
            randomShow();
        }
    }

    private void randomShow(){
        //1-3
        int index = new Random().nextInt(3)+1;
        if (index%3 ==0){
            mSharell.setVisibility(View.VISIBLE);
            mSharetv.setText(String.format(getString(R.string.home_cut_count), UserDatas.getInstance().getCutCount()));
            mBigAdIv.setVisibility(View.GONE);
            mInfoll.setVisibility(View.GONE);
        } else if (index%3 ==1){
            mSharell.setVisibility(View.GONE);
            mBigAdIv.setVisibility(View.VISIBLE);
            mInfoll.setVisibility(View.GONE);
        } else if (index%3 ==2){
            mSharell.setVisibility(View.GONE);
            mBigAdIv.setVisibility(View.GONE);
            mInfoll.setVisibility(View.VISIBLE);
        }
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
                if (position < 3) {
                    if (position==0){
                        UserDatas.getInstance().gotoIndex(1);
                    } else if (position==1){
                        UserDatas.getInstance().gotoIndex(3);
                    } else {
                        UserDatas.getInstance().gotoIndex(2);
                    }
                }
            }
        });
        mSharell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.shareHomeText(mActivity);
            }
        });
    }

    private void loadAds() {
        // add data
        showSmallNativeAd();
        showBigNativeAd();
    }

    @Override
    public void updatecount(int isong, int irecord, int icutter) {
        if (null == mDatas || mDatas.size() == 0) {
            return;
        }
        mDatas.get(0).subtitle = String.format(getString(R.string.home_music_count), isong);
        mDatas.get(1).subtitle = String.format(getString(R.string.home_cuttered_count), icutter);
        mDatas.get(2).subtitle = String.format(getString(R.string.home_record_count), irecord);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateCutCount(int count) {
        if (count >= 3) {
            randomShow();
        }
    }

    private void showSmallNativeAd() {
        mSmallNativeAd = new NativeAd(mActivity, Constants.AD_PLACE_HOME_SMALL);
        mSmallNativeAd.setAdListener(new AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                // Ad error callback
                System.err.println("onError " + error.getErrorCode() + " " + error.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                mDatas.add(new HomeModel(4, mSmallNativeAd));
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAdClicked(Ad ad) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, EVENT_Small_AD_ID);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, EVENT_Small_AD_NAME);
                mFirebaseAnalytics.logEvent(EVENT_Small_AD_TYPE, bundle);
            }
        });
        // Request an ad
        mSmallNativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);
    }

    private void showBigNativeAd() {
        mBigNativeAd = new NativeAd(mActivity, Constants.AD_PLACE_HOME_BIG);
//        AdSettings.addTestDevice("6707cd54fb24a306ba41dfacb8af2d8d");
        mBigNativeAd.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError error) {
                // Ad error callback
                System.err.println("onError " + error.getErrorCode() + " " + error.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Download and display the cover image.
                mBigAdIv.setNativeAd(mBigNativeAd);
                // Register the Title and CTA button to listen for clicks.
                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(mBigAdIv);
                mBigNativeAd.registerViewForInteraction(mBigAdIv, clickableViews);
            }

            @Override
            public void onAdClicked(Ad ad) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, EVENT_Big_AD_ID);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, EVENT_Big_AD_NAME);
                mFirebaseAnalytics.logEvent(EVENT_Big_AD_TYPE, bundle);
            }
        });
        // Request an ad
        mBigNativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);
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

            HomeModel local = mDatas.get(position);
            if (local.type == 4) {
                if (null != local.ad) {
                    // Download and display the ad icon.
                    NativeAd.Image adIcon = local.ad.getAdIcon();
                    NativeAd.downloadAndDisplayImage(adIcon, image);
                    title.setText(local.ad.getAdTitle());
                    subtitle.setText(local.ad.getAdSubtitle());

                    // Register the Title and CTA button to listen for clicks.
                    List<View> clickableViews = new ArrayList<>();
                    clickableViews.add(image);
                    clickableViews.add(title);
                    clickableViews.add(subtitle);
                    mSmallNativeAd.registerViewForInteraction(convertView, clickableViews);
                }
            } else {
                image.setImageResource(mDatas.get(position).resId);
                title.setText(mDatas.get(position).title);
                subtitle.setText(mDatas.get(position).subtitle);
            }

            return convertView;
        }
    }
}
