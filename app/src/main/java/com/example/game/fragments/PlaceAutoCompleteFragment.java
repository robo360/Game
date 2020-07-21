package com.example.game.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.game.R;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;

import java.util.Arrays;
import java.util.List;

import static com.parse.Parse.getApplicationContext;


public class PlaceAutoCompleteFragment extends AutocompleteSupportFragment {

    public static PlaceAutoCompleteFragment newInstance() {
        PlaceAutoCompleteFragment fragment = new PlaceAutoCompleteFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        // Initialize Places.
        Places.initialize(getApplicationContext(), getString(R.string.google_places_api));
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(getContext());


        setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_place_auto_complete, container, false);
    }
}