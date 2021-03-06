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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.Fade;

import com.example.game.R;
import com.example.game.adapters.EventAdapter;
import com.example.game.databinding.FragmentCommunityBinding;
import com.example.game.models.Community;
import com.example.game.models.Event;
import com.example.game.models.Subscription;
import com.example.game.utils.AnimationUtils;
import com.example.game.utils.ConstantUtils;
import com.example.game.utils.EndlessRecyclerViewScrollListener;
import com.example.game.utils.QueryUtil;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CommunityFragment extends Fragment implements EventAdapter.OnClickBtnDetail {
    public static final String COMMUNITY = "community";
    private static final String TAG = "CommunityFragment";

    private EventAdapter adapter;
    private List<Event> events;
    private FragmentCommunityBinding binding;

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
        binding = FragmentCommunityBinding.bind(view);
        if (getArguments() != null) {
            Bundle args = getArguments();
            Community community = Parcels.unwrap(args.getParcelable(COMMUNITY));
            RecyclerView rvEvents = binding.rvEvents;
            SwipeRefreshLayout swipeContainer = binding.swipeContainer;
            events = new ArrayList<>();
            adapter = new EventAdapter(getContext(), events, this);

            rvEvents.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            rvEvents.setLayoutManager(linearLayoutManager);

            if (community != null) {
                if (!community.getName().equals(ConstantUtils.BASE_COMMUNITY)) {
                    QueryUtil.addInteraction(community);
                }

                populateRecyclerView(community);

                rvEvents.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        if (!community.getName().equals(ConstantUtils.BASE_COMMUNITY)) {
                            populateWithCommunityEventsWithSkip(community, totalItemsCount);

                        }
                    }
                });

                swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        populateRecyclerView(Objects.requireNonNull(community));
                        swipeContainer.setRefreshing(false);
                    }
                });
                // Configure the refreshing colors
                swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                        android.R.color.holo_green_light,
                        android.R.color.holo_orange_light,
                        android.R.color.holo_red_light);
            }
        } else {
            Log.e(TAG, "Missing community argument:" + new NullPointerException().getMessage());
        }
    }

    /**
     * populates the recyclerView with events based on @community
     * @param community This is the community at which the user is currently at.
     *                  if this community names is BASE_COMMUNITY("public"), then feel the recycler with suggestions
     *                  otherwise, feel the recyclerView with events in that community
     * @see #populateRecyclerView(Community)
     * @see #populateWithCommunityEvents(Community)
     */
    private void populateRecyclerView(Community community) {
        events.clear();
        if (community.getName().equals(ConstantUtils.BASE_COMMUNITY)) {
            populateWithSuggestions(community);
        } else {
            populateWithCommunityEvents(community);
        }
    }

    /**
     * Populates the recyclerView using a pdf when the user already follows the other communities
     * otherwise, show public events only.
     * @param community This is the community at which the user is currently at.
     * @see #populateWithCommunityEvents(Community)
     * @see #populateWithPdf(List)
     */
    private void populateWithSuggestions(Community community) {
        ParseQuery<Subscription> subscriptionParseQuery = ParseQuery.getQuery(Subscription.class);
        subscriptionParseQuery.whereEqualTo(Subscription.KEY_USER, ParseUser.getCurrentUser());
        subscriptionParseQuery.include(Subscription.KEY_COMMUNITY);
        subscriptionParseQuery.findInBackground(new FindCallback<Subscription>() {
            @Override
            public void done(List<Subscription> objects, ParseException e) {
                if (objects.size() == 1) {
                    populateWithCommunityEvents(community);
                } else {
                    populateWithPdf(objects);
                }
            }
        });
    }

    /**
     *Generate a number of events from each community that corresponds to its fraction of interaction
     * in all interactions. Get the pmf and calculate the limit of the number of events using the total number
     * of events to populate a recyclerView. If the query results no results, inform the user that no events were found.
     * If the number of events of events is less than the limit, shuffle the result and notify the adapter.
     * Otherwise, pick the first "limit" events, shuffle them and notify the adapter.
     * @param subscriptions This is a list of all subscriptions between the user and various communities they follow.
     * @see #calculatePmf(List)
     */
    private void populateWithPdf(List<Subscription> subscriptions) {
        HashMap<Community, Float> pdf = calculatePmf(subscriptions);
        for (Community community : pdf.keySet()) {
            int limit = (int) (pdf.get(community) * ConstantUtils.MAX_EVENTS_COUNT);
            ParseQuery<Event> eventParseQuery = ParseQuery.getQuery(Event.class);
            eventParseQuery.include(Event.KEY_COMMUNITY);
            eventParseQuery.orderByDescending(Event.KEY_CREATED_AT);
            eventParseQuery.whereEqualTo(Event.KEY_COMMUNITY, community);
            eventParseQuery.setLimit(ConstantUtils.MAX_EVENTS_COUNT);
            eventParseQuery.findInBackground(new FindCallback<Event>() {
                @Override
                public void done(List<Event> objects, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, getString(R.string.error_events_query) + e);
                    } else {
                        Collections.shuffle(objects);
                        if (objects.size() == 0) {
                            binding.tvMessage.setVisibility(View.VISIBLE);
                            binding.tvMessage.setText(R.string.no_event_in_community);
                        } else if (objects.size() >= limit) {
                            binding.tvMessage.setVisibility(View.GONE);
                            QueryUtil.bindBookMarkStatuses(objects);
                            events.addAll(objects.subList(0, limit));
                            Collections.shuffle(events);
                            adapter.notifyDataSetChanged();
                        } else {
                            binding.tvMessage.setVisibility(View.GONE);
                            QueryUtil.bindBookMarkStatuses(objects);
                            events.addAll(objects);
                            Collections.shuffle(events);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }
    /**
     * Calculate the probability mass function for interactions in various communities.
     * Calculate total interactions and then a fraction contributed by each community.
     * @param subscriptions This is a list of all subscriptions between the user and various communities they follow.
     * @return pmf This is the probability mass function
     */
    private HashMap<Community, Float> calculatePmf(List<Subscription> subscriptions) {
        HashMap<Community, Float> pmf = new HashMap<>();
        float totalInteractions = 0;

        for (Subscription subscription : subscriptions) {
            Community community = subscription.getCommunity();
            if (!community.getName().equals(ConstantUtils.BASE_COMMUNITY)) {
                totalInteractions += subscription.getInteractionCount().floatValue();
            }
        }

        for (Subscription subscription : subscriptions) {
            Community community = subscription.getCommunity();
            if (!community.getName().equals(ConstantUtils.BASE_COMMUNITY)) {
                float frequency = subscription.getInteractionCount().floatValue() / totalInteractions;
                pmf.put(subscription.getCommunity(), frequency);
            }
        }
        return pmf;
    }

    /**
     * Feel the recyclerView with events(<=MAX_EVENTS_COUNT) in a certain community in a descending order by the date they were created.
     * @param community This is a community at which the user is at in the tabLayout.
     */
    private void populateWithCommunityEvents(Community community) {
        ParseQuery<Event> eventParseQuery = ParseQuery.getQuery(Event.class);
        eventParseQuery.orderByDescending(Event.KEY_CREATED_AT);
        eventParseQuery.include(Event.KEY_COMMUNITY);
        eventParseQuery.whereEqualTo(Event.KEY_COMMUNITY, community);
        eventParseQuery.setLimit(ConstantUtils.MAX_EVENTS_COUNT);
        eventParseQuery.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, getString(R.string.error_events_query) + e);
                } else {
                    if (objects.size() > 0) {
                        binding.tvMessage.setVisibility(View.GONE);
                        QueryUtil.bindBookMarkStatuses(objects);
                        events.addAll(objects);
                        adapter.notifyDataSetChanged();
                    } else {
                        binding.tvMessage.setVisibility(View.VISIBLE);
                        binding.tvMessage.setText(R.string.no_event_in_community);
                    }
                }
            }
        });
    }

    /**
     * Feel the recyclerView with events(<=MAX_EVENTS_COUNT, excluding the ones already in the list )
     * in a certain community in a descending order by the date they were created..
     * @param community This is a community at which the user is at in the tabLayout.
     * @param skip the number of events already seen.
     */
    private void populateWithCommunityEventsWithSkip(Community community, int skip) {
        ParseQuery<Event> eventParseQuery = ParseQuery.getQuery(Event.class);
        eventParseQuery.orderByDescending(Event.KEY_CREATED_AT);
        eventParseQuery.include(Event.KEY_COMMUNITY);
        eventParseQuery.whereEqualTo(Event.KEY_COMMUNITY, community);
        eventParseQuery.setSkip(skip);
        eventParseQuery.setLimit(ConstantUtils.MAX_EVENTS_COUNT);
        eventParseQuery.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, getString(R.string.error_events_query) + e);
                } else {
                    if (objects.size() > 0) {
                        binding.tvMessage.setVisibility(View.GONE);
                        QueryUtil.bindBookMarkStatuses(objects);
                        events.addAll(objects);
                        adapter.notifyDataSetChanged();
                    } else {
                        binding.tvMessage.setVisibility(View.VISIBLE);
                        binding.tvMessage.setText(R.string.no_event_in_community);
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community, container, false);
    }

    @Override
    public void onClickedBtnDetail(Event event, View view) {
        EventDetailFragment fragment = EventDetailFragment.newInstance(event, event.getCommunity());
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
