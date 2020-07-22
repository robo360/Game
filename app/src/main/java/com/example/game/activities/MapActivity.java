package com.example.game.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.example.game.R;
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
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

@RuntimePermissions
public class MapActivity extends AppCompatActivity implements GoogleMap.OnMapLongClickListener {
    private static final String KEY_LOCATION = "location";
    private static final long UPDATE_INTERVAL = 60000;
    private static final long FASTEST_INTERVAL = 5000;

    @Nullable
    private GoogleMap map;
    @Nullable
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            currentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));

        if (mapFragment != null) {
            mapFragment.getMapAsync(map -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    loadMap(map);
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            getMyLocation();
            startLocationUpdates();
            map.setOnMapLongClickListener(this);
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int requestCode = 20;
            String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            requestPermissions(permissions, requestCode);
            return;
        }

        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
        locationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        onLocationChanged(location);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("MapDemoActivity", "Error trying to get last GPS location");
                    e.printStackTrace();
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            getMyLocation();
            startLocationUpdates();
        } else{
            Toast.makeText(this, "Please Grant Permission", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();

        if (currentLocation != null) {
            Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            map.animateCamera(cameraUpdate);
        } else {
            Toast.makeText(this, "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
        startLocationUpdates();
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
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int requestCode = 20;
            String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            requestPermissions(permissions, requestCode);
            return;
        }

        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
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
        StringBuilder msg = new StringBuilder().append("Updated Location:")
                .append(location.getLatitude())
                .append(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, currentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Toast.makeText(this, "Long Press", Toast.LENGTH_LONG).show();
        showAlertDialogForPoint(latLng);
    }

    /*
    method creates an alert dialog box using the message item.
     */
    private void showAlertDialogForPoint(final LatLng latLng) {
        View messageView = LayoutInflater.from(MapActivity.this).
                inflate(R.layout.message_item, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(messageView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
                (dialog, which) -> {
                    // Define color of marker icon
                    BitmapDescriptor defaultMarker =
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    // Extract content from alert dialog
                    String title = ((EditText) Objects.requireNonNull(alertDialog.findViewById(R.id.etTitle))).
                            getText().toString();
                    String snippet = ((EditText) Objects.requireNonNull(alertDialog.findViewById(R.id.etSnippet))).
                            getText().toString();
                    // Creates and adds marker to the map
                    map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(title)
                            .snippet(snippet)
                            .icon(defaultMarker));
                });

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                (dialog, id) -> dialog.cancel());
        alertDialog.show();
    }

    /*
    class ErrorDialog fragment define a DialogFragment that displays the error dialog
     */
    public static class ErrorDialogFragment extends DialogFragment {
        private Dialog mDialog;

        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
}
