package com.example.game.fragments;

import android.app.Dialog;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.game.R;
import com.example.game.activities.MainActivity;
import com.example.game.databinding.FragmentCreateCommunityBinding;
import com.example.game.models.Community;
import com.example.game.models.Subscription;
import com.example.game.utils.ImageUtil;
import com.example.game.utils.NavigationUtil;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;

public class CreateCommunityFragment extends DialogFragment {
    private static final String TAG = "CreateCommunityFragment";
    public static final int PICK_PHOTO_CODE = 1046;

    private ImageView ivPoster;
    private File photoFile;

    public static CreateCommunityFragment newInstance() {
        CreateCommunityFragment fragment = new CreateCommunityFragment();
        Bundle args = new Bundle();
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
        com.example.game.databinding.FragmentCreateCommunityBinding binding = FragmentCreateCommunityBinding.bind(view);
        Button btnShare = binding.btnShare;
        EditText etTitle = binding.etTitle;
        EditText etDescription = binding.etDescription;
        ImageButton ibClose = binding.ibClose;
        ivPoster = binding.ivPoster;

        ivPoster.setOnClickListener(this::onPickPhoto);

        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnShare.setOnClickListener(view12 -> {
            Community community = new Community();
            community.setCreator(ParseUser.getCurrentUser());
            community.setName(etTitle.getText().toString());
            community.setImage(new ParseFile(photoFile));
            community.setDescription(etDescription.getText().toString());
            community.saveInBackground(e -> {
                if (e != null) {
                    Log.e(TAG, "Error making an event" + e);
                } else {
                    subscribeToCommunity(community);
                }
            });

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
        if (intent.resolveActivity(view.getContext().getPackageManager()) != null) {
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

    private void subscribeToCommunity(Community community) {
        //Subscribe to that community and go to make main Activity
        Subscription subscription = new Subscription();
        subscription.setUser(ParseUser.getCurrentUser());
        subscription.setCommunity(community);
        subscription.setFollowStatus(true);
        subscription.saveInBackground(e -> {
            if (e == null) {
                Toast.makeText(getContext(), "Successful Created A community", Toast.LENGTH_SHORT).show();
                NavigationUtil.goToActivity(getActivity(), MainActivity.class);
            } else {
                Log.e(TAG, "Error while making a subscription" + e);
            }
        });
    }
}
