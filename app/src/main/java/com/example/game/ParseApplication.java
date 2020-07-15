package com.example.game;

import android.app.Application;

import com.example.game.models.Attendance;
import com.example.game.models.Comment;
import com.example.game.models.Community;
import com.example.game.models.Event;
import com.example.game.models.Subscription;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Community.class);
        ParseObject.registerSubclass(Subscription.class);
        ParseObject.registerSubclass(Attendance.class);
        ParseObject.registerSubclass(Comment.class);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("gameparse") // should correspond to APP_ID env variable
                .server("https://gameparse.herokuapp.com/parse/").build());
    }
}
