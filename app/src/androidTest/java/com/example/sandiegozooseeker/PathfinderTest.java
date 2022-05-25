package com.example.sandiegozooseeker;

import org.jgrapht.GraphPath;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.sandiegozooseeker.pathfinder.IdentifiedWeightedEdge;
import com.example.sandiegozooseeker.pathfinder.Pathfinder;

import java.util.ArrayList;
import java.util.List;

public class PathfinderTest {

    @Test
    public void examplePath() {

        List<String> exhibits = new ArrayList<String>();
        exhibits.add("gorillas");
        exhibits.add("gators");
        exhibits.add("elephant_odyssey");
        Pathfinder pf = new Pathfinder(exhibits, ApplicationProvider.getApplicationContext(), "entrance_exit_gate");
        List<GraphPath<String, IdentifiedWeightedEdge>> paths = pf.plan();
        List<String> pathsToString = pf.pathsToStringList(paths);
        List<String> correctPaths = new ArrayList<String>();
        correctPaths.add("Alligators 110m");
        correctPaths.add("Gorillas 410m");
        correctPaths.add("Elephant Odyssey 810m");
        correctPaths.add("Entrance and Exit Gate 1320m");
        assertEquals(correctPaths, pathsToString);

    }
}
