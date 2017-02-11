package com.av.ringtone.logic.scan;

import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.av.ringtone.R;
import com.av.ringtone.base.BaseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScanActivity extends BaseActivity {

    private TextView fileName, searchPath, file;
    private MusicScan fileScan;
    private List<File> list = new ArrayList<>();
    String[] str = new String[] { ".mp3", ".m4a", ".wav", ".wma" };// aiff,flac,dsf
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MusicScan.FIND_FILE:
                    File file1 = (File) msg.obj;
                    file.setText(""+msg.obj);
                    // file.setText(++i);
                    list.add((File) msg.obj);
                    if (file1.getName().equals("a.mp3") || file1.getName().equals("b.mp3")) {
                        MediaScannerConnection.scanFile(ScanActivity.this, new String[] { file1.getAbsolutePath() },
                            null, null);
                    }
                    fileName.setText(" " + list.size());
                    break;
                case MusicScan.NOW_SCAN_FOLDER:
                    searchPath.setText("当前查找文件夹：" + msg.obj);
                    break;
                case MusicScan.FIND_FINISH:
                    fileName.setText(fileName.getText() + "  done ");
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan;
    }

    @Override
    protected void initBundleExtra() {

    }

    @Override
    protected void findViewById() {

    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        fileName = (TextView) findViewById(R.id.file_name);
        searchPath = (TextView) findViewById(R.id.search_path);
        file = (TextView) findViewById(R.id.file);

        fileScan = new MusicScan(str, handler);
        fileName.setText("当前查找文件名：" + str);
        Button button = (Button) findViewById(R.id.start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileScan.getState() != Thread.State.NEW) {
                    fileScan = null;
                    fileScan = new MusicScan(str, handler);
                }
                fileScan.start();
            }
        });
    }
}
