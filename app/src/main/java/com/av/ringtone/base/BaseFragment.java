package com.av.ringtone.base;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by LiJiaZhi on 16/11/7. base
 */

public abstract class BaseFragment extends Fragment {

    //统计
    protected FirebaseAnalytics mFirebaseAnalytics;

    // 为了防止getActivity()空指针，用mActivity代替getActivity()
    protected BaseActivity mActivity;

    /**
     * Fragment Content view
     */
    private View inflateView;

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void initView(View parentView, Bundle savedInstanceState);

    protected abstract void initData();

    protected abstract void initListener();

//    protected boolean isStartEventBus() {
//        return false;
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (BaseActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == inflateView) {
            // 强制竖屏显示
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            int layoutResId = getLayoutId();
            if (layoutResId > 0)
                inflateView = inflater.inflate(getLayoutId(), container, false);

            // 解决点击穿透问题
           /* inflateView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });*/
        }
        return inflateView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mActivity);
        initView(view, savedInstanceState);
        initListener();
        initData();
    }

    /**
     * 通过ID查找控件
     *
     * @param viewId 控件资源ID
     * @param <VIEW> 泛型参数，查找得到的View
     * @return 找到了返回控件对象，否则返回null
     */
    final public <VIEW extends View> VIEW findViewById(@IdRes int viewId) {
        return (VIEW) inflateView.findViewById(viewId);
    }

}
