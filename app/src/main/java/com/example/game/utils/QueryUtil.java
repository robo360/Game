package com.example.game.utils;

import android.content.Context;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.game.R;
import com.example.game.models.Attendance;
import com.example.game.models.Community;
import com.example.game.models.Event;
import com.example.game.models.Subscription;
import com.example.game.models.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Objects;

public class QueryUtil {
    public static final String TAG = "QueryUtil";

    public static void addInteraction(Community community) {
        ParseQuery<Subscription> subscriptionParseQuery = ParseQuery.getQuery(Subscription.class);
        subscriptionParseQuery.whereEqualTo(Subscription.KEY_COMMUNITY, community);
        subscriptionParseQuery.whereEqualTo(Subscription.KEY_USER, ParseUser.getCurrentUser());
        subscriptionParseQuery.getFirstInBackground((object, e) -> {
            if (e == null) {
                object.setInteractionCount(object.getInteractionCount().intValue() + 1);
                object.saveInBackground();
            }
        });
    }

    public static void removeFollowingToUserCount() {
        ParseUser user = ParseUser.getCurrentUser();
        int count = Objects.requireNonNull(user.getNumber(User.KEY_FOLLOWING_COUNT)).intValue();
        user.put(User.KEY_FOLLOWING_COUNT, count - 1);
        user.saveInBackground();

    }

    public static void addFollowingToUserCount() {
        ParseUser user = ParseUser.getCurrentUser();
        int count = Objects.requireNonNull(user.getNumber(User.KEY_FOLLOWING_COUNT)).intValue();
        user.put(User.KEY_FOLLOWING_COUNT, count + 1);
        user.saveInBackground();
    }

    private static void changeBookMarkDrawable(Context context, ImageButton btnBookMark){
        String currentState = btnBookMark.getContentDescription().toString();
        if (currentState.equals(context.getString(R.string.bookmarked))) {
            btnBookMark.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_bookmark_border_24));
            btnBookMark.setContentDescription(context.getString(R.string.not_bookmarked));
        } else {
            btnBookMark.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_bookmark_24));
            btnBookMark.setContentDescription(context.getString(R.string.bookmarked));
        }
    }

    public static void bookMarkEvent(Event event, Context context, ImageButton btnBookMark) {
        changeBookMarkDrawable(context, btnBookMark);
        ParseQuery<Attendance> attendance = ParseQuery.getQuery(Attendance.class);
        attendance.whereEqualTo(Attendance.KEY_EVENT, event);
        attendance.whereEqualTo(Attendance.KEY_USER, ParseUser.getCurrentUser());
        attendance.getFirstInBackground((object, e) -> {
            if (object != null) {
                object.setLikeStatus(!object.getLikeStatus());
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Toast.makeText(context, R.string.fail_bookmark, Toast.LENGTH_SHORT).show();
                            changeBookMarkDrawable(context, btnBookMark);
                        }
                    }
                });
            } else {
                //Create Attendance record if it does not exist:
                Attendance attendanceQuery = new Attendance();
                attendanceQuery.setUser(ParseUser.getCurrentUser());
                attendanceQuery.setEvent(event);
                attendanceQuery.setLikeStatus(true);
                attendanceQuery.saveInBackground(e1 -> {
                    if (e == null) {
                        btnBookMark.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_bookmark_24));
                    }
                });
            }
        });

        QueryUtil.addInteraction(event.getCommunity());
    }

    public static void bindBookMarkPerStatus(Context context, ImageButton btnBookMark, Event event) {
        ParseQuery<Attendance> attendance = ParseQuery.getQuery(Attendance.class);
        attendance.whereEqualTo(Attendance.KEY_USER, ParseUser.getCurrentUser());
        attendance.whereEqualTo(Attendance.KEY_EVENT, event);
        attendance.whereEqualTo(Attendance.KEY_LIKE_STATUS, true);
        attendance.getFirstInBackground(new GetCallback<Attendance>() {
            @Override
            public void done(Attendance object, ParseException e) {
                if (e == null) {
                    btnBookMark.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_bookmark_24));
                }
            }
        });
    }
}
