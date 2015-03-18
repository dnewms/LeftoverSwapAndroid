package com.teespire.leftoverswap.Adapters;

/**
 * Created by teespire on 14/02/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.teespire.leftoverswap.ParseClasses.Conversation;
import com.teespire.leftoverswap.ParseClasses.PicturePost;
import com.teespire.leftoverswap.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class LS_ConversationAdapter extends BaseAdapter {

    private static String DEBUG_TAG = "LS_ConversationAdapter";

    Context context;
    List<ParseObject> rowItems;
    ArrayList<ParseObject> threadItems;
    ParseUser currentUser;

    public LS_ConversationAdapter (Context context, List<ParseObject> items) {
        this.context = context;
        this.rowItems = items;

        LinkedHashMap<String, ParseObject> conversationHashMap;
        conversationHashMap = new LinkedHashMap<>();
        currentUser = ParseUser.getCurrentUser();

        for(ParseObject obj:rowItems){
            ParseUser fromUser = (ParseUser)(obj.get("fromUser"));
            ParseUser toUser = (ParseUser)(obj.get("toUser"));

            if(fromUser.getObjectId().equals(currentUser.getObjectId())){
                conversationHashMap.put(toUser.getObjectId(), obj);
            }
            else{
                conversationHashMap.put(fromUser.getObjectId(), obj);
            }
        }

        threadItems = new ArrayList<ParseObject>(conversationHashMap.values());
        notifyDataSetChanged();
        Log.d(DEBUG_TAG, "Total of " + threadItems.size() + " threads");
    }


    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = mInflater.inflate(R.layout.conversation, null);
        final TextView conversationName = (TextView) convertView.findViewById(R.id.tv_conversationName);
        final TextView postName = (TextView) convertView.findViewById(R.id.tv_forWhichPost);
        final TextView message = (TextView) convertView.findViewById(R.id.tv_message);
        final ParseImageView pic = (ParseImageView) convertView.findViewById(R.id.iv_down_Pic);
        RelativeLayout relativeLayout_conv_cell = (RelativeLayout)convertView.findViewById(R.id.relativeLayout_conv_cell);

        Conversation conv = (Conversation)threadItems.get(position);

        ParseUser fromUser = conv.getFromUser();
        ParseUser toUser = conv.getToUser();
        PicturePost post = conv.getPost();

        if(fromUser.getObjectId().equals(currentUser.getObjectId())){
            try{
                toUser = toUser.fetchIfNeeded();
                conversationName.setText(toUser.getUsername());
            }
            catch(ParseException pe){
                Log.e(DEBUG_TAG, "Error fetching user data: " + pe.getMessage());
            }
        }
        else{
            try{
                fromUser.fetchIfNeeded();
                conversationName.setText(fromUser.getUsername());
            }
            catch(ParseException pe){
                Log.e(DEBUG_TAG, "Error fetching user data: " + pe.getMessage());
            }
        }

        try{
            post = post.fetchIfNeeded();
        }
        catch (ParseException pe){
            Log.e(DEBUG_TAG, "Error fetching post data: " + pe.getMessage());
        }
        postName.setText(post.getTitle());

        message.setText(conv.getMessage());
        relativeLayout_conv_cell.setVisibility(View.VISIBLE);

        pic.setParseFile(post.getThumbnailFile());
        pic.loadInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                //relativeLayout_conv_cell.setVisibility(View.VISIBLE);
                //Log.i(DEBUG_TAG, "Thumbnail fetched");
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return threadItems.size();
    }

    @Override
    public Object getItem(int position) {
        return threadItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return threadItems.indexOf(getItem(position));
    }
}