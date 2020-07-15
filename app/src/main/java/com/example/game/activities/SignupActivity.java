package com.example.game.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.game.R;
import com.example.game.databinding.ActivityLoginBinding;
import com.example.game.databinding.ActivitySignupBinding;
import com.example.game.fragments.SignupFragment;
import com.example.game.helpers.Helper;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        final FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = SignupFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
    }
}
