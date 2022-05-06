package com.example.sandiegozooseeker.fragments;

import android.os.Bundle;
import android.util.Log;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.textfield.TextInputEditText;

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
        adapter.setOnClickedHandler(viewModel::toggleClicked);

        viewModel.getSelectedVertices().observe(getViewLifecycleOwner(), adapter::setVertices);

        // Test it

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


//        adapter.setOnClickedHandler(viewModel::toggleClicked);


        List<String> animal = viewModel.getSelectedAnimalId();

        // Bug appear:
        List<String> copyAnimal = animal;

//        int loop = 0;
//        try {
//            loop += 1;
        Pathfinder p = new Pathfinder(copyAnimal, getContext());
//        } catch (Exception e) {
//            Log.d("tag", loop + "");
//        }
        //Pathfinder p = new Pathfinder(copyAnimal);

//        Log.d("tag", animal.size() + "");

        if (this.editClicked) {
        } else {

        }


        viewModel.getSelectedVertices().observe(getViewLifecycleOwner(), adapter::setVertices);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}
