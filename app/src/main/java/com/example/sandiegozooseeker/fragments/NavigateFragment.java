package com.example.sandiegozooseeker.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandiegozooseeker.AnimalDB.VertexViewModel;
import com.example.sandiegozooseeker.R;
import com.example.sandiegozooseeker.adapaters.PlanListAdapter;

public class NavigateFragment extends Fragment {


    public NavigateFragment(){
        super(R.layout.navigate_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

    }
}
