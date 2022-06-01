package com.example.sandiegozooseeker.PathFinder;

import android.content.Context;

import androidx.annotation.VisibleForTesting;

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

public class PathFinderNew {
    private final List<GraphVertex> exhibits;
    private final List<GraphVertex> remainingExhibits;

    private final List<GraphVertex> visitedExhibits;
    private List<GraphVertex> orderedRemainingExhibits;

    private final ZooGraph zooGraph;

    private final GraphVertex start;
    private final GraphVertex end;

    private boolean briefDirections = false;

    List<GraphPath<String, IdentifiedWeightedEdge>> paths;
    GraphPath<String, IdentifiedWeightedEdge> currentPath;

    Map<String, Integer> distances;

    VertexDao vertexDao;

    public PathFinderNew(Context context, GraphVertex start){
        vertexDao = VertexDatabase.getSingleton(context).vertexDao();

        this.zooGraph = Zoo.getZoo(context);
        this.exhibits = vertexDao.getSelectedExhibits(GraphVertex.Kind.EXHIBIT);
        this.remainingExhibits = createRemainingExhibits();
        this.visitedExhibits = new ArrayList<>();

        this.start = start;
        this.end = zooGraph.getVertex("entrance_exit_gate");

        this.paths = createPlan();

        this.orderedRemainingExhibits = createOrderedRemainingExhibits();
        this.distances = calculateDistances();
    }

    @VisibleForTesting
    public PathFinderNew(Context context, GraphVertex start, List<GraphVertex> exhibits){
        this.zooGraph = Zoo.getZoo(context);
        this.exhibits = exhibits;
        this.remainingExhibits = createRemainingExhibits();
        this.visitedExhibits = new ArrayList<>();

        this.start = start;
        this.end = zooGraph.getVertex("entrance_exit_gate");

        this.paths = createPlan();

        this.orderedRemainingExhibits = createOrderedRemainingExhibits();
        this.distances = calculateDistances();
    }


    public List<String> getNextDirection(){
        List<String> directions;

        if(visitedExhibits.size() == 0){
            this.currentPath = paths.get(0);
            visitedExhibits.add(this.start);

            if(orderedRemainingExhibits.get(0).getId().equals(start.getId()))
                orderedRemainingExhibits.remove(0);

        } else if(orderedRemainingExhibits.size() == 1){
            this.currentPath = paths.get(paths.size() - 1);
            visitedExhibits.add(orderedRemainingExhibits.remove(0));
        } else {
            this.currentPath = getPath(orderedRemainingExhibits.get(0), orderedRemainingExhibits.get(1));
            visitedExhibits.add(orderedRemainingExhibits.remove(0));
        }

        directions = formatDirection(this.currentPath);

        return directions;
    }

    public List<String> getCurrentPath(){
        List<String> directions;

        directions = formatDirection(this.currentPath);

        return directions;
    }

    public List<String> getPreviousDirection(){
        List<String> directions;

        this.currentPath = getPreviousPath();
        directions = formatDirection(this.currentPath);

        orderedRemainingExhibits.add(0, visitedExhibits.remove(visitedExhibits.size() - 1));

        return directions;
    }

    public List<String> formatDirection(GraphPath<String, IdentifiedWeightedEdge> path){
        List<String> directions = new ArrayList<>();
        List<String> vertices = path.getVertexList();

        int vertexIndex = 0;

        if(this.briefDirections){
            String oldStreet = "";
            String oldSource = "";
            double totalWeight = 0;
            int curr = 0;
            for (IdentifiedWeightedEdge edge : path.getEdgeList()) {
                double weight = zooGraph.getGRAPH().getEdgeWeight(edge);
                totalWeight += weight;
                String street = zooGraph.getEdge(edge.getId()).getStreet();
                String source = zooGraph.getVertex(vertices.get(vertexIndex)).getName();
                vertexIndex++;
                String target = zooGraph.getVertex(vertices.get(vertexIndex)).getName();

                if (oldStreet.equals(street)) {

                    directions.set(curr - 1, "Walk " + totalWeight + " meters along " + street + " from " + oldSource + " to " + target + ".\n");
                } else {
                    directions.add("Walk " + weight + " meters along " + street + " from " + source + " to " + target + ".\n");
                    oldStreet = street;
                    oldSource = source;
                    totalWeight = weight;
                }

                curr++;
            }
        } else {
            for(IdentifiedWeightedEdge edge : path.getEdgeList()){
                double weight = zooGraph.getGRAPH().getEdgeWeight(edge);
                String street = zooGraph.getEdge(edge.getId()).getStreet();
                String source = zooGraph.getVertex(vertices.get(vertexIndex)).getName();
                vertexIndex++;
                String target = zooGraph.getVertex(vertices.get(vertexIndex)).getName();
                directions.add("Walk " + weight + " meters along " + street + " from " + source + " to " + target + ".\n");
            }
        }

        if(zooGraph.getVertex(vertices.get(vertices.size() - 1)).getKind() == GraphVertex.Kind.EXHIBIT_GROUP){
            List<GraphVertex> exhibitsInGroup = new ArrayList<>();

            for(GraphVertex graphVertex : exhibits){
                if(Objects.equals(graphVertex.getGroup_id(), vertices.get(vertices.size() - 1))){
                    exhibitsInGroup.add(graphVertex);
                }
            }


            StringBuilder findText = new StringBuilder("Find");
            for(int i = 0; i < exhibitsInGroup.size(); i++){
                if(i == 0)
                    findText.append(" ").append(exhibitsInGroup.get(i).getName());
                else
                    if(i == exhibitsInGroup.size() - 1)
                        findText.append(" and ").append(exhibitsInGroup.get(i).getName());
                    else
                        findText.append(", ").append(exhibitsInGroup.get(i).getName());
            }

            findText.append(" inside.");
            directions.add(findText.toString());
        }

        return directions;
    }

