package com.teespire.leftoverswap.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.teespire.leftoverswap.Application;
import com.teespire.leftoverswap.LS_Manager;
import com.teespire.leftoverswap.ParseClasses.PicturePost;
import com.teespire.leftoverswap.R;

import java.io.ByteArrayOutputStream;
import java.io.File;


public class LS_PostActivity extends Activity
{
    private static final String DEBUG_TAG = LS_PostActivity.class.getSimpleName();

    private ImageView iv_pictureTaken;
    private EditText editText_title;
    private EditText editText_description;
    private Button btn_Post;
    private Button btn_Cancel;
    Location location;
    ParseGeoPoint geoPoint;
    private ParseFile photoFile;
    private ParseFile thumbnailFile;
    int width;
    int height;
    Bitmap fullSizeImage;
    File file;
    ProgressDialog dialog;
    PicturePost post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        iv_pictureTaken = (ImageView) findViewById(R.id.iv_picture);
        editText_title = (EditText) findViewById(R.id.editText_title);
        editText_description = (EditText) findViewById(R.id.editText_description);
        btn_Post = (Button) findViewById(R.id.btn_donePost);
        btn_Cancel = (Button) findViewById(R.id.btn_cancelPost);

        Intent i = getIntent();
        location = i.getParcelableExtra(Application.INTENT_EXTRA_LOCATION);
    }

    @Override
     protected  void onResume()
    {
        super.onResume();


        if(location!=null) {
            geoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
            ViewTreeObserver vto = iv_pictureTaken.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    iv_pictureTaken.getViewTreeObserver().removeOnPreDrawListener(this);
                    width= iv_pictureTaken.getMeasuredWidth();
                    height= iv_pictureTaken.getMeasuredHeight();
                    file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
                    String path=file.getAbsolutePath();
                    BitmapWorkerTask task = new BitmapWorkerTask();
                    task.execute(path);
                    //  bitmap = LS_Manager.decodeSampledBitmapFromFile(file.getAbsolutePath(), width, height);
                    // iv_pictureTaken.setImageBitmap(bitmap);
                    return true;
                }
            });
        }
        else
        {
            Toast.makeText(LS_PostActivity.this,"Your location is not avalable",Toast.LENGTH_SHORT).show();
            ViewTreeObserver vto = iv_pictureTaken.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    iv_pictureTaken.getViewTreeObserver().removeOnPreDrawListener(this);
                    width= iv_pictureTaken.getMeasuredWidth();
                    height= iv_pictureTaken.getMeasuredHeight();
                    file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
                    String path=file.getAbsolutePath();
                    BitmapWorkerTask task = new BitmapWorkerTask();
                    task.execute(path);
                    //  bitmap = LS_Manager.decodeSampledBitmapFromFile(file.getAbsolutePath(), width, height);
                    // iv_pictureTaken.setImageBitmap(bitmap);
                    return true;
                }
            });
        }
        editText_title.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                updateBtnstate();
            }
        });


        editText_description.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {

                updateBtnstate();
            }
        });


        btn_Post.setEnabled(false);
        btn_Post.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(LS_Manager.NetworkAvailable(LS_PostActivity.this))
                {
                    // post();
                    if(geoPoint!=null) {
                        new Post().execute();
                    }
                    else
                    {
                        Toast.makeText(LS_PostActivity.this,"Your location is not available",Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        btn_Cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               /* Intent i=new Intent(LS_PostActivity.this,LS_MainActivity.class);
                startActivity(i);*/
                finish();
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
    }



    public void updateBtnstate()
    {
        if(editText_title.getText().toString().trim().length()>0 && editText_description.getText().toString().trim().length()>0 )
        {
            btn_Post.setEnabled(true);
        }
    }



    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap>
    {


        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
           String data=params[0];

            fullSizeImage=LS_Manager.decodeSampledBitmapFromFile(data,iv_pictureTaken.getMeasuredWidth(),iv_pictureTaken.getMeasuredHeight());


            return fullSizeImage;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {

               iv_pictureTaken.setImageBitmap(bitmap);

        }
    }


    class Post extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(LS_PostActivity.this);
            dialog.setMessage("Posting...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        // Decode image in background.
        @Override
        protected Boolean doInBackground(Void... params)
        {
            final String Title = editText_title.getText().toString().trim();
            final String Desc = editText_description.getText().toString().trim();
            post = new PicturePost();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            fullSizeImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] scaledData = bos.toByteArray();
            ByteArrayOutputStream bosThumb = new ByteArrayOutputStream();

            Bitmap thumbnailBitmap = ThumbnailUtils.extractThumbnail(fullSizeImage, 86, 86);
            Bitmap roundCornerBitmap = toRoundCorner (thumbnailBitmap, 14);
          //  thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bosThumb);
            roundCornerBitmap.compress(Bitmap.CompressFormat.PNG, 100, bosThumb);
            byte[] thumbnailBytes = bosThumb.toByteArray();

            // Save the scaled image to Parse
            photoFile=new ParseFile("file", scaledData);
            thumbnailFile = new ParseFile("file", thumbnailBytes);
            // Set the location to the current user's location
            post.setLocation(geoPoint);
            post.setTitle(Title);
            post.setPhotoFile(photoFile);
            post.setThumbnailFile(thumbnailFile);
            post.setDescription(Desc);
            post.setUser(ParseUser.getCurrentUser());

            ParseACL acl = new ParseACL();

            // Give public read access
            acl.setPublicReadAccess(true);
            acl.setPublicWriteAccess(true);
            post.setACL(acl);

            // Save the post

            try{
                post.save();
                return true;
            }
            catch (ParseException pe){
                Log.e(DEBUG_TAG, "Error posting to Parse");
                return false;
            }
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Boolean result)
        {
            if(result){
                Log.i(DEBUG_TAG, "Posted successfully");
                Toast.makeText(LS_PostActivity.this,"Successfully posted",Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                Toast.makeText(LS_PostActivity.this, "Error posting", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }



}