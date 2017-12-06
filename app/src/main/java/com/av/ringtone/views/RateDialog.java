package com.av.ringtone.views;


import java.util.Date;

import com.av.ringtone.App;
import com.av.ringtone.utils.ShareUtils;
import com.example.ad.MyToast;
import com.example.ad.StatisticsManager;
import com.music.ringtonemaker.ringtone.cutter.maker.R;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by LiJiaZhi on 16/12/31.
 *
 * 评分提示框
 */

public class RateDialog extends DialogFragment implements View.OnClickListener{
    private static final String TAG = "RateDialog";

    private TextView mRateAction;
    private View mCancel;
    private TextView mRateTips;

    private ImageView oneIv,twoIv,threeIv,fourIv,fiveIv;

    private int mRateScore = 5;
    private int mDefaultScore = 3;

    private View mRoot;

    public void setDefaultScore(int defaultScore) {
        mDefaultScore = defaultScore;
    }

    public interface OnRateDismiss {
        void onDismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().setWindowAnimations(R.style.DialogAnimation);
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        View v = inflater.inflate(R.layout.dialog_rate, container, false);

        mRoot = v.findViewById(R.id.rate_root_container);
        mRateAction = (TextView) v.findViewById(R.id.rate_action);
        mCancel = v.findViewById(R.id.rate_cancel);
//        mDislike = v.findViewById(R.id.rate_dislike);
        mRateAction.setOnClickListener(this);
        mCancel.setOnClickListener(this);
//        mDislike.setOnClickListener(this);
        mRateTips = (TextView) v.findViewById(R.id.rate_tips);

        oneIv = (ImageView) v.findViewById(R.id.one);
        twoIv = (ImageView) v.findViewById(R.id.two);
        threeIv = (ImageView) v.findViewById(R.id.three);
        fourIv = (ImageView) v.findViewById(R.id.four);
        fiveIv = (ImageView) v.findViewById(R.id.five);
        oneIv.setOnClickListener(this);
        twoIv.setOnClickListener(this);
        threeIv.setOnClickListener(this);
        fourIv.setOnClickListener(this);
        fiveIv.setOnClickListener(this);
        score(mDefaultScore == 0 ? 5 : mDefaultScore);
        setCancelable(true);
        return v;
    }

    private void score(int score) {
        mRateScore = score;
        oneIv.setImageResource(score >= 1 ? R.drawable.rate1_normal : R.drawable.rate1_grey);
        twoIv.setImageResource(score >= 2 ? R.drawable.rate2_normal : R.drawable.rate2_grey);
        threeIv.setImageResource(score >= 3 ? R.drawable.rate3_normal : R.drawable.rate3_grey);
        fourIv.setImageResource(score >= 4 ? R.drawable.rate4_normal : R.drawable.rate4_grey);
        fiveIv.setImageResource(score >= 5 ? R.drawable.rate5_normal : R.drawable.rate5_grey);
        mRateTips.setText(score == 5 ? App.getAppContext().getResources().getString(R.string.rate_content_5) : App.getAppContext().getResources().getString(R.string.rate_content_1_4));
        mRateAction.setText(score > 3 ? App.getAppContext().getResources().getString(R.string.rate_us) : App.getAppContext().getResources().getString(R.string.rate_feedback));
    }

    @Override
    public void onClick(View view) {
        long time = new Date().getTime();
        switch (view.getId()) {
            case R.id.rate_action:
                if (mRateScore >= 4) {
                    MyToast.makeText(App.getAppContext(), "", Toast.LENGTH_LONG).show();
                    StatisticsManager.submit(App.getAppContext(), StatisticsManager.EVENT_RATE, StatisticsManager.ITEM_RATE_STAR,null,null);
                    ShareUtils.launchAppDetail(App.getAppContext(), App.getAppContext().getPackageName());
                } else {
                    StatisticsManager.submit(App.getAppContext(), StatisticsManager.EVENT_RATE, StatisticsManager.ITEM_RATE_DISLIKE,null,null);
                    ShareUtils.adviceEmail(App.getAppContext());
                }
                dismissRate(R.anim.anim_leave_right);
                break;
            case R.id.rate_cancel:
                StatisticsManager.submit(App.getAppContext(), StatisticsManager.EVENT_RATE, StatisticsManager.ITEM_RATE_CANCEL,null,null);
                dismissRate(R.anim.anim_leave_right);
                break;
            case R.id.one:
                score(1);
                break;
            case R.id.two:
                score(2);
                break;
            case R.id.three:
                score(3);
                break;
            case R.id.four:
                score(4);
                break;
            case R.id.five:
                score(5);
                break;
        }
    }

    private boolean animStarted = false;
    private void dismissRate(int animId) {
        if (animStarted) {
            return;
        }
        animStarted = false;
        Animation animLeave = AnimationUtils.loadAnimation(App.getAppContext(), animId);
        animLeave.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animStarted = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "dismiss onAnimationEnd");
                animStarted = false;
                mRoot.setVisibility(View.GONE);
                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mRoot.startAnimation(animLeave);
    }
}