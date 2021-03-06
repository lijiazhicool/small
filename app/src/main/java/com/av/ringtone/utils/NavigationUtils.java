package com.av.ringtone.utils;

import com.av.ringtone.logic.AboutActivity;
import com.av.ringtone.logic.edit.CutterActivity;
import com.av.ringtone.logic.scan.ScanActivity;
import com.av.ringtone.model.BaseModel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by LiJiaZhi on 16/12/19.
 */

public class NavigationUtils {
    public static void goToCutter(Context context, BaseModel fileModel) {
        Intent intent = new Intent(context, CutterActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(CutterActivity.INTENT_IN_MODEL, fileModel);
        intent.putExtras(mBundle);
        context.startActivity(intent);
    }

    public static void goToAbout(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    public static void goToScan(Context context) {
        Intent intent = new Intent(context, ScanActivity.class);
        context.startActivity(intent);
    }
}
