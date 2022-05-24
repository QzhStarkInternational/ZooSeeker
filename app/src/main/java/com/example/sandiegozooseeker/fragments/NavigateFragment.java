package com.example.sandiegozooseeker.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.jgrapht.GraphPath;

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
    //private TextView locView;
    Directions dir;

    public NavigateFragment(){
        super(R.layout.fragment_navigate);
    }

    //keep track of which animal exhibit direction to display
    private int mCurrentIndex = 0;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        vertexDao = VertexDatabase.getSingleton(getActivity()).vertexDao();
        List<String> selectedExhibits = vertexDao.getSelectedExhibitsID(Vertex.Kind.EXHIBIT);
        //System.out.println(selectedExhibits);
        Pathfinder pf = new Pathfinder(selectedExhibits, getActivity());

        List<GraphPath<String, IdentifiedWeightedEdge>> plan = pf.plan();
        List<String> orderedPaths = pf.pathsToStringList(plan);
        // System.out.println(orderedPaths);

        directionText = (TextView)view.findViewById(R.id.direction_text);
        animalText = (TextView) view.findViewById(R.id.animal_name);
        nextView = view.findViewById(R.id.nextView);
        nextAnimalNameTextView = view.findViewById(R.id.textView2);
        nextAnimalDistanceTextView = view.findViewById(R.id.textView);

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
            }
        });



        LoadLocation t = new LoadLocation(getActivity());






        ///////
//        locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(5000);
//        locationRequest.setFastestInterval(2000);






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
            if (mCurrentIndex == directions.size() - 1) {
                nextView.setVisibility(View.GONE);
            } else {
                nextAnimalNameTextView.setText(vertexDao.getAnimalName(orderedList.get(mCurrentIndex + 1)));
                nextAnimalDistanceTextView.setText(dir.nextLabel(mCurrentIndex));
            }
        }

    }



    //
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == 1){
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
//
//                if (isGPSEnabled()) {
//
//                    getCurrentLocation();
//
//                }else {
//                    turnOnGPS();
//                }
//            }
//        }
//        else {
//            Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
//        }
//
//
//    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 2) {
//            if (resultCode == Activity.RESULT_OK) {
//
//                getCurrentLocation();
//            }
//        }
//    }
//
//    private void getCurrentLocation() {
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            double fakeLat = 32.7354;
//            double fakelong = 0;
//            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//
//                if (isGPSEnabled()) {
//
//                    LocationServices.getFusedLocationProviderClient(getActivity())
//                        .requestLocationUpdates(locationRequest, new LocationCallback() {
//                            @Override
//                            public void onLocationResult(@NonNull LocationResult locationResult) {
//                                super.onLocationResult(locationResult);
//
//                                LocationServices.getFusedLocationProviderClient(getActivity())
//                                    .removeLocationUpdates(this);
//
//                                if (locationResult != null && locationResult.getLocations().size() >0){
//
//                                    int index = locationResult.getLocations().size() - 1;
//                                    double latitude = locationResult.getLocations().get(index).getLatitude();
//                                    double longitude = locationResult.getLocations().get(index).getLongitude();
//
//                                    String text = "Latitude: "+ latitude + "\n" + "Longitude: "+ longitude;
//
//                                    locView.setText(text);
//
//                                   // accessLoc.setText("Latitude: "+ latitude + "\n" + "Longitude: "+ longitude);
//
//                                }
//                            }
//                        }, Looper.getMainLooper());
//
//                } else {
//                    turnOnGPS();
//                }
//
//            } else {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            }
//        }
//
//
//
//
//    }
//
//
//    private void turnOnGPS() {
//
//
//
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//            .addLocationRequest(locationRequest);
//        builder.setAlwaysShow(true);
//
//        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getActivity())
//            .checkLocationSettings(builder.build());
//
//        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
//            @Override
//            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
//
//                try {
//                    LocationSettingsResponse response = task.getResult(ApiException.class);
//                    Toast.makeText(getActivity(), "GPS is already tured on", Toast.LENGTH_SHORT).show();
//
//                } catch (ApiException e) {
//
//                    switch (e.getStatusCode()) {
//                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//
//                            try {
//                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
//                                resolvableApiException.startResolutionForResult(getActivity(), 2);
//                            } catch (IntentSender.SendIntentException ex) {
//                                ex.printStackTrace();
//                            }
//                            break;
//
//                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                            //Device does not have location
//                            break;
//                    }
//                }
//            }
//        });
//
//    }
//
//    private boolean isGPSEnabled() {
//        LocationManager locationManager = null;
//        boolean isEnabled = false;
//
//        if (locationManager == null) {
//            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//        }
//
//        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        return isEnabled;
//
//    }


    //
}
