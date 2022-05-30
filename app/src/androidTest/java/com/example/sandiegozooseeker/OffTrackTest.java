package com.example.sandiegozooseeker;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.contrib.RecyclerViewActions;

import com.example.sandiegozooseeker.AnimalDB.DataConverter;
import com.example.sandiegozooseeker.AnimalDB.Vertex;
import com.example.sandiegozooseeker.AnimalDB.VertexDao;
import com.example.sandiegozooseeker.AnimalDB.VertexDatabase;
import com.example.sandiegozooseeker.pathfinder.Directions;
import com.example.sandiegozooseeker.pathfinder.IdentifiedWeightedEdge;
import com.example.sandiegozooseeker.pathfinder.Pathfinder;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.location.FusedLocationProviderClient;

import org.jgrapht.GraphPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import android.location.LocationManager;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class OffTrackTest {

    // Define a LocationClient object
    private FusedLocationProviderClient fusedLocationClient;
    public LocationManager locationManager;

    VertexDatabase vertexDb;
    VertexDao vertexDao;


    private static final String PROVIDER = "flp";
//    private static final double LAT = 37.377166;
//    private static final double LNG = -122.086966;
    private double Lat;
    private double Lng;
    private static final float ACCURACY = 3.0f;

    private void enableMock() {
        this.fusedLocationClient.setMockMode(true);
    }


    private Location setMock(double lat, double lng /* , float accuracy */) {
        enableMock();
        Location mock1 = new Location(PROVIDER);
        mock1.setLatitude(lat);
        mock1.setLongitude(lng);
        //mock1.setAccuracy(accuracy);
        return mock1;
    }

    @After
    public void setLoc() {
        this.Lat = 0;
        this.Lng = 0;
    }

    private static void forceLayout(RecyclerView recyclerView) {
        recyclerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        recyclerView.layout(0,0,1080,2288);
    }

    @Before
    public void resetDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        vertexDb = Room.inMemoryDatabaseBuilder(context, VertexDatabase.class)
            .allowMainThreadQueries()
            .addTypeConverter(new DataConverter())
            .build();
        VertexDatabase.injectTestDatabase(vertexDb);
        List<Vertex> vertices = Vertex.loadJSON(context, "zoo_node_info.json");
        vertexDao = vertexDb.vertexDao();
        vertexDao.insertAll(vertices);
    }

    /*

    Gorillas:
    "lat": 32.74812588554637,
    "lng": -117.17565073656901

    "lat": 32.73561,
    "lng": -117.14936


     */


    @Test
    public void testCurrentLocation() {
        // Connect to Location Services
        this.Lat = 32.73561;
        this.Lng = -117.14936;

        List<String> exhibits = new ArrayList<String>();
        exhibits.add("gorillas");

        Pathfinder pf = new Pathfinder(exhibits, ApplicationProvider.getApplicationContext(), "entrance_exit_gate");
        List<GraphPath<String, IdentifiedWeightedEdge>> paths = pf.plan();

        Location myLocation = setMock(this.Lat, this.Lng);

        ActivityScenario<MainActivity> scenario
            = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            activity.getSupportFragmentManager().beginTransaction();
        });

        onView(withId(R.id.vertex_items_search)).perform(RecyclerViewActions.actionOnItemAtPosition(3, click()));

        Vertex listofNode = vertexDao.get("entrance_exit_gate");

        assertEquals(listofNode.getLat(), this.Lat, .2f);
        assertEquals(listofNode.getLng(), this.Lng, .2f);
        Vertex animal = vertexDao.get("gorillas");

        assertNotEquals(animal.getLat(), this.Lat, .2f);
        assertNotEquals(animal.getLng(), this.Lng, .2f);



    }
}
