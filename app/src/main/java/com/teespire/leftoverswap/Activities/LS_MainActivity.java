package com.teespire.leftoverswap.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.teespire.leftoverswap.Application;
import com.teespire.leftoverswap.Fragments.LS_CoversationsFragment;
import com.teespire.leftoverswap.LS_LocationProvider;
import com.teespire.leftoverswap.Fragments.LS_MapFragment;
import com.teespire.leftoverswap.Fragments.LS_MeFragment;
import com.teespire.leftoverswap.R;

import java.io.File;
import java.io.IOException;

public class LS_MainActivity extends FragmentActivity implements
        OnClickListener,LS_LocationProvider.LocationCallback

{
    private static String DEBUG_TAG = "LS_MainActivity";

    private static String SAVED_LOCATION = "location";

    private LinearLayout ll_Map;
    private LinearLayout ll_Post;
    private LinearLayout ll_Conversation;
    private LinearLayout ll_Me;

    private ImageView ib_Map;
    private ImageView ib_Post;
    private ImageView ib_Conversation;
    private ImageView ib_Me;


    private TextView tv_Map;
    private TextView tv_Post;
    private TextView tv_Conversation;
    private TextView tv_Me;

    FragmentManager fg;
    FragmentTransaction ft;
    ImageView iv;

    public  static File file;


    private LS_LocationProvider mLocationProvider;
    private Location currentLocation;

    private Boolean checkMeFragment =false;
    private Boolean checkConversationFragment =false;
    private Boolean checkMapFragment =false;

    private ProgressDialog progressDialog;
    private BroadcastReceiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null){
            currentLocation = (Location)savedInstanceState.getParcelable(SAVED_LOCATION);
        }

        getServiceBroadcast();

        ll_Map = (LinearLayout)findViewById(R.id.linearLayout_tab_map);
        ll_Post = (LinearLayout)findViewById(R.id.linearLayout_tab_post);
        ll_Conversation = (LinearLayout)findViewById(R.id.linearLayout_tab_conv);
        ll_Me = (LinearLayout)findViewById(R.id.linearLayout_tab_me);

        iv=(ImageView)findViewById(R.id.iv);
        ib_Map=(ImageView)findViewById(R.id.ib_map);
        ib_Post=(ImageView)findViewById(R.id.ib_post);
        ib_Conversation=(ImageView)findViewById(R.id.ib_conversation);
        ib_Me=(ImageView)findViewById(R.id.ib_me);

        tv_Map=(TextView)findViewById(R.id.tv_map);
        tv_Post=(TextView)findViewById(R.id.tv_post);
        tv_Conversation=(TextView)findViewById(R.id.tv_conversation);
        tv_Me=(TextView)findViewById(R.id.tv_me);

        /*
        ib_Map.setOnClickListener(this);
        ib_Post.setOnClickListener(this);
        ib_Conversation.setOnClickListener(this);
        ib_Me.setOnClickListener(this);
        */

        ll_Map.setOnClickListener(this);
        ll_Post.setOnClickListener(this);
        ll_Conversation.setOnClickListener(this);
        ll_Me.setOnClickListener(this);


        fg=getFragmentManager();
        ft=fg.beginTransaction();
        /*
        LS_MeFragment fragment=new LS_MeFragment();
        ft.replace(R.id.fragmentContainer, fragment,null);
        ft.commit();

        tv_Me.setTextColor(Color.parseColor("#2cc71b"));
        tv_Post.setTextColor(Color.parseColor("#929292"));
        tv_Conversation.setTextColor(Color.parseColor("#929292"));
        */

        Intent i = getIntent();
        boolean isChatPush = i.getBooleanExtra("isChatPush", false);

        if(isChatPush){
            ft = fg.beginTransaction();
            LS_CoversationsFragment fragment = new LS_CoversationsFragment();
            ft.replace(R.id.fragmentContainer, fragment, null);
            ft.commit();
            tv_Me.setTextColor(Color.parseColor("#929292"));
            tv_Post.setTextColor(Color.parseColor("#929292"));
            tv_Conversation.setTextColor(Color.parseColor("#2cc71b"));
            tv_Map.setTextColor(Color.parseColor("#929292"));

            checkMeFragment = false;
            checkConversationFragment = true;
            checkMapFragment=false;
        }
        else{
            Bundle bundle = new Bundle();
            bundle.putParcelable(Application.INTENT_EXTRA_LOCATION, currentLocation);

            LS_MapFragment fragment=new LS_MapFragment();
            fragment.setArguments(bundle);
            ft.replace(R.id.fragmentContainer, fragment,"fragment_map");
            ft.commit();

            tv_Me.setTextColor(Color.parseColor("#929292"));
            tv_Post.setTextColor(Color.parseColor("#929292"));
            tv_Conversation.setTextColor(Color.parseColor("#929292"));
            tv_Map.setTextColor(Color.parseColor("#2cc71b"));

            checkMeFragment = false;
            checkConversationFragment = false;
            checkMapFragment=true;
        }



        mLocationProvider = new LS_LocationProvider(this, this);
        ParsePush.subscribeInBackground("user_" + ParseUser.getCurrentUser().getObjectId(), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse,push", "successfully subscribed to the " + "user_" + ParseUser.getCurrentUser().getObjectId() + " channel");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelable(SAVED_LOCATION, currentLocation);
    }

    @Override
    public void onClick(View v) {

        if (v == ll_Me)
        {
          if(checkMeFragment==false)
          {
              ft = fg.beginTransaction();
              LS_MeFragment fragment = new LS_MeFragment();
              ft.replace(R.id.fragmentContainer, fragment, null);
              ft.commit();
              tv_Me.setTextColor(Color.parseColor("#2cc71b"));
              tv_Post.setTextColor(Color.parseColor("#929292"));
              tv_Conversation.setTextColor(Color.parseColor("#929292"));
              tv_Map.setTextColor(Color.parseColor("#929292"));

              checkMeFragment =true;
              checkConversationFragment = false;
              checkMapFragment=false;
          }

        }

        if (v == ll_Conversation)
        {

            if(checkConversationFragment==false)
            {
                ft = fg.beginTransaction();
                LS_CoversationsFragment fragment = new LS_CoversationsFragment();
                ft.replace(R.id.fragmentContainer, fragment, null);
                ft.commit();
                tv_Me.setTextColor(Color.parseColor("#929292"));
                tv_Post.setTextColor(Color.parseColor("#929292"));
                tv_Conversation.setTextColor(Color.parseColor("#2cc71b"));
                tv_Map.setTextColor(Color.parseColor("#929292"));

                checkMeFragment = false;
                checkConversationFragment = true;
                checkMapFragment=false;
            }


        }

        if (v == ll_Post)
        {



                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File file = new File(Environment.getExternalStorageDirectory() +File.separator + "image.jpg");
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(takePictureIntent,100);

                checkMeFragment =false;
                checkConversationFragment = false;
                checkMapFragment=false;


        }


        if (v == ll_Map) {

            if(checkMapFragment==false)
            {
                Bundle bundle = new Bundle();
                bundle.putParcelable(Application.INTENT_EXTRA_LOCATION, currentLocation);
                ft = fg.beginTransaction();

                tv_Me.setTextColor(Color.parseColor("#929292"));
                tv_Post.setTextColor(Color.parseColor("#929292"));
                tv_Conversation.setTextColor(Color.parseColor("#929292"));
                tv_Map.setTextColor(Color.parseColor("#2cc71b"));

                LS_MapFragment fragment = new LS_MapFragment();
                ft.replace(R.id.fragmentContainer, fragment, "fragment_map");
                fragment.setArguments(bundle);
                ft.commit();
                checkMeFragment = false;
                checkConversationFragment = false;
                checkMapFragment=true;
            }



        }

    }
    @Override
    protected void onResume() {
        super.onResume();

       mLocationProvider.connect();

     }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationProvider.disconnect();

    }

    public void handleNewLocation(Location location)
    {

        currentLocation=location;

        Fragment fragment_map = getFragmentManager().findFragmentByTag("fragment_map");

        if(fragment_map != null)
        {
            LS_MapFragment map_fragment = (LS_MapFragment)fragment_map;
            if (map_fragment.isVisible()) {
                map_fragment.MoveCamera(currentLocation);
            }
        }


    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {


        if (requestCode == 100)
        {

            if (resultCode == RESULT_OK)
            {
                try
                {
                    ExifInterface ei = new ExifInterface(Environment.getExternalStorageDirectory() +File.separator + "image.jpg");
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                    switch(orientation)
                    {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            Log.i(DEBUG_TAG, "ORIENTATION_ROTATE_90");
                            System.out.print("ORIENTATION_ROTATE_90");
                            //rotateImage(bitmap, 90);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            Log.i(DEBUG_TAG, "ORIENTATION_ROTATE_180");
                            System.out.print("ORIENTATION_ROTATE_180");
                            //rotateImage(bitmap, 180);
                            break;
                        // etc.
                    }
                }
                catch(IOException e){
                    Log.e(DEBUG_TAG, "Error reading Exif");
                }


                Intent i = new Intent(LS_MainActivity.this, LS_PostActivity.class);
                i.putExtra(Application.INTENT_EXTRA_LOCATION, currentLocation);
                startActivity(i);


            }


        } else if (resultCode == RESULT_CANCELED)
        {

            Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();


        } else
        {


            Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();

        }

    }

    private void getServiceBroadcast() {

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean success = intent.getBooleanExtra("success", false);

                if (!success) {
                    Toast.makeText(getApplicationContext(), "Messaging service failed to start", Toast.LENGTH_LONG).show();
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("com.teespire.leftoverswap.Activities.LS_MainActivity"));
    }


}
