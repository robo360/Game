package com.example.game.models;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

/*
Parse Model: Community
| Property | Type     | Description |
| -------- | -------- | -------- |
|objectId     | String    | unique id for the user post |
|name | File	|a unique name of the community |
|image | File	|profile pic of the community|
|creater |	Pointer to User | the person who created the post|
|postsCount|	Number|	number of posts in the community|
|createdAt|	DateTime|	date when community is created (default field)|
|updatedAt|	DateTime|	date when community is last updated (default field)|
 */
@Parcel(analyze = {Community.class})
@ParseClassName("Community")
public class Community extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_CREATOR = "creator";
    public static final String KEY_POSTSCOUNT = "postsCount";
    public static final String KEY_CREATEDAT = "postsCount";

    public ParseUser getCreator() {
        return getParseUser(KEY_CREATOR);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public int getPostsCount() {
        return getNumber(KEY_POSTSCOUNT).intValue();
    }

    public void setCreator(ParseUser user) {
        put(KEY_CREATOR, user);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public void setPostsCount(int count) {
        put(KEY_POSTSCOUNT, count);
    }
}
