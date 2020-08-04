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
import com.example.game.utils.ConstantUtils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventSearchFragment extends Fragment implements EventSearchAdapter.OnViewClickHandler {
    private static final String TAG = "EventSearchFragment";

    public String query;
    public List<Event> events;
    public EventSearchAdapter adapter;
    public TextView tvMessage;
    public RecyclerView rvEventsSearch;

    public EventSearchFragment(String query) {
        this.query = query;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        return inflater.inflate(R.layout.fragment_event_search, container, false);
    }

    public void getEvents(String query) {
        ParseQuery<Event> q = ParseQuery.getQuery(Event.class);
        if (query.length() != 0) {
            q.whereContains(Event.KEY_TITLE, query);
        }
        q.include(Community.KEY_CREATOR);
        q.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                if (objects.size() > 0) {
                    events.addAll(objects);
                    adapter.notifyDataSetChanged();
                } else {
                    tvMessage.setVisibility(View.VISIBLE);
                    tvMessage.setText(String.format("No Events matches '%s'", query));
                    rvEventsSearch.setVisibility(View.GONE);
                    Log.e(TAG, "Error: " + e);
                }
            }
        });
    }

    @Override
    public void btnOnClickedListener(Event event) {
        EventDetailFragment fragment = EventDetailFragment.newInstance(event, (Community) event.getCommunity());
        Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(ConstantUtils.MAIN_TAG)
                .commit();
    }
}
