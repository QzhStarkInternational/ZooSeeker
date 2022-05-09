package com.example.sandiegozooseeker.pathfinder;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

public class Pathfinder {

    List<String> exhibits;
    String start;
    String end;
    Graph<String, IdentifiedWeightedEdge> g;
    Map<String, ZooData.VertexInfo> vInfo;
    Map<String, ZooData.EdgeInfo> eInfo;

    public Pathfinder(List<String> list, Context context) {
        exhibits = list;
        start = "entrance_exit_gate";
        end = start;

        g = ZooData.loadZooGraphJSON(context, "sample_zoo_graph.json");
        vInfo = ZooData.loadVertexInfoJSON(context, "sample_node_info.json");
        eInfo = ZooData.loadEdgeInfoJSON(context, "sample_edge_info.json");

    }

    public List<GraphPath<String, IdentifiedWeightedEdge>> plan(){
        List<String> tempExhibits = exhibits;
        List<GraphPath<String, IdentifiedWeightedEdge>> paths = new ArrayList<GraphPath<String, IdentifiedWeightedEdge>>();

        GraphPath<String, IdentifiedWeightedEdge> minPath = null;
        int minDistance = Integer.MAX_VALUE;

        while (!(tempExhibits.isEmpty())) {

            for (String exhibit : tempExhibits) {
                GraphPath<String, IdentifiedWeightedEdge> tempPath = DijkstraShortestPath.findPathBetween(g, start, exhibit);
                if ((int)tempPath.getWeight() < minDistance) {
                    minPath = tempPath;
                    minDistance = (int)minPath.getWeight();
                }
            }
            paths.add(minPath);
            start = minPath.getEndVertex();
            tempExhibits.remove(start);
            minPath = null;
            minDistance = Integer.MAX_VALUE;
        }

        GraphPath<String, IdentifiedWeightedEdge> exitPath = DijkstraShortestPath.findPathBetween(g, start, end);
        paths.add(exitPath);

        return paths;
    }

    public List<String> pathsToStringList(List<GraphPath<String, IdentifiedWeightedEdge>> paths) {

        List<String> info = new ArrayList<String>();
        int totalDistance = 0;

        for (GraphPath<String, IdentifiedWeightedEdge> path : paths) {
            int distance = (int)path.getWeight();
            String name = vInfo.get(path.getEndVertex().toString()).name;
            totalDistance += distance;
            info.add(name + " " + totalDistance + "m");
        }

        return info;
    }


    // Update
    public GraphPath<String, IdentifiedWeightedEdge> getExactGraph(
            List<GraphPath<String, IdentifiedWeightedEdge>> paths, String animal) {

        GraphPath<String, IdentifiedWeightedEdge> match = null;
        for (GraphPath<String, IdentifiedWeightedEdge> path : paths) {
            if (vInfo.get(path.getEndVertex().toString()).name.equals(animal)) {
                match = path;
            }
        }
        return match;
    }

    // for testing do Log.d("name", "print this"); instead of system.out
}

