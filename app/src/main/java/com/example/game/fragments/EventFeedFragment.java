package com.example.game.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.game.R;
import com.example.game.adapters.CommunityAdapter;
import com.example.game.models.Community;
import com.example.game.models.Subscription;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class EventFeedFragment extends Fragment {
    public static final String TAG = "EventFeedFragment";

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
        communityAdapter =  new CommunityAdapter(this, communities);
        ViewPager2 viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(communityAdapter);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout,viewPager,(tab, position) -> tab.setText("@"+communities.get(position).getName()));
        tabLayoutMediator.attach();
        getCommunities();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_feed, container, false);
    }
    public void getCommunities() {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<Subscription> q = ParseQuery.getQuery(Subscription.class);
        //q.whereEqualTo(Subscription.KEY_USER, user);
        q.include(Subscription.KEY_COMMUNITY);
        q.findInBackground(new FindCallback<Subscription>() {
            @Override
            public void done(List<Subscription> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error querying events: " + e);
                } else {
                    Log.i(TAG, "Subscriptions: " + objects.size());
                    for(Subscription subscription : objects){
                        communities.add((Community) subscription.getCommunity());
                    }
                    communityAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}

