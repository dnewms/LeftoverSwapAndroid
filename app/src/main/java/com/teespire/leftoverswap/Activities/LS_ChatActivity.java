package com.teespire.leftoverswap.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.teespire.leftoverswap.Adapters.LS_ChatListAdapter;
import com.teespire.leftoverswap.ParseClasses.Conversation;
import com.teespire.leftoverswap.ParseClasses.PicturePost;
import com.teespire.leftoverswap.R;
import com.teespire.leftoverswap.util.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LS_ChatActivity extends Activity
{
    private static String DEBUG_TAG = "LS_ChatActivity";

    private String currentUserName;
    private EditText messageBodyField;
    private TextView tv_recepientName;
    private String messageBody;
    private LS_ChatListAdapter messageAdapter;
    private ListView messagesList;
    private Button btn_send;

    private IntentFilter mIntentFilter;

    String fromUserobjectid;
    String postObjectId;
    String conversationWith;
    String checkActivty;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ls_chat);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.parse.push.intent.RECEIVE");

        tv_recepientName=(TextView)findViewById(R.id.tv_RecepientName);
        messagesList = (ListView) findViewById(R.id.listMessages);
        messageBodyField = (EditText) findViewById(R.id.messageBodyField);
        btn_send = (Button)findViewById(R.id.sendButton);

        Intent intent = getIntent();
        fromUserobjectid = intent.getStringExtra("Objectid");
        postObjectId = intent.getStringExtra("postObjectId");
        checkActivty=intent.getStringExtra("From");
        conversationWith = intent.getStringExtra("fromUsername");

        tv_recepientName.setText(conversationWith);

        currentUserName=ParseUser.getCurrentUser().getUsername();

        List<ParseObject> parseObjectArrayList = new ArrayList<ParseObject>();
        messageAdapter = new LS_ChatListAdapter();
        messageAdapter.setParseObjectArrayList(parseObjectArrayList);
        messagesList.setAdapter(messageAdapter);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        new getResult().execute();
    }

    private class getResult extends AsyncTask<Void, Void, List<ParseObject>>
    {
        @Override
        protected List<ParseObject> doInBackground(final Void... params)
        {
            if(checkActivty.equalsIgnoreCase("Conversation"))
            {
                Log.e(DEBUG_TAG, "Loading conversations");
                Date olderDate = DateUtils.getOlderDateFromNow(180);
                ParseUser fromUser = new ParseUser();
                fromUser.setObjectId(fromUserobjectid);

                List<ParseQuery<ParseObject>> queryList = new ArrayList<ParseQuery<ParseObject>>();

                ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Conversation");
                query1.whereEqualTo("toUser", ParseUser.getCurrentUser());
                query1.whereEqualTo("fromUser", fromUser);

                ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Conversation");
                query2.whereEqualTo("fromUser", ParseUser.getCurrentUser());
                query2.whereEqualTo("toUser", fromUser);

                queryList.add(query1);
                queryList.add(query2);

                ParseQuery<ParseObject> mainQuery = ParseQuery.or(queryList);
                mainQuery.orderByAscending("updatedAt");
                mainQuery.whereGreaterThanOrEqualTo("createdAt", olderDate);


                List<ParseObject> parseObjects = new ArrayList<>();

                try{
                    parseObjects = mainQuery.find();
                }
                catch (ParseException pe){
                    Log.e(DEBUG_TAG, "Error fetching conversations: " + pe.getMessage());
                }

                Log.d(DEBUG_TAG, "Found " + parseObjects.size() + " conversations");

                return parseObjects;
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<ParseObject> parseObjects) {
            if(parseObjects == null) return;
            messageAdapter.setParseObjectArrayList(parseObjects);
            messageAdapter.notifyDataSetChanged();
        }
    }

    private void sendMessage()
    {
        messageBody = messageBodyField.getText().toString();
        if (messageBody.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_LONG).show();
            return;
        }

        btn_send.setEnabled(false);
        messageBodyField.setEnabled(false);

        final ParseUser sender = ParseUser.getCurrentUser();
        final ParseUser reciever = new ParseUser();
        reciever.setObjectId(fromUserobjectid);

        PicturePost post = new PicturePost();
        post.setObjectId(postObjectId);

        ParseACL acl = new ParseACL();

        // Give public read access
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);

        final Conversation conversation = new Conversation();
        conversation.setFromUser(sender);
        conversation.setToUser(reciever);
        conversation.setMessage(messageBody);
        conversation.setPost(post);
        conversation.setACL(acl);

        //messageAdapter.addMessage(conversation);
        messageBodyField.setText("");

        conversation.saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    Log.d(DEBUG_TAG, "Message sending failed: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Error sending message", Toast.LENGTH_SHORT).show();
                    //messageAdapter.removeLastMessage();
                    messageBodyField.setText(messageBody);
                    messageBodyField.setEnabled(true);
                    btn_send.setEnabled(true);
                    return;
                }

                messageAdapter.addMessage(conversation);

                messageBodyField.setEnabled(true);
                btn_send.setEnabled(true);

                ParsePush push = new ParsePush();
                push.setChannel("user_" + reciever.getObjectId());

                /*
                List<ParseQuery<ParseInstallation>> queries = new ArrayList<ParseQuery<ParseInstallation>>();

                ParseQuery<ParseInstallation> androidQuery = ParseQuery.getQuery(ParseInstallation.class);
                ParseQuery<ParseInstallation> iosQuery = ParseQuery.getQuery(ParseInstallation.class);
                ParseQuery<ParseInstallation> winphoneQuery = ParseQuery.getQuery(ParseInstallation.class);
                ParseQuery<ParseInstallation> jsQuery = ParseQuery.getQuery(ParseInstallation.class);
                ParseQuery<ParseInstallation> embeddedQuery = ParseQuery.getQuery(ParseInstallation.class);

                androidQuery.whereEqualTo("deviceType", "android");
                iosQuery.whereEqualTo("deviceType", "ios");
                winphoneQuery.whereEqualTo("deviceType", "winphone");
                jsQuery.whereEqualTo("deviceType", "js");
                embeddedQuery.whereEqualTo("deviceType", "embedded");

                queries.add(androidQuery);
                queries.add(iosQuery);
                queries.add(winphoneQuery);
                queries.add(jsQuery);
                queries.add(embeddedQuery);

                ParseQuery<ParseInstallation> mainQuery = ParseQuery.or(queries);
                */

                try {
                    JSONObject jsonData = new JSONObject("{\"c\": \" " + conversation.getObjectId() + "\", " +
                                                          "\"alert\": \"" + sender.getUsername() + ": " + conversation.get("message") + "\", " +
                                                          "\"badge\": \"Increment\" }");
                    Log.d(DEBUG_TAG, "Pushing jsonString: " + jsonData.toString());
                    push.setData(jsonData);
                    //push.setQuery(mainQuery);
                }
                catch (JSONException je){
                    Log.e(DEBUG_TAG, "Error parsing push data: " + je.getMessage());
                }

                push.sendInBackground();
            }
        });
    }

    public ParsePushBroadcastReceiver chatReceiver = new ParsePushBroadcastReceiver(){
        @Override
        protected void onPushReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String jsonData = extras.getString("com.parse.Data");
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(jsonData);
                Log.d(DEBUG_TAG, "Received push json: " + jsonObject.toString());
                String objectIdConv = jsonObject.getString("c");

                Conversation conv = new Conversation();
                conv.setObjectId(objectIdConv);

                try{
                    conv.fetch();
                    messageAdapter.addMessage(conv);
                }
                catch(ParseException pe){
                    Log.e(DEBUG_TAG, "Error fetching conversation: " + pe.getMessage());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //super.onPushReceive(context, intent);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(chatReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(chatReceiver);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}

