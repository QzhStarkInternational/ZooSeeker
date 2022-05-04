package com.example.sandiegozooseeker;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
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
    private LiveData<List<Vertex>> vertices = null;
    private LiveData<List<Vertex>> selectedVertices = null;
    private final VertexDao vertexDao;

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
}
