package com.example.game.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.game.R;
import com.example.game.databinding.ActivitySignupBinding;
import com.example.game.fragments.SignupFragment;

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
