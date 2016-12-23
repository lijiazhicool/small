package com.example.test.utils;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

/**
 * Created by LiJiaZhi on 16/12/18. share
 */

public class ShareUtils {
    // 分享文字
    public static void shareText(Activity act) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT,
            "Great app for mp3 cutter! Try Mp3 Cutter & Ringtone Maker now! https://play.google.com/store/apps/details?id=com.happykids.zoo");
        shareIntent.setType("text/plain");

        // 设置分享列表的标题，并且每次都显示分享列表
        act.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    public static void shareMultipleImage(Activity act) {
        ArrayList<Uri> uriList = new ArrayList<>();

        String path = Environment.getExternalStorageDirectory() + File.separator;
        uriList.add(Uri.fromFile(new File(path + "australia_1.jpg")));
        uriList.add(Uri.fromFile(new File(path + "australia_2.jpg")));
        uriList.add(Uri.fromFile(new File(path + "australia_3.jpg")));

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
        shareIntent.setType("image/*");
        act.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    public static void shareFile(Activity act, Uri uri) {
        // intent.setType("text/plain"); //纯文本
        /*
         * 图片分享 it.setType("image/png"); //添加图片 File f = new
         * File(Environment.getExternalStorageDirectory()+"/name.png");
         *
         * Uri uri = Uri.fromFile(f); intent.putExtra(Intent.EXTRA_STREAM, uri);
         */
        Intent intent = new Intent(Intent.ACTION_SEND);
        // intent.setType("image/*");
        // intent.setType("audio/*");//此处可发送多种文件
        intent.setType("audio/mp3");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, "I have successfully share my message through my app");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        act.startActivity(Intent.createChooser(intent, "Share To"));
    }

    public static void adviceEmail(Activity act, String content) {
        Intent data = new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:lijiazhicool@gmail.com"));
        data.putExtra(Intent.EXTRA_SUBJECT, "Cutter");
        data.putExtra(Intent.EXTRA_TEXT, content);
        act.startActivity(data);
        ToastUtils.makeToastAndShow(act,"Thanks for your advice");
    }
}
