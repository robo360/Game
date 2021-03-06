package com.example.game.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.game.R;
import com.example.game.adapters.CommunityAdapter;
import com.example.game.models.Community;
import com.example.game.models.Event;
import com.example.game.models.Subscription;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventFeedFragment extends Fragment {
    private static final String TAG = "EventFeedFragment";
    public static final String BASE_COMMUNITY = "public";

    private List<Community> communities;
    private CommunityAdapter communityAdapter;

    public static EventFeedFragment newInstance() {
        EventFeedFragment fragment = new EventFeedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        communities = new ArrayList<>();
        communityAdapter = new CommunityAdapter(this, communities);
        ViewPager2 viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(communityAdapter);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.removeBadge();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, this::populateTab);
        tabLayoutMediator.attach();
        getCommunities();
    }

    public void populateTab(TabLayout.Tab tab, int position) {
        if (communities.get(position).getName().equals(BASE_COMMUNITY)){
            tab.setText("@" + getString(R.string.foryou));
        } else {
            tab.setText("@" + communities.get(position).getName());
        }
        //set a badge
        ParseQuery<Subscription> subscription = ParseQuery.getQuery(Subscription.class);
        subscription.whereEqualTo(Subscription.KEY_COMMUNITY, communities.get(position));
        subscription.whereEqualTo(Subscription.KEY_USER, ParseUser.getCurrentUser());
        subscription.getFirstInBackground((object, e) -> {
            if (object != null) {
                Date date = object.getUpdatedAt();
                ParseQuery<Event> eventsQuery = ParseQuery.getQuery(Event.class);
                eventsQuery.whereGreaterThan(Event.KEY_CREATED_AT, date);
                eventsQuery.whereEqualTo(Event.KEY_COMMUNITY, communities.get(position));
                eventsQuery.findInBackground(new FindCallback<Event>() {
                    @Override
                    public void done(List<Event> objects, ParseException e) {
                        if (position != 0 && objects.size() != 0) {
                            tab.getOrCreateBadge().setNumber(objects.size());
                        }
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_feed, container, false);
    }

    public void getCommunities() {
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
                    communityAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
