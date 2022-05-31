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
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import kotlin.collections.ArrayDeque;

public class NavigateFragment extends Fragment {
    private CardView nextView;
    private TextView nextAnimalNameTextView;
    private TextView nextAnimalDistanceTextView;
    private TextView directionText;
    private TextView animalText;
    private List<String> directions;
    private List<String> orderedList;
    private List<String> remainingExhibits;
    private VertexDao vertexDao;
    private LocationRequest locationRequest;
    private CancellationTokenSource taskCancellationSource;
    private Map<String,Integer> tempMapping;

    private EditText latInput;
    private EditText lngInput;
    private Button enterButton;

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

    private CardView previousView;
    private TextView previousAnimalNameTextView;
    private TextView previousAnimalDistanceTextView;

    private Button skipButton;
    private String start;
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

        start = "entrance_exit_gate";
        directionText = (TextView)view.findViewById(R.id.direction_text);
        animalText = (TextView) view.findViewById(R.id.animal_name);
        nextView = view.findViewById(R.id.nextView);
        nextAnimalNameTextView = view.findViewById(R.id.textView2);
        nextAnimalDistanceTextView = view.findViewById(R.id.textView);

        //input json mock coordinates to indicate off track
        latInput = (EditText) view.findViewById(R.id.latInput);
        lngInput = (EditText) view.findViewById(R.id.lngInput);
        enterButton = (Button) view.findViewById(R.id.enterCordButton);

        //previous view
        previousView = view.findViewById(R.id.previousView);
        previousAnimalNameTextView = (TextView) view.findViewById(R.id.previousAnimalName);
        previousAnimalDistanceTextView = (TextView) view.findViewById(R.id.previousAnimalDirection);

        //skip button
        skipButton = (Button)view.findViewById(R.id.skipButton);
        //set up directions and update heading (animal name)
        dir = new Directions(plan,getActivity());
        directions = dir.getDirectionsAllAnimals();
        //orderedList = dir.getOrderedList();
        //dummy call to populate ordered list
        tempMapping = pf.getDistanceMapping(plan);
        orderedList = pf.getOrderedList();
        remainingExhibits = new ArrayList<String>(orderedList);
        //System.out.println(orderedList);
        //display directions to first exhibit
        updateDirections();

        if(orderedList.size() < 2){
            nextView.setVisibility(View.GONE);
        } else {
            nextAnimalNameTextView.setText(vertexDao.getAnimalName(orderedList.get(mCurrentIndex + 1)));
            nextAnimalDistanceTextView.setText(dir.nextLabel(mCurrentIndex));
        }

