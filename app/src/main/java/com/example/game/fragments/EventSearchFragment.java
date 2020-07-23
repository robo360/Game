package com.example.game.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.game.R;
import com.example.game.adapters.EventSearchAdapter;
import com.example.game.databinding.FragmentEventSearchBinding;
import com.example.game.models.Community;
import com.example.game.models.Event;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class EventSearchFragment extends Fragment implements EventSearchAdapter.OnViewClickHandler {
    public static final String QUERY = "QUERY";
    private static final String TAG = "EventSearchFragment";

    private String query;
    private List<Event> events;
    private EventSearchAdapter adapter;
    private TextView tvMessage;
    private RecyclerView rvEventsSearch;

    public static EventSearchFragment newInstance(String query) {
        EventSearchFragment fragment = new EventSearchFragment();
        Bundle args = new Bundle();
        args.putString(QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            query = args.getString(QUERY);
        }
        FragmentEventSearchBinding binding = FragmentEventSearchBinding.bind(view);
        tvMessage = binding.tvMessage;
        events = new ArrayList<>();
        //set the adapter
        adapter = new EventSearchAdapter(events, view.getContext(), this);
        rvEventsSearch = binding.rvEventsSearch;
        rvEventsSearch.setAdapter(adapter);
        rvEventsSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        getEvents(query);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_search, container, false);
    }

    public void getEvents(String query) {
        ParseQuery<Event> q = ParseQuery.getQuery(Event.class);
        if( query.length() != 0 ){
            q.whereContains(Community.KEY_NAME, query);
        }
        q.include(Community.KEY_CREATOR);
        q.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                if (objects.size() > 0) {
                    events.addAll(objects);
                    adapter.notifyDataSetChanged();
                    Log.e(TAG, "Results: " + objects);
                } else {
                    tvMessage.setVisibility(View.VISIBLE);
                    tvMessage.setText(String.format("No communities matches '%s'", query));
                    rvEventsSearch.setVisibility(View.GONE);
                    Log.e(TAG, "Error: " + e);
                }
            }
        });
    }

    @Override
    public void btnOnClickedListener(Event event) {
        EventDetailFragment fragment = EventDetailFragment.newInstance(event, (Community) event.getCommunity());
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
    }
}
