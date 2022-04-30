package com.example.sandiegozooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class PathActivity extends AppCompatActivity {
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);

        //test out Vertex class works
//        List<Vertex> vertex = Vertex.loadJSON(this,"sample_node_info.json");
//        Log.d("Vertices",vertex.toString());


        //change data source to database
        //VertexDao vertexDao = VertexDatabase.getSingleton(this).vertexDao();
        VertexDatabase db = VertexDatabase.getSingleton(this);
        VertexDao vertexDao = db.vertexDao();
        List<Vertex> vertices = vertexDao.getAll();

        VertexViewModel viewModel = new ViewModelProvider(this)
                .get(VertexViewModel.class);

        VertexListAdapter adapter = new VertexListAdapter();
        adapter.setHasStableIds(true);
        //adapter.setOnDeleteClickedHandler(viewModel::deleteVertex);
        adapter.setOnCheckBoxClickedHandler(viewModel::toggleChecked);
        viewModel.getVertices().observe(this, adapter::setVertices);

        adapter.setVertices(vertices);

        //just show the list of available animals and as you select it, it will add to the database
        //you can remove on the "plan" screen to make last minute edits

        //adapter.setVertices(Vertex.loadJSON(this,"sample_node_info.json"));

        recyclerView = findViewById(R.id.vertex_items_plan);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }
}