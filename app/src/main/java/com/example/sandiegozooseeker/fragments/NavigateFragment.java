package com.example.sandiegozooseeker.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.sandiegozooseeker.PathFinder.PathFinder;
import com.example.sandiegozooseeker.R;
import com.example.sandiegozooseeker.graph.Zoo;

import java.util.List;

public class NavigateFragment extends Fragment {
    private TextView nextAnimalNameTextView;
    private TextView nextAnimalDistanceTextView;
    private TextView directionText;
    private TextView animalText;

    PathFinder pf;

    public NavigateFragment() {
        super(R.layout.fragment_navigate);
    }
    //keep track of which animal exhibit direction to display

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        directionText = view.findViewById(R.id.direction_text);
        animalText = view.findViewById(R.id.animal_name);
        CardView nextView = view.findViewById(R.id.nextView);
        nextAnimalNameTextView = view.findViewById(R.id.textView2);
        nextAnimalDistanceTextView = view.findViewById(R.id.textView);
        CardView previousAnimalView = view.findViewById(R.id.previousView);


        pf = new PathFinder(getContext(), Zoo.getZoo(getContext()).getVertex("entrance_exit_gate"));

        updateDirections(0);

        nextView.setOnClickListener(view1 -> {
            updateDirections(0);
        });

        previousAnimalView.setOnClickListener(view1 -> {
            updateDirections(1);
        });
    }

    private void updateDirections(int j) {
        List<String> directions = pf.getDirection();
        if(j == 1){
            directions = pf.getPrevious();
        }

        StringBuilder directionString = new StringBuilder();

        for(int i = 0; i < directions.size(); i++){
            directionString.append("\n").append(directions.get(i));
        }

        animalText.setText(String.format("Directions to: %s", pf.currentAnimalName()));
        directionText.setText(directionString.toString());

        nextAnimalNameTextView.setText(pf.nextAnimalName());
        nextAnimalDistanceTextView.setText(pf.nextLabel());

    }
}