package com.example.test.logic;

import com.example.test.R;
import com.example.test.base.BaseActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * 关于
 */
public class AboutActivity extends BaseActivity {
    private ImageView mBackIv;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initBundleExtra() {

    }

    @Override
    protected void findViewById() {
        mBackIv = findView(R.id.back);

    }

    @Override
    protected void initListeners() {
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }
}
