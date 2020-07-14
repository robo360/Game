package com.example.game.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

/*
Parse Model: Subscription (user - community)
| Property | Type     | Description |
| -------- | -------- | -------- |
|objectId     | String    | unique id for the user post |
|user |	Pointer to User | the user who follows a community|
|community |Pointer to Community | A community that the user is following|
|eventAttend |Number | number of events in attended from this community|
|interactionCount |Number | number of times the user checks, clicks on an event in the community,
or create an event in the community|
|createdAt|	DateTime|	date when the user follows a community|
 */
@Parcel(analyze = {Subscription.class})
@ParseClassName("Subscription")
public class Subscription extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_COMMUNITY = "community";
    public static final String KEY_FOLLOW_STATUS = "followStatus";
    public static final String KEY_EVENT_ATTEND = "eventAttend";
    public static final String KEY_INTERACTION_COUNT = "interactionCount";
    public static final String KEY_CREATED_AT = "createdAt";

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public ParseObject getCommunity() {
        return getParseObject(KEY_COMMUNITY);
    }

    public boolean getFollowStatus() {
        return getBoolean(KEY_FOLLOW_STATUS);
    }

    public Number getEventAttend() {
        return getNumber(KEY_EVENT_ATTEND);
    }

    public Number getInteractionCount() {
        return getNumber(KEY_INTERACTION_COUNT);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setCommunity(ParseObject community) {
        put(KEY_COMMUNITY, community);
    }

    public void setFollowStatus(boolean followStatus) {
        put(KEY_FOLLOW_STATUS, followStatus);
    }

    public void setEventAttend(int eventAttendCount) {
        put(KEY_EVENT_ATTEND, eventAttendCount);
    }

    public void setInteractionCount(int interactionCount) {
        put(KEY_INTERACTION_COUNT, interactionCount);
    }
}
