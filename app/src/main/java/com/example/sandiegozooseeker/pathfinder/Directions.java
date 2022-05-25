package com.example.sandiegozooseeker.pathfinder;

import android.content.Context;
import android.util.Log;

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
    List<String> orderedList;
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
    public String nextLabel(int index){
        if (index +1 >= paths.size()) {
            return "";
        }

        GraphPath<String, IdentifiedWeightedEdge> path = paths.get(index+1);
        return path.getWeight() + "m";
    }


    //helper method to retrieve all the directions for all selected exhibits
    public List<String> getDirectionsAllAnimals() {
        //reset index
        animal = 0;
        List<String> directions = new ArrayList<String>();
        orderedList = new ArrayList<>();
        //orderedList.add("entrance_exit_gate");
        //if the method is called after going through every animal in the plan, an empty list is returned
        for (int x=0; x<paths.size(); x++) {
            String s = "";
            GraphPath<String, IdentifiedWeightedEdge> path = paths.get(animal);
            //Log.d("GET_END_VERTEX", "getDirectionsAllAnimals: " + path.getEndVertex());
            orderedList.add(path.getEndVertex());
            int i = 1;
            List<String> vertices = path.getVertexList();
            int vertex = 0;
            for (IdentifiedWeightedEdge e : path.getEdgeList()) {
                double weight = g.getEdgeWeight(e);
                String street = eInfo.get(e.getId()).street;
                String source = vInfo.get(vertices.get(vertex).toString()).name;
                vertex++;
                String target = vInfo.get(vertices.get(vertex).toString()).name;
                s += i + ". Walk " + weight + " meters along " + street + " from " + source + " to " + target + ".\n";
                i++;
            }
            directions.add(s);
            animal++;
        }
        //System.out.println(directions);
        return directions;
    }

    //return ordered list
    public List<String> getOrderedList() {
        return orderedList;
    }

    //Directions back to the previous exhibit, give input of current exhibit
    public String getPrevious(int currentAnimal) {
        String previousDirections = "";

        if (currentAnimal < 1) {
            return previousDirections;
        }

        GraphPath<String, IdentifiedWeightedEdge> path = paths.get(currentAnimal -1);
        List<String> vertices = path.getVertexList();
        int vertex = vertices.size() -1;
        for (int i = 0; i < path.getLength(); i++) {
            IdentifiedWeightedEdge e = path.getEdgeList().get(path.getLength() -1 - i);
            double weight = g.getEdgeWeight(e);
            String street = eInfo.get(e.getId()).street;
            String source = vInfo.get(vertices.get(vertex).toString()).name;
            vertex--;
            String target = vInfo.get(vertices.get(vertex).toString()).name;
            previousDirections += (i+1) + ". Walk " + weight + " meters along " + street + " from " + source + " to " + target + ".\n";
        }

        return previousDirections;
    }
}