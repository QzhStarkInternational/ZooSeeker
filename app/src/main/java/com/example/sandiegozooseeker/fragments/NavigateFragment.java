package com.example.sandiegozooseeker.fragments;

import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

import kotlin.collections.ArrayDeque;

public class NavigateFragment extends Fragment {
    private CardView nextView;
    private CardView previousView;
    private TextView nextAnimalNameTextView;
    private TextView nextAnimalDistanceTextView;
    private TextView directionText;
    private TextView animalText;
    private List<String> directions;
    private List<String> orderedList;
    private List<String> remainingExhibits;
    private VertexDao vertexDao;
    private Button skipButton;
    private String start;
    Directions dir;

    public NavigateFragment(){
        super(R.layout.fragment_navigate);
    }

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

        start = "entrance_exit_gate";
        directionText = (TextView)view.findViewById(R.id.direction_text);
        animalText = (TextView) view.findViewById(R.id.animal_name);
        nextView = view.findViewById(R.id.nextView);
        previousView = view.findViewById(R.id.previousView);
        previousView.setVisibility(View.GONE);
        nextAnimalNameTextView = view.findViewById(R.id.textView2);
        nextAnimalDistanceTextView = view.findViewById(R.id.textView);

        //skip button
        skipButton = (Button)view.findViewById(R.id.skipButton);
        //set up directions and update heading (animal name)
        dir = new Directions(plan,getActivity());
        directions = dir.getDirectionsAllAnimals();
        orderedList = dir.getOrderedList();
        remainingExhibits = new ArrayList<String>(orderedList);
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
                start = remainingExhibits.get(0);
                remainingExhibits.remove(0);
                //Log.d("REMAINING_EXHIBITS", "onClick: " + remainingExhibits);
                mCurrentIndex = (mCurrentIndex+1) % directions.size();
                updateDirections();
            }
        });

        previousView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                directions.add(mCurrentIndex, dir.getPrevious(mCurrentIndex));
                orderedList.add(mCurrentIndex, orderedList.get(mCurrentIndex -1));
                updateDirections();
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //completely remove exhibit and replan (everything from current index onwards)
                //update orderedlist and deselect isSelected
                if (((mCurrentIndex+1) % directions.size()) != 0) {
                    Vertex vertexToChange = vertexDao.get(orderedList.get(mCurrentIndex));
                    System.out.println(vertexToChange);
                    vertexToChange.isSelected = !vertexToChange.isSelected;
                    vertexDao.update(vertexToChange);
                    remainingExhibits.remove(0);
                    //replanning
                    //List<String> selectedExhibits = vertexDao.getSelectedExhibitsID(Vertex.Kind.EXHIBIT);
                    Log.d("ORDERED_LIST_CURRENT", "onClick: " + start);
                    Log.d("REMAINING_EXHIBITS", "onClick: " + remainingExhibits);
                    remainingExhibits.remove(remainingExhibits.size()-1);
                    Pathfinder pf = new Pathfinder(remainingExhibits, getActivity(), start);
                    List<GraphPath<String, IdentifiedWeightedEdge>> plan = pf.plan();
                    dir = new Directions(plan, getActivity());
                    directions = dir.getDirectionsAllAnimals();
                    orderedList = dir.getOrderedList();
                    remainingExhibits = new ArrayList<String>(orderedList);
                    mCurrentIndex = 0;
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

        if(orderedList.size() < 1) {
            animalText.setText("");
            directionText.setText("");
            skipButton.setVisibility(View.GONE);
            nextView.setVisibility(View.GONE);
        } else {
            animalText.setText("Directions from " + vertexDao.getAnimalName(start) + " to: " + vertexDao.getAnimalName(orderedList.get(mCurrentIndex)));
            directionText.setText(direction);
            if (mCurrentIndex == directions.size() - 1) {
                skipButton.setVisibility(View.GONE);
            }
            if (mCurrentIndex != 0) {
                previousView.setVisibility(View.VISIBLE);
            }
            if (mCurrentIndex == directions.size()-1) {
                nextView.setVisibility(View.GONE);
            } else {
                nextAnimalNameTextView.setText(vertexDao.getAnimalName(orderedList.get(mCurrentIndex + 1)));
                nextAnimalDistanceTextView.setText(dir.nextLabel(mCurrentIndex));
            }
        }

    }
}
