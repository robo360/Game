package com.example.game.fragments;

import android.os.Bundle;
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
import com.google.android.material.button.MaterialButton;

import java.util.Objects;

public class SearchFragment extends Fragment {
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
        searchFragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        query = "";

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setIconifiedByDefault(false);
                searchView.requestFocus();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // TODO: send the query to the right window
                query = s;
                createCommunitySearchFragment(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        btnCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCommunitySearchFragment(query);
            }
        });

        btnEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEventSearchFragment(query);
            }
        });

        createCommunitySearchFragment(query);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    public void createCommunitySearchFragment(String query) {
        // change color to mark the selected option
        btnCommunity.setStrokeColorResource(R.color.colorPrimary);
        btnCommunity.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.colorPrimary));
        btnEvent.setStrokeColorResource(R.color.gray);
        btnEvent.setTextColor(getContext().getResources().getColor(R.color.gray));

        Fragment fragment = new CommunitySearchFragment(query);
        searchFragmentManager.beginTransaction()
                .replace(R.id.SearchFlContainer, fragment)
                .commit();
    }

    public void createEventSearchFragment(String query) {
        // change colors to mark the selected option
        btnEvent.setStrokeColorResource(R.color.colorPrimary);
        btnEvent.setTextColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.colorPrimary));
        btnCommunity.setStrokeColorResource(R.color.gray);
        btnCommunity.setTextColor(getContext().getResources().getColor(R.color.gray));

        Fragment fragment = new EventSearchFragment(query);
        searchFragmentManager.beginTransaction()
                .replace(R.id.SearchFlContainer, fragment)
                .commit();
    }
}
