package com.example.sandiegozooseeker;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

public class PathFragment extends Fragment {
    public RecyclerView recyclerView;

    public PathFragment(){
        super(R.layout.path_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        //test out Vertex class works
//        List<Vertex> vertex = Vertex.loadJSON(this,"sample_node_info.json");
//        Log.d("Vertices",vertex.toString());


        //change data source to database
        VertexDao vertexDao = VertexDatabase.getSingleton(getContext()).vertexDao();
        List<Vertex> vertices = vertexDao.getAll();

        VertexViewModel viewModel = new ViewModelProvider(this)
                .get(VertexViewModel.class);

        VertexListAdapter adapter = new VertexListAdapter();
        adapter.setHasStableIds(true);
        //adapter.setOnDeleteClickedHandler(viewModel::deleteVertex);
        adapter.setOnLayoutClickedHandler(viewModel::toggleClicked);
        viewModel.getVertices().observe(getViewLifecycleOwner(), adapter::setVertices);

        adapter.setVertices(vertices);

        //just show the list of available animals and as you select it, it will add to the database
        //you can remove on the "plan" screen to make last minute edits

        //adapter.setVertices(Vertex.loadJSON(this,"sample_node_info.json"));

        recyclerView = requireView().findViewById(R.id.vertex_items_plan);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}
