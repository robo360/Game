package com.example.game.fragments;

import android.util.Log;
import android.view.View;

import com.example.game.R;
import com.example.game.models.Attendance;
import com.example.game.models.Event;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MarkedEventProfileFragment extends EventSearchFragment {
    private static final String TAG = "CreatedEventFragment";

    // Argument 'query' required for EventSearchFragment
    public MarkedEventProfileFragment(String query) {
        super(query);
    }

    @Override
    public void getEvents(String query) {
        ParseQuery<Attendance> q = ParseQuery.getQuery(Attendance.class);
        q.whereEqualTo(Attendance.KEY_USER, ParseUser.getCurrentUser());
        q.whereEqualTo(Attendance.KEY_ATTEND_STATUS, true);
        q.findInBackground((List<Attendance> objects, ParseException e) -> {
            if (objects.size() > 0) {
                for (Attendance object : objects) {
                    events.add((Event) object.getEvent());
                }
                adapter.notifyDataSetChanged();
            } else {
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(R.string.no_marked_events_message);
                rvEventsSearch.setVisibility(View.GONE);
                Log.e(TAG,getString(R.string.error_events_query) + e);
            }
        });
    }
}
