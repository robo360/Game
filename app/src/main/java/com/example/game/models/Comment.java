package com.example.game.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

/*
Parse Model: Comment (User - event)
| Property | Type     | Description |
| -------- | -------- | -------- |
|objectId     | String    | unique id for the user post |
|author |	Pointer to User | the person who created the post|
|event |	Pointer to Event| Comments to a post|
|comment |String | text of the comment written by the user|
|createdAt|	DateTime|	date when the first interaction is created (default field)|
|updatedAt|	DateTime|	date when comment is last updated (default) field)|
 */
@Parcel(analyze = {Comment.class})
@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_EVENT = "event";
    public static final String KEY_COMMENT = "comment";

    public ParseUser getAuthor() {
        return getParseUser(KEY_AUTHOR);
    }

    public ParseObject getEvent() {
        return getParseObject(KEY_EVENT);
    }

    public String getComment() {
        return getString(KEY_COMMENT);
    }

    public void setAuthor(ParseUser user) {
        put(KEY_AUTHOR, user);
    }

    public void setEvent(ParseObject event) {
        put(KEY_EVENT, event);
    }

    public void setComment(String comment) {
        put(KEY_COMMENT, comment);
    }
}
