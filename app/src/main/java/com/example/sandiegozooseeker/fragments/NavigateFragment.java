package com.example.sandiegozooseeker.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.sandiegozooseeker.AnimalDB.Vertex;
import com.example.sandiegozooseeker.AnimalDB.VertexDao;
import com.example.sandiegozooseeker.AnimalDB.VertexDatabase;
import com.example.sandiegozooseeker.R;
import com.example.sandiegozooseeker.pathfinder.Directions;
import com.example.sandiegozooseeker.pathfinder.IdentifiedWeightedEdge;
import com.example.sandiegozooseeker.pathfinder.Pathfinder;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;

import org.jgrapht.GraphPath;

import java.util.Arrays;
import java.util.List;

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
    Pathfinder pf;
    List<String> selectedExhibits;

    public NavigateFragment() {
        super(R.layout.fragment_navigate);
    }

    //keep track of which animal exhibit direction to display
    private int mCurrentIndex = 0;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        vertexDao = VertexDatabase.getSingleton(getActivity()).vertexDao();
        selectedExhibits = vertexDao.getSelectedExhibitsID(Vertex.Kind.EXHIBIT);
        //System.out.println(selectedExhibits);
        pf = new Pathfinder(selectedExhibits, getActivity(), "entrance_exit_gate");

        List<GraphPath<String, IdentifiedWeightedEdge>> plan = pf.plan();
        List<String> orderedPaths = pf.pathsToStringList(plan);
        // System.out.println(orderedPaths);

        directionText = (TextView)view.findViewById(R.id.direction_text);
        animalText = (TextView) view.findViewById(R.id.animal_name);
        nextView = view.findViewById(R.id.nextView);
        nextAnimalNameTextView = view.findViewById(R.id.textView2);
        nextAnimalDistanceTextView = view.findViewById(R.id.textView);

        //skip button
        skipButton = (Button)view.findViewById(R.id.skipButton);
        //set up directions and update heading (animal name)
        dir = new Directions(plan,getActivity());
        directions = dir.getDirectionsAllAnimals();
        orderedList = dir.getOrderedList();
        //display directions to first exhibit
        updateDirections();

        if(orderedList.size() < 2){
            nextView.setVisibility(View.GONE);
        } else {
            nextAnimalNameTextView.setText(vertexDao.getAnimalName(orderedList.get(mCurrentIndex + 1)));
            nextAnimalDistanceTextView.setText(dir.nextLabel(mCurrentIndex));
        }


        nextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex+1) % directions.size();
                updateDirections();
                checkLoc();
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
                    pf = new Pathfinder(selectedExhibits, getActivity(), "entrance_exit_gate");
                    List<GraphPath<String, IdentifiedWeightedEdge>> plan = pf.plan();
                    dir = new Directions(plan, getActivity());
                    directions = dir.getDirectionsAllAnimals();
                    orderedList = dir.getOrderedList();
                    checkLoc();
                }
                //mCurrentIndex = (mCurrentIndex+1) % directions.size();
                openDialog();

                updateDirections();

            }
        });

        // Check permission
        checkLoc();
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

        String provider = LocationManager.GPS_PROVIDER;
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        fusedLocationClient.getCurrentLocation(100, ct)
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            Vertex currentVertex = dir.getCurrentVertex(mCurrentIndex);

                            if (currentVertex.getLat() != currentLocation.latitude && currentVertex.getLng() != currentLocation.longitude) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                builder.setPositiveButton("Replan", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        int maxIndex = pf.getvertexNums();

                                        for(int i = 0; i < maxIndex; i++){
                                            Vertex currentV = pf.getVertexId(i);
                                            if(currentV.getLat() == currentLocation.latitude && currentV.getLng() == currentLocation.longitude){
                                                pf = new Pathfinder(selectedExhibits, getActivity(), currentV.id);
                                                List<GraphPath<String, IdentifiedWeightedEdge>> plan = pf.plan();
                                                dir = new Directions(plan, getActivity());
                                                directions = dir.getDirectionsAllAnimals();
                                                orderedList = dir.getOrderedList();
                                                updateDirections();
                                            }
                                        }
                                    }
                                });
                                builder.setMessage("Did you take the wrong turn?").setTitle("Oops");
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    }
                });
    }


    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("").setTitle("Replanning");
        AlertDialog dialog = builder.create();
        dialog.show();
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