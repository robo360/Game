package com.example.game.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.game.R;
import com.example.game.activities.MainActivity;
import com.example.game.databinding.FragmentTakePictureBinding;
import com.example.game.models.User;
import com.example.game.utils.ImageUtil;
import com.example.game.utils.NavigationUtil;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class TakePictureFragment extends Fragment {
    public static final int PICK_PHOTO_CODE = 1046;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private static final String TAG = "TakePictureFragment";
    public static final String PHOTO_FILE_NAME = "photo.jpg";

    private Button btnSubmit;
    private ImageView ivProfile;
    private File photoFile;

    public static TakePictureFragment newInstance() {
        TakePictureFragment fragment = new TakePictureFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        com.example.game.databinding.FragmentTakePictureBinding binding = FragmentTakePictureBinding.bind(view);
        ImageButton ibCamera = binding.ibCamera;
        ImageButton ibFile = binding.ibFile;
        btnSubmit = binding.btnSubmit;
        ivProfile = binding.ivProfile;
        TextView tvSkip = binding.tvSkip;

        //fill with a profile if there is one already
        ParseUser user = ParseUser.getCurrentUser();
        @Nullable ParseFile imageFile = user.getParseFile(User.KEY_IMAGE);
        if(imageFile != null){
            Glide.with(getContext()).load(imageFile.getUrl()).into(ivProfile);
        }

        //set a listener on all elements
        ibFile.setOnClickListener(view1 -> {
            onPickPhoto();
            btnSubmit.setVisibility(View.VISIBLE);
        });

        ibCamera.setOnClickListener(view12 -> {
            LaunchCamera();
            btnSubmit.setVisibility(View.VISIBLE);
        });

        tvSkip.setOnClickListener(view13 -> NavigationUtil.goToActivity(getActivity(), MainActivity.class));

        btnSubmit.setOnClickListener(view14 -> {
            if (photoFile == null || ivProfile.getDrawable() == null) {
                Log.e(TAG, "Picture cannot be empty cannot be empty");
            } else {
                save(ParseUser.getCurrentUser(), new ParseFile(photoFile));
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_take_picture, container, false);
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto() {
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
            ivProfile.setImageBitmap(image);
        }
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = ImageUtil.rotateBitmapOrientation(photoFile.getPath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivProfile.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void LaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = ImageUtil.getPhotoFileUri(Objects.requireNonNull(getContext()), PHOTO_FILE_NAME);
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.example.game", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        // make sure if the intent can be handled
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private void save(final ParseUser currentUser, final ParseFile image) {
        currentUser.put("image", image);
        currentUser.saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Error signing up: " + e);
                Snackbar.make(btnSubmit, "Error signing up" + e, BaseTransientBottomBar.LENGTH_SHORT);
            } else {
                NavigationUtil.goToActivity(getActivity(), MainActivity.class);
            }
        });
    }
}
