package com.av.ringtone.logic;

import com.av.ringtone.base.BaseActivity;
import com.av.ringtone.utils.ShareUtils;
import com.music.ringtonemaker.ringtone.cutter.maker.R;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 关于
 */
public class AboutActivity extends BaseActivity {
    private ImageView mBackIv;
    LinearLayout linkll,contactll, ratell;
    TextView versionTv;
    @Override
    protected int getLayoutId() {
        initState();
        return R.layout.activity_about;
    }

    @Override
    protected void initBundleExtra() {

    }

    @Override
    protected void findViewById() {
        mBackIv = findView(R.id.back);
        linkll = findView(R.id.link);
        contactll = findView(R.id.contact);
        ratell = findView(R.id.rate);
        versionTv = findView(R.id.version);
    }

    @Override
    protected void initListeners() {
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        linkll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        contactll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.adviceEmail(AboutActivity.this);
            }
        });
        ratell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appPackageName = getPackageName();
                launchAppDetail(appPackageName, "com.android.vending");
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        versionTv.setText(String.format(getString(R.string.about_version),getVersion()));
    }
    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
