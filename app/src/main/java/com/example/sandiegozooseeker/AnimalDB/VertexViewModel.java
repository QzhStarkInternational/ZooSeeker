package com.example.sandiegozooseeker.AnimalDB;

import android.app.Application;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.sandiegozooseeker.AnimalDB.Vertex;
import com.example.sandiegozooseeker.AnimalDB.VertexDao;
import com.example.sandiegozooseeker.AnimalDB.VertexDatabase;

import java.util.List;
import java.util.stream.Collectors;

public class VertexViewModel extends AndroidViewModel {
    private LiveData<List<Vertex>> vertices = null;
    private LiveData<List<Vertex>> selectedVertices = null;
    private final VertexDao vertexDao;
    private List<Vertex> vertexList = null;
    private List<String> animalList;


    public VertexViewModel(@NonNull Application application) {
        super(application);
        Context context = application.getApplicationContext();
        VertexDatabase db = VertexDatabase.getSingleton(context);
        vertexDao = db.vertexDao();
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
    public void loadSeletectedVertice() {
        vertexList = vertexDao.getSelectedExhibits(Vertex.Kind.EXHIBIT);
    }

    public List<String> getSelectedAnimalId() {
        if (vertexList == null) {
            loadSeletectedVertice();
        }

        List<Vertex> vList = vertexList;

        List<String> res = vList.stream().map(Vertex::getId).collect(Collectors.toList());
        return res;
    }
}
