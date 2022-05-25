package com.example.sandiegozooseeker.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.sandiegozooseeker.AnimalDB.Vertex;
import com.example.sandiegozooseeker.AnimalDB.VertexDao;
import com.example.sandiegozooseeker.AnimalDB.VertexDatabase;
import com.example.sandiegozooseeker.Prompt;
import com.example.sandiegozooseeker.R;
import com.example.sandiegozooseeker.pathfinder.Directions;
import com.example.sandiegozooseeker.pathfinder.IdentifiedWeightedEdge;
import com.example.sandiegozooseeker.pathfinder.Pathfinder;

import org.jgrapht.GraphPath;

import java.util.List;

public class NavigateFragment extends Fragment {
    private CardView nextView;
    private TextView nextAnimalNameTextView;
    private TextView nextAnimalDistanceTextView;
    private TextView directionText;
    private TextView animalText;
    private List<String> directions;
    private List<String> orderedList;
    private VertexDao vertexDao;
    private Button skipButton;
    Directions dir;

    public NavigateFragment(){ super(R.layout.fragment_navigate); }

    //keep track of which animal exhibit direction to display
    private int mCurrentIndex = 0;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        vertexDao = VertexDatabase.getSingleton(getActivity()).vertexDao();
        List<String> selectedExhibits = vertexDao.getSelectedExhibitsID(Vertex.Kind.EXHIBIT);
        //System.out.println(selectedExhibits);
        Pathfinder pf = new Pathfinder(selectedExhibits, getActivity(), "entrance_exit_gate");

        List<GraphPath<String, IdentifiedWeightedEdge>> plan = pf.plan();
        List<String> orderedPaths = pf.pathsToStringList(plan);
        // System.out.println(orderedPaths);

        directionText = (TextView)view.findViewById(R.id.direction_text);
        animalText = (TextView) view.findViewById(R.id.animal_name);
        nextView = view.findViewById(R.id.nextView);
        nextAnimalNameTextView = view.findViewById(R.id.textView2);
        nextAnimalDistanceTextView = view.findViewById(R.id.textView);

        //skip button
        skipButton = (Button)view.findViewById(R.id.skipButton);
        //set up directions and update heading (animal name)
        dir = new Directions(plan,getActivity());
        directions = dir.getDirectionsAllAnimals();
        orderedList = dir.getOrderedList();
        //display directions to first exhibit
        updateDirections();

        if(orderedList.size() < 2){
            nextView.setVisibility(View.GONE);
        } else {
            nextAnimalNameTextView.setText(vertexDao.getAnimalName(orderedList.get(mCurrentIndex + 1)));
            nextAnimalDistanceTextView.setText(dir.nextLabel(mCurrentIndex));
        }


        nextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex+1) % directions.size();
                updateDirections();
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //completely remove exhibit and replan (everything from current index onwards)
                //update orderedlist and deselect isSelected
                if (((mCurrentIndex+1) % directions.size()) != 0) {
                    Vertex vertexToChange = vertexDao.get(orderedList.get(mCurrentIndex + 1));
                    System.out.println(vertexToChange);
                    vertexToChange.isSelected = !vertexToChange.isSelected;
                    vertexDao.update(vertexToChange);
                    //replanning
                    List<String> selectedExhibits = vertexDao.getSelectedExhibitsID(Vertex.Kind.EXHIBIT);
                    Pathfinder pf = new Pathfinder(selectedExhibits, getActivity());
                    List<GraphPath<String, IdentifiedWeightedEdge>> plan = pf.plan();
                    dir = new Directions(plan, getActivity());
                    directions = dir.getDirectionsAllAnimals();
                    orderedList = dir.getOrderedList();
                }
                //mCurrentIndex = (mCurrentIndex+1) % directions.size();
                openDialog();

                updateDirections();

            }
        });
    }

    private void openDialog() {
        Prompt prompt = new Prompt();
        prompt.show(getActivity().getSupportFragmentManager(), "what is this");
    }

    //update question method
    private void updateDirections() {
        String direction = directions.get(mCurrentIndex);

        if(orderedList.size() < 2) {
            animalText.setText("");
            directionText.setText("");
        } else {
            animalText.setText("Directions to: " + vertexDao.getAnimalName(orderedList.get(mCurrentIndex)));
            directionText.setText(direction);
            if (mCurrentIndex == directions.size() - 2) {
                skipButton.setVisibility(View.GONE);
            }
            if (mCurrentIndex == directions.size() - 1) {
                nextView.setVisibility(View.GONE);
            } else {
                nextAnimalNameTextView.setText(vertexDao.getAnimalName(orderedList.get(mCurrentIndex + 1)));
                nextAnimalDistanceTextView.setText(dir.nextLabel(mCurrentIndex));
            }
        }

    }
}
