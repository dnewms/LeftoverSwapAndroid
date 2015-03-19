package com.teespire.leftoverswap.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.teespire.leftoverswap.Activities.LS_ChatActivity;
import com.teespire.leftoverswap.Adapters.LS_ConversationAdapter;
import com.teespire.leftoverswap.ParseClasses.Conversation;
import com.teespire.leftoverswap.R;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by teespire on 16/01/2015.
 */
public class LS_CoversationsFragment extends Fragment implements OnItemClickListener {

    private static final String DEBUG_TAG = LS_CoversationsFragment.class.getSimpleName();

    private ListView messagesList;
    private LS_ConversationAdapter adapter;
    private static List<ParseObject> parseObjectList;
    private static String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.fragment_conversations, container, false);
        messagesList = (ListView) view.findViewById(R.id.lv_conversations);
        messagesList.setOnItemClickListener(this);
        TextView emptyText = (TextView)view.findViewById(android.R.id.empty);
        messagesList.setEmptyView(emptyText);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(ParseUser.getCurrentUser().getObjectId().equals(currentUserId)){
            if(parseObjectList != null){
                if(parseObjectList.size() != 0){
                    adapter = new LS_ConversationAdapter(getActivity(), parseObjectList);
                    messagesList.setAdapter(adapter);
                }
            }
        }
        else parseObjectList = null;

        new FetchConversation().execute();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        //Log.i(DEBUG_TAG, "onActivityCreated");
    }

    class  FetchConversation extends AsyncTask<Void,Void,List<ParseObject>>
    {
        @Override
        protected List<ParseObject> doInBackground(Void... params)
        {
            Log.d(DEBUG_TAG, "Running Parse query for user: " + ParseUser.getCurrentUser().getUsername());

            List<ParseQuery<ParseObject>> queryList = new ArrayList<ParseQuery<ParseObject>>();

            ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Conversation");
            query1.whereEqualTo("toUser", ParseUser.getCurrentUser());

            ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Conversation");
            query2.whereEqualTo("fromUser", ParseUser.getCurrentUser());

            queryList.add(query1);
            queryList.add(query2);

            ParseQuery<ParseObject> mainQuery = ParseQuery.or(queryList);
            mainQuery.orderByAscending("updatedAt");

            List<ParseObject> parseObjects = new ArrayList<>();

            try{
                parseObjects = mainQuery.find();
            }
            catch (ParseException pe){
                Log.e(DEBUG_TAG, "Parse Query Error: " + pe.getMessage());
            }

            return parseObjects;
        }

        @Override
        protected void onPostExecute(List<ParseObject> parseObjects) {
            Log.d(DEBUG_TAG, "Found " + parseObjects.size() + " conversations");
            if(parseObjects.size() == 0) return;

            adapter = new LS_ConversationAdapter(getActivity(), parseObjects);
            messagesList.setAdapter(adapter);

            currentUserId = ParseUser.getCurrentUser().getObjectId();
            parseObjectList = parseObjects;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Conversation conv = (Conversation) messagesList.getItemAtPosition(position);

        String fromUsername;
        String convFromUserObjId;

        if(conv.getFromUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
            convFromUserObjId = conv.getToUser().getObjectId();
            fromUsername = conv.getToUser().getUsername();
        }
        else{
            convFromUserObjId = conv.getFromUser().getObjectId();
            fromUsername = conv.getFromUser().getUsername();
        }

        String postObjectId = conv.getPost().getObjectId();

        Log.d(DEBUG_TAG, "Opening conversation for: " + fromUsername);
        Intent i = new Intent(getActivity(), LS_ChatActivity.class);

        i.putExtra("Objectid", convFromUserObjId);
        i.putExtra("postObjectId", postObjectId);
        i.putExtra("From", "Conversation");
        i.putExtra("fromUsername", fromUsername);
        startActivity(i);
    }
 }
