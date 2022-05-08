package com.example.sandiegozooseeker.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandiegozooseeker.AnimalDB.VertexViewModel;
import com.example.sandiegozooseeker.R;
import com.example.sandiegozooseeker.adapaters.SearchListAdapter;
import com.google.android.material.textfield.TextInputEditText;

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

//        TextInputEditText searchText = view.findViewById(R.id.textInputEditText);
//        searchText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                Log.d("search", charSequence.toString());
//                adapter.getFilter().filter(charSequence.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//            }
//        });

        recyclerView = requireActivity().findViewById(R.id.vertex_items_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);
    }
}
