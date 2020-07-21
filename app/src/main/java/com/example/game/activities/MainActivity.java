package com.example.game.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.game.R;
import com.example.game.databinding.ActivityMainBinding;
import com.example.game.fragments.CreateCommunityFragment;
import com.example.game.fragments.CreateEventFragment;
import com.example.game.fragments.EventFeedFragment;
import com.example.game.fragments.ProfileFragment;
import com.example.game.fragments.SearchFragment;
import com.example.game.models.Community;
import com.example.game.models.Subscription;
import com.example.game.utils.NavigationUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Toolbar toolbar;
    private Fragment fragment;
    private ArrayList<String> communities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView bottomNavigationView = binding.bottomNavigation;
        FloatingActionButton fab = binding.fab;
        toolbar = binding.toolbar;
        communities = new ArrayList<>();
        getCommunityNames();
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_event:

                        fragment = EventFeedFragment.newInstance();
                        Toast.makeText(MainActivity.this, R.string.home, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_search:
                        fragment = SearchFragment.newInstance();
                        Toast.makeText(MainActivity.this, R.string.search, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        fragment = ProfileFragment.newInstance();
                        Toast.makeText(MainActivity.this, R.string.profile, Toast.LENGTH_SHORT).show();
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_event);

        //set a listener on the FAB
        fab.setOnClickListener(view -> {
            fragment = CreateEventFragment.newInstance(communities);
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
        });

        //set a listener on the menu items
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.logout:
                    logout();
                    break;
                case R.id.create_event:
                    Toast.makeText(MainActivity.this, R.string.create_event, Toast.LENGTH_SHORT).show();
                    fragment = CreateEventFragment.newInstance(communities);
                    fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                    break;
                default:
                    Toast.makeText(MainActivity.this, R.string.create_community, Toast.LENGTH_SHORT).show();
                    fragment = CreateCommunityFragment.newInstance();
                    fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                    break;
            }
            return true;
        });
    }

    private void logout() {
        ParseUser.getCurrentUser().logOutInBackground(e -> {
            if (e != null) {
                Log.e(TAG, getString(R.string.error_logout) + e);
                Snackbar.make(toolbar, R.string.error_logout_message, BaseTransientBottomBar.LENGTH_SHORT);
            } else {
                NavigationUtil.goToActivity(MainActivity.this, LoginActivity.class);
            }
        });
    }

    public void getCommunityNames() {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<Subscription> q = ParseQuery.getQuery(Subscription.class);
        q.whereEqualTo(Subscription.KEY_USER, user);
        q.include(Subscription.KEY_COMMUNITY);
        q.findInBackground((objects, e) -> {
            if (e != null) {
                Log.e(TAG, getString(R.string.error_events_query) + e);
            } else {
                for (Subscription subscription : objects) {
                    Community community = (Community) subscription.getCommunity();
                    communities.add(community.getName());
                }
            }
        });
    }
}
