package com.av.ringtone.views;

import com.av.ringtone.R;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by LiJiaZhi on 16/12/31.
 */

public class CommonDialog extends Dialog {

    public CommonDialog(Context context, String title, String content, String ok,  final  View.OnClickListener listener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_common);

        TextView titleTV = (TextView)findViewById(R.id.title);
        if (!TextUtils.isEmpty(title)){
            titleTV.setText(title);
        }
        TextView contentTV = (TextView)findViewById(R.id.content);
        contentTV.setText(content);
        TextView okTv = (TextView)findViewById(R.id.ok);
        if (!TextUtils.isEmpty(ok)){
            okTv.setText(ok);
        }
        okTv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (null != listener) {
                    listener.onClick(view);
                }
                dismiss();
            }
        });
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dismiss();
            }
        });
    }

}
