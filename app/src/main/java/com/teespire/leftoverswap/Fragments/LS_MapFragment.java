package com.teespire.leftoverswap.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.teespire.leftoverswap.Activities.LS_PostInfoActivity;
import com.teespire.leftoverswap.Application;
import com.teespire.leftoverswap.ParseClasses.PicturePost;
import com.teespire.leftoverswap.R;
import com.teespire.leftoverswap.util.DateUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Created by teespire on 26/01/2015.
 */
public class LS_MapFragment extends Fragment
{
    private static String DEBUG_TAG = "LS_MapFragment";

    private static String SAVED_LOCATION = "location";
    private static String SAVED_CAMERA_TARGET = "cameratarget";
    private static String SAVED_ZOOMLEVEL = "zoomlevel";
    private static final int MAX_HASHMAP_SIZE = 5;

    private MapFragment fragment;
    private GoogleMap map;
    private static View view;
    private Activity mContext;
    private Location location;
    private Double currentLatitude;
    private Double currentLongitude;
    private LatLng currentCameraTarget;
    private float currentzoomlevel;
    private LatLngBounds bounds;
    private ProgressDialog dialog;
    private HashMap<String, byte[]> thumbPicLoadHashMap;
    private HashMap<String, PicturePost> postHashMap;
    DoMapQuery doMapQuery;
    String currentUserId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (view != null)
        {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try
        {
            view = inflater.inflate(R.layout.activity_map, container, false);
        } catch (InflateException e)
        {
        /* map is already there, just return view as it is */
        }

        mContext = getActivity();

        currentzoomlevel = 15;
        Bundle bundle = this.getArguments();
        location = bundle.getParcelable(Application.INTENT_EXTRA_LOCATION);

        if(savedInstanceState != null){
            Log.i(DEBUG_TAG, "Restoring Map Camera Position");
            location = (Location)savedInstanceState.getParcelable(SAVED_LOCATION);
            currentzoomlevel = savedInstanceState.getFloat(SAVED_ZOOMLEVEL);
            currentCameraTarget = (LatLng)savedInstanceState.getParcelable(SAVED_CAMERA_TARGET);
        }

        //
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        thumbPicLoadHashMap = new HashMap<>();
        postHashMap = new HashMap<>();
        doMapQuery = new DoMapQuery();

        FragmentManager fm = getFragmentManager();
        fragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (fragment == null) {
            fragment = MapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, fragment).commit();
        }

