package com.example.game.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.game.R;
import com.example.game.activities.MainActivity;
import com.example.game.databinding.FragmentCreateEventBinding;
import com.example.game.models.Community;
import com.example.game.utils.ImageUtil;
import com.example.game.utils.NavigationUtil;
import com.example.game.models.Event;
import com.example.game.utils.ValidatorsUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.parse.Parse.getApplicationContext;

public class CreateEventFragment extends Fragment {
    private static final String TAG = "CreateEventFragment";
    public static final int PICK_PHOTO_CODE = 1046;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    public static final String COMMUNITIES = "communities";

    private ImageView ivPoster;
    private File photoFile;
    private String addressString;
    private LatLng addressLatLng;
    private TextView tvAddressDisplay;
    private Community community;

    public static CreateEventFragment newInstance(ArrayList<String> comments) {
        CreateEventFragment fragment = new CreateEventFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(COMMUNITIES, comments);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentCreateEventBinding binding = FragmentCreateEventBinding.bind(view);
        Bundle args = getArguments();
        ArrayList<String> communities = null;
        if (args != null) {
            communities = args.getStringArrayList(COMMUNITIES);
        }
        Button btnShare = binding.btnShare;
        EditText etTitle = binding.etTitle;
        EditText etDescription = binding.etDescription;
        ImageButton ibLoc = binding.ibLoc;
        ImageButton ibFile = binding.ibFile;
        ivPoster = binding.ivPoster;
        tvAddressDisplay = binding.tvAddressDisplay;
        AutoCompleteTextView autoTvCommunity = binding.autoTvCommunity;
        ArrayAdapter<String> adapter = null;
        if (communities != null) {
            adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_dropdown_item_1line, communities);
        }
        autoTvCommunity.setAdapter(adapter);

        autoTvCommunity.setOnItemClickListener((adapterView, view13, i, l) -> {
            String communityName = (String) adapterView.getItemAtPosition(i);
            if (communityName == null) {
                Log.e(TAG, "Community cannot be null");
                return;
            }
            ParseQuery<Community> communityParseQuery = ParseQuery.getQuery(Community.class);
            communityParseQuery.whereEqualTo(Community.KEY_NAME, communityName);
            communityParseQuery.getFirstInBackground((object, e) -> community = object);
        });

        ibFile.setOnClickListener(this::onPickPhoto);

        ibLoc.setOnClickListener(this::openPlaces);

        btnShare.setOnClickListener(view12 -> {
            Event event = new Event();
            String dateString = binding.etMonth.getText().toString().replaceAll("\\s", "") + "/" +
                    binding.etDay.getText().toString().replaceAll("\\s", "") + "/" +
                    binding.etYear.getText().toString().replaceAll("\\s", "") + " " +
                    binding.etHour.getText().toString().replaceAll("\\s", "") + ":" +
                    binding.etMinute.getText().toString().replaceAll("\\s", "");
            if (ValidatorsUtil.checkTimePattern(dateString)) {
                SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.pattern_time), Locale.US);
                try {
                    Date date = formatter.parse(dateString);
                    event.setDate(date);
                } catch (ParseException e) {
                    Log.e(TAG, "Error while parsing time" + e);
                    Snackbar.make(binding.etMonth, getString(R.string.wrong_date_message), BaseTransientBottomBar.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Snackbar.make(binding.etMonth, R.string.wrong_date_message, BaseTransientBottomBar.LENGTH_SHORT).show();
                return;
            }
            event.setUser(ParseUser.getCurrentUser());
            event.setTitle(etTitle.getText().toString());
            if (addressLatLng != null) {
                event.setAddress(new ParseGeoPoint(addressLatLng.latitude, addressLatLng.longitude));
                event.setAddressString(addressString);
            } else {
                Snackbar.make(btnShare, "Address cannot be empty", BaseTransientBottomBar.LENGTH_SHORT).show();
                return;
            }
            event.setImage(new ParseFile(photoFile));
            event.setCommunity(community);
            event.setDescription(etDescription.getText().toString());
            event.saveInBackground(e -> {
                if (e != null) {
                    Log.i(TAG, "Error making an event" + e);
                } else {
                    //go to make main Activity
                    Toast.makeText(getContext(), "Successful Created an event", Toast.LENGTH_SHORT).show();
                    NavigationUtil.goToActivity(getActivity(), MainActivity.class);
                }
            });
        });
    }

    private void openPlaces(View view) {
        // Initialize Places.
        Places.initialize(getApplicationContext(), getString(R.string.google_places_api));
        List<Place.Field> fields = Arrays.asList(Place.Field.values());
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(view.getContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_event, container, false);
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(Objects.requireNonNull(getContext()).getPackageManager()) != null) {
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();
            Bitmap image = ImageUtil.loadFromUri(getContext(), photoUri);
            photoFile = new File(ImageUtil.saveToInternalStorage(getContext(), image));
            ivPoster.setImageBitmap(image);
        }
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place;
                if (data != null) {
                    place = Autocomplete.getPlaceFromIntent(data);
                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + place.getAddress() + place.getLatLng());
                    addressLatLng = place.getLatLng();
                    addressString = place.getAddress();
                    tvAddressDisplay.setText(addressString);
                } else {
                    Snackbar.make(tvAddressDisplay, R.string.no_address_message, BaseTransientBottomBar.LENGTH_SHORT).show();
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                if (data != null) {
                    Log.i(TAG, Objects.requireNonNull(Autocomplete.getStatusFromIntent(data).getStatusMessage()));
                }
                Snackbar.make(tvAddressDisplay, R.string.no_address_message, BaseTransientBottomBar.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Snackbar.make(tvAddressDisplay, R.string.no_address_message, BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        }
    }
}
