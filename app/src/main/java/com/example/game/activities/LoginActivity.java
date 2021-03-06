package com.example.game.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.game.databinding.ActivityLoginBinding;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ParseUser.getCurrentUser() != null) {
            goToActivity(MainActivity.class);
        }
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        TextView tvSingup = binding.tvSignUpMessage;
        setContentView(binding.getRoot());
        etEmail = binding.etMail;
        etPassword = binding.etPassword;
        btnLogin = binding.btnLogin;
        progressBar = binding.progressBar;
        //TODO: [UX] When the etPassword or etEmail lose focus, remove the virtual keyboard.

        //set a listener on the btnLogin
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check whether the fields are not empty else login
                String email = etEmail.getText().toString().replaceAll(" ", "");
                String password = etPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Snackbar.make(btnLogin, "Email or password cannot be empty", BaseTransientBottomBar.LENGTH_SHORT).show();
                } else {
                    btnLogin.setClickable(false);
                    progressBar.setVisibility(View.VISIBLE);
                    //Send Async request
                    ParseUser.logInInBackground(email, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error while logging in" + e);
                                Snackbar.make(btnLogin, "Wrong email or password" , BaseTransientBottomBar.LENGTH_SHORT).show();
                                btnLogin.setClickable(true);
                            } else {
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                goToActivity(MainActivity.class);
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });

        //set a listener on the tvSingup
        tvSingup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToActivity(SignupActivity.class);
            }
        });
    }

    private void goToActivity(Class aClass) {
        Intent i = new Intent(LoginActivity.this, aClass);
        startActivity(i);
        finish();
    }
}