        //enter mock coordinates for off track
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLoc();
            }
        });

        nextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start = remainingExhibits.get(0);
                remainingExhibits.remove(0);
                mCurrentIndex = (mCurrentIndex+1) % directions.size();
                updateDirections();
                checkLoc();
            }
        });

        //currently can only go backwards once
        previousView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDirections();
                if (mCurrentIndex != 0) {
                    animalText.setText("Directions from " + vertexDao.getAnimalName(orderedList.get(mCurrentIndex)) + " to: " + vertexDao.getAnimalName(orderedList.get(mCurrentIndex-1)));
                    directionText.setText(dir.getPrevious(mCurrentIndex+1));
                }
                //mCurrentIndex = (mCurrentIndex-1) % directions.size();
                checkLoc();
            }
        });

        //when you skip you can't use previous
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //completely remove exhibit and replan (everything from current index onwards)
                //update orderedlist and deselect isSelected
                if (((mCurrentIndex+1) % directions.size()) != 0) {
                    Vertex vertexToChange = vertexDao.get(orderedList.get(mCurrentIndex));
                    //System.out.println(vertexToChange);
                    vertexToChange.isSelected = !vertexToChange.isSelected;
                    vertexDao.update(vertexToChange);
                    remainingExhibits.remove(0);
                    //replanning
                    //Log.d("ORDERED_LIST_CURRENT", "onClick: " + start);
                    //Log.d("REMAINING_EXHIBITS", "onClick: " + remainingExhibits);
                    remainingExhibits.remove(remainingExhibits.size()-1);
                    Pathfinder pf = new Pathfinder(remainingExhibits, getActivity(), start);
                    List<GraphPath<String, IdentifiedWeightedEdge>> plan = pf.plan();
                    dir = new Directions(plan, getActivity());
                    directions = dir.getDirectionsAllAnimals();
                    //dummy call to populate ordered list
                    tempMapping = pf.getDistanceMapping(plan);
                    orderedList = pf.getOrderedList();
                    remainingExhibits = new ArrayList<String>(orderedList);
                    mCurrentIndex = 0;
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

        //setting mocked location
        Location loc = new Location("dummyProvider");
        Double lat = 0.0;
        Double lng = 0.0;
        if (latInput.getText().toString().equals("") || lngInput.getText().toString().equals("")) {
            lat = dir.getCurrentVertex(mCurrentIndex).getLat();
            lng = dir.getCurrentVertex(mCurrentIndex).getLng();
        } else {
            lat = Double.parseDouble(latInput.getText().toString());
            lng = Double.parseDouble(lngInput.getText().toString());
            latInput.setText("");
            lngInput.setText("");
        }
        loc.setLatitude((double)lat);
        //Log.d("CurrentLocation", loc.getLatitude() + "");
        loc.setLongitude((double)lng);
        //Log.d("OffTrack", loc.getLatitude() + " " + loc.getLongitude());
//        fusedLocationClient.setMockMode(true);
//        Double lat = 32.735851415117665;
//        Double lng = -117.16626781198586;
        fusedLocationClient.setMockLocation(loc);
        fusedLocationClient.getCurrentLocation(100, ct)
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            //LatLng currentLocation = new LatLng((double)location.getLatitude(), (double)location.getLongitude());
                            //Log.d("CurrentLocation", loc.getLatitude() + "");
                            Vertex currentVertex = dir.getCurrentVertex(mCurrentIndex);
                            //Log.d("Current", currentVertex.getLat() + " " + currentVertex.getLng());

                            if (currentVertex.getLat() != loc.getLatitude() && currentVertex.getLng() != loc.getLatitude()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                builder.setPositiveButton("Replan", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //loop through all exhibits selected
                                        int maxIndex = orderedList.size();

                                        for(int i = 0; i < maxIndex; i++){
                                            Vertex currentV = dir.getCurrentVertex(i);
                                            //check parent-child relationship
                                            //is a child exhibit
                                            Double lat;
                                            Double lng;
                                            if (currentV.group_id != null) {
                                                lat = pf.getVertexId(currentV.group_id).lat;
                                                lng = pf.getVertexId(currentV.group_id).lng;
                                            } else {
                                                lat = currentV.getLat();
                                                lng = currentV.getLng();
                                            }
                                            if(lat == loc.getLatitude() && lng == loc.getLongitude()){
                                                //Log.d("New Start",currentV.id);
                                                pf = new Pathfinder(selectedExhibits, getActivity(), currentV.id);
                                                List<GraphPath<String, IdentifiedWeightedEdge>> plan = pf.plan();
                                                dir = new Directions(plan, getActivity());
                                                directions = dir.getDirectionsAllAnimals();
                                                Log.d("Directions new", directions.toString());
                                                //dummy call to populate ordered list
                                                tempMapping = pf.getDistanceMapping(plan);
                                                orderedList = pf.getOrderedList();
                                                remainingExhibits = new ArrayList<String>(orderedList);
                                                Log.d("New Ordered List", remainingExhibits.toString());
                                                mCurrentIndex = 0;
                                                start = currentV.id;
                                                //orderedList = dir.getOrderedList();
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
        builder.setTitle("The Map is replanning")
                .setMessage("Replanning")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    //update question method
    private void updateDirections() {
        String direction = directions.get(mCurrentIndex);

        if(orderedList.size() < 1) {
            animalText.setText("");
            directionText.setText("");
            skipButton.setVisibility(View.GONE);
            nextView.setVisibility(View.GONE);
            previousView.setVisibility(View.GONE);
        } else {
            //Log.d("DISPLAY", "display: " + start);
            animalText.setText("Directions from " + vertexDao.getAnimalName(start) + " to: " + vertexDao.getAnimalName(orderedList.get(mCurrentIndex)));
            directionText.setText(direction);
            if (mCurrentIndex == directions.size() - 1) {
                skipButton.setVisibility(View.GONE);
            }
            if (mCurrentIndex != 0) {
                previousView.setVisibility(View.VISIBLE);
                previousAnimalNameTextView.setText(vertexDao.getAnimalName(orderedList.get(mCurrentIndex - 1)));
                previousAnimalDistanceTextView.setText(dir.nextLabel(mCurrentIndex-1));
            } else {
                previousView.setVisibility(View.GONE);
            }
            if (mCurrentIndex == directions.size()-1) {
                nextView.setVisibility(View.GONE);
            } else {
                nextAnimalNameTextView.setText(vertexDao.getAnimalName(orderedList.get(mCurrentIndex + 1)));
                nextAnimalDistanceTextView.setText(dir.nextLabel(mCurrentIndex));
            }
        }

    }
}