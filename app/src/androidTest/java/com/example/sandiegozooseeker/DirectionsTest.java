package com.example.sandiegozooseeker;

import static org.junit.Assert.assertEquals;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.sandiegozooseeker.PathFinder.PathFinder;
import com.example.sandiegozooseeker.PathFinder.PathFinderNew;
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
        StringBuilder directionsBrief = new StringBuilder();
        StringBuilder directionsDetailed = new StringBuilder();
        List<GraphVertex> exhibits = new ArrayList<GraphVertex>();
        GraphVertex flamingos = Zoo.getZoo(ApplicationProvider.getApplicationContext()).getVertex("flamingo");
        GraphVertex koi = Zoo.getZoo(ApplicationProvider.getApplicationContext()).getVertex("koi");
        exhibits.add(koi);
        exhibits.add(flamingos);
        PathFinderNew pf = new PathFinderNew(ApplicationProvider.getApplicationContext(), Zoo.getZoo(ApplicationProvider.getApplicationContext()).getVertex("entrance_exit_gate"), exhibits);
        for (String s : pf.getNextDirection()) {
            directionsDetailed.append(s);
        }
        for (String s : pf.getNextDirection()) {
            directionsBrief.append(s);
        }
        assertEquals("Walk 10.0 meters along Gate Path from Entrance and Exit Gate to Front Street / Treetops Way.\n" +
                "Walk 30.0 meters along Front Street from Front Street / Treetops Way to Front Street / Terrace Lagoon Loop (South).\n" +
                "Walk 20.0 meters along Terrace Lagoon Loop from Front Street / Terrace Lagoon Loop (South) to Koi Fish.\n", directionsDetailed.toString());
        assertEquals("Walk 20.0 meters along Terrace Lagoon Loop from Koi Fish to Front Street / Terrace Lagoon Loop (South).\n" +
                "Walk 80.0 meters along Front Street from Front Street / Terrace Lagoon Loop (South) to Front Street / Monkey Trail.\n" +
                "Walk 30.0 meters along Monkey Trail from Front Street / Monkey Trail to Flamingos.\n", directionsBrief.toString());
    }
}
