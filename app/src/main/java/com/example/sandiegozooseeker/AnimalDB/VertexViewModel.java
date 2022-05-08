package com.example.sandiegozooseeker.AnimalDB;

import android.app.Application;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.sandiegozooseeker.AnimalDB.Vertex;
import com.example.sandiegozooseeker.AnimalDB.VertexDao;
import com.example.sandiegozooseeker.AnimalDB.VertexDatabase;
import com.example.sandiegozooseeker.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Collections;
import java.util.List;

public class VertexViewModel extends AndroidViewModel {
    private LiveData<List<Vertex>> vertices = null;
    private LiveData<List<Vertex>> selectedVertices = null;
    private LiveData<TextView> searchText;
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

    public int getExhibitSelectedCount() {
        return vertexDao.getSelectedExhibits(Vertex.Kind.EXHIBIT).size();
    }

}
