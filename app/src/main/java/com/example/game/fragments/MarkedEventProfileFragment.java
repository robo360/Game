package com.example.game.fragments;

import android.util.Log;
import android.view.View;

import com.example.game.R;
import com.example.game.models.Attendance;
import com.example.game.models.Event;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MarkedEventProfileFragment extends EventSearchFragment {
    private static final String TAG = "CreatedEventFragment";

    // Argument 'query' required for EventSearchFragment
    public MarkedEventProfileFragment(String query) {
        super(query);
    }

    @Override
    public void getEvents(String query) {
        ParseQuery<Attendance> qLiked = ParseQuery.getQuery(Attendance.class);
        qLiked.whereEqualTo(Attendance.KEY_USER, ParseUser.getCurrentUser());
        qLiked.whereEqualTo(Attendance.KEY_LIKE_STATUS, true);
        ParseQuery<Attendance> qAttend = ParseQuery.getQuery(Attendance.class);
        qAttend.whereEqualTo(Attendance.KEY_USER, ParseUser.getCurrentUser());
        qAttend.whereEqualTo(Attendance.KEY_ATTEND_STATUS, true);
        List<ParseQuery<Attendance>> queryList = new ArrayList<>();
        queryList.add(qLiked);
        queryList.add(qAttend);
        ParseQuery<Attendance> q =  ParseQuery.or(queryList);
        q.findInBackground(new FindCallback<Attendance>() {
            @Override
            public void done(List<Attendance> objects, ParseException e) {
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
            }
        });
    }
}
