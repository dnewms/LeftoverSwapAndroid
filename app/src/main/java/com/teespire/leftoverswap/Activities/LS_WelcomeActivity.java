package com.teespire.leftoverswap.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.teespire.leftoverswap.Application;
import com.teespire.leftoverswap.Fragments.LS_DialogFragment;
import com.teespire.leftoverswap.LS_Manager;
import com.teespire.leftoverswap.R;


public class LS_WelcomeActivity extends FragmentActivity implements OnClickListener
{

    private ImageButton ib_appIcon;
    private Button btn_login;
    private Button btn_signup;
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private Boolean image1=true;
    private Boolean image2=false;
    private Boolean image3=false;
    private Boolean image4=false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

    }


    @Override
    protected void onResume()
    {
        super.onResume();

            // Then we're good to go!
            ib_appIcon=(ImageButton)findViewById(R.id.imageBtn_icon);
            btn_login=(Button)findViewById(R.id.btn_login);
            btn_signup=(Button)findViewById(R.id.btn_signup);
            ib_appIcon.setOnClickListener( this);
            btn_login.setOnClickListener(this);
            btn_signup.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        if(v==btn_login)
        {
            if(servicesConnected()&& LS_Manager.NetworkAvailable(this)) {
                Intent i = new Intent(this, LS_LoginActivity.class);
                startActivity(i);

            }
        }
        if(servicesConnected()&& v==btn_signup)
        {
            if(LS_Manager.NetworkAvailable(this)) {
                Intent i = new Intent(this, LS_SignupActivity.class);
                startActivity(i);
            }
        }
        if(v==ib_appIcon)
        {

            if(image2==true&&image3==true&&image4==true)
            {
                ib_appIcon.setBackgroundResource(R.drawable.plain);
                image1=true;
                image2=false;
                image3=false;
                image4=false;

            }
           else if(image1==true||(image3==true&&image4==true))
           {
               ib_appIcon.setBackgroundResource(R.drawable.plain2);
               image1=false;
               image2=true;
           }
           else if(image2==true||image4==true)
            {
                ib_appIcon.setBackgroundResource(R.drawable.plain3);
                image2=false;
                image3=true;
            }
           else if(image3==true)
            {
                ib_appIcon.setBackgroundResource(R.drawable.plain4);
                image3=false;
                image4=true;
            }


        }

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

    public boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            if (Application.APPDEBUG) {
                // In debug mode, log the status
                Log.d(Application.APPTAG, "Google play services available");
            }
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                LS_DialogFragment dialogFragment=new LS_DialogFragment();
                dialogFragment.setDialog(dialog);
                dialogFragment.show(getSupportFragmentManager(),Application.APPTAG);

            }
            return false;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Choose what to do based on the request code
        switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:

                        if (Application.APPDEBUG) {
                            // Log the result
                            Log.d(Application.APPTAG, "Connected to Google Play services");
                        }

                        break;

                    // If any other result was returned by Google Play services
                    default:
                        if (Application.APPDEBUG) {
                            // Log the result
                            Log.d(Application.APPTAG, "Could not connect to Google Play services");
                        }
                        break;
                }

                // If any other request code was received
            default:
                if (Application.APPDEBUG) {
                    // Report that this Activity received an unknown requestCode
                    Log.d(Application.APPTAG, "Unknown request code received for the activity");
                }
                break;
        }
    }



}
