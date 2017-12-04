package com.av.ringtone.logic.home;

import com.av.ringtone.StatisticsManager;
import com.av.ringtone.ad.ADConstants;
import com.av.ringtone.ad.ADManager;
import com.av.ringtone.Constants;
import com.av.ringtone.R;
import com.av.ringtone.UserDatas;
import com.av.ringtone.ad.NativeAD;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
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

    private Animation mTranstionAnim;

    private boolean isVisibleToUser = false;//当前界面是否可见

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

//        去掉定时请求小广告
//        handler.postDelayed(runnable, TIME); //每隔1s执行
        mIsInit = true;
    }

    private void randomShow() {
        mSavedSharell.setVisibility(View.GONE);
        mMusicSharell.setVisibility(View.GONE);
        showBigNativeAd();

//        if (UserDatas.getInstance().getCutCount() ==0){
//            mSavedSharell.setVisibility(View.GONE);
//            mMusicSharell.setVisibility(View.GONE);
//            showBigNativeAd();
//            return;
//        }
//        // 1-3
//        int index = new Random().nextInt(3) + 1;
//        if (index % 3 == 0) {
//            mSavedSharell.setVisibility(View.VISIBLE);
//            mSavedSharetv.setText(String.format(getString(R.string.home_cut_count), UserDatas.getInstance().getCutCount()));
//            mNativeAdContainer.setVisibility(View.GONE);
//            mMusicSharell.setVisibility(View.GONE);
//            mSavedSharell.startAnimation(mTranstionAnim);
//        } else if (index % 3 == 1) {
//            mSavedSharell.setVisibility(View.GONE);
//            showBigNativeAd();
//            mMusicSharell.setVisibility(View.GONE);
//        } else if (index % 3 == 2) {
//            mSavedSharell.setVisibility(View.GONE);
//            mNativeAdContainer.setVisibility(View.GONE);
//            mMusicSharell.setVisibility(View.VISIBLE);
//            mMusicSharetv.setText(String.format(getString(R.string.home_music_count_share), UserDatas.getInstance().getSongs().size()));
//            mMusicSharell.startAnimation(mTranstionAnim);
//        }
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
                    StatisticsManager.submit(mActivity,StatisticsManager.EVENT_HOME_GRID, String.valueOf(position),null,null);
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


    private void showBigNativeAd() {
        new NativeAD().loadAD(getActivity(), ADManager.AD_Facebook, ADConstants.facebook_pause_native, new NativeAD.ADListener() {
            @Override
            public void onLoadedSuccess(NativeAd nativeAd, String adId) {
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
                // TextView nativeAdSocialContext = (TextView) googleAdView.findViewById(R.id.native_ad_social_context);
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

            @Override
            public void onLoadedFailed(String msg, String adId, int errorcode) {

            }

            @Override
            public void onAdClick() {

            }

            @Override
            public void onAdImpression(NativeAd ad, String adId) {

            }
        });

    }

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
