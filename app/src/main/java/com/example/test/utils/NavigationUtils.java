package com.example.test.utils;

import com.example.test.logic.AboutActivity;
import com.example.test.logic.edit.CutterActivity;
import com.example.test.model.SongModel;

import android.content.Context;
import android.content.Intent;

/**
 * Created by LiJiaZhi on 16/12/19.
 */

public class NavigationUtils {
    public static void goToCutter(Context context, SongModel song) {
        Intent intent = new Intent(context, CutterActivity.class);
        intent.putExtra(CutterActivity.INTENT_IN_MODEL, song);
        context.startActivity(intent);
    }

    public static void goToAbout(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }
}
