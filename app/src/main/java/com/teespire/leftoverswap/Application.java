package com.teespire.leftoverswap;

import com.parse.Parse;
import com.parse.ParseObject;
import com.teespire.leftoverswap.ParseClasses.Conversation;
import com.teespire.leftoverswap.ParseClasses.PicturePost;

/**
 * Created by teespire on 20/01/2015.
 */
public class Application extends android.app.Application {
    // Debugging switch
    public static final boolean APPDEBUG = false;
    // Debugging tag for the application
    public static final String APPTAG = "LeftoverSwap";
    // Used to pass location from MainActivity to PostActivity
    public static final String INTENT_EXTRA_LOCATION = "location";

    public Application()
    {

    }

    @Override
    public void onCreate()
    {
        super.onCreate();

       // Parse.enableLocalDatastore(getApplicationContext());
        ParseObject.registerSubclass(PicturePost.class);
        ParseObject.registerSubclass(Conversation.class);
        /*Parse.initialize(this, "q75RnciHFggDHWADN2sZXJF3Ak7Bd74UTMz8isRE",
                "81j63q9hSjOsUAWR1bEDkb0PhqwXbFbiSEl8FxEF");*/
      /*Parse.initialize(this, "otWAzUAaIDdnCByzmwFCH0f9YXlQvo2rsjhdw4kA",
                "wFvhJGofWDY1wuo56H6cCIWbiBXOenraxxP3s5zr");*/

        // Test App
        /*Parse.initialize(this, "dRZm6rAXhPPBDK6B2YRGUsppFYhKuH3khThsQPMx",
                "EQgG6NZBsXqDfvtCbpc6PI1BXAhVeIfNXQuqk8uP");*/

        // Production keys
        Parse.initialize(this, "rxURqAiZdT4w3QiLPpecMAOyFF2qzVxsLPD1FcGR",
                "HF41j3NxMvnykjW2Cbu7LL48NA2Ebk98qUCT252h");
    }
}