    private Map<String, Integer> calculateDistances(){
        Map<String, Integer> distances = new HashMap<>();
        int totalDistance = 0;

        for(GraphPath<String, IdentifiedWeightedEdge> path : this.paths){
            int distance = (int) path.getWeight();
            String endVertexId = zooGraph.getVertex(path.getEndVertex()).getId();

            totalDistance += distance;
            distances.put(endVertexId, totalDistance);
        }

        return distances;
    }

    public Map<String, Integer> getDistances(){
        return distances;
    }


    private List<GraphVertex> createOrderedRemainingExhibits(){
        List<GraphVertex> orderedRemainingExhibits = new ArrayList<>();
        orderedRemainingExhibits.add(zooGraph.getVertex(this.paths.get(0).getEndVertex()));

        for(int i = 1; i < this.paths.size() - 1; i++){
            orderedRemainingExhibits.add(zooGraph.getVertex(this.paths.get(i).getEndVertex()));
        }

        return orderedRemainingExhibits;
    }

    private List<GraphVertex> createRemainingExhibits(){
        List<GraphVertex> remainingExhibits = new ArrayList<>();

        for(GraphVertex exhibit : exhibits){
            if(exhibit.getGroup_id() != null){
                boolean inList = false;

                for(GraphVertex graphVertex : remainingExhibits){
                    if (graphVertex.getId().equals(exhibit.getGroup_id())) {
                        inList = true;
                        break;
                    }
                }

                if(!inList)
                    remainingExhibits.add(zooGraph.getVertex(exhibit.getGroup_id()));

            } else {
                remainingExhibits.add(exhibit);
            }
        }

        return remainingExhibits;
    }

    public List<String> skip(){
        GraphVertex exhibit = vertexDao.get(orderedRemainingExhibits.get(0).getId());
        exhibit.setSelected(false);
        vertexDao.update(exhibit);
        orderedRemainingExhibits.remove(0);
        List<GraphPath<String, IdentifiedWeightedEdge>> newPaths = replan(visitedExhibits.get(visitedExhibits.size() - 1));

        this.paths.addAll(newPaths);

        this.orderedRemainingExhibits = createOrderedRemainingExhibits();
        
        for(int i = 0; i < visitedExhibits.size() - 1; i++){
            GraphVertex remove = this.orderedRemainingExhibits.remove(i);
        }

        this.currentPath = newPaths.get(0);
        return getCurrentPath();
    }

    public List<GraphPath<String, IdentifiedWeightedEdge>> replan(GraphVertex start){
        List<GraphVertex> exhibitsToMap = new ArrayList<>(orderedRemainingExhibits);

        List<GraphPath<String, IdentifiedWeightedEdge>> newPaths = new ArrayList<>();
        GraphVertex startLocation = start;

        int toRemove = visitedExhibits.size() - 1;
        this.paths.subList(toRemove, this.paths.size()).clear();


        if(!start.getId().equals(visitedExhibits.get(visitedExhibits.size() - 1).getId())){
            this.paths.add(DijkstraShortestPath.findPathBetween(zooGraph.getGRAPH(), visitedExhibits.get(visitedExhibits.size() - 1).getId(), start.getId()));
            visitedExhibits.add(start);
        }

        exhibitsToMap.removeIf(graphVertex -> graphVertex.getId().equals(start.getId()));

        while(!exhibitsToMap.isEmpty()){
            GraphPath<String, IdentifiedWeightedEdge> minPath = DijkstraShortestPath.findPathBetween(zooGraph.getGRAPH(), startLocation.getId(), exhibitsToMap.get(0).getId());

            for(GraphVertex graphVertex : exhibitsToMap){
                GraphPath<String, IdentifiedWeightedEdge> potentialPath = DijkstraShortestPath.findPathBetween(zooGraph.getGRAPH(), startLocation.getId(), graphVertex.getId());
                if(potentialPath.getWeight() < minPath.getWeight()) {
                    minPath = potentialPath;
                }
            }

            newPaths.add(minPath);
            startLocation = zooGraph.getVertex(minPath.getEndVertex());

            for(GraphVertex graphVertex : exhibitsToMap){
                if(graphVertex.equals(startLocation)){
                    exhibitsToMap.remove(graphVertex);
                    break;
                }
            }

        }

        newPaths.add(DijkstraShortestPath.findPathBetween(zooGraph.getGRAPH(), startLocation.getId(), end.getId()));

        return newPaths;
    }

