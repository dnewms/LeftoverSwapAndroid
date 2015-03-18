package com.teespire.leftoverswap;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.teespire.leftoverswap.Fragments.LS_DialogFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by teespire on 27/01/2015.
 */
public class LS_Manager
{
    private static LS_Manager sharedInstance;




   /* public static LS_Manager getSharedInstance()
    {
        if (sharedInstance == null)
        {
            sharedInstance = new LS_Manager();
        }
        return sharedInstance;
    }*/

    public static boolean NetworkAvailable (Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            LS_DialogFragment dialogFragment = new LS_DialogFragment();
            dialogFragment.buildDialog(context).show();


            return false;
        }
    }
    public static  Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      //  inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public static  String getRealPathFromURI(Uri uri,Context context) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public static File createFolder()
    {

        File imageStorageFolder = new File(Environment.getExternalStorageDirectory()+File.separator+"LeftoverSwap1");
        if (!imageStorageFolder.exists())
        {
            imageStorageFolder.mkdirs();
            Log.e("Folder created at: ", imageStorageFolder.toString());

        }
        return imageStorageFolder;
    }


    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap d=BitmapFactory.decodeFile(path, options);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        d= Bitmap.createBitmap(d, 0, 0, d.getWidth(),d.getHeight(), matrix, true);

        return d;
    }


        public static Bitmap  createThumbnails(Bitmap bmp)
        {
            File file = new File(Environment.getExternalStorageDirectory() +File.separator + "imageThumbnail.jpg");
            try
            {
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);


                out.flush();
                out.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return  bmp;
        }




}
