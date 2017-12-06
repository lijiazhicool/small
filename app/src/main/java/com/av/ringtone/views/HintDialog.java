package com.av.ringtone.views;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;

import com.music.ringtonemaker.ringtone.cutter.maker.R;

/**
 * Created by LiJiaZhi on 16/12/31.
 * 提示对话框
 */

public class HintDialog extends Dialog {

    public HintDialog(Context context, final  View.OnClickListener listener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_hint);
        findViewById(R.id.close_tv).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                CheckBox cb = ((CheckBox) findViewById(R.id.confirm_cb));
                view.setTag(cb.isChecked());
                if (null != listener) {
                    listener.onClick(view);
                }
                dismiss();
            }
        });

    }
}
