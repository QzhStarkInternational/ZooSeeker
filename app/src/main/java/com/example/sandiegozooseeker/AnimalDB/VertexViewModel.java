package com.example.sandiegozooseeker.AnimalDB;

import android.app.Application;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.sandiegozooseeker.graph.GraphVertex;

import java.util.List;
import java.util.stream.Collectors;

public class VertexViewModel extends AndroidViewModel {
    private final VertexDao vertexDao;

    private LiveData<List<GraphVertex>> vertices = null;
    private LiveData<List<GraphVertex>> selectedVertices = null;
    private LiveData<Integer> vertexCount;

    private List<GraphVertex> graphVertexList = null;

    public VertexViewModel(@NonNull Application application) {
        super(application);
        Context context = application.getApplicationContext();
        VertexDatabase db = VertexDatabase.getSingleton(context);
        vertexDao = db.vertexDao();
    }

    public LiveData<Integer> getVertexCount(){
        if(vertexCount == null){
            getExhibitSelectedCount();
        }

        return vertexCount;
    }

    public LiveData<List<GraphVertex>> getVertices() {
        if (vertices == null) {
            loadAnimals();
        }
        return vertices;
    }

    public LiveData<List<GraphVertex>> getSelectedVertices(){
        if(selectedVertices == null){
            loadSelectedAnimals();
        }

        return selectedVertices;
    }

    private void loadAnimals() {
        vertices = vertexDao.getAllOfKindLive(GraphVertex.Kind.EXHIBIT);
    }

    private void loadSelectedAnimals() {
        selectedVertices = vertexDao.getSelectedOfKindLive(GraphVertex.Kind.EXHIBIT);
    }

    public void toggleClicked(GraphVertex graphVertex, View view) {
        graphVertex.setSelected(!graphVertex.getIsSelected());
        vertexDao.update(graphVertex);
    }

    // Update
    public void loadSelectedVertices() {
        graphVertexList = vertexDao.getSelectedExhibits(GraphVertex.Kind.EXHIBIT);
    }

    public void getExhibitSelectedCount() {
        vertexCount = vertexDao.getSelectedExhibitsCount(GraphVertex.Kind.EXHIBIT);
    }

    public List<String> getSelectedAnimalId() {
        if (graphVertexList == null) {
            loadSelectedVertices();
        }

        List<GraphVertex> vList = graphVertexList;
        List<String> res = vList.stream().map(GraphVertex::getId).collect(Collectors.toList());
        return res;
    }
}
