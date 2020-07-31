package com.example.game.fragments;

import android.util.Log;

import com.example.game.R;
import com.example.game.models.Community;
import com.example.game.models.Subscription;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class CommunityProfileFragment extends CommunitySearchFragment {
    private static final String TAG = "CommunityPpFragment";

    public CommunityProfileFragment(String query) {
        super(query);
    }

    @Override
    public void getCommunities(String query) {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<Subscription> q = ParseQuery.getQuery(Subscription.class);
        q.whereEqualTo(Subscription.KEY_USER, user);
        q.include(Subscription.KEY_COMMUNITY);
        q.findInBackground(new FindCallback<Subscription>() {
            @Override
            public void done(List<Subscription> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, getString(R.string.error_events_query) + e);
                } else {
                    for (Subscription subscription : objects) {
                        communities.add((Community) subscription.getCommunity());
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
