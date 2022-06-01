package com.example.sandiegozooseeker.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.sandiegozooseeker.PathFinder.PathFinder;
import com.example.sandiegozooseeker.PathFinder.PathFinderNew;
import com.example.sandiegozooseeker.R;
import com.example.sandiegozooseeker.graph.GraphVertex;
import com.example.sandiegozooseeker.locations.Coords;
import com.example.sandiegozooseeker.graph.Zoo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.util.Arrays;
import java.util.List;

public class NavigateFragment extends Fragment {
    private TextView nextAnimalNameTextView;
    private TextView nextAnimalDistanceTextView;
    private TextView previousAnimalNameTextView;
    private TextView previousAnimalDistanceTextView;
    private TextView directionText;
    private TextView animalText;
    private CardView nextView;
    private CardView previousAnimalView;

    private View skipButton;

    private Switch briefDirectionsSwitch;

    private LocationRequest locationRequest;
    private CancellationTokenSource taskCancellationSource;
    private FusedLocationProviderClient fusedLocationClient;

    private final static boolean isTesting = false;
    private Coords coords;

    PathFinder pf;
    PathFinderNew pfNew;

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                         Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION, false);
                        if (fineLocationGranted != null && fineLocationGranted) {
                            // Precise location access granted.
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            // Only approximate location access granted.
                        } else {
                            // No location access granted.
                        }
                    }
            );


    public NavigateFragment() {
        super(R.layout.fragment_navigate);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        directionText = view.findViewById(R.id.direction_text);
        animalText = view.findViewById(R.id.animal_name);
        nextView = view.findViewById(R.id.nextView);
        nextAnimalNameTextView = view.findViewById(R.id.textView2);
        nextAnimalDistanceTextView = view.findViewById(R.id.textView);
        previousAnimalView = view.findViewById(R.id.previousView);
        previousAnimalNameTextView = (TextView) view.findViewById(R.id.previousAnimalName);
        previousAnimalDistanceTextView = (TextView) view.findViewById(R.id.previousAnimalDirection);
        skipButton = view.findViewById(R.id.skipButton);
        briefDirectionsSwitch = view.findViewById(R.id.switch1);
        previousAnimalView.setVisibility(View.INVISIBLE);

        pf = new PathFinder(getContext(), Zoo.getZoo(getContext()).getVertex("entrance_exit_gate"));
        pfNew = new PathFinderNew(getContext(), Zoo.getZoo(getContext()).getVertex("entrance_exit_gate"));

        //FOR TESTING LOCATION
        Coords coords = new Coords();

        updateDirections(pfNew.getNextDirection());
        checkLoc();

        nextView.setOnClickListener(view1 -> {
            updateDirections(pfNew.getNextDirection());
            coords.nextLoc();
            checkLoc();
        });

        previousAnimalView.setOnClickListener(view1 -> {
            updateDirections(pfNew.getPreviousDirection());
            coords.previousLoc();
            checkLoc();
        });

        skipButton.setOnClickListener(view1 -> {
            updateDirections(pfNew.skip());
            checkLoc();
        });

        briefDirectionsSwitch.setOnCheckedChangeListener ((buttonView, isChecked) -> {
            pfNew.toggleBriefDirections(isChecked);
            updateDirections(pfNew.getCurrentPath());
        });


    }

    private void updateDirections(List<String> directions) {
        StringBuilder directionString = new StringBuilder();

        for(int i = 0; i < directions.size(); i++){
            directionString.append("\n").append(directions.get(i));
        }

        animalText.setText(String.format("Directions to: %s", pfNew.currentAnimal()));
        directionText.setText(directionString.toString());


        if (pfNew.getVisitedExhibits().size() != 0) {
            previousAnimalNameTextView.setText(pfNew.previousExhibitName());
            previousAnimalDistanceTextView.setText(pfNew.previousExhibitDistance());
            previousAnimalView.setVisibility(View.VISIBLE);
        } else {
            previousAnimalView.setVisibility(View.INVISIBLE);
        }

        if (pfNew.getRemainingExhibits().size() != 0) {
            nextAnimalNameTextView.setText(pfNew.nextExhibitName());
            nextAnimalDistanceTextView.setText(pfNew.nextExhibitDistance());
            nextView.setVisibility(View.VISIBLE);
        } else {
            nextView.setVisibility(View.INVISIBLE);
        }

        if(pfNew.getVisitedExhibits().size() == 0 || pfNew.getRemainingExhibits().size() == 0){
            skipButton.setVisibility(View.INVISIBLE);
        } else {
            skipButton.setVisibility(View.VISIBLE);
        }

        if (pfNew.getVisitedExhibits().size() == 1 && pfNew.getRemainingExhibits().size() == 1) {
            previousAnimalView.setVisibility(View.INVISIBLE);
            skipButton.setVisibility(View.INVISIBLE);
            nextAnimalNameTextView.setText(pfNew.nextExhibitName());
            nextAnimalDistanceTextView.setText(pfNew.nextExhibitDistance());
            nextView.setVisibility(View.VISIBLE);
        }
    }

    private void checkLoc(){
        {
            String[] requiredPermissions = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };

            boolean hasNoLocationPerms = Arrays.stream(requiredPermissions)
                    .map(perm -> ContextCompat.checkSelfPermission(requireActivity(), perm))
                    .allMatch(status -> status == PackageManager.PERMISSION_DENIED);

            if (hasNoLocationPerms) {
                requestPermissionLauncher.launch(requiredPermissions);
                return;
            }

        }

        // Get user current location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        CancellationTokenSource cts = new CancellationTokenSource();
        CancellationToken ct = cts.getToken();

        fusedLocationClient.getCurrentLocation(100, ct)
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        boolean ofTrack = false;

                        if (isTesting) {
                            ofTrack = pfNew.checkLocation(Coords.GORILLA);
                        } else {
                            ofTrack = false; // pfNew.checkLocation(currentLocation)
                        }

                        if (ofTrack) {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
                            builder.setPositiveButton("Replan", (dialog, which) -> {
                                if(isTesting){
                                    updateDirections(pfNew.updateBasedOnLocation(Coords.GORILLA));

                                } else {
                                    updateDirections(pfNew.updateBasedOnLocation(currentLocation));
                                }
                            });


                            builder.setMessage("Did you take the wrong turn?").setTitle("Oops").show();
                        }
                    }
                });
    }
}