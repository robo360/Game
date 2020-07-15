package com.example.game.models;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

/*
Parse Model: Attendance (User - event)
| Property | Type     | Description |
| -------- | -------- | -------- |
|objectId     | String    | unique id for the user post |
|user |	Pointer to User | the person who created the post|
|event |	Pointer to Event| Comments to a post|
|attendStatus | Boolean |whether attending or not attending|
|likeStatus | Boolean | whether likes or not|
|createdAt|	DateTime|	date when the first interaction is created (default field)|
|updatedAt|	DateTime|	date when comment is last updated (default) field)|
 */
@Parcel(analyze = {Attendance.class})
@ParseClassName("Attendance")
public class Attendance extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_EVENT = "event";
    public static final String KEY_ATTEND_STATUS = "attendStatus";
    public static final String KEY_LIKE_STATUS = "likeStatus";
    public static final String KEY_CREATED_AT = "createdAt";

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public ParseObject getEvent() {
        return getParseObject(KEY_EVENT);
    }

    public boolean getAttendStatus() {
        return getBoolean(KEY_ATTEND_STATUS);
    }

    public boolean getLikeStatus() {
        return getBoolean(KEY_LIKE_STATUS);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setAttendStatus(boolean attendStatus) {
        put(KEY_ATTEND_STATUS, attendStatus);
    }

    public void setLikeStatus(boolean likeStatus) {
        put(KEY_LIKE_STATUS, likeStatus);
    }
}
