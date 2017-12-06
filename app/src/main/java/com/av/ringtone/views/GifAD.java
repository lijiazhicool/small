package com.av.ringtone.views;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.music.ringtonemaker.ringtone.cutter.maker.R;

import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

/**
 * @author：LiJiaZhi on 2017/12/5
 * @des：ToDo
 * @org mtime.com
 */
public class GifAD extends FrameLayout {
    private GifImageView mGifImageView;
    private Random mRandom = new Random();
    private int[] mDrawables = {R.drawable.ad01, R.drawable.ad02,R.drawable.ad03,R.drawable.ad04, R.drawable.ad05, R.drawable.ad06, R.drawable.ad07};

    public GifAD(@NonNull Context context) {
        super(context);
        init();
    }

    public GifAD(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GifAD(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.layout_gif_ad, this, true);
        mGifImageView = (GifImageView) rootView.findViewById(R.id.gif_ad_iv);
        updateBackResource();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void updateBackResource() {
        //每次点击都切换一次图标
        int random = mRandom.nextInt(7);
        mGifImageView.setImageResource(mDrawables[random]);
    }
}