package com.example.sandiegozooseeker.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sandiegozooseeker.AnimalDB.Vertex;
import com.example.sandiegozooseeker.AnimalDB.VertexDao;
import com.example.sandiegozooseeker.AnimalDB.VertexDatabase;
import com.example.sandiegozooseeker.AnimalDB.VertexViewModel;
import com.example.sandiegozooseeker.MainActivity;
import com.example.sandiegozooseeker.R;
import com.example.sandiegozooseeker.pathfinder.Directions;
import com.example.sandiegozooseeker.pathfinder.IdentifiedWeightedEdge;
import com.example.sandiegozooseeker.pathfinder.Pathfinder;

import org.jgrapht.GraphPath;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class NavigateFragment extends Fragment {
    private Button next;
    private TextView directionText;
    private TextView animalText;
    private List<String> directions;
    private List<String> orderedList;
    private VertexDao vertexDao;

    public NavigateFragment(){
        super(R.layout.navigate_fragment);
    }

    //keep track of which animal exhibit direction to display
    private int mCurrentIndex = 0;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        vertexDao = VertexDatabase.getSingleton(getActivity()).vertexDao();
        List<String> selectedExhibits = vertexDao.getSelectedExhibitsID(Vertex.Kind.EXHIBIT);
        //System.out.println(selectedExhibits);
        Pathfinder pf = new Pathfinder(selectedExhibits, getActivity());

        List<GraphPath<String, IdentifiedWeightedEdge>> plan = pf.plan();
        List<String> orderedPaths = pf.pathsToStringList(plan);
       // System.out.println(orderedPaths);

        directionText = (TextView)view.findViewById(R.id.direction_text);
        animalText = (TextView) view.findViewById(R.id.animal_name);
        next = (Button)view.findViewById(R.id.nextButton);
        next.setText("Proceed");
        next.setEnabled(true);

        //set up directions and update heading (animal name)
        Directions dir = new Directions(plan,getActivity());
        directions = dir.getDirectionsAllAnimals();
        orderedList = dir.getOrderedList();
        //display directions to first exhibit
        updateDirections();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //display all directions
                mCurrentIndex = (mCurrentIndex+1) % directions.size();
                updateDirections();
            }
        });
    }

    //update question method
    private void updateDirections() {
        String direction = directions.get(mCurrentIndex);
        animalText.setText("Directions to: " + vertexDao.getAnimalName(orderedList.get(mCurrentIndex)));
        directionText.setText(direction);
        if (mCurrentIndex == directions.size() - 1) {
            //disable next button since no more exhibits in plan
            next.setEnabled(false);
            next.setText("Done");
        }
    }
}
