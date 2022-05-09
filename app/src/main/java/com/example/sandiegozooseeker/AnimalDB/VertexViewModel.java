package com.example.sandiegozooseeker.AnimalDB;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.stream.Collectors;

public class VertexViewModel extends AndroidViewModel {
    private final VertexDao vertexDao;

    private LiveData<List<Vertex>> vertices = null;
    private LiveData<List<Vertex>> selectedVertices = null;
    private LiveData<Integer> vertexCount;

    private List<Vertex> vertexList = null;

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

    public LiveData<List<Vertex>> getVertices() {
        if (vertices == null) {
            loadAnimals();
        }
        return vertices;
    }

    public LiveData<List<Vertex>> getSelectedVertices(){
        if(selectedVertices == null){
            loadSelectedAnimals();
        }

        return selectedVertices;
    }

    private void loadAnimals() {
        vertices = vertexDao.getAllOfKindLive(Vertex.Kind.EXHIBIT);
    }

    private void loadSelectedAnimals() {
        selectedVertices = vertexDao.getSelectedOfKindLive(Vertex.Kind.EXHIBIT);
    }

    public void toggleClicked(Vertex vertex, View view) {
        vertex.isSelected = !vertex.isSelected;
        vertexDao.update(vertex);
    }

    // Update
    public void loadSelectedVertices() {
        vertexList = vertexDao.getSelectedExhibits(Vertex.Kind.EXHIBIT);
    }

    public void getExhibitSelectedCount() {
        vertexCount = vertexDao.getSelectedExhibitsCount(Vertex.Kind.EXHIBIT);
    }

    public List<String> getSelectedAnimalId() {
        if (vertexList == null) {
            loadSelectedVertices();
        }

        List<Vertex> vList = vertexList;
        List<String> res = vList.stream().map(Vertex::getId).collect(Collectors.toList());
        return res;
    }
}
