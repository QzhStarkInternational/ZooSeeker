package com.example.sandiegozooseeker.AnimalDB;

import android.app.Application;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.sandiegozooseeker.AnimalDB.Vertex;
import com.example.sandiegozooseeker.AnimalDB.VertexDao;
import com.example.sandiegozooseeker.AnimalDB.VertexDatabase;
import com.example.sandiegozooseeker.fragments.PlanFragment;
import com.example.sandiegozooseeker.pathfinder.IdentifiedWeightedEdge;
import com.example.sandiegozooseeker.pathfinder.Pathfinder;
import com.google.android.material.textfield.TextInputEditText;

import org.jgrapht.GraphPath;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class VertexViewModel extends AndroidViewModel {
    private LiveData<List<Vertex>> vertices = null;
    private LiveData<List<Vertex>> selectedVertices = null;
    private final VertexDao vertexDao;
    private List<Vertex> vertexList = null;
    private List<String> animalList;
    private MutableLiveData<List<Vertex>> mutableLiveData = null;


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

    public List<String> getSelectedAnimalName() {
        if (vertexList == null) {
            loadSeletectedVertice();
        }

        List<Vertex> vList = vertexList;

        List<String> res = vList.stream().map(Vertex::getName).collect(Collectors.toList());
        return res;
    }
//


    public MutableLiveData<List<Vertex>> transformType() {
        if (mutableLiveData == null) {
            loadAnimals();
            mutableLiveData = new MutableLiveData<>(this.vertices.getValue());
        }

        return this.mutableLiveData;
    }

    public void updateData(HashMap<String, String> map) {
//        MutableLiveData<List<Vertex>> t = transformType();
//        List<Vertex> list = t.getValue();
//
//        for (Vertex v: list) {
//            v.setDistance(map.get(v.name));
//        }
//        this.vertices = new LiveData<List<Vertex>>(list) {
//            @Override
//            public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super List<Vertex>> observer) {
//                super.observe(owner, observer);
//            }
//        };

        int loop = 0;
        if (this.selectedVertices.getValue().size() != 0) {
            loadSelectedAnimals();

                for (Vertex v: this.selectedVertices.getValue()) {
                    v.setDistance(map.get(v.name));
                    Log.d("tag", loop + v.getDistance() + "");
                    loop += 1;
                }


        }

    }






}
