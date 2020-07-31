package com.example.game.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.game.R;
import com.example.game.databinding.ActivityMainBinding;
import com.example.game.fragments.CreateCommunityFragment;
import com.example.game.fragments.CreateEventFragment;
import com.example.game.fragments.EventFeedFragment;
import com.example.game.fragments.MapFragment;
import com.example.game.fragments.ProfileFragment;
import com.example.game.fragments.SearchFragment;
import com.example.game.models.Community;
import com.example.game.models.Subscription;
import com.example.game.models.User;
import com.example.game.utils.ConstantUtils;
import com.example.game.utils.NavigationUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = ConstantUtils.MAIN_TAG;

    private Toolbar toolbar;
    private Fragment fragment;
    private ArrayList<String> communityNames;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FloatingActionButton fab = binding.fab;
        ImageView ivProfile = binding.ivProfile;
        bottomNavigationView = binding.bottomNavigation;
        toolbar = binding.toolbar;
        communityNames = new ArrayList<>();
        getCommunityNames();

        try {
            ParseFile imageProfile = (ParseFile) ParseUser.getCurrentUser().fetchIfNeeded().get(User.KEY_IMAGE);
            if (imageProfile != null) {
                Glide.with(this).load(imageProfile.getUrl()).circleCrop().into(ivProfile);
            }
        } catch (ParseException e) {
            Log.e(TAG, getString(R.string.error_loading_profile) + e);
        }

        //set event handlers
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = ProfileFragment.newInstance();
                Toast.makeText(MainActivity.this, R.string.profile, Toast.LENGTH_SHORT)
                        .show();
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_event:
                        fragment = EventFeedFragment.newInstance();
                        Toast.makeText(MainActivity.this, R.string.home, Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case R.id.action_search:
                        fragment = SearchFragment.newInstance();
                        Toast.makeText(MainActivity.this, R.string.search, Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case R.id.action_map:
                        fragment = MapFragment.newInstance();
                        Toast.makeText(MainActivity.this, R.string.map, Toast.LENGTH_SHORT)
                                .show();
                        break;
                    default:
                        fragment = ProfileFragment.newInstance();
                        Toast.makeText(MainActivity.this, R.string.profile, Toast.LENGTH_SHORT)
                                .show();
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment)
                        .addToBackStack(TAG)
                        .commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_event);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, R.string.create_event, Toast.LENGTH_SHORT).show();
                CreateEventFragment createEventFragment = CreateEventFragment.newInstance(communityNames);
                createEventFragment.show(fragmentManager, TAG);
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        logout();
                        break;
                    case R.id.create_event:
                        Toast.makeText(MainActivity.this, R.string.create_event, Toast.LENGTH_SHORT).show();
                        CreateEventFragment createEventFragment = CreateEventFragment.newInstance(communityNames);
                        createEventFragment.show(fragmentManager, TAG);
                        break;
                    default:
                        Toast.makeText(MainActivity.this, R.string.create_community, Toast.LENGTH_SHORT).show();
                        CreateCommunityFragment createCommunityFragment = CreateCommunityFragment.newInstance();
                        createCommunityFragment.show(fragmentManager, TAG);
                        break;
                }
                return true;
            }
        });
    }

    private void logout() {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, getString(R.string.error_logout) + e);
                    Snackbar.make(toolbar, R.string.error_logout_message, BaseTransientBottomBar.LENGTH_SHORT);
                } else {
                    NavigationUtil.goToActivity(MainActivity.this, LoginActivity.class);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        uncheckBottomNavigationItems();
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    /*
    used to uncheck bottom Navigation boxes when we use backPress
    to ensure the the current fragment does not wrongly correspond to a selected navigation item.
     */
    private void uncheckBottomNavigationItems() {
        int size = bottomNavigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            bottomNavigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    private void getCommunityNames() {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<Subscription> q = ParseQuery.getQuery(Subscription.class);
        q.whereEqualTo(Subscription.KEY_USER, user);
        q.include(Subscription.KEY_COMMUNITY);
        q.findInBackground(new FindCallback<Subscription>() {
            @Override
            public void done(List<Subscription> objects, ParseException e) {
                for (Subscription subscription : objects) {
                    Community community = subscription.getCommunity();
                    communityNames.add(community.getName());
                }
            }
        });
    }
}
