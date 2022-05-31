package com.example.sandiegozooseeker.PathFinder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.example.sandiegozooseeker.AnimalDB.VertexDao;
import com.example.sandiegozooseeker.AnimalDB.VertexDatabase;
import com.example.sandiegozooseeker.graph.GraphVertex;
import com.example.sandiegozooseeker.graph.IdentifiedWeightedEdge;
import com.example.sandiegozooseeker.graph.Zoo;
import com.example.sandiegozooseeker.graph.ZooGraph;
import com.google.android.gms.maps.model.LatLng;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PathFinder {
    private List<GraphVertex> exhibits;
    private List<GraphVertex> remainingExhibits;
    private final ZooGraph zooGraph;
    private GraphVertex start;
    private final GraphVertex end;
    List<GraphPath<String, IdentifiedWeightedEdge>> paths;
    private int animalIndex;
    List<String> animalList;
    Context context;

    public PathFinder(Context context, GraphVertex start){
        VertexDao vertexDao = VertexDatabase.getSingleton(context).vertexDao();
        this.zooGraph = Zoo.getZoo(context);
        this.context = context;
        this.exhibits = vertexDao.getSelectedExhibits(GraphVertex.Kind.EXHIBIT);
        this.remainingExhibits = vertexDao.getSelectedExhibits(GraphVertex.Kind.EXHIBIT);
        this.start = start;
        this.end = zooGraph.getVertex("entrance_exit_gate");
        this.paths = createPlan();
        this.animalList = getOrderedList();
        animalIndex = 0;
    }

    public void replanPath(List<GraphVertex> exhibits, GraphVertex start){
        this.exhibits = exhibits;
        this.start = start;
        this.paths = createPlan();
        this.animalList = getOrderedList();
        this.animalIndex = 0;
    }

    public List<GraphVertex> getRemainingExhibits(){
        return remainingExhibits;
    }

    public String nextAnimalName(){
        if(animalIndex >= animalList.size()){
            return "NULL";
        }

        return zooGraph.getVertex(animalList.get(animalIndex)).getName();
    }

    public String currentAnimalName(){
        if(animalIndex >= animalList.size()){
            return "NULL";
        }

        return zooGraph.getVertex(animalList.get(animalIndex - 1)).getName();
    }

    public List<String> getDirection(){
        List<String> directions = new ArrayList<>();

        if(animalIndex >= paths.size()){
            return directions;
        }

        GraphPath<String, IdentifiedWeightedEdge> path = paths.get(animalIndex);
        List<String> vertices = path.getVertexList();
        int vertex = 0;

        for (IdentifiedWeightedEdge edge : path.getEdgeList()) {
            double weight = zooGraph.getGRAPH().getEdgeWeight(edge);
            String street = zooGraph.getEdge(edge.getId()).getStreet();
            String source = zooGraph.getVertex(vertices.get(vertex).toString()).getName();
            vertex++;
            String target = zooGraph.getVertex(vertices.get(vertex).toString()).getName();
            directions.add("Walk " + weight + " meters along " + street + " from " + source + " to " + target + ".\n");
        }

        animalIndex++;

        if(animalIndex > 1){
            int animalToRemove = 0;

            for(int i = 0; i < exhibits.size(); i++){
                if(Objects.equals(remainingExhibits.get(i).getId(), animalList.get(animalIndex - 2))){
                    animalToRemove = i;
                    break;
                }
            }

            remainingExhibits.remove(animalToRemove);
        }



        return directions;
    }

    // Directions back to the previous exhibit, give input of current exhibit
    public List<String> getPrevious() {
        List<String> directions = new ArrayList<>();

        if (animalIndex < 1) {
            return directions;
        }

        GraphPath<String, IdentifiedWeightedEdge> path = paths.get(animalIndex - 1);
        List<String> vertices = path.getVertexList();
        int vertex = vertices.size() - 1;
        for (int i = 0; i < path.getLength(); i++) {
            IdentifiedWeightedEdge edge = path.getEdgeList().get(path.getLength() - 1 - i);
            double weight = zooGraph.getGRAPH().getEdgeWeight(edge);
            String street = zooGraph.getEdge(edge.getId()).getStreet();
            String source = zooGraph.getVertex(vertices.get(vertex).toString()).getName();
            vertex--;
            String target = zooGraph.getVertex(vertices.get(vertex).toString()).getName();
            directions.add((i+1) + ". Walk " + weight + " meters along " + street + " from " + source + " to " + target + ".\n");
        }

        animalIndex--;
        return directions;
    }

    public String nextLabel(){
        if (animalIndex + 1 >= paths.size()) {
            return "";
        }

        GraphPath<String, IdentifiedWeightedEdge> path = paths.get(animalIndex + 1);
        return path.getWeight() + "m";
    }

    private List<String> getOrderedList(){
        List<String> orderedList = new ArrayList<>();

        for (int x = 0; x < paths.size(); x++) {
            GraphPath<String, IdentifiedWeightedEdge> path = paths.get(x);
            orderedList.add(path.getEndVertex());
        }

        return orderedList;
    }

    private List<GraphPath<String, IdentifiedWeightedEdge>> createPlan(){
        List<GraphVertex> tempExhibits = exhibits;
        List<GraphPath<String, IdentifiedWeightedEdge>> paths = new ArrayList<>();

        GraphPath<String, IdentifiedWeightedEdge> minPath = null;
        int minDistance = Integer.MAX_VALUE;

        GraphVertex startVertex = this.start;

        while (!(tempExhibits.isEmpty())) {

            for (GraphVertex exhibit : tempExhibits) {
                GraphPath<String, IdentifiedWeightedEdge> tempPath = DijkstraShortestPath.findPathBetween(zooGraph.getGRAPH(), startVertex.getId(), exhibit.getId());
                if ((int)tempPath.getWeight() < minDistance) {
                    minPath = tempPath;
                    minDistance = (int) minPath.getWeight();
                }
            }
            paths.add(minPath);
            assert minPath != null;
            startVertex = zooGraph.getVertex(minPath.getEndVertex());
            int indexToRemove = 0;

            for(int i = 0; i < tempExhibits.size(); i++) {
                if(startVertex.getId().equals(tempExhibits.get(i).getId())){
                    indexToRemove = i;
                    break;
                }
            }

            tempExhibits.remove(indexToRemove);
            minPath = null;
            minDistance = Integer.MAX_VALUE;
        }

        GraphPath<String, IdentifiedWeightedEdge> exitPath = DijkstraShortestPath.findPathBetween(zooGraph.getGRAPH(), startVertex.getId(), end.getId());
        paths.add(exitPath);

        return paths;
    }

    public List<String> pathsToStringList() {

        List<String> info = new ArrayList<String>();
        int totalDistance = 0;

        for (GraphPath<String, IdentifiedWeightedEdge> path : this.paths) {
            int distance = (int)path.getWeight();
            String name = zooGraph.getVertex(path.getEndVertex()).getName();
            totalDistance += distance;
            info.add(name + " " + totalDistance + "m");
        }

        return info;
    }

    public GraphPath<String, IdentifiedWeightedEdge> getExactGraph(String animal) {

        GraphPath<String, IdentifiedWeightedEdge> match = null;
        for (GraphPath<String, IdentifiedWeightedEdge> path : paths) {
            if (zooGraph.getVertex(path.getEndVertex()).getName().equals(animal)) {
                match = path;
            }
        }
        return match;
    }

    public Map<String,Integer> getDistanceMapping() {
        Map<String, Integer> distanceMapping = new HashMap();
        int totalDistance = 0;

        for (GraphPath<String, IdentifiedWeightedEdge> path : this.paths) {
            int distance = (int)path.getWeight();
            String id = zooGraph.getVertex(path.getEndVertex()).getId();
            totalDistance += distance;
            distanceMapping.put(id,totalDistance);
        }

        return distanceMapping;
    }

    public GraphVertex checkLocation(LatLng location){
        GraphVertex planedVertex;
        GraphVertex newStart = null;
        if(animalIndex == 1){
            planedVertex = zooGraph.getVertex(start.getId());
        } else {
            planedVertex = zooGraph.getVertex(animalList.get(animalIndex - 2));
        }

        if(planedVertex.getLat() != location.longitude && planedVertex.getLng() != location.longitude){
            for(GraphVertex graphVertex : zooGraph.getVERTICES()){
                if(graphVertex.getLat() == location.longitude && graphVertex.getLng() == location.longitude){
                    newStart = graphVertex;
                    break;
                }
            }
        }

        return newStart == null ? planedVertex : newStart;
    }
}
