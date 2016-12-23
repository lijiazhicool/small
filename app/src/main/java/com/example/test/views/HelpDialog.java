package com.example.test.views;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.test.R;

/**
 * Created by LiJiaZhi on 16/12/23. 反馈
 */

public class HelpDialog extends DialogFragment {
    private SendlListener mListener;

    public interface SendlListener {
        void onSendClick(View v, String text);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_help, container);
        final EditText editText = (EditText) view.findViewById(R.id.content);
        Button sendButton = (Button) view.findViewById(R.id.send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onSendClick(v, editText.getText().toString());
                }
            }
        });
        return view;
    }

    public void setSendlListener(SendlListener l) {
        mListener = l;
    }
}
