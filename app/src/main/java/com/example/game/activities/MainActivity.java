package com.example.game.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.game.R;
import com.example.game.models.Community;
import com.example.game.models.Event;
import com.example.game.models.Subscription;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Run some tests
        createCommunity("Brazil vs Spain");
        getCommunities();
        getEvents();
    }

    public void getEvents() {
        ParseQuery<Event> qEvents = ParseQuery.getQuery(Event.class);
        qEvents.include(Event.KEY_CREATOR);
        qEvents.include(Event.KEY_COMMUNITY);
        qEvents.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error querying events: " + e);
                } else {
                    Log.i(TAG, "Results:" + objects.size());
                }
            }
        });
    }

    public void getCommunities() {
        ParseQuery<Subscription> q = ParseQuery.getQuery(Subscription.class);
        q.whereEqualTo(Subscription.KEY_USER, ParseUser.getCurrentUser());
        q.include(Subscription.KEY_COMMUNITY);
        q.findInBackground(new FindCallback<Subscription>() {
            @Override
            public void done(List<Subscription> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error querying events: " + e);
                } else {
                    Log.i(TAG, "Results:" + objects.get(0).getCommunity());
                }
            }
        });
    }

    public void createCommunity(String name) {
        Community community = new Community();
        community.setCreator(ParseUser.getCurrentUser());
        community.setName(name);
        community.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error making a community" + e);
                } else {
                    Log.i(TAG, "Successful created a community");
                }
            }
        });
    }
}
