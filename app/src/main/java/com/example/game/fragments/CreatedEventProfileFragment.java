package com.example.game.fragments;

import android.util.Log;
import android.view.View;

import com.example.game.R;
import com.example.game.models.Event;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class CreatedEventProfileFragment extends EventSearchFragment {
    private static final String TAG = "CreatedEventFragment";

    // Argument 'query' required for EventSearchFragment
    public CreatedEventProfileFragment(String query) {
        super(query);
    }

    @Override
    public void getEvents(String query) {
        ParseQuery<Event> q = ParseQuery.getQuery(Event.class);
        q.whereEqualTo(Event.KEY_CREATOR, ParseUser.getCurrentUser());
        q.include(Event.KEY_CREATOR);
        q.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                if (objects.size() > 0) {
                    events.addAll(objects);
                    adapter.notifyDataSetChanged();
                } else {
                    tvMessage.setVisibility(View.VISIBLE);
                    tvMessage.setText(R.string.no_created_events);
                    rvEventsSearch.setVisibility(View.GONE);
                    Log.e(TAG, getString(R.string.error_events_query) + e);
                }
            }
        });
    }
}
