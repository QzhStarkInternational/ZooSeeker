package com.example.sandiegozooseeker.pathfinder;

import android.content.Context;

import com.example.sandiegozooseeker.AnimalDB.Vertex;
import com.example.sandiegozooseeker.AnimalDB.VertexDao;
import com.example.sandiegozooseeker.AnimalDB.VertexDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

public class Pathfinder {

    private final VertexDao vertexDao;
    List<String> exhibits;
    String start;
    String end;
    Graph<String, IdentifiedWeightedEdge> g;
    Map<String, ZooData.VertexInfo> vInfo;
    Map<String, ZooData.EdgeInfo> eInfo;
    List<String> orderedList;

    public Pathfinder(List<String> list, Context context, String currentExhibit) {
        exhibits = list;
        start = currentExhibit;
        end = "entrance_exit_gate";

        g = ZooData.loadZooGraphJSON(context, "zoo_graph.json");
        vInfo = ZooData.loadVertexInfoJSON(context, "zoo_node_info.json");
        eInfo = ZooData.loadEdgeInfoJSON(context, "zoo_edge_info.json");
        VertexDatabase db = VertexDatabase.getSingleton(context);
        vertexDao = db.vertexDao();
    }

    public Vertex getVertexId(String name){
        return vertexDao.get(vInfo.get(name).id);
    }

    public Vertex getVertexId(int i){
        return vertexDao.get(vInfo.get(i).id);
    }

    public int getvertexNums(){
        return vInfo.size();
    }

    public List<GraphPath<String, IdentifiedWeightedEdge>> plan(){
        List<String> tempExhibits = new ArrayList<>();
        for (String s : exhibits) {
            tempExhibits.add(s);
        }
        //check for parent-child exhibits
        for (int x = 0; x<tempExhibits.size();x++) {
            if (vInfo.get(tempExhibits.get(x)).group_id != null) {
                String id = vertexDao.getParentVertex(vInfo.get(tempExhibits.get(x)).group_id).id;
                tempExhibits.set(x,id);
            }
        }
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
            //System.out.println(minPath);
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
        List<String> tempExhibits = new ArrayList<>();
        for (String s : exhibits) {
            tempExhibits.add(s);
        }
        List<String> info = new ArrayList<String>();
        int totalDistance = 0;

        for (GraphPath<String, IdentifiedWeightedEdge> path : paths) {
            int distance = (int)path.getWeight();
            String id = vInfo.get(path.getEndVertex().toString()).id;
            String name = vInfo.get(path.getEndVertex().toString()).name;
            for (String s : tempExhibits) {
                if (vInfo.get(s).group_id != null && vInfo.get(s).group_id.equals(id)) {
                    name = vInfo.get(s).name;
                    tempExhibits.remove(s);
                    break;
                }
            }
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

    //this is a map to associate the animal exhibit with the calculated distance for sorting the plan list and displaying the distance
    public Map<String,Integer> getDistanceMapping(List<GraphPath<String, IdentifiedWeightedEdge>> paths) {
        List<String> tempExhibits = new ArrayList<>();
        orderedList = new ArrayList<>();
        for (String s : exhibits) {
            tempExhibits.add(s);
        }
        Map<String, Integer> distanceMapping = new HashMap();
        int totalDistance = 0;

        for (GraphPath<String, IdentifiedWeightedEdge> path : paths) {
            int distance = (int)path.getWeight();
            String id = Objects.requireNonNull(vInfo.get(path.getEndVertex())).id;
            for (String s : tempExhibits) {
                if (vInfo.get(s).group_id != null && vInfo.get(s).group_id.equals(id)) {
                    id = vInfo.get(s).id;
                    tempExhibits.remove(s);
                    break;
                }
            }
            totalDistance += distance;
            distanceMapping.put(id,totalDistance);
            orderedList.add(id);
        }
        return distanceMapping;
    }

    //return ordered list
    public List<String> getOrderedList() {
        return orderedList;
    }
    // for testing do Log.d("name", "print this"); instead of system.out
}