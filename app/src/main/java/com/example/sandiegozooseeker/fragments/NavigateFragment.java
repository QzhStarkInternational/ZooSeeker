package com.example.sandiegozooseeker.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.sandiegozooseeker.AnimalDB.Vertex;
import com.example.sandiegozooseeker.AnimalDB.VertexDao;
import com.example.sandiegozooseeker.AnimalDB.VertexDatabase;
import com.example.sandiegozooseeker.R;
import com.example.sandiegozooseeker.pathfinder.Directions;
import com.example.sandiegozooseeker.pathfinder.IdentifiedWeightedEdge;
import com.example.sandiegozooseeker.pathfinder.LoadLocation;
import com.example.sandiegozooseeker.pathfinder.Pathfinder;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;

import org.jgrapht.GraphPath;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

public class NavigateFragment extends Fragment {
    private CardView nextView;
    private TextView nextAnimalNameTextView;
    private TextView nextAnimalDistanceTextView;
    private TextView directionText;
    private TextView animalText;
    private List<String> directions;
    private List<String> orderedList;
    private VertexDao vertexDao;
    private LocationRequest locationRequest;
    private CancellationTokenSource taskCancellationSource;

    private FusedLocationProviderClient fusedLocationClient;
    private TextView locView;

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

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private Button skipButton;
    Directions dir;

    public NavigateFragment() {
        super(R.layout.fragment_navigate);
    }
    public NavigateFragment(){ super(R.layout.fragment_navigate); }

    //keep track of which animal exhibit direction to display
    private int mCurrentIndex = 0;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        vertexDao = VertexDatabase.getSingleton(getActivity()).vertexDao();
        List<String> selectedExhibits = vertexDao.getSelectedExhibitsID(Vertex.Kind.EXHIBIT);
        //System.out.println(selectedExhibits);
        Pathfinder pf = new Pathfinder(selectedExhibits, getActivity(), "entrance_exit_gate");

        List<GraphPath<String, IdentifiedWeightedEdge>> plan = pf.plan();
        List<String> orderedPaths = pf.pathsToStringList(plan);
        // System.out.println(orderedPaths);

        directionText = (TextView) view.findViewById(R.id.direction_text);
        animalText = (TextView) view.findViewById(R.id.animal_name);
        nextView = view.findViewById(R.id.nextView);
        nextAnimalNameTextView = view.findViewById(R.id.textView2);
        nextAnimalDistanceTextView = view.findViewById(R.id.textView);
        locView = view.findViewById(R.id.textView3);

        //skip button
        skipButton = (Button)view.findViewById(R.id.skipButton);
        //set up directions and update heading (animal name)
        dir = new Directions(plan, getActivity());
        directions = dir.getDirectionsAllAnimals();
        orderedList = dir.getOrderedList();
        //display directions to first exhibit
        updateDirections();

        if (orderedList.size() < 2) {
            nextView.setVisibility(View.GONE);
        } else {
            nextAnimalNameTextView.setText(vertexDao.getAnimalName(orderedList.get(mCurrentIndex + 1)));
            nextAnimalDistanceTextView.setText(dir.nextLabel(mCurrentIndex));
        }


        nextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % directions.size();
                updateDirections();
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //completely remove exhibit and replan (everything from current index onwards)
                //update orderedlist and deselect isSelected
                if (((mCurrentIndex+1) % directions.size()) != 0) {
                    Vertex vertexToChange = vertexDao.get(orderedList.get(mCurrentIndex + 1));
                    System.out.println(vertexToChange);
                    vertexToChange.isSelected = !vertexToChange.isSelected;
                    vertexDao.update(vertexToChange);
                    //replanning
                    List<String> selectedExhibits = vertexDao.getSelectedExhibitsID(Vertex.Kind.EXHIBIT);
                    Pathfinder pf = new Pathfinder(selectedExhibits, getActivity());
                    List<GraphPath<String, IdentifiedWeightedEdge>> plan = pf.plan();
                    dir = new Directions(plan, getActivity());
                    directions = dir.getDirectionsAllAnimals();
                    orderedList = dir.getOrderedList();
                }
                //mCurrentIndex = (mCurrentIndex+1) % directions.size();
                openDialog();

                updateDirections();

            }
        });
    }

    private void openDialog() {
        Prompt prompt = new Prompt();
        prompt.show(getActivity().getSupportFragmentManager(), "what is this");
    }

    //update question method
    private void updateDirections() {
        String direction = directions.get(mCurrentIndex);

        if(orderedList.size() < 2) {
            animalText.setText("");
            directionText.setText("");
        } else {
            animalText.setText("Directions to: " + vertexDao.getAnimalName(orderedList.get(mCurrentIndex)));
            directionText.setText(direction);
            if (mCurrentIndex == directions.size() - 2) {
                skipButton.setVisibility(View.GONE);
            }
            if (mCurrentIndex == directions.size() - 1) {
                nextView.setVisibility(View.GONE);
            } else {
                nextAnimalNameTextView.setText(vertexDao.getAnimalName(orderedList.get(mCurrentIndex + 1)));
                nextAnimalDistanceTextView.setText(dir.nextLabel(mCurrentIndex));
            }
        }

    }
}
