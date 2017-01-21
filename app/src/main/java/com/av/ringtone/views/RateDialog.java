package com.av.ringtone.views;

import com.av.ringtone.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;

/**
 * Created by LiJiaZhi on 16/12/31.
 * 评分提示框
 */

public class RateDialog extends Dialog {

    public RateDialog(Context context, final  View.OnClickListener listener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rate);
        findViewById(R.id.starll).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (null != listener){
                    listener.onClick(view);
                }
                dismiss();
            }
        });
        findViewById(R.id.now).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (null != listener){
                    listener.onClick(view);
                }
                dismiss();
            }
        });
        findViewById(R.id.later).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
