package com.teespire.leftoverswap.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.teespire.leftoverswap.ParseClasses.Conversation;
import com.teespire.leftoverswap.R;
import com.teespire.leftoverswap.util.DateUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by Matih on 21/2/2015.
 */
public class LS_ChatListAdapter extends BaseAdapter {
    private static String DEBUG_TAG = "LS_ChatListAdapter";

    private static long TIME_GROUP_DIFFERENCE = 30;

    private List<ParseObject> parseObjectArrayList;
    private ParseUser currentUser;

    public LS_ChatListAdapter() {
        if(currentUser == null){
            currentUser = ParseUser.getCurrentUser();
        }
    }

    @Override
    public int getCount() {
        return parseObjectArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return parseObjectArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Conversation conv = (Conversation)parseObjectArrayList.get(position);

        //if (convertView == null)
        {
            try{
                conv = conv.fetchIfNeeded();
            }
            catch (ParseException pe){
                Log.e(DEBUG_TAG, "Error fetching conversation: " + pe.getMessage());
            }

            int res = 0;

            if (conv.getFromUser().getObjectId().equals(currentUser.getObjectId())) {
                res = R.layout.message_right;
            } else {
                res = R.layout.message_left;
            }
            LayoutInflater layoutInfObj = LayoutInflater.from(parent.getContext());
            convertView = layoutInfObj.inflate(res, parent, false);
        }

        String message = conv.getMessage();
        Date messageDate = conv.getCreatedAt();

        TextView txtView_Message = (TextView)convertView.findViewById(R.id.txtMessage);
        TextView txtView_timestamp = (TextView)convertView.findViewById(R.id.txtDate);

        txtView_Message.setText(message);

        if(position == 0){
            txtView_timestamp.setVisibility(View.VISIBLE);
            txtView_timestamp.setText(DateUtils.formatToYesterdayOrToday(messageDate));
        }
        else{
            Date prevMessageDate =  parseObjectArrayList.get(position - 1).getCreatedAt();
            if(DateUtils.minutesDiff(prevMessageDate, messageDate) > TIME_GROUP_DIFFERENCE){

                txtView_timestamp.setVisibility(View.VISIBLE);
                txtView_timestamp.setText(DateUtils.formatToYesterdayOrToday(messageDate));
            }
            else{
                txtView_timestamp.setVisibility(View.GONE);
            }
        }

        //Log.d(DEBUG_TAG, "Message from: " + conv.getFromUser().getObjectId()
        //                + " to: " + conv.getToUser().getObjectId());

        return convertView;
    }

    public void setParseObjectArrayList(List<ParseObject> parseObjectArrayList) {
        this.parseObjectArrayList = parseObjectArrayList;
    }

    public void addMessage(ParseObject pObj)
    {
        parseObjectArrayList.add(pObj);
        notifyDataSetChanged();
    }

    public void removeLastMessage()
    {
        if(parseObjectArrayList.size() > 0){
            parseObjectArrayList.remove(parseObjectArrayList.size() - 1);
        }
        notifyDataSetChanged();
    }
}
