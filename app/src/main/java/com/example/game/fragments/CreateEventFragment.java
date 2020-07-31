package com.example.game.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.game.R;
import com.example.game.activities.MainActivity;
import com.example.game.databinding.FragmentCreateEventBinding;
import com.example.game.models.Community;
import com.example.game.models.Event;
import com.example.game.utils.ImageUtil;
import com.example.game.utils.NavigationUtil;
import com.example.game.utils.ValidatorsUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.parse.Parse.getApplicationContext;

public class CreateEventFragment extends DialogFragment {
    private static final String TAG = "CreateEventFragment";
    public static final int PICK_PHOTO_CODE = 1046;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    public static final String COMMUNITIES = "communities";

    private ImageView ivPoster;
    private File photoFile;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private String addressString;
    private LatLng addressLatLng;
    private TextView tvAddressDisplay;
    private ArrayAdapter<String> adapter;
    private Community community;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    public static CreateEventFragment newInstance(ArrayList<String> communities) {
        CreateEventFragment fragment = new CreateEventFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(COMMUNITIES, communities);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
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
        ImageButton btnDate = binding.ibDate;
        ImageButton btnTime = binding.ibTime;
        ImageButton ibLoc = binding.ibLoc;
        ImageButton ibClose = binding.ibClose;
        ivPoster = binding.ivPoster;
        tvAddressDisplay = binding.tvAddressDisplay;
        if (communities != null) {
            adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_single_choice, communities);
        }

        ivPoster.setOnClickListener(this::onPickPhoto);

        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        ibLoc.setOnClickListener(this::openPlaces);
        View.OnClickListener dateClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        DecimalFormat formatter = new DecimalFormat("00");
                        year = i;
                        month = i1;
                        day = i2;
                        binding.tvDisplayDate.setText(String.format("%s/%s/%s",
                                formatter.format(month), formatter.format(day), year));
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        };

        View.OnClickListener timeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hour = 12;
                minute = 0;
                timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        DecimalFormat formatter = new DecimalFormat("00");
                        hour = i ;
                        minute = i1;
                        binding.tvDisplayTime.setText(String.format("%s:%s",
                                formatter.format(hour), formatter.format(minute)));
                    }
                }, hour,minute, false);

                timePickerDialog.show();
            }
        };
        btnDate.setOnClickListener(dateClickListener);
        btnTime.setOnClickListener(timeClickListener);
        binding.tvDisplayDate.setOnClickListener(dateClickListener);
        binding.tvDisplayTime.setOnClickListener(timeClickListener);
        binding.autoTvCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Adapter elements:" + adapter.getCount());
                MaterialAlertDialogBuilder communityDialog = new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Select Community")
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setSingleChoiceItems(adapter, 1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                binding.autoTvCommunity.setText(adapter.getItem(i));
                                ParseQuery<Community> communityParseQuery = ParseQuery.getQuery(Community.class);
                                communityParseQuery.whereEqualTo(Community.KEY_NAME, adapter.getItem(i));
                                communityParseQuery.getFirstInBackground(new GetCallback<Community>() {
                                    @Override
                                    public void done(Community object, com.parse.ParseException e) {
                                        if(e == null){
                                            community = object;
                                            Toast.makeText(getContext(), "Community set", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                communityDialog.show();
            }
        });

        btnShare.setOnClickListener(view12 -> {
            Event event = new Event();
            DecimalFormat formatter = new DecimalFormat("00");
            String dateString = String.format("%s/%s/%s %s:%s",
                    formatter.format(month), formatter.format(day), year,
                    formatter.format(hour), formatter.format(minute));
            if (ValidatorsUtil.checkTimePattern(dateString)) {
                SimpleDateFormat format = new SimpleDateFormat(getString(R.string.pattern_time), Locale.US);
                try {
                    Date date = format.parse(dateString);
                    event.setDate(date);
                } catch (ParseException e) {
                    Log.e(TAG, "Error while parsing time" + e);
                    Snackbar.make(binding.ibDate, getString(R.string.wrong_date_message), BaseTransientBottomBar.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Snackbar.make(binding.ibLoc, R.string.wrong_date_message, BaseTransientBottomBar.LENGTH_SHORT).show();
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
        Places.initialize(getApplicationContext(), getString(R.string.google_places_key));
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
        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                Place place;
                place = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
                addressLatLng = place.getLatLng();
                addressString = place.getAddress();
                tvAddressDisplay.setText(addressString);
            } else {
                if (data != null) {
                    Log.i(TAG, Objects.requireNonNull(Autocomplete.getStatusFromIntent(data).getStatusMessage()));
                }
                Snackbar.make(tvAddressDisplay, R.string.no_address_message, BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        }
    }
}
