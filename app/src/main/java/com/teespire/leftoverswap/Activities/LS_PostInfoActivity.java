package com.teespire.leftoverswap.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.teespire.leftoverswap.Adapters.LS_ShareIntentListAdapter;
import com.teespire.leftoverswap.ParseClasses.PicturePost;
import com.teespire.leftoverswap.R;
import com.teespire.leftoverswap.util.DateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


public class LS_PostInfoActivity extends Activity
{
    private TextView tv_Title;
    private TextView tv_postedBy;
    private TextView tv_description;
    private Button btn_Contact;
    private Button btn_Cancel;
    private Button btn_Share;
    private Button btn_MarkAsTaken;
    private ParseImageView picPreview;
    private ParseFile picFile;
    private String objectidFromPostInfoActivity;
    private String recepientid;
    private String recipientName;
    private String postTitle;
    private String postObjectId;
    private ParseGeoPoint postLocation;
    static String DEBUG_TAG = "LS_PostInfoActivity";

    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ls_postinfo);
        //showSpinner();
        tv_Title=(TextView)findViewById(R.id.tv_title);
        tv_postedBy=(TextView)findViewById(R.id.tv_postedBy);
        tv_description=(TextView)findViewById(R.id.tv_description);
        picPreview=(ParseImageView)findViewById(R.id.preview_image);
        btn_Contact=(Button)findViewById(R.id.btn_contact);
        btn_Cancel=(Button)findViewById(R.id.btn_cancelPostInfo);
        btn_Share = (Button)findViewById(R.id.btn_share);
        btn_MarkAsTaken = (Button)findViewById(R.id.btn_taken);
        Intent i=getIntent();
        objectidFromPostInfoActivity=i.getStringExtra("Objectid");
        //Toast.makeText(this, objectidFromPostInfoActivity,Toast.LENGTH_SHORT).show();
        dialog=new ProgressDialog(this);
        dialog.setMessage("Please Wait..");
        dialog.show();


        btn_Share.setVisibility(View.INVISIBLE);
        btn_Contact.setVisibility(View.INVISIBLE);
        btn_MarkAsTaken.setVisibility(View.GONE);


        btn_Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(DEBUG_TAG, "Opening conversation for: " + recipientName);
                Intent i = new Intent(getApplicationContext(), LS_ChatActivity.class);

                i.putExtra("Objectid", recepientid);
                i.putExtra("postObjectId", postObjectId);
                i.putExtra("From", "Conversation");
                i.putExtra("fromUsername", recipientName);
                startActivity(i);
            }
        });

        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(DEBUG_TAG, "tap_share");
                sharePost();
            }
        });
        btn_MarkAsTaken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MarkAsTaken().execute();
            }
        });

        new getResult().execute();
    }

    private void sharePost()
    {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        List activities = getPackageManager().queryIntentActivities(sendIntent, 0);

        ResolveInfo fbActivity = new ResolveInfo();

        boolean fbExists = false;

        Iterator<List> actiIterator = activities.iterator();

        while (actiIterator.hasNext())
        {
            ResolveInfo info = (ResolveInfo) actiIterator.next();
            System.out.println(info.activityInfo.packageName);
            if(info.activityInfo.packageName.contains("com.facebook.katana"))
            {
                fbActivity = info;
                fbExists = true;
                actiIterator.remove();
            }
            /*
            else if(info.activityInfo.packageName.contains("com.htc.Twitter3DWidget")
                    || info.activityInfo.packageName.contains("com.htc.htctwitter")
                    || info.activityInfo.packageName.contains("com.htc.engine.twitter"))
            {
                actiIterator.remove();
            }
            */
        }

        String[] array_validActivities={"com.facebook.katana","com.facebook.orca","com.twitter.android"/*,"com.android.email","com.whatsapp","com.google.android.apps.plus"*/};

        Iterator<List> theIterator = activities.iterator();

        while (theIterator.hasNext())
        {
            ResolveInfo info = (ResolveInfo) theIterator.next();
            Boolean isValidActivity=false;
            for(int i = 0; i < array_validActivities.length; i++)
            {
                if(info.activityInfo.packageName.contains(array_validActivities[i]))
                {
                    isValidActivity=true;
                }
            }
            if(!isValidActivity)
            {
                theIterator.remove();
            }
        }

        if(fbExists) activities.add(0, (ResolveInfo)fbActivity);

        if(activities.size() == 0){
            Toast.makeText(this, "No sharing application found", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_share));
        final LS_ShareIntentListAdapter adapter = new LS_ShareIntentListAdapter(this, R.layout.cell_sharing, activities.toArray());
        adapter.setFbExists(fbExists);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(final DialogInterface dialogInterface, int which)
            {
                byte[] photoBytes;
                try
                {
                    photoBytes = picFile.getData();
                }
                catch(ParseException e)
                {
                    return;
                }

                File photo = new File(Environment.getExternalStorageDirectory(), "share_photo.jpg");
                photo.delete();
                try
                {
                    FileOutputStream fos = new FileOutputStream(photo.getPath());
                    fos.write(photoBytes);
                    fos.close();
                }
                catch (java.io.IOException e)
                {
                    Log.i(DEBUG_TAG, "Exception in photoCallback: " + e.getMessage());
                    return;
                }

                final ResolveInfo info = (ResolveInfo) adapter.getItem(which);

                String caption = "";
                caption += postTitle;
                final String sharingMsg = getString(R.string.sharing_msg) + " " + caption;

                final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
                if(info.activityInfo.packageName.contains("twitter"))
                {
                    intent.setType("application/twitter");
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photo));
                    Log.i(DEBUG_TAG, picFile.getUrl());
                }
                else if(info.activityInfo.packageName.contains("facebook"))
                {
                    intent.setType("image/jpeg");
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photo));
                    intent.putExtra(Intent.EXTRA_TITLE, sharingMsg);
                }
                else intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, postTitle);
                intent.putExtra(Intent.EXTRA_TEXT, sharingMsg);

                Log.i(DEBUG_TAG, "subject="+postTitle);
                Log.i(DEBUG_TAG, "msg="+sharingMsg);
                Log.i(DEBUG_TAG, "package="+info.activityInfo.packageName);
                Log.i(DEBUG_TAG, "name="+info.activityInfo.name);
                Log.i(DEBUG_TAG, "caption="+caption);

                new AsyncTask<Void, Void, String>(){
                    @Override
                    protected void onPreExecute() {
                        dialog.setMessage("Sharing post...");
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.US);
                        try{
                            List<Address> locations = geocoder.getFromLocation(postLocation.getLatitude(), postLocation.getLongitude(), 1);
                            Log.i(DEBUG_TAG, "Locations found: " + locations.size());
                            if(locations.size() > 0){
                                return locations.get(0).getLocality();
                            }
                            return null;
                        }
                        catch (IOException ex){
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(String name) {
                        if(dialog != null) dialog.cancel();
                        if(name != null){
                            intent.putExtra(Intent.EXTRA_TEXT, sharingMsg + " near " + name + ".");
                            if(info.activityInfo.packageName.contains("facebook"))
                            {
                                intent.putExtra(Intent.EXTRA_TITLE, sharingMsg + " near " + name + ".");
                            }
                        }
                        startActivity(intent);
                    }
                }.execute();
            }
        });

        builder.create().show();
    }

    private class getResult extends AsyncTask<Void, Void, PicturePost>
    {
        @Override
        protected PicturePost doInBackground(Void... params)
        {
            ParseQuery<PicturePost> query = ParseQuery.getQuery(PicturePost.class);
            // Specify the object id

            try{
                PicturePost item = query.get(objectidFromPostInfoActivity);
                item.getUser().fetchIfNeeded();
                Log.i(DEBUG_TAG, "Post retrieved from server");
                return item;
            }
            catch (ParseException pe){
                Log.e(DEBUG_TAG, "Unable to retrieve post");
                return null;
            }
        }

        @Override
        protected void onPostExecute(PicturePost item) {
            if(item == null){
                Toast.makeText(getApplicationContext(), "Unable to load post", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Date postDate = item.getCreatedAt();
            String postTimeAgo = DateUtils.getTimeAgoString(postDate);
            recepientid = item.getUser().getObjectId();
            recipientName = item.getUser().getUsername();
            postTitle = item.getTitle();
            postObjectId = item.getObjectId();
            postLocation = item.getLocation();
            picFile = item.getPhotoFile();
            tv_Title.setText(item.getTitle());
            tv_postedBy.setText("Posted by " + item.getUser().getUsername() + " about " + postTimeAgo);
            tv_description.setText(item.getDescription());
            picPreview.setParseFile(item.getPhotoFile());

            if(item.getUser().getUsername().equals(ParseUser.getCurrentUser().getUsername())){
                btn_Share.setVisibility(View.VISIBLE);
                btn_Contact.setVisibility(View.INVISIBLE);
                btn_MarkAsTaken.setVisibility(View.VISIBLE);
            }
            else{
                btn_Share.setVisibility(View.VISIBLE);
                btn_Contact.setVisibility(View.VISIBLE);
                btn_MarkAsTaken.setVisibility(View.GONE);
            }

            picPreview.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if(dialog != null) dialog.dismiss();
                }
            });
        }
    }

    class MarkAsTaken extends AsyncTask<Void,Void,Boolean>
    {
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Marking as taken...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        protected Boolean doInBackground(Void... params) {
            ParseQuery<PicturePost> queryTaken = PicturePost.getQuery();

            try{
                PicturePost picturePost = queryTaken.get(postObjectId);
                picturePost.setTaken(true);

                try{
                    picturePost.save();
                    return true;
                }
                catch (ParseException pe2){
                    Log.e(DEBUG_TAG, "Error saving post: " + pe2.getMessage());
                    return false;
                }
            }
            catch (ParseException pe){
                Log.e(DEBUG_TAG, "Error retrieving post: " + pe.getMessage());
                return false;
            }
        }

        protected void onPostExecute(Boolean result) {
            if(result){
                if(dialog != null) dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Post marked as taken", Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(), "Error setting as taken", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
