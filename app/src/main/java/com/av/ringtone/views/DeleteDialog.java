package com.av.ringtone.views;

import com.av.ringtone.R;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiJiaZhi on 16/12/31.
 */

public class DeleteDialog extends Dialog {

    public DeleteDialog(Context context,final  View.OnClickListener listener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_delete);
        ((TextView) findViewById(R.id.exit)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (null != listener) {
                    listener.onClick(view);
                }
                dismiss();
            }
        });
        ((TextView) findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dismiss();
            }
        });

    }
}
