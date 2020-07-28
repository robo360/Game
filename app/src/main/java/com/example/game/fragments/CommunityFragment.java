package com.example.game.fragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Fade;

import com.example.game.R;
import com.example.game.adapters.EventAdapter;
import com.example.game.models.Community;
import com.example.game.models.Event;
import com.example.game.models.Subscription;
import com.example.game.utils.AnimationUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommunityFragment extends Fragment implements EventAdapter.OnClickBtnDetail {
    public static final String COMMUNITY = "community";
    public static final String TAG = "CommunityFragment";

    private EventAdapter adapter;
    private List<Event> events;

    public static Fragment newInstance(Community community) {
        CommunityFragment fragment = new CommunityFragment();
        Bundle args = new Bundle();
        args.putParcelable(COMMUNITY, Parcels.wrap(community));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            Bundle args = getArguments();
            Community community = Parcels.unwrap(args.getParcelable(COMMUNITY));
            RecyclerView rvEvents = view.findViewById(R.id.rvEvents);
            events = new ArrayList<>();
            adapter = new EventAdapter(getContext(), events, community, this);
            rvEvents.setAdapter(adapter);
            rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));
            //add an interaction to a list
            ParseQuery<Subscription> subscriptionParseQuery = ParseQuery.getQuery(Subscription.class);
            subscriptionParseQuery.whereEqualTo(Subscription.KEY_COMMUNITY, community);
            subscriptionParseQuery.whereEqualTo(Subscription.KEY_USER, ParseUser.getCurrentUser());
            subscriptionParseQuery.getFirstInBackground((object, e) -> {
                if(e != null){
                    object.setInteractionCount(object.getInteractionCount().intValue() + 1);
                    object.saveInBackground();
                }
            });

            getQueryEvents();
        } else {
            Log.e(TAG, "Missing community argument:" + new NullPointerException().getMessage());
        }
    }

    private void getQueryEvents() {

        ParseQuery<Event> eventParseQuery = ParseQuery.getQuery(Event.class);
        eventParseQuery.orderByDescending(Event.KEY_CREATED_AT);
        eventParseQuery.findInBackground((objects, e) -> {
            if (e != null) {
                Log.e(TAG, getString(R.string.error_events_query) + e);
            } else {
                events.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community, container, false);
    }

    @Override
    public void onClickedBtnDetail(Event event, Community community, View view) {
        EventDetailFragment fragment = EventDetailFragment.newInstance(event, community);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.setSharedElementEnterTransition(new AnimationUtils.DetailsTransition());
            fragment.setEnterTransition(new Fade());
            setExitTransition(new Fade());
            fragment.setSharedElementReturnTransition(new AnimationUtils.DetailsTransition());
        }
        ViewCompat.setTransitionName(view.findViewById(R.id.ivImage), "image");
        Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .beginTransaction().addSharedElement(view, "image")
                .addToBackStack(TAG).replace(R.id.flContainer, fragment)
                .commit();
    }
}
