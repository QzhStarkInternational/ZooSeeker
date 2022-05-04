package com.example.sandiegozooseeker;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class SearchFragment extends Fragment {

    public RecyclerView recyclerView;

    public SearchFragment() {
        super(R.layout.search_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        VertexDao vertexDao = VertexDatabase.getSingleton(getActivity()).vertexDao();

        VertexViewModel viewModel = new ViewModelProvider(this)
                .get(VertexViewModel.class);

        VertexListAdapter adapter = new VertexListAdapter();
        adapter.setHasStableIds(true);
        adapter.setVertices(Vertex.loadJSON(requireActivity(),"sample_node_info.json"));
        adapter.setOnLayoutClickedHandler(viewModel::toggleClickedAddToArray);

        recyclerView = requireActivity().findViewById(R.id.vertex_items_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }
}