        if(savedInstanceState != null){
            location = (Location)savedInstanceState.getParcelable(SAVED_LOCATION);
            currentzoomlevel = savedInstanceState.getFloat(SAVED_ZOOMLEVEL);
            currentCameraTarget = (LatLng)savedInstanceState.getParcelable(SAVED_CAMERA_TARGET);
            /*
            Log.i(DEBUG_TAG, "onActivityCreated: Restoring Map position :: Location=" + location.toString()
                    + " Zoom Level=" + currentzoomlevel
                    + " Camera Target=" + currentCameraTarget.toString());
            */
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        Log.i(DEBUG_TAG, "onSaveInstanceState: Saving Map position");
        savedInstanceState.putParcelable(SAVED_LOCATION, location);
        savedInstanceState.putFloat(SAVED_ZOOMLEVEL, currentzoomlevel);
        savedInstanceState.putParcelable(SAVED_CAMERA_TARGET, currentCameraTarget);
        /*
        Log.i(DEBUG_TAG, "onActivityCreated: Restoring Map position :: Location=" + location.toString()
                + " Zoom Level=" + currentzoomlevel
                + " Camera Target=" + currentCameraTarget.toString());
        */
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.i(DEBUG_TAG, "onPause");
        if(map != null){
            Log.i(DEBUG_TAG, "Updating Map Camera Position");
            currentCameraTarget = map.getCameraPosition().target;
            currentzoomlevel = map.getCameraPosition().zoom;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        currentUserId = ParseUser.getCurrentUser().getObjectId();

        Log.i(DEBUG_TAG, "onResume");
        if (map == null)
        {
            map = fragment.getMap();
            map.setInfoWindowAdapter(new MyInfoWindowAdapter());
            map.setMyLocationEnabled(false);
            bounds = map.getProjection().getVisibleRegion().latLngBounds;

            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
            {
                @Override
                public void onInfoWindowClick(Marker marker)
                {
                    String id = marker.getSnippet();
                    Log.d(DEBUG_TAG, "PostID: " + id);
                    Intent intent = new Intent(getActivity(), LS_PostInfoActivity.class);
                    intent.putExtra("Objectid", id);
                    startActivity(intent);
                }
            });

            map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    //Log.i(DEBUG_TAG, "Camera moved");
                    currentzoomlevel = cameraPosition.zoom;
                    currentCameraTarget = cameraPosition.target;
                    bounds = map.getProjection().getVisibleRegion().latLngBounds;
                    doMapQuery.cancel(true);
                    doMapQuery = new DoMapQuery();
                    doMapQuery.execute();
                }
            });

            map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    Log.i(DEBUG_TAG, "My location tapped");
                    if(location != null) {
                        currentCameraTarget = new LatLng(location.getLatitude(), location.getLongitude());
                        currentzoomlevel = 15;
                        //map.animateCamera(CameraUpdateFactory.zoomTo(currentzoomlevel));
                        //map.animateCamera(CameraUpdateFactory.newLatLng(currentCameraTarget));
                        map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(currentCameraTarget, currentzoomlevel, 0, 0)));
                    }
                    else{
                        Toast.makeText(getActivity(), "Getting your location...", Toast.LENGTH_SHORT).show();
                    }

                    return true;
                }
            });
        }

        if(location!=null)
        {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            LatLng latLng = new LatLng(currentLatitude, currentLongitude);
            if(currentCameraTarget != null) {
                Log.i(DEBUG_TAG, "Restoring Map Camera Position");
                latLng = new LatLng(currentCameraTarget.latitude, currentCameraTarget.longitude);
            }
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.animateCamera(CameraUpdateFactory.zoomTo(currentzoomlevel));
            currentCameraTarget = latLng;
            map.setMyLocationEnabled(true);
        }
        else
        {
            map.setMyLocationEnabled(false);
           // Toast.makeText(getActivity(),"Your location is not available",Toast.LENGTH_SHORT).show();
        }

         postHashMap.clear();
         map.clear();

         doMapQuery.cancel(true);
         doMapQuery = new DoMapQuery();
         doMapQuery.execute();
    }

    public void MoveCamera(Location newLocation)
    {
        if(newLocation!=null)
        {
            currentLatitude = newLocation.getLatitude();
            currentLongitude = newLocation.getLongitude();
            LatLng latLng = new LatLng(currentLatitude, currentLongitude);
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.animateCamera(CameraUpdateFactory.zoomTo(15));
            location = newLocation;
            map.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();

    }

    class DoMapQuery extends AsyncTask<Void, MarkerOptions, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            ParseQuery<PicturePost> mapQuery = PicturePost.getQuery();

            Date olderDate = DateUtils.getOlderDateFromNow(20);
            ParseGeoPoint southwest = new ParseGeoPoint(bounds.southwest.latitude, bounds.southwest.longitude);
            ParseGeoPoint northeast = new ParseGeoPoint(bounds.northeast.latitude, bounds.northeast.longitude);
            mapQuery.whereWithinGeoBox("location", southwest, northeast);
            mapQuery.whereNotEqualTo("taken", true);
            mapQuery.whereGreaterThanOrEqualTo("createdAt", olderDate);

            List<PicturePost> objects;

            try{
                objects = mapQuery.find();
            }
            catch (ParseException pe){
                Log.e(DEBUG_TAG, "Error retrieving posts: " + pe.getMessage());
                return null;
            }

            if(objects == null) return null;

            for (PicturePost postObject : objects)
            {
                if(isCancelled()){
                    break;
                }

                PicturePost postFromHM = postHashMap.get(postObject.getObjectId());
                if(postFromHM == null)
                {
                    // New post

                    try{
                        postFromHM = postObject.fetchIfNeeded();

                    }
                    catch (ParseException pe){
                        Log.e(DEBUG_TAG, "Error fetching post: " + pe.getMessage());
                        continue;
                    }

                    if(postFromHM.getThumbnailFile() == null) {
                        Log.e(DEBUG_TAG, "No thumbnail file for post: " + postFromHM.getObjectId());
                        continue;
                    }

                    postHashMap.put(postFromHM.getObjectId(), postFromHM);

                    byte[] bytesFromHashMap = thumbPicLoadHashMap.get(postFromHM.getObjectId());

                    if(bytesFromHashMap == null){
                        try {
                            bytesFromHashMap = postFromHM.getThumbnailFile().getData();
                        }
                        catch (ParseException pe) {
                            Log.e(DEBUG_TAG, "Error fetching post thumbnail: " + pe.getMessage());
                            continue;
                       }
                    }

                    MarkerOptions markerOpts;

                    if (postFromHM.getUser().getObjectId().equals(currentUserId))
                    {
                        markerOpts = new MarkerOptions()
                                .position(new LatLng(postObject.getLocation().getLatitude(),
                                        postObject.getLocation().getLongitude()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                .snippet(postObject.getObjectId());
                    }
                    else
                    {
                        markerOpts = new MarkerOptions()
                                .position(new LatLng(postObject.getLocation().getLatitude(),
                                        postObject.getLocation().getLongitude()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                .snippet(postObject.getObjectId());
                    }

                    thumbPicLoadHashMap.put(postFromHM.getObjectId(), bytesFromHashMap);
                    publishProgress(markerOpts);
                }
                else
                {
                    try{
                        postFromHM = postObject.fetchIfNeeded();

                    }
                    catch (ParseException pe){
                        Log.e(DEBUG_TAG, "Error fetching post: " + pe.getMessage());
                        continue;
                    }

                    postHashMap.put(postFromHM.getObjectId(), postFromHM);
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(MarkerOptions... values) {
            map.addMarker(values[0]);
        }
    }

    class MyInfoWindowAdapter implements InfoWindowAdapter
    {
        private   View myContentsView;

        MyInfoWindowAdapter(){
            myContentsView = getActivity().getLayoutInflater().inflate(R.layout.infowindow, null);
        }

        @Override
        public View getInfoContents(Marker marker)
        {
            //Log.d(DEBUG_TAG, "Post id: " + marker.getSnippet());
            String id = marker.getSnippet();

            PicturePost post;
            try
            {
                post = postHashMap.get(id);
                post = post.fetchIfNeeded();
              // Log.d(DEBUG_TAG, "Post title: " + post.getTitle());
                Log.e("Post Title",""+post.getTitle());

                Date postDate = post.getCreatedAt();
                String postTitle = post.getTitle();
                String postTimeAgo = DateUtils.getTimeAgoString(postDate);

                Log.e("Post Time Ago: " , postTimeAgo);
                TextView tvTitle = (TextView) myContentsView.findViewById(R.id.textView_infowindow_title);
                TextView textView_infowindow_timeago = (TextView)myContentsView.findViewById(R.id.textView_infowindow_timeago);

                tvTitle.setText(postTitle);
                textView_infowindow_timeago.setText(postTimeAgo);

                ImageView picPreview = (ImageView)myContentsView.findViewById(R.id.preview_image);
                byte[] fileData = thumbPicLoadHashMap.get(id);
                if(fileData != null){
                    Bitmap bmp = BitmapFactory.decodeByteArray(fileData, 0, fileData.length);
                    picPreview.setImageBitmap(bmp);
                }
            }
            catch (Exception pe){
                Log.e(DEBUG_TAG, pe.getMessage());
            }

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    /*
    public class MapClusterItem implements ClusterItem{

        @Override
        public LatLng getPosition() {
            return null;
        }
    }
    */
}
