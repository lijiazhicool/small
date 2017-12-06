/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.av.ringtone.views;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.av.ringtone.utils.FileUtils;
import com.music.ringtonemaker.ringtone.cutter.maker.R;

import static com.av.ringtone.Constants.*;

public class FileSaveDialog extends Dialog {

    private Spinner mTypeSpinner;
    private EditText mFilename;
    private Message mResponse;
    private String mOriginalName;
    private TextView mSavePathTv;
    private ArrayList<String> mTypeArray;
    private int mPreviousSelection;

    /**
     * Return a human-readable name for a kind (music, alarm, ringtone, ...). These won't be displayed on-screen (just
     * in logs) so they shouldn't be translated.
     */
    public static String KindToName(int kind) {
        switch (kind) {
            default:
                return "Unknown";
            case FILE_KIND_MUSIC:
                return "Music";
            case FILE_KIND_ALARM:
                return "Alarm";
            case FILE_KIND_NOTIFICATION:
                return "Notification";
            case FILE_KIND_RINGTONE:
                return "Ringtone";
        }
    }

    public FileSaveDialog(final Context context, Resources resources, String originalName, Message response) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Inflate our UI from its XML layout description.
        setContentView(R.layout.dialog_file_save);

//        setTitle(resources.getString(R.string.file_save_title));

        mTypeArray = new ArrayList<String>();
        mTypeArray.add(resources.getString(R.string.type_music));
        mTypeArray.add(resources.getString(R.string.type_alarm));
        mTypeArray.add(resources.getString(R.string.type_notification));
        mTypeArray.add(resources.getString(R.string.type_ringtone));

        mFilename = (EditText) findViewById(R.id.filename);

        mFilename.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int lines = mFilename.getLineCount();
                // 限制最大输入行数
                if (lines > 2) {
                    String str = s.toString();
                    int cursorStart = mFilename.getSelectionStart();
                    int cursorEnd = mFilename.getSelectionEnd();
                    if (cursorStart == cursorEnd && cursorStart < str.length() && cursorStart >= 1) {
                        str = str.substring(0, cursorStart-1) + str.substring(cursorStart);
                    } else {
                        str = str.substring(0, s.length()-1);
                    }
                    // setText会触发afterTextChanged的递归
                    mFilename.setText(str);
                    // setSelection用的索引不能使用str.length()否则会越界
                    mFilename.setSelection(mFilename.getText().length());
                }
            }
        });

        mOriginalName = originalName;

        ArrayAdapter<String> adapter =
            new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, mTypeArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSpinner = (Spinner) findViewById(R.id.ringtone_type);
        mSavePathTv = (TextView) findViewById(R.id.savepath);
        mTypeSpinner.setAdapter(adapter);
        mTypeSpinner.setSelection(FILE_KIND_MUSIC);
        mSavePathTv.setText(FileUtils.getMusicPath(context));
        mPreviousSelection = FILE_KIND_RINGTONE;

        setFilenameEditBoxFromName(false);

        mTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView parent, View v, int position, long id) {
                setFilenameEditBoxFromName(true);
                if (position==0){
                    mSavePathTv.setText(FileUtils.getMusicDir_show());
                } else if (position==1){
                    mSavePathTv.setText(FileUtils.getAlarmDir_show());
                }else if (position==2){
                    mSavePathTv.setText(FileUtils.getNotificationDir_show());
                }else if (position==3){
                    mSavePathTv.setText(FileUtils.getRingtoneDir_show());
                }
            }

            public void onNothingSelected(AdapterView parent) {
            }
        });

        TextView save = (TextView) findViewById(R.id.save);
        save.setOnClickListener(saveListener);
        TextView cancel = (TextView) findViewById(R.id.cancel);
        cancel.setOnClickListener(cancelListener);
        mResponse = response;
    }

    private void setFilenameEditBoxFromName(boolean onlyIfNotEdited) {
        if (onlyIfNotEdited) {
            CharSequence currentText = mFilename.getText();
            String expectedText = mOriginalName + " " + mTypeArray.get(mPreviousSelection);

            if (!expectedText.contentEquals(currentText)) {
                return;
            }
        }

        int newSelection = mTypeSpinner.getSelectedItemPosition();
        String newSuffix = mTypeArray.get(newSelection);
        mFilename.setText(mOriginalName + " " + newSuffix);
        mPreviousSelection = mTypeSpinner.getSelectedItemPosition();
    }

    private View.OnClickListener saveListener = new View.OnClickListener() {
        public void onClick(View view) {
            mResponse.obj = mFilename.getText();
            mResponse.arg1 = mTypeSpinner.getSelectedItemPosition();
            mResponse.sendToTarget();
            dismiss();
        }
    };

    private View.OnClickListener cancelListener = new View.OnClickListener() {
        public void onClick(View view) {
            dismiss();
        }
    };
}
