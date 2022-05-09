package com.example.sandiegozooseeker;

import android.content.Context;

import com.example.sandiegozooseeker.pathfinder.IdentifiedWeightedEdge;
import com.example.sandiegozooseeker.pathfinder.ZooData;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Directions {

    List<GraphPath<String, IdentifiedWeightedEdge>> paths;
    //private global variable to keep track of which animal path is next for directions
    private int animal;
    Graph<String, IdentifiedWeightedEdge> g;
    Map<String, ZooData.VertexInfo> vInfo;
    Map<String, ZooData.EdgeInfo> eInfo;

    //constructor needs the List of GraphPaths created from PathFinder's plan() method
    public Directions(List<GraphPath<String, IdentifiedWeightedEdge>> p, Context context) {
        paths = p;
        animal = 0;
        g = ZooData.loadZooGraphJSON(context,"sample_zoo_graph.json");
        vInfo = ZooData.loadVertexInfoJSON(context, "sample_node_info.json");
        eInfo = ZooData.loadEdgeInfoJSON(context, "sample_edge_info.json");
    }

    //gives a list of Strings that are step-by-step directions to go to the next animal in the path
    //this should be called every time the "Next" button is pressed and when the "Directions" button is pressed for the first time
    public List<String> getDirectionsOneAnimal() {
        List<String> directions = new ArrayList<String>();
        //if the method is called after going through every animal in the plan, an empty list is returned
        if (animal >= paths.size()) {
            return directions;
        }

        GraphPath<String, IdentifiedWeightedEdge> path = paths.get(animal);

        int i = 1;
        List<String> vertices = path.getVertexList();
        int vertex = 0;
        for (IdentifiedWeightedEdge e : path.getEdgeList()) {
            double weight = g.getEdgeWeight(e);
            String street = eInfo.get(e.getId()).street;
            String source = vInfo.get(vertices.get(vertex).toString()).name;
            vertex++;
            String target = vInfo.get(vertices.get(vertex).toString()).name;
            directions.add(i + ". Walk " + weight + " meters along " + street + " from " + source + " to " + target + ".\n");
            i++;
        }

        animal++;
        return directions;
    }

    //get text for the next button
    public String nextLabel(){
        GraphPath<String, IdentifiedWeightedEdge> path = paths.get(animal);
        return vInfo.get(path.getEndVertex().toString()).name + ", " + path.getWeight() + "m";
    }
}
