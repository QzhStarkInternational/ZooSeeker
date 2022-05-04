package com.example.sandiegozooseeker;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VertexViewModel extends AndroidViewModel {
    private LiveData<List<Vertex>> vertices;
    private final VertexDao vertexDao;
    private List<Vertex> addedAnimals;

    public VertexViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        VertexDatabase db = VertexDatabase.getSingleton(context);

        vertexDao = db.vertexDao();
        addedAnimals = new ArrayList<Vertex>();
    }

    public LiveData<List<Vertex>> getVertices() {
        if (vertices == null) {
            loadAnimals();
        }
        return vertices;
    }
    private void loadAnimals() {
        vertices = vertexDao.getAllLive();
    }

    //remove record
    public void deleteVertex(Vertex vertex) {
        vertexDao.delete(vertex);
    }

    //okay it works but you gotta click twice
    public void toggleClickedAddToArray(Vertex vertex, ConstraintLayout layout) {
        //this one is for calling in main activity to add to an arraylist and then populating the database
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!addedAnimals.contains(vertex)) {
                    addedAnimals.add(vertex);
                }
            }
        });
    }

    //okay it works but you gotta click twice
    public void toggleClicked(Vertex vertex, ConstraintLayout layout) {
        //this one is for editing the plan list
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vertexDao.delete(vertex);
            }
        });
    }

    public List<Vertex> getAddedAnimals() {
        return addedAnimals;
    }
    public void clearAddedAnimals() {
        addedAnimals.clear();
    }

}
