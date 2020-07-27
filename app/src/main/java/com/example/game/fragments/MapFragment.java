package com.example.game.fragments;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.game.R;
import com.example.game.models.Community;
import com.example.game.models.Event;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import permissions.dispatcher.NeedsPermission;

public class MapFragment extends Fragment {
    private static final String KEY_LOCATION = "location";
    private static final String TAG = "MapFragment";
    private static final long UPDATE_INTERVAL = 60000;
    private static final long FASTEST_INTERVAL = 5000;

    @Nullable
    private GoogleMap map;
    @Nullable
    private Location currentLocation;
    private List<Event> events;

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            currentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));

        if (mapFragment != null) {
            mapFragment.getMapAsync((GoogleMap map) -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    loadMap(map);
                    events = new ArrayList<>();
                    addEventsMarkers();
                }
            });
        } else {
            Log.e(TAG, "Error - Map Fragment was null!!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    Objects.requireNonNull(getContext()), R.raw.map_style_json));

            getMyLocation();
            startLocationUpdates();

            if (currentLocation != null) {
                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
                if (map != null) {
                    map.animateCamera(cameraUpdate);
                } else {
                    Log.e(TAG, "Map is null");
                }
            }
        } else {
            Log.e(TAG, "Error - Map was null!!");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int requestCode = 20;
            String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            requestPermissions(permissions, requestCode);
            return;
        }

        Objects.requireNonNull(map).setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(getContext());
        locationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        onLocationChanged(location);
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
                        if (map != null) {
                            map.animateCamera(cameraUpdate);
                        } else {
                            Log.e(TAG, "Map is null");
                        }
                    } else {
                        Log.d(TAG, "Location is null");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Error trying to get last GPS location");
                    e.printStackTrace();
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                getMyLocation();
                startLocationUpdates();
            }
        } else {
            Toast.makeText(getContext(), R.string.grant_permission_request_message, Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    protected void startLocationUpdates() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        SettingsClient settingsClient = LocationServices.getSettingsClient(Objects.requireNonNull(getContext()));
        settingsClient.checkLocationSettings(locationSettingsRequest);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int requestCode = 20;
            String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            requestPermissions(permissions, requestCode);
            return;
        }

        getFusedLocationProviderClient(getContext()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        // GPS may be turned off
        if (location == null) {
            return;
        }
        currentLocation = location;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, currentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void addEventsMarkers() {
        ParseQuery<Event> eventParseQuery = ParseQuery.getQuery(Event.class);
        eventParseQuery.orderByDescending(Event.KEY_CREATED_AT);

        eventParseQuery.findInBackground((objects, e) -> {
            if (e != null) {
                Log.e(TAG, getString(R.string.error_events_query) + e);
            } else {
                events.addAll(objects);
                for (Event event : events) {
                    BitmapDescriptor defaultMarker =
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    if (event.getAddress() != null && map != null) {
                        LatLng position =
                                new LatLng(event.getAddress().getLatitude(), event.getAddress().getLongitude());
                        map.addMarker(new MarkerOptions()
                                .position(position)
                                .title(event.getTitle())
                                .icon(defaultMarker));
                    }
                }
            }
        });

        //set a listener on the marker info window
        if (map != null) {
            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    String title = marker.getTitle();
                    ParseQuery<Event> q = ParseQuery.getQuery(Event.class);
                    q.whereEqualTo(Event.KEY_TITLE, title);
                    q.include(Event.KEY_COMMUNITY);
                    q.getFirstInBackground(new GetCallback<Event>() {
                        @Override
                        public void done(Event object, ParseException e) {
                            if (e == null) {
                                Fragment fragment = EventDetailFragment.newInstance(object, (Community) object.getCommunity());
                                Objects.requireNonNull(getFragmentManager()).beginTransaction().replace(R.id.flContainer, fragment).commit();
                            }
                        }
                    });
                }
            });
        }
    }
}
