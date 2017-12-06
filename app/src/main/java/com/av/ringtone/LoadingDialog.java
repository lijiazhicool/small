package com.av.ringtone;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import com.music.ringtonemaker.ringtone.cutter.maker.R;

/**
 * @author：LiJiaZhi on 2017/12/4
 * open ad loading对话框
 */

public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    public LoadingDialog(Context context) {
        super(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_open_ad_loading);
    }
}
