package com.example.sandiegozooseeker;

import android.app.Application;
import android.content.Context;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class VertexViewModel extends AndroidViewModel {
    //get updated info on UI when database changes
    private LiveData<List<Vertex>> vertices;
    private final VertexDao vertexDao;

    public VertexViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        VertexDatabase db = VertexDatabase.getSingleton(context);
        vertexDao = db.vertexDao();
    }

    public LiveData<List<Vertex>> getVertices() {
        if (vertices == null) {
            loadUsers();
        }
        return vertices;
    }
    private void loadUsers() {
        vertices = vertexDao.getAllLive();
    }

    //remove record
    public void deleteVertex(Vertex vertex) {
        vertexDao.delete(vertex);
    }

    public void toggleChecked(Vertex vertex, CheckBox checkBox) {
        if (checkBox.isChecked()) {
            //add animal
            vertexDao.insert(vertex);
            //make checkbox checked
            checkBox.setChecked(true);

            vertex.isSelected = true;
            vertexDao.update(vertex);
        } else {
            //remove animal
            vertex.isSelected = false;
            vertexDao.update(vertex);
            vertexDao.delete(vertex);
        }
    }

}
