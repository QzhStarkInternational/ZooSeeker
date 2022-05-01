package com.example.sandiegozooseeker;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class FindFragment extends Fragment {
    public FindFragment(){
        super(R.layout.find_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        int someInt = requireArguments().getInt("some_int");
    }
}
