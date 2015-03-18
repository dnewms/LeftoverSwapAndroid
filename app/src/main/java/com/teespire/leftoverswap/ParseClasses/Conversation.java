package com.teespire.leftoverswap.ParseClasses;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


/**
 * Created by Matih on 20/2/2015.
 */

@ParseClassName("Conversation")
public class Conversation extends ParseObject {

    public ParseUser getFromUser()
    {
        return getParseUser("fromUser");
    }
    public void setFromUser(ParseUser user)
    {
        put("fromUser", user);
    }

    public String getMessage()
    {
        return getString("message");
    }
    public void setMessage(String message)
    {
        put("message", message);
    }

    public PicturePost getPost()
    {
        return (PicturePost)getParseObject("post");
    }
    public void setPost(PicturePost post)
    {
        put("post", post);
    }

    public ParseUser getToUser()
    {
        return getParseUser("toUser");
    }
    public void setToUser(ParseUser user)
    {
        put("toUser", user);
    }

    public static ParseQuery<Conversation> getQuery() {
        return ParseQuery.getQuery(Conversation.class);
    }

}
