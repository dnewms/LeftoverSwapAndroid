package com.teespire.leftoverswap;

import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseUser;
import com.teespire.leftoverswap.Activities.LS_MainActivity;

/**
 * Created by Matih on 22/2/2015.
 */
public class ParsePushNotificationsReceiver extends ParsePushBroadcastReceiver {
    @Override
    protected void onPushOpen(Context context, Intent intent) {
        //super.onPushOpen(context, intent);
        if(ParseUser.getCurrentUser() != null){
            Intent splashIntent = new Intent(context, LS_MainActivity.class);
            splashIntent.putExtra("isChatPush", true);
            splashIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(splashIntent);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(ParseUser.getCurrentUser() != null){
            super.onReceive(context, intent);
        }
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
    }
}
