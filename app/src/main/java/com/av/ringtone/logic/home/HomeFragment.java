package com.av.ringtone.logic.home;

import com.av.ringtone.ADManager;
import com.av.ringtone.Constants;
import com.av.ringtone.R;
import com.av.ringtone.UserDatas;
import com.av.ringtone.base.BaseFragment;
import com.av.ringtone.model.HomeModel;
import com.av.ringtone.utils.ShareUtils;
import com.av.ringtone.views.Rotatable;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends BaseFragment implements UserDatas.DataCountChangedListener {

    private GridView mGridView;
    private LinearLayout mNativeAdContainer;
    private List<HomeModel> mDatas = new ArrayList<>();
    private MyAdapter mAdapter;

    private LinearLayout mSavedSharell;
    private TextView mSavedSharetv;

    private LinearLayout mMusicSharell;
    private TextView mMusicSharetv;




    private NativeAd mSmallNativeAd;

    private Animation mTranstionAnim;

    private boolean isVisibleToUser = false;//当前界面是否可见

    private final static String EVENT_Small_AD_TYPE = "Home_Small_NativeAd_Click";
    private final static String EVENT_Small_AD_NAME = "Home_Small_NativeAd";
    private final static String EVENT_Small_AD_ID = "Home_Small_NativeAd_ID";

    private boolean mIsInit = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View parentView, Bundle savedInstanceState) {
        mGridView = findViewById(R.id.gv);
        mSavedSharell = findViewById(R.id.share_ll);
        mSavedSharetv = findViewById(R.id.cutcount_tv);
        mNativeAdContainer = findViewById(R.id.home_ad_ll);

        mMusicSharell = findViewById(R.id.share_music_ll);
        mMusicSharetv = findViewById(R.id.musiccount_tv);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    protected void initData() {
        mTranstionAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_bottom_in);
        mDatas.clear();
        mDatas.add(new HomeModel(1, R.drawable.ic_music, getString(R.string.tab_two),
            String.format(getString(R.string.home_music_count), 0)));
        mDatas.add(new HomeModel(2, R.drawable.ic_tones, getString(R.string.tab_three),
            String.format(getString(R.string.home_cuttered_count), 0)));
        mDatas.add(new HomeModel(3, R.drawable.icon_record, getString(R.string.tab_four),
            String.format(getString(R.string.home_record_count), 0)));
        mAdapter = new MyAdapter(getActivity());
        mGridView.setAdapter(mAdapter);
        loadAds();

        randomShow();
        handler.postDelayed(runnable, TIME); //每隔1s执行
        mIsInit = true;
    }

    private void randomShow() {
        if (UserDatas.getInstance().getCutCount() ==0){
            mSavedSharell.setVisibility(View.GONE);
            mMusicSharell.setVisibility(View.GONE);
            showBigNativeAd();
            return;
        }
        // 1-3
        int index = new Random().nextInt(3) + 1;
        if (index % 3 == 0) {
            mSavedSharell.setVisibility(View.VISIBLE);
            mSavedSharetv.setText(String.format(getString(R.string.home_cut_count), UserDatas.getInstance().getCutCount()));
            mNativeAdContainer.setVisibility(View.GONE);
            mMusicSharell.setVisibility(View.GONE);
            mSavedSharell.startAnimation(mTranstionAnim);
        } else if (index % 3 == 1) {
            mSavedSharell.setVisibility(View.GONE);
            showBigNativeAd();
            mMusicSharell.setVisibility(View.GONE);
        } else if (index % 3 == 2) {
            mSavedSharell.setVisibility(View.GONE);
            mNativeAdContainer.setVisibility(View.GONE);
            mMusicSharell.setVisibility(View.VISIBLE);
            mMusicSharetv.setText(String.format(getString(R.string.home_music_count_share), UserDatas.getInstance().getSongs().size()));
            mMusicSharell.startAnimation(mTranstionAnim);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            if (UserDatas.getInstance().getCutCount() >= 3 && mIsInit) {
                randomShow();
            }
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
        if (null!= handler) {
            handler.removeCallbacks(runnable);
        }
        UserDatas.getInstance().unregister(this);
    }

    @Override
    protected void initListener() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 3) {
                    if (position == 0) {
                        UserDatas.getInstance().gotoIndex(1);
                    } else if (position == 1) {
                        UserDatas.getInstance().gotoIndex(2);
                    } else {
                        UserDatas.getInstance().gotoIndex(3);
                    }
                }
            }
        });
        mSavedSharell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.shareHomeSavedText(mActivity);
            }
        });
        mMusicSharell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.shareHomeMusicText(mActivity);
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
//        AdSettings.addTestDevice("28098e2b8ba1737743e3a116ec401021");
        mSmallNativeAd.setAdListener(new AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                // Ad error callback
                System.err.println("onError " + error.getErrorCode() + " " + error.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                //已经有广告了
                if (mDatas.size()>3){
                    HomeModel model = mDatas.get(3);
                    mDatas.remove(model);
                    model.addAd(mSmallNativeAd);
                    mDatas.add(model);
                    mAdapter.notifyDataSetChanged();
                } else {
                    HomeModel tempModel = new HomeModel(4);
                    tempModel.addAd(mSmallNativeAd);
                    mDatas.add(tempModel);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, EVENT_Small_AD_ID);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, EVENT_Small_AD_NAME);
                mFirebaseAnalytics.logEvent(EVENT_Small_AD_TYPE, bundle);
                // 广告点击后，请求新的广告缓存
                showSmallNativeAd();
            }
        });
        // Request an ad_front
        mSmallNativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);
    }

    private void showBigNativeAd() {
        NativeAd nativeAd = ADManager.getInstance().getHomeAd();
        if (null == nativeAd) {
            mNativeAdContainer.setVisibility(View.GONE);
            return;
        }

        mNativeAdContainer.setVisibility(View.VISIBLE);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        RelativeLayout adView = (RelativeLayout) inflater.inflate(R.layout.layout_big_ad, mNativeAdContainer, false);
        mNativeAdContainer.removeAllViews();
        mNativeAdContainer.addView(adView);

        // Create native UI using the ad_front metadata.
        ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = (TextView) adView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.native_ad_media);
        // TextView nativeAdSocialContext = (TextView) adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = (TextView) adView.findViewById(R.id.native_ad_body);
        Button nativeAdCallToAction = (Button) adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdTitle());
        // nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdBody.setText(nativeAd.getAdBody());
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());

        // Download and display the ad_front icon.
        NativeAd.Image adIcon = nativeAd.getAdIcon();
        NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

        // Download and display the cover image.
        nativeAdMedia.setNativeAd(nativeAd);

        // Add the AdChoices icon
        LinearLayout adChoicesContainer = (LinearLayout) findViewById(R.id.ad_choices_container);
        AdChoicesView adChoicesView = new AdChoicesView(getActivity(), nativeAd, true);
        adChoicesContainer.addView(adChoicesView);

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeAd.registerViewForInteraction(mNativeAdContainer, clickableViews);
        mNativeAdContainer.startAnimation(mTranstionAnim);
    }

    private int TIME = 4000;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // handler自带方法实现定时器
            try {
                handler.postDelayed(this, TIME);
                if (isVisibleToUser){
                    showSmallNativeAd();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    // 自定义适配器
    class MyAdapter extends BaseAdapter {
        // 上下文对象
        private Context context;
        CircleImageView imageViewFront;
        CircleImageView imageViewBack;
        RelativeLayout rlCardRoot;

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
            rlCardRoot = (RelativeLayout) convertView.findViewById(R.id.targetView);
            setCameraDistance();
            imageViewFront = (CircleImageView) convertView.findViewById(R.id.frontView);
            imageViewBack = (CircleImageView) convertView.findViewById(R.id.backView);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView subtitle = (TextView) convertView.findViewById(R.id.subtitle);

            HomeModel local = mDatas.get(position);
            if (local.type == 4) {
//                if (null != local.ad_front) {
//                    // Download and display the ad_front icon.
//                    NativeAd.Image adIcon = local.ad_front.getAdIcon();
//                    NativeAd.downloadAndDisplayImage(adIcon, imageViewFront);
//                    title.setText(local.ad_front.getAdTitle());
//                    subtitle.setText(local.ad_front.getAdSubtitle());
//
//                    // Register the Title and CTA button to listen for clicks.
//                    List<View> clickableViews = new ArrayList<>();
//                    clickableViews.add(imageViewFront);
//                    clickableViews.add(title);
//                    clickableViews.add(subtitle);
//                    local.ad_front.registerViewForInteraction(convertView, clickableViews);
//                }

                if (null != local.ad_front) {
                    if (null != local.ad_back){
                        // Download and display the ad_front icon.
                        NativeAd.Image adIcon = local.ad_front.getAdIcon();
                        NativeAd.downloadAndDisplayImage(adIcon, imageViewFront);
                        title.setText(local.ad_front.getAdTitle());
                        subtitle.setText(local.ad_front.getAdSubtitle());

                        // Register the Title and CTA button to listen for clicks.
                        List<View> clickableViews = new ArrayList<>();
                        clickableViews.add(imageViewFront);
                        clickableViews.add(title);
                        clickableViews.add(subtitle);
                        local.ad_front.registerViewForInteraction(convertView, clickableViews);

                        // Download and display the ad_front icon.
                        NativeAd.Image adIconBack = local.ad_back.getAdIcon();
                        NativeAd.downloadAndDisplayImage(adIconBack, imageViewBack);
                        title.setText(local.ad_back.getAdTitle());
                        subtitle.setText(local.ad_back.getAdSubtitle());

                        // Register the Title and CTA button to listen for clicks.
                        List<View> clickableViewsBack = new ArrayList<>();
                        clickableViewsBack.add(imageViewBack);
                        clickableViewsBack.add(title);
                        clickableViewsBack.add(subtitle);
                        local.ad_back.registerViewForInteraction(convertView, clickableViewsBack);
                        //旋转
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                rotable();
                            }
                        },1000);
                    } else {
                        // Download and display the ad_front icon.
                        NativeAd.Image adIcon = local.ad_front.getAdIcon();
                        NativeAd.downloadAndDisplayImage(adIcon, imageViewFront);
                        title.setText(local.ad_front.getAdTitle());
                        subtitle.setText(local.ad_front.getAdSubtitle());

                        // Register the Title and CTA button to listen for clicks.
                        List<View> clickableViews = new ArrayList<>();
                        clickableViews.add(imageViewFront);
                        clickableViews.add(title);
                        clickableViews.add(subtitle);
                        local.ad_front.registerViewForInteraction(convertView, clickableViews);
                    }
                }




            } else {
                imageViewFront.setImageResource(mDatas.get(position).resId);
                title.setText(mDatas.get(position).title);
                subtitle.setText(mDatas.get(position).subtitle);
            }

            return convertView;
        }

        /**
         * 翻转
         */
        public void rotable() {
            if (View.VISIBLE == imageViewBack.getVisibility()) {
                Rotatable rotatable = new Rotatable.Builder(rlCardRoot).sides(R.id.frontView, R.id.backView)
                    .direction(Rotatable.ROTATE_X).rotationCount(1).build();
                rotatable.setTouchEnable(false);
                rotatable.rotate(Rotatable.ROTATE_X, 0, 1500);
            } else if (View.VISIBLE == imageViewFront.getVisibility()) {
                imageViewBack.setRotationX(180f);
                Rotatable rotatable = new Rotatable.Builder(rlCardRoot).sides(R.id.frontView, R.id.backView)
                    .direction(Rotatable.ROTATE_X).rotationCount(1).build();
                rotatable.setTouchEnable(false);
                rotatable.rotate(Rotatable.ROTATE_X, -180, 1500);
            }
        }

        /**
         * 改变视角距离, 贴近屏幕
         */
        private void setCameraDistance() {
            int distance = 10000;
            float scale = getResources().getDisplayMetrics().density * distance;
            rlCardRoot.setCameraDistance(scale);
        }
    }
}
