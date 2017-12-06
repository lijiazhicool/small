package com.av.ringtone.base;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.music.ringtonemaker.ringtone.cutter.maker.R;

/**
 * Created by LiJiaZhi on 16/11/7. base
 */

public abstract class BaseActivity extends FragmentActivity {

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

    /**
     * 沉浸式状态栏
     */
    protected void initState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
    /**
     * 启动到应用商店app详情界面 http://www.jianshu.com/p/a4a806567368
     *
     * @param appPkg 目标App的包名
     * @param marketPkg 应用商店包名 ,如果为""则由系统弹出应用商店列表供用户选择,否则调转到目标市场的应用详情界面，某些应用商店可能会失败 例如com.android.vending Google Play
     */
    protected void launchAppDetail(String appPkg, String marketPkg) {
        try {
            if (TextUtils.isEmpty(appPkg))
                return;

            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
