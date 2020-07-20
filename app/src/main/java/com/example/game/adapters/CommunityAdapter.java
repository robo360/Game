package com.example.game.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.game.fragments.CommunityFragment;
import com.example.game.models.Community;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class CommunityAdapter extends FragmentStateAdapter {
    private List<Community> communities;

    public CommunityAdapter(@NonNull Fragment fragment, List<Community> communities) {
        super(fragment);
        this.communities = communities;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Community community = communities.get(position);
        return CommunityFragment.newInstance(community);
    }

    @Override
    public int getItemCount() {
        return communities.size();
    }
}

