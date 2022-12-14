package com.example.sandiegozooseeker.PathFinder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.VisibleForTesting;

import com.example.sandiegozooseeker.AnimalDB.VertexDao;
import com.example.sandiegozooseeker.AnimalDB.VertexDatabase;
import com.example.sandiegozooseeker.graph.GraphVertex;
import com.example.sandiegozooseeker.graph.IdentifiedWeightedEdge;
import com.example.sandiegozooseeker.graph.Zoo;
import com.example.sandiegozooseeker.graph.ZooGraph;
import com.google.android.gms.maps.model.LatLng;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PathFinder {
    private VertexDao vertexDao;
    private List<GraphVertex> exhibits;
    private List<GraphVertex> remainingExhibits;
    private List<GraphVertex> visitedExhibits;
    private final ZooGraph zooGraph;
    private GraphVertex start;
    private final GraphVertex end;
    List<GraphPath<String, IdentifiedWeightedEdge>> paths;
    private int animalIndex;
    List<String> animalList;
    Context context;

    private Map<String,Integer> tempMapping;
    List<String> orderedNamedList;

    public PathFinder(Context context, GraphVertex start){
        vertexDao = VertexDatabase.getSingleton(context).vertexDao();
        this.zooGraph = Zoo.getZoo(context);
        this.context = context;
        this.exhibits = vertexDao.getSelectedExhibits(GraphVertex.Kind.EXHIBIT);
        this.remainingExhibits = vertexDao.getSelectedExhibits(GraphVertex.Kind.EXHIBIT);
        this.visitedExhibits = new ArrayList<>();
        this.start = start;
        this.end = zooGraph.getVertex("entrance_exit_gate");
        this.paths = createPlan();
        this.animalList = getOrderedList();
        this.tempMapping = getDistanceMapping();
        this.orderedNamedList = getOrderedNamedList();
        animalIndex = 0;
    }

    @VisibleForTesting
    public PathFinder(Context context, GraphVertex start, List<GraphVertex> exhibits){
        this.zooGraph = Zoo.getZoo(context);
        this.context = context;
        this.exhibits = exhibits;
        this.remainingExhibits = exhibits;
        this.visitedExhibits = new ArrayList<GraphVertex>();
        this.start = start;
        this.end = zooGraph.getVertex("entrance_exit_gate");
        this.paths = createPlan();
        this.animalList = getOrderedList();
        this.tempMapping = getDistanceMapping();
        this.orderedNamedList = getOrderedNamedList();
        animalIndex = 0;
    }

    public void replanPath(List<GraphVertex> exhibits, GraphVertex start){
        this.exhibits = exhibits;
        this.start = start;
        this.paths = createPlan();
        this.animalList = getOrderedList();
        this.animalIndex = 0;
        this.tempMapping = getDistanceMapping();
        this.orderedNamedList = getOrderedNamedList();
    }

    public int getAnimalIndex() {
        return animalIndex;
    }

    public List<GraphVertex> getRemainingExhibits(){
        return remainingExhibits;
    }

    public List<GraphVertex> getVisitedExhibits(){
        return visitedExhibits;
    }

    public String nextAnimalName(){
        if(remainingExhibits.size() == 1){
            return zooGraph.getVertex("entrance_exit_gate").getName();
        }

        return remainingExhibits.get(1).getName();
    }

    public String previousAnimalName(){
        if(visitedExhibits.size() == 0){
            return zooGraph.getVertex("entrance_exit_gate").getName();
        }

        return visitedExhibits.get(visitedExhibits.size() - 1).getName();
    }

    public String currentAnimalName(){
        if(remainingExhibits.size() == 0){
            return zooGraph.getVertex("entrance_exit_gate").getName();
        }

        return remainingExhibits.get(0).getName();
    }

    public List<String> getDirection(boolean brief){
        List<String> directions = new ArrayList<>();

        if(animalIndex >= paths.size()){
            return directions;
        }

        GraphPath<String, IdentifiedWeightedEdge> path = paths.get(animalIndex);
        List<String> vertices = path.getVertexList();
        int vertex = 0;

        if (brief) {
            String oldStreet = "";
            String oldSource = "";
            double totalWeight = 0;
            int curr = 0;
            for (IdentifiedWeightedEdge edge : path.getEdgeList()) {
                double weight = zooGraph.getGRAPH().getEdgeWeight(edge);
                totalWeight += weight;
                String street = zooGraph.getEdge(edge.getId()).getStreet();
                String source = zooGraph.getVertex(vertices.get(vertex).toString()).getName();
                vertex++;
                String target = zooGraph.getVertex(vertices.get(vertex).toString()).getName();

                if (oldStreet.equals(street)) {

                    directions.set(curr-1, "Walk " + totalWeight + " meters along " + street + " from " + oldSource + " to " + target + ".\n");
                }
                else {
                    directions.add("Walk " + weight + " meters along " + street + " from " + source + " to " + target + ".\n");
                    oldStreet = street;
                    oldSource = source;
                    totalWeight = weight;
                }

                curr++;
            }
        }
        else {

            for (IdentifiedWeightedEdge edge : path.getEdgeList()) {
                double weight = zooGraph.getGRAPH().getEdgeWeight(edge);
                String street = zooGraph.getEdge(edge.getId()).getStreet();
                String source = zooGraph.getVertex(vertices.get(vertex).toString()).getName();
                vertex++;
                String target = zooGraph.getVertex(vertices.get(vertex).toString()).getName();
                directions.add("Walk " + weight + " meters along " + street + " from " + source + " to " + target + ".\n");
            }
        }

        animalIndex++;

        if(animalIndex > 1){
            visitedExhibits.add(remainingExhibits.remove(0));
        }

        return directions;
    }

    // Directions back to the previous exhibit, give input of current exhibit
    public List<String> getPrevious(boolean brief) {
        List<String> directions = new ArrayList<>();

        if (animalIndex < 1) {
            return directions;
        }

        remainingExhibits.add(0, visitedExhibits.remove(visitedExhibits.size() - 1));
        animalIndex = 1;
        this.paths = createPlan();

        GraphPath<String, IdentifiedWeightedEdge> path = paths.get(animalIndex);
        List<String> vertices = path.getVertexList();
        int vertex = vertices.size() - 1;

        if (brief) {
            String oldStreet = "";
            String oldSource = "";
            double totalWeight = 0;
            int curr = 0;
            for (int i = 0; i < path.getLength(); i++) {
                IdentifiedWeightedEdge edge = path.getEdgeList().get(path.getLength() - 1 - i);
                double weight = zooGraph.getGRAPH().getEdgeWeight(edge);
                totalWeight += weight;
                String street = zooGraph.getEdge(edge.getId()).getStreet();
                String source = zooGraph.getVertex(vertices.get(vertex).toString()).getName();
                vertex--;
                String target = zooGraph.getVertex(vertices.get(vertex).toString()).getName();

                if (oldStreet.equals(street)) {

                    directions.set(curr-1, "Walk " + totalWeight + " meters along " + street + " from " + oldSource + " to " + target + ".\n");
                }
                else {
                    directions.add("Walk " + weight + " meters along " + street + " from " + source + " to " + target + ".\n");
                    oldStreet = street;
                    oldSource = source;
                    totalWeight = weight;
                }

                curr++;
            }
        }
        else {

            for (int i = 0; i < path.getLength(); i++) {
                IdentifiedWeightedEdge edge = path.getEdgeList().get(path.getLength() - 1 - i);
                double weight = zooGraph.getGRAPH().getEdgeWeight(edge);
                String street = zooGraph.getEdge(edge.getId()).getStreet();
                String source = zooGraph.getVertex(vertices.get(vertex).toString()).getName();
                vertex--;
                String target = zooGraph.getVertex(vertices.get(vertex).toString()).getName();
                directions.add((i+1) + ". Walk " + weight + " meters along " + street + " from " + source + " to " + target + ".\n");
            }
        }

        return directions;
    }

    public void skip() {
        animalIndex--;
        exhibits.remove(animalIndex);
        remainingExhibits.remove(0);
        GraphVertex vertexToChange = vertexDao.get(orderedNamedList.get(animalIndex));
        System.out.println(vertexToChange);
        vertexToChange.isSelected = !vertexToChange.isSelected;
        vertexDao.update(vertexToChange);
        orderedNamedList.remove(animalIndex);
        if (visitedExhibits.size() != 0) {
            start = visitedExhibits.get(visitedExhibits.size()-1);
        }
        this.replanPath(remainingExhibits, start);
    }

    public String nextLabel(){
        if (animalIndex >= paths.size()) {
            return "";
        }

        GraphPath<String, IdentifiedWeightedEdge> path = paths.get(animalIndex);
        return path.getWeight() + "m";
    }

    public String previousLabel(){
        if (animalIndex - 2 < 0) {
            return "";
        }

        GraphPath<String, IdentifiedWeightedEdge> path = paths.get(animalIndex - 1);
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
        List<GraphVertex> tempExhibits = new ArrayList<>();
        for (GraphVertex s : remainingExhibits) {
            tempExhibits.add(s);
        }
        //check for parent-child exhibits
        for (int x = 0; x < tempExhibits.size();x++) {
            if (tempExhibits.get(x).group_id != null) {
                //replace with parent vertex
                GraphVertex id = vertexDao.getParentVertex(tempExhibits.get(x).group_id);
                tempExhibits.set(x,id);
            }
        }
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
        List<GraphVertex> tempExhibits = new ArrayList<>();
        for (GraphVertex s : exhibits) {
            tempExhibits.add(s);
        }

        List<String> info = new ArrayList<String>();
        int totalDistance = 0;

        for (GraphPath<String, IdentifiedWeightedEdge> path : this.paths) {
            int distance = (int)path.getWeight();
            String id = zooGraph.getVertex(path.getEndVertex()).getId();
            String name = zooGraph.getVertex(path.getEndVertex()).getName();
            for (GraphVertex s : tempExhibits) {
                if (s.group_id != null && s.group_id.equals(id)) {
                    name = s.name;
                    tempExhibits.remove(s);
                    break;
                }
            }

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
        List<GraphVertex> tempExhibits = new ArrayList<>();
        orderedNamedList = new ArrayList<>();
        for (GraphVertex s : exhibits) {
            tempExhibits.add(s);
        }
        Map<String, Integer> distanceMapping = new HashMap();
        int totalDistance = 0;

        for (GraphPath<String, IdentifiedWeightedEdge> path : this.paths) {
            int distance = (int)path.getWeight();
            String id = zooGraph.getVertex(path.getEndVertex()).getId();
            for (GraphVertex s : tempExhibits) {
                if (s.group_id != null && s.group_id.equals(id)) {
                    id = s.id;
                    tempExhibits.remove(s);
                    break;
                }
            }
            totalDistance += distance;
            distanceMapping.put(id,totalDistance);
            orderedNamedList.add(id);
        }

        return distanceMapping;
    }

    public GraphVertex checkLocation(LatLng location){
        GraphVertex planedVertex;
        GraphVertex newStart = null;

        if(remainingExhibits.size() == 0){
            return null;
        }

        if(animalIndex == 1){
            planedVertex = zooGraph.getVertex(start.getId());
        } else {
            planedVertex = remainingExhibits.get(0);
        }

        if(planedVertex.getLat() != location.longitude && planedVertex.getLng() != location.longitude){
            for(GraphVertex graphVertex : zooGraph.getVERTICES()){
                if(graphVertex.getLat() == location.latitude && graphVertex.getLng() == location.longitude){
                    newStart = graphVertex;
                    break;
                }
            }
        }

        return newStart == null ? null : newStart;
    }

    //return ordered list for naming on labels (parent-child)
    public List<String> getOrderedNamedList() {
        return orderedNamedList;
    }
}
