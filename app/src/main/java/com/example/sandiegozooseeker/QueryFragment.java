package com.example.sandiegozooseeker;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class QueryFragment extends Fragment {
    public QueryFragment(){
        super(R.layout.query_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        int someInt = requireArguments().getInt("some_int");
    }
}
