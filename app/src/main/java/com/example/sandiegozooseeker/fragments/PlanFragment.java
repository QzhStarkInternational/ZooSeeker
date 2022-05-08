package com.example.sandiegozooseeker.fragments;

import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandiegozooseeker.AnimalDB.Vertex;
import com.example.sandiegozooseeker.AnimalDB.VertexViewModel;
import com.example.sandiegozooseeker.R;
import com.example.sandiegozooseeker.adapaters.PlanListAdapter;
import com.example.sandiegozooseeker.pathfinder.IdentifiedWeightedEdge;
import com.example.sandiegozooseeker.pathfinder.Pathfinder;

import java.util.HashMap;
import java.util.List;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import org.jgrapht.GraphPath;

public class PlanFragment extends Fragment {
    public RecyclerView recyclerView;
    public PlanListAdapter adapter;
    private Button editButton;
    private boolean editClicked = false;
    private VertexViewModel viewModel;

    public PlanFragment() {
        super(R.layout.plan_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(requireActivity())
                .get(VertexViewModel.class);

        adapter = new PlanListAdapter(getContext());
        adapter.setHasStableIds(true);

        viewModel.getSelectedVertices().observe(getViewLifecycleOwner(), adapter::setVertices);

        this.recyclerView = requireView().findViewById(R.id.vertex_items_plan);

        this.editButton = requireView().findViewById(R.id.edit_but);
        this.editButton.setOnClickListener(view1 -> {
            this.editClicked = !this.editClicked;
            if (this.editClicked) {
                this.editButton.setText("DONE");
                adapter.setOnClickedHandler(viewModel::toggleClicked);

            } else {
                this.editButton.setText("EDIT");
                adapter.setOnClickedHandler(null);
            }
        });



        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }




}
