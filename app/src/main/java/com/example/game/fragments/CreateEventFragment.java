package com.example.game.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import com.example.game.helpers.ImageUtil;
import com.example.game.helpers.NavigationUtil;
import com.example.game.models.Event;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreateEventFragment extends Fragment {
    private static final String TAG = "CreateEventFragment";
    public static final int PICK_PHOTO_CODE = 1046;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public static final String PHOTO_FILE_NAME = "photo.jpg";

    private FragmentCreateEventBinding binding;
    private ImageView ivPoster;
    private Bitmap image;
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
        binding = FragmentCreateEventBinding.bind(view);
        Button btnShare = binding.btnShare;
        EditText etTitle = binding.etTitle;
        EditText etDescription = binding.etDescription;
        ImageButton ibLoc = binding.ibLoc;
        ImageButton ibFile = binding.ibFile;
        ivPoster = binding.ivPoster;

        ibFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto(view);
            }
        });

        ibLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsFragment mapsFragment = new MapsFragment();
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().replace(R.id.flContainer, mapsFragment).commit();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Event event = new Event();
                event.setUser(ParseUser.getCurrentUser());
                event.setTitle(etTitle.getText().toString());
                event.setImage(new ParseFile(photoFile));
                event.setDescription(etDescription.getText().toString());
                event.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!= null){
                            Log.i(TAG, "Error making an event" + e);
                        } else{
                            //go to make main Activity
                            Toast.makeText(getContext(), "Successful Created an event", Toast.LENGTH_SHORT).show();
                            NavigationUtil.goToActivity(getActivity(), MainActivity.class);
                        }
                    }
                });
            }
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
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();
            // Load the image located at photoUri into selectedImage
            image = ImageUtil.loadFromUri(getContext(), photoUri);
            photoFile = new File(ImageUtil.saveToInternalStorage(getContext(), image));
            ivPoster.setImageBitmap(image);
        }
    }
}