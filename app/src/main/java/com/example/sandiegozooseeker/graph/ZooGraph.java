package com.example.sandiegozooseeker.graph;

import android.content.Context;

import com.example.sandiegozooseeker.utils.Constants;
import com.example.sandiegozooseeker.utils.JSONLoader;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.nio.json.JSONImporter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Objects;

public class ZooGraph {
    private final Graph<String, IdentifiedWeightedEdge> GRAPH;
    private final List<GraphEdge> EDGES;
    private final List<GraphVertex> VERTICES;

    public ZooGraph(Context context){
        this.GRAPH = buildGraph(context);
        this.VERTICES = JSONLoader.loadVertices(context);
        this.EDGES = JSONLoader.loadEdges(context);
    }

    public GraphVertex getVertex(String id){
        for(int i = 0; i < VERTICES.size(); i++){
            if(Objects.equals(VERTICES.get(i).getId(), id)){
                return VERTICES.get(i);
            }
        }

        return null;
    }

    public GraphEdge getEdge(String id){
        for(int i = 0; i < EDGES.size(); i++){
            if(Objects.equals(EDGES.get(i).getId(), id)){
                return EDGES.get(i);
            }
        }

        return null;
    }

    private Graph<String, IdentifiedWeightedEdge> buildGraph(Context context){
        // Create an empty graph to populate.
        Graph<String, IdentifiedWeightedEdge> graph = new DefaultUndirectedWeightedGraph<>(IdentifiedWeightedEdge.class);

        // Create an importer that can be used to populate our empty graph.
        JSONImporter<String, IdentifiedWeightedEdge> importer = new JSONImporter<>();

        // We don't need to convert the vertices in the graph, so we return them as is.
        importer.setVertexFactory(v -> v);

        // We need to make sure we set the IDs on our edges from the 'id' attribute.
        // While this is automatic for vertices, it isn't for edges. We keep the
        // definition of this in the IdentifiedWeightedEdge class for convenience.
        importer.addEdgeAttributeConsumer(IdentifiedWeightedEdge::attributeConsumer);

        try {
            InputStream inputStream = context.getAssets().open(Constants.GRAPH_JSON_PATH);
            Reader reader = new InputStreamReader(inputStream);
            importer.importGraph(graph, reader);

            return graph;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<GraphEdge> getEDGES(){
        return EDGES;
    }

    public List<GraphVertex> getVERTICES(){
        return VERTICES;
    }

    public Graph<String, IdentifiedWeightedEdge> getGRAPH(){
        return GRAPH;
    }
}
