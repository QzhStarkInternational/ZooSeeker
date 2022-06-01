package com.example.sandiegozooseeker.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sandiegozooseeker.PathFinder.PathFinder;
import com.example.sandiegozooseeker.R;
import com.example.sandiegozooseeker.graph.GraphVertex;
import com.example.sandiegozooseeker.graph.Zoo;
import com.example.sandiegozooseeker.locations.Coords;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Arrays;
import java.util.List;

public class NavigateFragment extends Fragment {
    private TextView nextAnimalNameTextView;
    private TextView nextAnimalDistanceTextView;
    private TextView directionText;
    private TextView animalText;

    private LocationRequest locationRequest;
    private CancellationTokenSource taskCancellationSource;
    private FusedLocationProviderClient fusedLocationClient;

    private final static boolean isTesting = true;

    PathFinder pf;

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
        CardView nextView = view.findViewById(R.id.nextView);
        nextAnimalNameTextView = view.findViewById(R.id.textView2);
        nextAnimalDistanceTextView = view.findViewById(R.id.textView);
        CardView previousAnimalView = view.findViewById(R.id.previousView);

        pf = new PathFinder(getContext(), Zoo.getZoo(getContext()).getVertex("entrance_exit_gate"));

        updateDirections(pf.getDirection());
        checkLoc();

        nextView.setOnClickListener(view1 -> {
            updateDirections(pf.getDirection());
            checkLoc();
        });

        previousAnimalView.setOnClickListener(view1 -> {
            updateDirections(pf.getPrevious());
            checkLoc();
        });

    }

    private void updateDirections(List<String> directions) {
        StringBuilder directionString = new StringBuilder();

        for(int i = 0; i < directions.size(); i++){
            directionString.append("\n").append(directions.get(i));
        }

        animalText.setText(String.format("Directions to: %s", pf.currentAnimalName()));
        directionText.setText(directionString.toString());

        nextAnimalNameTextView.setText(pf.nextAnimalName());
        nextAnimalDistanceTextView.setText(pf.nextLabel());

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
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            GraphVertex newStart;
                            if(isTesting){
                                newStart = pf.checkLocation(Coords.CURRENT_LOCATION);
                            } else {
                                newStart = pf.checkLocation(currentLocation);
                            }

                            if(newStart != null) {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
                                builder.setPositiveButton("Replan", (dialog, which) -> {
                                    pf.replanPath(pf.getRemainingExhibits(), newStart);
                                    updateDirections(pf.getDirection());
                                });

                                builder.setMessage("Did you take the wrong turn?").setTitle("Oops").show();
                            }
                        }
                    }
                });

    }
}