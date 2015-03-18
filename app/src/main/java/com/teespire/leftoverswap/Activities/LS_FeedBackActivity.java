package com.teespire.leftoverswap.Activities;

import android.app.Activity;
import android.os.Bundle;

import com.teespire.leftoverswap.R;


public class LS_FeedBackActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

    }
    @Override
    public void onBackPressed()

    {

        finish();
    }
}
