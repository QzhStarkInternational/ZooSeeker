package com.example.sandiegozooseeker.locations;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Coords {
    public static final LatLng ENTRANCE_EXIT = new LatLng(32.73561, -117.14936);
    public static final LatLng KOI = new LatLng(32.72109826903826, -117.15952052282296);
    public static final LatLng FLAMINGO = new LatLng(32.7440416465169, -117.15952052282296);
    public static final LatLng CAPUCHIN = new LatLng(32.750121730961745, -117.16626781198586);
    public static final LatLng GORILLA = new LatLng(32.74711745394194, -117.18047982358976);

    public static final LatLng HIPPO = new LatLng(32.74531131120979, -117.16626781198586);

    private static int currentIndex;

    private final List<LatLng> path = new ArrayList<>();

    public Coords(){
        path.add(KOI);
        path.add(FLAMINGO);
        path.add(CAPUCHIN);
        path.add(GORILLA);
        path.add(ENTRANCE_EXIT);
        path.add(HIPPO);

        currentIndex = 0;
    }

    public void nextLoc(){ currentIndex++; }

    public void previousLoc(){
        currentIndex--;
    }


    public LatLng getCurrentLocation(){
        return this.path.get(currentIndex);
    }
}