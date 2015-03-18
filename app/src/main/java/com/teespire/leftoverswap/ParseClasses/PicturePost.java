package com.teespire.leftoverswap.ParseClasses;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by teespire on 21/01/2015.
 */
@ParseClassName("Post")
public class PicturePost extends ParseObject {

    public void setDescription(String description)
    {
        put("description", description);
    }
    public String getDescription()
    {
        return getString("description");
    }

    public ParseFile getPhotoFile()
    {
        return getParseFile("image");
    }
    public void setPhotoFile(ParseFile file)
    {
        put("image", file);
    }

    public void setLocation(ParseGeoPoint value)
    {
        put("location", value);
    }
    public ParseGeoPoint getLocation()
    {
        return getParseGeoPoint("location");
    }

    public void setTaken(Boolean title)
    {
        put("taken", title);
    }
    public Boolean getTaken()
    {
        return getBoolean("taken");
    }

    public ParseFile getThumbnailFile()
    {
        return getParseFile("thumbnail");
    }
    public void setThumbnailFile(ParseFile file)
    {
        put("thumbnail", file);
    }

    public void setTitle(String title)
    {
        put("title", title);
    }
    public String getTitle()
    {
        return getString("title");
    }

    public ParseUser getUser()
    {
        return getParseUser("user");
    }
    public void setUser(ParseUser user)
    {
        put("user", user);
    }

    public static ParseQuery<PicturePost> getQuery() {
        return ParseQuery.getQuery(PicturePost.class);
    }

}