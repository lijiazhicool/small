package com.av.ringtone.views;

import com.av.ringtone.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by LiJiaZhi on 16/12/31.
 */

public class RecordingDialog extends Dialog {

    public RecordingDialog(Context context, final View.OnClickListener cancelListener,
        final View.OnClickListener okListener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_record_audio);

        MusicVisualizer musicanimate = (MusicVisualizer) findViewById(R.id.musicanimate);
        musicanimate.setColor(context.getResources().getColor(R.color.colorPrimary));
        ((TextView) findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (null != cancelListener) {
                    cancelListener.onClick(view);
                }
                dismiss();
            }
        });
        ((TextView) findViewById(R.id.exit)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (null != okListener) {
                    okListener.onClick(view);
                }
                dismiss();
            }
        });
    }
}