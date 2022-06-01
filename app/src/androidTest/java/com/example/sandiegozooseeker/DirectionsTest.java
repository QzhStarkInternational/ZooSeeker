package com.example.sandiegozooseeker;

import static org.junit.Assert.assertEquals;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.sandiegozooseeker.PathFinder.PathFinder;
import com.example.sandiegozooseeker.graph.GraphVertex;
import com.example.sandiegozooseeker.graph.Zoo;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class DirectionsTest {

    @Test
    public void exampleDirections() {
//        List<String> exhibits = new ArrayList<String>();
//        exhibits.add("gorillas");
//        exhibits.add("gators");
//        exhibits.add("elephant_odyssey");
//        pathfinderold pf = new pathfinderold(exhibits, ApplicationProvider.getApplicationContext(), "entrance_exit_gate");
//        List<GraphPath<String, IdentifiedWeightedEdge>> paths = pf.plan();
//        Directions dir = new Directions(paths, ApplicationProvider.getApplicationContext());
//        List<String> correctDirections = new ArrayList<String>();
//        correctDirections
//                .add("1. Walk 10.0 meters along Entrance Way from Entrance and Exit Gate to Entrance Plaza.\n");
//        correctDirections.add("2. Walk 100.0 meters along Reptile Road from Entrance Plaza to Alligators.\n");
//        assertEquals(correctDirections, dir.getDirectionsOneAnimal());
//        assertEquals("300.0m", dir.nextLabel(0));
//        correctDirections.clear();
//        correctDirections.add("1. Walk 100.0 meters along Reptile Road from Alligators to Entrance Plaza.\n");
//        correctDirections.add("2. Walk 200.0 meters along Africa Rocks Street from Entrance Plaza to Gorillas.\n");
//        assertEquals(correctDirections, dir.getDirectionsOneAnimal());
//        dir.getDirectionsOneAnimal();
//        List<String> directions = dir.getDirectionsOneAnimal();
//        assertEquals("4. Walk 10.0 meters along Entrance Way from Entrance Plaza to Entrance and Exit Gate.\n",
//                directions.get(directions.size() - 1));
//        assertEquals(new ArrayList<String>(), dir.getDirectionsOneAnimal());
//        assertEquals("" , dir.getPrevious(0));
//        assertEquals("1. Walk 100.0 meters along Reptile Road from Alligators to Entrance Plaza.\n" +
//                        "2. Walk 10.0 meters along Entrance Way from Entrance Plaza to Entrance and Exit Gate.\n"
//                , dir.getPrevious(1));

        String directionsBrief = "";
        String directionsDetailed = "";
        List<GraphVertex> exhibits = new ArrayList<GraphVertex>();
        GraphVertex flamingos = Zoo.getZoo(ApplicationProvider.getApplicationContext()).getVertex("flamingo");
        GraphVertex koi = Zoo.getZoo(ApplicationProvider.getApplicationContext()).getVertex("koi");
        exhibits.add(koi);
        exhibits.add(flamingos);
        PathFinder pf = new PathFinder(ApplicationProvider.getApplicationContext(), Zoo.getZoo(ApplicationProvider.getApplicationContext()).getVertex("entrance_exit_gate"), exhibits);
        for (String s : pf.getDirection(false)) {
            directionsDetailed += s;
        }
        for (String s : pf.getDirection(true)) {
            directionsBrief += s;
        }
        assertEquals("Walk 10.0 meters along Gate Path from Entrance and Exit Gate to Front Street / Treetops Way.\n" +
                "Walk 30.0 meters along Front Street from Front Street / Treetops Way to Front Street / Terrace Lagoon Loop (South).\n" +
                "Walk 20.0 meters along Terrace Lagoon Loop from Front Street / Terrace Lagoon Loop (South) to Koi Fish.\n", directionsDetailed);
        assertEquals("Walk 20.0 meters along Terrace Lagoon Loop from Koi Fish to Front Street / Terrace Lagoon Loop (South).\n" +
                "Walk 80.0 meters along Front Street from Front Street / Terrace Lagoon Loop (South) to Front Street / Monkey Trail.\n" +
                "Walk 30.0 meters along Monkey Trail from Front Street / Monkey Trail to Flamingos.\n", directionsBrief);
    }
}