    private List<GraphPath<String, IdentifiedWeightedEdge>> createPlan(){
        List<GraphVertex> exhibitsToMap = new ArrayList<>(remainingExhibits);

        List<GraphPath<String, IdentifiedWeightedEdge>> paths = new ArrayList<>();
        GraphVertex startLocation = this.start;

        while(!exhibitsToMap.isEmpty()){
            GraphPath<String, IdentifiedWeightedEdge> minPath = DijkstraShortestPath.findPathBetween(zooGraph.getGRAPH(), startLocation.getId(), exhibitsToMap.get(0).getId());

            for(GraphVertex graphVertex : exhibitsToMap){
                GraphPath<String, IdentifiedWeightedEdge> potentialPath = DijkstraShortestPath.findPathBetween(zooGraph.getGRAPH(), startLocation.getId(), graphVertex.getId());
                if(potentialPath.getWeight() < minPath.getWeight()) {
                    minPath = potentialPath;
                }
            }

            paths.add(minPath);
            startLocation = zooGraph.getVertex(minPath.getEndVertex());

            for(GraphVertex graphVertex : exhibitsToMap){
                if(graphVertex.equals(startLocation)){
                    exhibitsToMap.remove(graphVertex);
                    break;
                }
            }

        }

        paths.add(DijkstraShortestPath.findPathBetween(zooGraph.getGRAPH(), startLocation.getId(), end.getId()));

        return paths;
    }

    public GraphPath<String, IdentifiedWeightedEdge> getPreviousPath(){
        if(orderedRemainingExhibits.size() == 0){
            return DijkstraShortestPath.findPathBetween(zooGraph.getGRAPH(), end.getId(), visitedExhibits.get(visitedExhibits.size() - 1).getId());
        }

        return DijkstraShortestPath.findPathBetween(zooGraph.getGRAPH(), orderedRemainingExhibits.get(0).getId(), visitedExhibits.get(visitedExhibits.size() - 1).getId());
    }

    public GraphPath<String, IdentifiedWeightedEdge> getPath(GraphVertex start, GraphVertex end){
        for(GraphPath<String, IdentifiedWeightedEdge> path : this.paths){
            if(path.getStartVertex().equals(start.getId()) && path.getEndVertex().equals(end.getId())){
                return path;
            }
        }

        return null;
    }


    public void toggleBriefDirections(boolean briefDirections){ this.briefDirections = briefDirections; }

    public List<GraphVertex> getRemainingExhibits(){ return orderedRemainingExhibits; }

    public List<GraphVertex> getVisitedExhibits(){
        return visitedExhibits;
    }

    public String previousExhibitName(){ return visitedExhibits.get(visitedExhibits.size() - 1).getName(); }

    public String currentAnimal(){
        if(orderedRemainingExhibits.size() == 0){
            return end.getName();
        }

        return orderedRemainingExhibits.get(0).getName();
    }

    public String previousExhibitDistance(){ return String.valueOf(getPreviousPath().getWeight()); }

    public String nextExhibitName(){
        if(orderedRemainingExhibits.size() == 1){
            return end.getName();
        } else {
            return orderedRemainingExhibits.get(1).getName();
        }
    }

    public String nextExhibitDistance(){
        if(orderedRemainingExhibits.size() == 1){
            return paths.get(0).getWeight() + "m";
        } else {
            return getPath(orderedRemainingExhibits.get(0), orderedRemainingExhibits.get(1)).getWeight() + "m";
        }
    }

    public boolean checkLocation(LatLng location){
        GraphVertex planedVertex = visitedExhibits.get(visitedExhibits.size() - 1);

        return planedVertex.getLat() != location.longitude && planedVertex.getLng() != location.longitude;
    }

    public List<String> updateBasedOnLocation(LatLng location){
        GraphVertex planedVertex = visitedExhibits.get(visitedExhibits.size() - 1);
        GraphVertex newStart = null;


        if(planedVertex.getLat() != location.longitude && planedVertex.getLng() != location.longitude) {
            for (GraphVertex graphVertex : zooGraph.getVERTICES()) {
                if (graphVertex.getLat() == location.latitude && graphVertex.getLng() == location.longitude) {
                    newStart = graphVertex;
                    break;
                }
            }

            assert newStart != null;
            List<GraphPath<String, IdentifiedWeightedEdge>> newPaths = replan(newStart);

            this.paths.addAll(newPaths);

            this.orderedRemainingExhibits = createOrderedRemainingExhibits();

            for (int i = 0; i < visitedExhibits.size() - 1; i++) {
                GraphVertex remove = this.orderedRemainingExhibits.remove(0);
            }

            this.currentPath = newPaths.get(0);
            return getCurrentPath();
        }

        return getCurrentPath();
    }
}