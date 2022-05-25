package com.example.sandiegozooseeker;

import androidx.test.core.app.ApplicationProvider;

import com.example.sandiegozooseeker.pathfinder.Directions;
import com.example.sandiegozooseeker.pathfinder.IdentifiedWeightedEdge;
import com.example.sandiegozooseeker.pathfinder.Pathfinder;

import org.jgrapht.GraphPath;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class DirectionsTest {

    @Test
    public void exampleDirections() {
        List<String> exhibits = new ArrayList<String>();
        exhibits.add("gorillas");
        exhibits.add("gators");
        exhibits.add("elephant_odyssey");
        Pathfinder pf = new Pathfinder(exhibits, ApplicationProvider.getApplicationContext(), "entrance_exit_gate");
        List<GraphPath<String, IdentifiedWeightedEdge>> paths = pf.plan();
        Directions dir = new Directions(paths, ApplicationProvider.getApplicationContext());
        List<String> correctDirections = new ArrayList<String>();
        correctDirections
                .add("1. Walk 10.0 meters along Entrance Way from Entrance and Exit Gate to Entrance Plaza.\n");
        correctDirections.add("2. Walk 100.0 meters along Reptile Road from Entrance Plaza to Alligators.\n");
        assertEquals(correctDirections, dir.getDirectionsOneAnimal());
        assertEquals("300.0m", dir.nextLabel(0));
        correctDirections.clear();
        correctDirections.add("1. Walk 100.0 meters along Reptile Road from Alligators to Entrance Plaza.\n");
        correctDirections.add("2. Walk 200.0 meters along Africa Rocks Street from Entrance Plaza to Gorillas.\n");
        assertEquals(correctDirections, dir.getDirectionsOneAnimal());
        dir.getDirectionsOneAnimal();
        List<String> directions = dir.getDirectionsOneAnimal();
        assertEquals("4. Walk 10.0 meters along Entrance Way from Entrance Plaza to Entrance and Exit Gate.\n",
                directions.get(directions.size() - 1));
        assertEquals(new ArrayList<String>(), dir.getDirectionsOneAnimal());
        assertEquals("" , dir.getPrevious(0));
        assertEquals("1. Walk 100.0 meters along Reptile Road from Alligators to Entrance Plaza.\n" +
                        "2. Walk 10.0 meters along Entrance Way from Entrance Plaza to Entrance and Exit Gate.\n"
                , dir.getPrevious(1));
    }
}
