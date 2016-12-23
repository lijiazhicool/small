package com.example.test.base;

import com.example.test.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by LiJiaZhi on 16/11/7. base
 */

public abstract class BaseActivity extends FragmentActivity {
    //统计
    protected FirebaseAnalytics mFirebaseAnalytics;

    protected FrameLayout mContentLayout;

    protected View rootView;

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void initBundleExtra();

    protected abstract void findViewById();

    protected abstract void initListeners();

    protected abstract void initData(Bundle savedInstanceState);

    protected boolean isStartEventBus() {
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        if (bundle != null) {
            // 如果系统回收的Activity， 但是系统却保留了Fragment， 当Activity被重新初始化， 此时， 系统保存的Fragment 的getActivity为空，
            // 所以要移除旧的Fragment ， 重新初始化新的Fragment
            String FRAGMENTS_TAG = "android:support:fragments";
            bundle.remove(FRAGMENTS_TAG);
        }
        super.onCreate(bundle);
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_base, null);
        setContentView(rootView);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mContentLayout = (FrameLayout) findViewById(R.id.layout_content);

        int layoutId = getLayoutId();
        if (layoutId != 0) {
            mContentLayout.addView(LayoutInflater.from(this).inflate(layoutId, null));
        }

        initPopupFragment(getPopupFragment());

        initBundleExtra();
        findViewById();
        initListeners();
        initData(bundle);
    }

    // @Override
    // public void onStart() {
    // super.onStart();
    // if (isStartEventBus()) {
    // EventBus.getDefault().register(this);
    // }
    // }
    //
    // @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    // public void onEvent(LPBaseEventBean event) {
    // }
    //
    // @Override
    // public void onStop() {
    // if (isStartEventBus()) {
    // EventBus.getDefault().unregister(this);
    // }
    // super.onStop();
    // }

    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }

    protected void initPopupFragment(Fragment popupFragment) {
        if (popupFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.layout_content, popupFragment);
            transaction.commitAllowingStateLoss();
        }
    }

    protected Fragment getPopupFragment() {
        return null;
    }
}
