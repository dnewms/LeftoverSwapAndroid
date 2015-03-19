package com.teespire.leftoverswap.Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.teespire.leftoverswap.Activities.LS_DispatchActivity;
import com.teespire.leftoverswap.Activities.LS_FeedBackActivity;
import com.teespire.leftoverswap.ParseClasses.PicturePost;
import com.teespire.leftoverswap.R;
import com.teespire.leftoverswap.util.DateUtils;

import java.util.Date;


/**
 * Created by teespire on 16/01/2015.
 */
public class LS_MeFragment extends Fragment{

    private static String DEBUG_TAG = "LS_MeFragment";

    Button btn_logout;
    Button btn_feedBack;
    TextView tv_userName;
    ListView lv_posts;
    TextView emptyText;
    public ParseQueryAdapter postsQueryAdapter;

    @Override
    public void onResume() {
        super.onResume();
        new fetchPosts().execute();


        btn_feedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),LS_FeedBackActivity.class);
                startActivity(i);
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParsePush.unsubscribeInBackground("user_" + ParseUser.getCurrentUser().getObjectId());
                ParseUser.logOut();
                // Start and intent for the dispatch activity
                Intent intent = new Intent(getActivity(), LS_DispatchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                }
            });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View  view=inflater.inflate(R.layout.fragment_me, container,false);
        btn_logout=(Button)view.findViewById(R.id.btn_logout);
        tv_userName=(TextView)view.findViewById(R.id.tv_UserName);
        tv_userName.setText(ParseUser.getCurrentUser().getUsername());
         lv_posts=(ListView)view.findViewById(R.id.lv_myposts);
        btn_feedBack=(Button)view.findViewById(R.id.btn_feedback);
        emptyText = (TextView)view.findViewById(android.R.id.empty);
        lv_posts.setEmptyView(emptyText);

        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    class  fetchPosts extends AsyncTask<Void,Void,Void>
    {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... params) {
            ParseQueryAdapter.QueryFactory<PicturePost> factory =new ParseQueryAdapter.QueryFactory<PicturePost>()
            {
                public ParseQuery<PicturePost> create()
                {
                    ParseUser user = ParseUser.getCurrentUser();
                    Date olderDate = DateUtils.getOlderDateFromNow(20);
                    ParseQuery<PicturePost> query = PicturePost.getQuery();
                    query.whereEqualTo("user", user);
                    query.whereNotEqualTo("taken", true);
                    query.orderByDescending("createdAt");
                    query.whereGreaterThanOrEqualTo("createdAt", olderDate);

                   // query.fromLocalDatastore();

                    return query;
                }
             };

            postsQueryAdapter = new ParseQueryAdapter<PicturePost>(getActivity(), factory)
            {
                @Override
                public View getItemView(final PicturePost post, View view, final ViewGroup parent)
                {
                    if (view == null) {
                        view = View.inflate(getContext(), R.layout.list_post, null);
                    }

                    TextView title = (TextView) view.findViewById(R.id.tv_down_title);
                    TextView desc = (TextView) view.findViewById(R.id.tv_down_desc);
                    Button btn_markAsTaken = (Button) view.findViewById(R.id.btn_markAsTaken);
                    ParseImageView pic = (ParseImageView) view.findViewById(R.id.iv_down_Pic);

                    title.setText(post.getTitle());
                    desc.setText(post.getDescription());
                    pic.setParseFile(post.getThumbnailFile());

                    pic.loadInBackground();

                    btn_markAsTaken.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            final ProgressDialog progressDialog = new ProgressDialog(getActivity(), ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
                            progressDialog.setMessage("Marking as taken");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();

                            ParseQuery<PicturePost> queryTaken = PicturePost.getQuery();

                            queryTaken.getInBackground(post.getObjectId(), new GetCallback<PicturePost>()
                            {
                                @Override
                                public void done(PicturePost picturePost, ParseException e)
                                {
                                    if(e != null) {
                                        Log.e(DEBUG_TAG, "Error getting post: " + e.getMessage());
                                        if(progressDialog != null) progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "Error setting as taken", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    post.setTaken(true);

                                    post.saveInBackground(new SaveCallback()
                                    {
                                        @Override
                                        public void done(ParseException e)
                                        {
                                            if(progressDialog != null) progressDialog.dismiss();
                                            if(e != null) {
                                                Log.e(DEBUG_TAG, "Error saving post as taken: " + e.getMessage());
                                                Toast.makeText(getActivity(), "Error setting as taken", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            postsQueryAdapter.loadObjects();
                                            postsQueryAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            });

                        }
                    });

                    return view;
                }
            };

            return null;
        }

        protected void onPostExecute(Void o) {
            super.onPostExecute(o);
            lv_posts.setAdapter(postsQueryAdapter);
        }
    }

}

