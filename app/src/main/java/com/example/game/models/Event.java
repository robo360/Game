package com.example.game.models;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.Date;

/*
Parse Model: Event
| Property | Type     | Description |
| -------- | -------- | -------- |
|objectId  | String    | unique id for the user post |
|creator |Pointer to User | the user who created the event|
|image | File	|event poster|
|title  |	String | the title of the event|
|address | GeoPoint |the address of the Match|
|community| Pointer to Community| the community in which the user is posting in|
|createdAt|	DateTime| date when the user likes a post|
 */
@Parcel(analyze = {Event.class})
@ParseClassName("Event")
public class Event extends ParseObject {
    public static final String KEY_CREATOR = "creator";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_TITLE = "title";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_COMMUNITY = "community";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_DATE = "date";

    public ParseUser getUser() {
        return getParseUser(KEY_CREATOR);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public ParseGeoPoint getAddress() {
        return getParseGeoPoint(KEY_ADDRESS);
    }

    public Date getDate(){
        return getDate(KEY_DATE);
    }

    public ParseObject getCommunity() {
        return getParseObject(KEY_COMMUNITY);
    }

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void setUser(ParseUser user) {
        put(KEY_CREATOR, user);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public void setAddress(ParseGeoPoint address) {
        put(KEY_ADDRESS, address);
    }

    public void setCommunity(ParseObject community) {
        put(KEY_COMMUNITY, community);
    }

    public void setDate(Date date){
        put(KEY_DATE, date);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }
}
