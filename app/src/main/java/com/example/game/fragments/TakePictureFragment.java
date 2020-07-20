package com.example.game.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
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

import com.example.game.R;
import com.example.game.activities.MainActivity;
import com.example.game.databinding.FragmentTakePictureBinding;
import com.example.game.helpers.ImageUtil;
import com.example.game.helpers.NavigationUtil;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class TakePictureFragment extends Fragment {
    public static final int PICK_PHOTO_CODE = 1046;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private static final String TAG = "TakePictureFragment";
    public static final String PHOTO_FILE_NAME = "photo.jpg";

    private FragmentTakePictureBinding binding;
    private ImageButton ibFile;
    private ImageButton ibCamera;
    private Button btnSubmit;
    private ImageView ivProfile;
    private TextView tvSkip;
    private Bitmap image;
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
        binding = FragmentTakePictureBinding.bind(view);
        ibCamera = binding.ibCamera;
        ibFile = binding.ibFile;
        btnSubmit = binding.btnSubmit;
        ivProfile = binding.ivProfile;
        tvSkip = binding.tvSkip;

        //set a listener on all elements
        ibFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto(view);
                btnSubmit.setVisibility(View.VISIBLE);
            }
        });

        ibCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchCamera();
                btnSubmit.setVisibility(View.VISIBLE);
            }
        });

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationUtil.goToActivity(getActivity(), MainActivity.class);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photoFile == null || ivProfile.getDrawable() == null) {
                    Log.e(TAG, "Picture cannot be empty cannot be empty");
                } else {
                    save(ParseUser.getCurrentUser(), new ParseFile(photoFile));
                }
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
        photoFile = ImageUtil.getPhotoFileUri(getContext(), PHOTO_FILE_NAME);
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.example.game", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        // make sure if the intent can be handled
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private void save(final ParseUser currentUser, final ParseFile image) {
        ParseUser.getCurrentUser().put("image", image);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error signing up: " + e);
                    Snackbar.make(btnSubmit, "Error signing up" + e, BaseTransientBottomBar.LENGTH_SHORT);
                } else {
                    NavigationUtil.goToActivity(getActivity(), MainActivity.class);
                }
            }
        });
    }
}
