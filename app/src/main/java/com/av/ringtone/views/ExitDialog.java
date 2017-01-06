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

public class ExitDialog extends Dialog {

    public ExitDialog(Context context, NativeAd nativeAd, final View.OnClickListener listener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Inflate our UI from its XML layout description.
        setContentView(R.layout.dialog_exit);

        MediaView mediaView = (MediaView)findViewById(R.id.adIv);
        if (null != nativeAd){
            // Download and display the cover image.
            mediaView.setNativeAd(nativeAd);
            // Register the Title and CTA button to listen for clicks.
            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(mediaView);
            nativeAd.registerViewForInteraction(mediaView, clickableViews);
        }

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
