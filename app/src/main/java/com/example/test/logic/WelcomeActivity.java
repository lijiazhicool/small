package com.example.test.logic;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import com.example.test.R;

public class WelcomeActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        startNextPage();
    }

    private void startNextPage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.launch(WelcomeActivity.this);
                finish();
            }
        }, 200);
    }
}
