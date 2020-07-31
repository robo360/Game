package com.example.game.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.game.R;
import com.example.game.activities.LoginActivity;
import com.example.game.databinding.FragmentSignupBinding;
import com.example.game.models.Community;
import com.example.game.models.Subscription;
import com.example.game.utils.ConstantUtils;
import com.example.game.utils.NavigationUtil;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Objects;

public class SignupFragment extends Fragment {
    private static final String TAG = "SignupActivity";

    private EditText etEmail;
    private EditText etPassword;
    private EditText etName;
    private EditText etRepeatedPassword;
    private Button btnSignup;

    public static SignupFragment newInstance() {
        SignupFragment fragment = new SignupFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentSignupBinding binding = FragmentSignupBinding.bind(view);
        TextView tvLoginMessage = binding.tvLoginMessage;
        etEmail = binding.etMail;
        etPassword = binding.etPassword;
        etName = binding.etName;
        etRepeatedPassword = binding.etRepeatPassword;
        btnSignup = binding.btnSignUp;

        //set a listener on the btnSignup
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = etEmail.getText().toString();
                String name = etName.getText().toString();
                final String password = etPassword.getText().toString();
                String repeatedPassword = etRepeatedPassword.getText().toString();
                if (password.equals(repeatedPassword)) {
                    ParseUser user = new ParseUser();
                    user.put("name", name);
                    user.setUsername(email);
                    user.setPassword(password);
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error Signup: " + e);
                            } else {
                                login(email, password);
                            }
                        }
                    });
                } else {
                    Snackbar.make(btnSignup, "Password and Repeated Password should match",
                            BaseTransientBottomBar.LENGTH_SHORT).show();
                    etPassword.setBackground(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.edit_text_border_danger));
                    etRepeatedPassword.setBackground(getActivity().getDrawable(R.drawable.edit_text_border_danger));
                }
            }
        });

        //set a listener on the tvLoginMessage
        tvLoginMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationUtil.goToActivity(getActivity(), LoginActivity.class);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    private void login(String email, String password) {
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error Logging in: " + e);
                } else {
                    //Subscribe to public community
                    ParseQuery<Community> communityParseQuery = ParseQuery.getQuery(Community.class);
                    communityParseQuery.whereEqualTo(Community.KEY_NAME, ConstantUtils.BASE_COMMUNITY);
                    communityParseQuery.getFirstInBackground(new GetCallback<Community>() {
                        @Override
                        public void done(Community object, ParseException e) {
                            Subscription subscription = new Subscription();
                            subscription.setCommunity(object);
                            subscription.setUser(ParseUser.getCurrentUser());
                            subscription.saveInBackground();
                        }
                    });
                    Toast.makeText(getContext(), "Successful logged in", Toast.LENGTH_SHORT).show();
                    Fragment fragment = new TakePictureFragment();
                    Objects.requireNonNull(getFragmentManager()).beginTransaction().replace(R.id.flContainer, fragment).commit();
                }
            }
        });
    }
}
