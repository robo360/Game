package com.example.game.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.game.R;
import com.example.game.adapters.CommunityAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class EventFeedFragment extends Fragment {

    public static EventFeedFragment newInstance() {
        EventFeedFragment fragment = new EventFeedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CommunityAdapter communityAdapter =  new CommunityAdapter(this);
        ViewPager2 viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(communityAdapter);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout,viewPager,(tab, position) -> tab.setText("commune " + (position + 1)));
        tabLayoutMediator.attach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_feed, container, false);
    }
}