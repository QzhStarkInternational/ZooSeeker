package com.example.sandiegozooseeker.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandiegozooseeker.AnimalDB.Vertex;
import com.example.sandiegozooseeker.AnimalDB.VertexViewModel;
import com.example.sandiegozooseeker.R;
import com.example.sandiegozooseeker.adapaters.PlanListAdapter;
import com.example.sandiegozooseeker.pathfinder.Pathfinder;

import java.util.List;

public class PlanFragment extends Fragment {
    public RecyclerView recyclerView;
    public PlanListAdapter adapter;
    private Button editButton;
    private boolean editClicked = false;

    public PlanFragment() {
        super(R.layout.plan_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        VertexViewModel viewModel = new ViewModelProvider(requireActivity())
                .get(VertexViewModel.class);

        adapter = new PlanListAdapter();
        adapter.setHasStableIds(true);

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

        List<String> animal = viewModel.getSelectedAnimalId();

        // List<id>
        List<String> copyAnimal = animal;
        Pathfinder path = new Pathfinder(copyAnimal, this.getActivity().getApplicationContext());
        path.plan();




        viewModel.getSelectedVertices().observe(getViewLifecycleOwner(), adapter::setVertices);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}
