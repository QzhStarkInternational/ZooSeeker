package com.example.sandiegozooseeker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment {

    public RecyclerView recyclerView;

    public SearchFragment() {
        super(R.layout.search_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        VertexViewModel viewModel = new ViewModelProvider(requireActivity())
                .get(VertexViewModel.class);

        SearchListAdapter adapter = new SearchListAdapter();
        adapter.setHasStableIds(true);
        adapter.setOnClickedHandler(viewModel::toggleClicked);

        viewModel.getVertices().observe(getViewLifecycleOwner(), adapter::setVertices);

        recyclerView = requireActivity().findViewById(R.id.vertex_items_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);
    }
}
