package com.av.ringtone.views;

import com.av.ringtone.R;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;

/**
 * Created by LiJiaZhi on 16/12/31.
 *
 * 评分提示框
 */

public class RateDialog extends Dialog {

    public RateDialog(Context context, final View.OnClickListener listener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rate);
        findViewById(R.id.close_iv).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dismiss();
            }
        });
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingbar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating >= 3.0f) {
                    if (null != listener) {
                        listener.onClick(ratingBar);
                    }
                    dismiss();
                } else {
                    findViewById(R.id.hint_tv).setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
