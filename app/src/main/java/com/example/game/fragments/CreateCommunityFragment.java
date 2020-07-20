package com.example.game.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import com.example.game.R;
import com.example.game.activities.MainActivity;
import com.example.game.databinding.FragmentCreateCommunityBinding;
import com.example.game.databinding.FragmentCreateEventBinding;
import com.example.game.helpers.NavigationUtil;
import com.example.game.models.Community;
import com.example.game.models.Event;
import com.example.game.models.Subscription;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreateCommunityFragment extends Fragment {
    private static final String TAG = "CreateCommunityFragment";
    public static final int PICK_PHOTO_CODE = 1046;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public static final String PHOTO_FILE_NAME = "photo.jpg";

    private FragmentCreateCommunityBinding binding;
    private ImageView ivPoster;
    private Bitmap image;
    private File photoFile;

    public CreateCommunityFragment() {
        // Required empty public constructor
    }

    public static CreateCommunityFragment newInstance() {
        CreateCommunityFragment fragment = new CreateCommunityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentCreateCommunityBinding.bind(view);
        Button btnShare = binding.btnShare;
        EditText etTitle = binding.etTitle;
        EditText etDescription = binding.etDescription;
        ImageButton ibFile = binding.ibFile;
        ivPoster = binding.ivPoster;

        ibFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto(view);
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Community community = new Community();
                community.setCreator(ParseUser.getCurrentUser());
                community.setName(etTitle.getText().toString());
                community.setImage(new ParseFile(photoFile));
                community.setDescription(etDescription.getText().toString());
                community.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!= null){
                            Log.e(TAG, "Error making an event" + e);
                        } else{
                            //go to make main Activity
                            Subscription subscription = new Subscription();
                            subscription.setUser(ParseUser.getCurrentUser());
                            subscription.setCommunity(community);
                            subscription.setFollowStatus(true);
                            subscription.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null) {
                                        Toast.makeText(getContext(), "Successful Created A community", Toast.LENGTH_SHORT).show();
                                        NavigationUtil.goToActivity(getActivity(), MainActivity.class);
                                    } else{
                                        Log.e(TAG, "Error while making a subscription" + e);
                                    }
                                }
                            });

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
        return inflater.inflate(R.layout.fragment_create_community, container, false);
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
            image = loadFromUri(photoUri);
            photoFile = new File(saveToInternalStorage(image));
            ivPoster.setImageBitmap(image);
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if (Build.VERSION.SDK_INT > 27) {
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        File file = getPhotoFileUri("profile.jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }
}