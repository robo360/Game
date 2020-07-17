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
import com.example.game.fragments.CreateEventFragment;
import com.example.game.fragments.EventFeedFragment;
import com.example.game.fragments.ProfileFragment;
import com.example.game.fragments.SearchFragment;
import com.example.game.helpers.NavigationUtil;
import com.example.game.models.Community;
import com.example.game.models.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bottomNavigationView = binding.bottomNavigation;
        toolbar = binding.toolbar;
        fab = binding.fab;

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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = CreateEventFragment.newInstance();
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        });

        //set a listener on the menu items
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        logout();
                        break;
                    case R.id.create_event:
                        Toast.makeText(MainActivity.this, R.string.create_event, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(MainActivity.this, R.string.create_community, Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }

    private void logout() {
        ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
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

    public void createCommunity(String name) {
        Community community = new Community();
        community.setCreator(ParseUser.getCurrentUser());
        community.setName(name);
        community.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error making a community" + e);
                } else {
                    Log.i(TAG, "Successful created a community");
                }
            }
        });
    }
}
