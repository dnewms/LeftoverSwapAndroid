package com.teespire.leftoverswap.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.teespire.leftoverswap.R;


public class LS_SplashActivity extends Activity {

    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        thread=new Thread()
        {
            @Override
            public void run()
            {
                super.run();

                try
                {
                    sleep(1000);
                    Intent i=new Intent(getApplicationContext(),LS_DispatchActivity.class);
                    startActivity(i);
                    finish();

                }
                catch(InterruptedException e)

                {

                    e.printStackTrace();

                }
            }
        };
        thread.start();


    }



}
