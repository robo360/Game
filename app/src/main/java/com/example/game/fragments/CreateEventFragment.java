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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.game.R;
import com.example.game.activities.MainActivity;
import com.example.game.databinding.FragmentCreateEventBinding;
import com.example.game.utils.ImageUtil;
import com.example.game.utils.NavigationUtil;
import com.example.game.models.Event;
import com.example.game.utils.ValidatorsUtil;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class CreateEventFragment extends Fragment {
    private static final String TAG = "CreateEventFragment";
    public static final int PICK_PHOTO_CODE = 1046;

    private ImageView ivPoster;
    private File photoFile;

    public static CreateEventFragment newInstance() {
        CreateEventFragment fragment = new CreateEventFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        com.example.game.databinding.FragmentCreateEventBinding binding = FragmentCreateEventBinding.bind(view);
        Button btnShare = binding.btnShare;
        EditText etTitle = binding.etTitle;
        EditText etDescription = binding.etDescription;
        ImageButton ibLoc = binding.ibLoc;
        ImageButton ibFile = binding.ibFile;
        ivPoster = binding.ivPoster;

        ibFile.setOnClickListener(this::onPickPhoto);

        ibLoc.setOnClickListener(view1 -> {
            MapsFragment mapsFragment = new MapsFragment();
            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction().replace(R.id.flContainer, mapsFragment).commit();
            }
        });

        btnShare.setOnClickListener(view12 -> {
            String dateString = binding.etMonth.getText().toString().replaceAll("\\s", "") + "/" +
                    binding.etDay.getText().toString().replaceAll("\\s", "") + "/" +
                    binding.etYear.getText().toString().replaceAll("\\s", "") + " " +
                    binding.etHour.getText().toString().replaceAll("\\s", "") + ":" +
                    binding.etMinute.getText().toString().replaceAll("\\s", "");
            if(ValidatorsUtil.checkTimePattern(dateString)){
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm");
                try {
                    Date date = formatter.parse(dateString);
                } catch (ParseException e) {
                    Log.e(TAG, "Error while parsing time" + e);
                    Snackbar.make(binding.etMonth, "Wrong Date and Time input. Please follow the format.", BaseTransientBottomBar.LENGTH_SHORT).show();
                    return;
                }
            } else{
                Snackbar.make(binding.etMonth, "Wrong Date and Time input. Please follow the format.", BaseTransientBottomBar.LENGTH_SHORT).show();
                return;
            }
            Event event = new Event();
            event.setUser(ParseUser.getCurrentUser());
            event.setTitle(etTitle.getText().toString());
            event.setImage(new ParseFile(photoFile));
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();
            // Load the image located at photoUri into selectedImage
            Bitmap image = ImageUtil.loadFromUri(getContext(), photoUri);
            photoFile = new File(ImageUtil.saveToInternalStorage(getContext(), image));
            ivPoster.setImageBitmap(image);
        }
    }
}
