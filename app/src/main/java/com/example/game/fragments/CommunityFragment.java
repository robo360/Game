package com.example.game.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.game.R;
import com.example.game.adapters.EventAdapter;
import com.example.game.models.Community;
import com.example.game.models.Event;
import com.example.game.models.Subscription;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment implements EventAdapter.OnClickBtnDetail {
    public static final String COMMUNITY = "community";
    public static final String TAG = "CommunityFragment";

    private EventAdapter adapter;
    private List<Event> events;
    private Community community;
    private RecyclerView rvEvents;

    public CommunityFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(Community community) {
        CommunityFragment fragment = new CommunityFragment();
        Bundle args = new Bundle();
        args.putParcelable(COMMUNITY, Parcels.wrap(community));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        community = Parcels.unwrap(getArguments().getParcelable(COMMUNITY));
        rvEvents = view.findViewById(R.id.rvEvents);
        events = new ArrayList<>();
        adapter = new EventAdapter(getContext(), events, community, this);
        rvEvents.setAdapter(adapter);
        rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        //add an interaction to a list
        ParseQuery<Subscription> subscriptionParseQuery = ParseQuery.getQuery(Subscription.class);
        subscriptionParseQuery.whereEqualTo(Subscription.KEY_COMMUNITY, community);
        subscriptionParseQuery.whereEqualTo(Subscription.KEY_USER, ParseUser.getCurrentUser());
        subscriptionParseQuery.getFirstInBackground(new GetCallback<Subscription>() {
            @Override
            public void done(Subscription object, ParseException e) {
                object.setInteractionCount(object.getInteractionCount().intValue() + 1);
                object.saveInBackground();
            }
        });
        getQueryEvents(community);
    }

    private void getQueryEvents(Community community) {

        ParseQuery<Event> eventParseQuery = ParseQuery.getQuery(Event.class);
//        if (community.getName() != "public"){
//            eventParseQuery.whereEqualTo(Event.KEY_COMMUNITY, community);
//        }
        eventParseQuery.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Error querying events: " + e);
                } else{
                    events.addAll(objects);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community, container, false);
    }

    @Override
    public void onClickedBtnDetail(Event event, Community community) {
        EventDetailFragment fragment = EventDetailFragment.newInstance(event, community);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
    }
}