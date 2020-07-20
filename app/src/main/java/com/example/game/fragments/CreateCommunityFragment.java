package com.example.game.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
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
import com.example.game.databinding.FragmentCreateCommunityBinding;
import com.example.game.helpers.ImageUtil;
import com.example.game.helpers.NavigationUtil;
import com.example.game.models.Community;
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
                           subscribeToCommunity(community);
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
            image = ImageUtil.loadFromUri(getContext(), photoUri);
            photoFile = new File(ImageUtil.saveToInternalStorage(getContext(),image));
            ivPoster.setImageBitmap(image);
        }
    }

    private void subscribeToCommunity(Community community){
        //Subscribe to that community and go to make main Activity
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