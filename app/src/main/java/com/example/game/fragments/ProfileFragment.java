package com.example.game.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.game.R;
import com.example.game.databinding.FragmentProfileBinding;
import com.example.game.models.User;
import com.example.game.utils.TimeUtil;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    public static final String QUERY = ""; // Required for reusing search fragments and their adapters
    private static final String TAG = "ProfileFragment";
    private static final int INDEX_TAB_CREATED = 1;
    private static final int INDEX_TAB_FOLLOWING = 2;
    private static final int INDEX_TAB_MARKED = 0;

    private FragmentManager profileFragmentManager;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentProfileBinding binding = FragmentProfileBinding.bind(view);
        profileFragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        ImageView ivProfile = binding.ivProfile1;
        TextView tvName = binding.tvName;
        TextView tvFollowing = binding.tvFollowing;
        TextView tvDate = binding.tvDateJoined;
        TabLayout tabLayout = binding.tlProfile;

        //set the default tab:
        TabLayout.Tab tab = tabLayout.getTabAt(INDEX_TAB_MARKED);
        if (tab != null) {
            createMarkedEventsFragment();
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case INDEX_TAB_CREATED:
                        createCreatedEventsFragment();
                        break;
                    case INDEX_TAB_FOLLOWING:
                        createCommunityProfileFragment();
                        break;
                    default:
                        createMarkedEventsFragment();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ParseUser user = ParseUser.getCurrentUser();

        try {
            ParseFile imageProfile = (ParseFile) user.fetchIfNeeded().get(User.KEY_IMAGE);
            if (imageProfile != null) {
                Glide.with(this).load(imageProfile.getUrl()).circleCrop().into(ivProfile);
            }
        } catch (ParseException e) {
            Log.e(TAG, "Error while loading profile:" + e);
        }

        try {
            tvName.setText(user.fetchIfNeeded().getString(User.KEY_NAME));
            tvFollowing.setText(String.format("%s following", user.fetchIfNeeded().getNumber(User.KEY_POSTS_COUNT)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String joinDate = TimeUtil.getRelativeTimeAgo(user.getCreatedAt().toString());
        tvDate.setText(String.format("Joined %s", joinDate));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    private void createMarkedEventsFragment() {
        Fragment fragment;
        fragment = new MarkedEventProfileFragment(QUERY);
        profileFragmentManager.beginTransaction().replace(R.id.profileFlContainer, fragment).commit();
    }

    private void createCreatedEventsFragment() {
        Fragment fragment;
        fragment = new CreatedEventProfileFragment(QUERY);
        profileFragmentManager.beginTransaction().replace(R.id.profileFlContainer, fragment).commit();
    }

    private void createCommunityProfileFragment() {
        Fragment fragment;
        fragment = new CommunityProfileFragment(QUERY);
        profileFragmentManager.beginTransaction().replace(R.id.profileFlContainer, fragment).commit();
    }
}
