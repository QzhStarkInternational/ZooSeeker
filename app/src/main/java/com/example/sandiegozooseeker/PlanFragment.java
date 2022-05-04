package com.example.sandiegozooseeker;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PlanFragment extends Fragment {
    public RecyclerView recyclerView;

    public PlanFragment(){
        super(R.layout.plan_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        VertexViewModel viewModel = new ViewModelProvider(requireActivity())
                .get(VertexViewModel.class);

        PlanListAdapter adapter = new PlanListAdapter();
        adapter.setHasStableIds(true);
        adapter.setOnClickedHandler(viewModel::toggleClicked);
        viewModel.getSelectedVertices().observe(getViewLifecycleOwner(), adapter::setVertices);

        recyclerView = requireView().findViewById(R.id.vertex_items_plan);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}
