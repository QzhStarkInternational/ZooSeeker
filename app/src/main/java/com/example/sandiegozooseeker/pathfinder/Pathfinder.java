package com.example.sandiegozooseeker.pathfinder;

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

    public Pathfinder(List<String> list) {
        exhibits = list;
        start = "entrance_exit_gate";
        end = start;

        g = ZooData.loadZooGraphJSON("sample_zoo_graph.json");
        vInfo = ZooData.loadVertexInfoJSON("sample_node_info.json");
        eInfo = ZooData.loadEdgeInfoJSON("sample_edge_info.json");

    }

    public List<GraphPath<String, IdentifiedWeightedEdge>> plan(){
        List<String> tempExhibits = exhibits;
        List<GraphPath<String, IdentifiedWeightedEdge>> paths = new ArrayList<GraphPath<String, IdentifiedWeightedEdge>>();

        GraphPath<String, IdentifiedWeightedEdge> minPath = null;
        int minDistance = Integer.MAX_VALUE;

        while (!(tempExhibits.isEmpty())) {

            for (String exhibit : tempExhibits) {
                GraphPath<String, IdentifiedWeightedEdge> tempPath = DijkstraShortestPath.findPathBetween(g, start, exhibit);
                if (pathDistance(tempPath) < minDistance) {
                    minPath = tempPath;
                    minDistance = pathDistance(minPath);
                }
            }
            paths.add(minPath);
            start = minPath.getEndVertex();
            tempExhibits.remove(start);
        }

        GraphPath<String, IdentifiedWeightedEdge> exitPath = DijkstraShortestPath.findPathBetween(g, start, end);
        paths.add(exitPath);

        return paths;
    }

    public int pathDistance(GraphPath<String, IdentifiedWeightedEdge> path) {
        int distance = 0;
        for (IdentifiedWeightedEdge e : path.getEdgeList()) {
            distance += g.getEdgeWeight(e);
        }

        return distance;
    }

    public List<String> pathsToStringList(List<GraphPath<String, IdentifiedWeightedEdge>> paths) {

        List<String> info = new ArrayList<String>();
        int totalDistance = 0;

        for (GraphPath<String, IdentifiedWeightedEdge> path : paths) {
            int distance = pathDistance(path);
            String name = vInfo.get(path.getEndVertex().toString()).name;
            totalDistance += distance;
            info.add(name + " " + totalDistance + "m");
        }

        return info;
    }

    // for testing do Log.d("name", "print this"); instead of system.out
}

