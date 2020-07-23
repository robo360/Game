package com.example.game.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.game.R;
import com.example.game.databinding.FragmentSearchBinding;
import com.example.game.models.Event;
import com.google.android.material.button.MaterialButton;

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";

    private FragmentManager searchFragmentManager;
    private MaterialButton btnEvent;
    private MaterialButton btnCommunity;
    private String query;

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentSearchBinding binding = FragmentSearchBinding.bind(view);
        SearchView searchView = binding.searchView;
        btnEvent = binding.btnFilter;
        btnCommunity = binding.btnCommunity;
        searchFragmentManager = getActivity().getSupportFragmentManager();
        query = "";

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.i(TAG, "Query: " + s);
                query = s;
                //TODO: send the query to the right window
                createCommunitySearchFragment(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        btnCommunity.setOnClickListener(view1 -> createCommunitySearchFragment(query));

        btnEvent.setOnClickListener(view12 -> createEventSearchFragment(query));

        createCommunitySearchFragment(query);
    }

    public void createCommunitySearchFragment(String query){
        Fragment fragment = CommunitySearchFragment.newInstance(query);
        btnCommunity.setStrokeColorResource(R.color.colorPrimary);
        btnCommunity.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        btnEvent.setStrokeColorResource(R.color.gray);
        btnEvent.setTextColor(getContext().getResources().getColor(R.color.gray));
        searchFragmentManager.beginTransaction()
                .replace(R.id.SearchFlContainer, fragment)
                .commit();
    }

    public void createEventSearchFragment(String query){
        btnEvent.setStrokeColorResource(R.color.colorPrimary);
        btnEvent.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        btnCommunity.setStrokeColorResource(R.color.gray);
        btnCommunity.setTextColor(getContext().getResources().getColor(R.color.gray));
        Fragment fragment = EventSearchFragment.newInstance(query);
        searchFragmentManager.beginTransaction()
                .replace(R.id.SearchFlContainer, fragment)
                .commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }
}
