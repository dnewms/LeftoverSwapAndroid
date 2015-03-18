package com.teespire.leftoverswap.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;

public final class LS_DispatchActivity extends Activity {

    /*
 * Define a request code to send to Google Play services This code is returned in
 * Activity.onActivityResult
 */




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume()
    {
        super.onResume();

 // Check if there is current user info
            if (ParseUser.getCurrentUser() != null)
            {
                // Start an intent for the logged in activity
                Intent i=new Intent(this,LS_MainActivity.class);
                startActivity(i);
                finish();

            } else
            {
                // Start and intent for the logged out activity

                Intent i=new Intent(this,LS_WelcomeActivity.class);
                startActivity(i);
                finish();
            }




    }







}