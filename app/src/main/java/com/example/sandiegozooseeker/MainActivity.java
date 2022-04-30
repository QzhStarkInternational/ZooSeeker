package com.example.sandiegozooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.nio.file.Path;

public class MainActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    private Button createPlan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //should i initialize the database here?
        //hello?? I wanted an empty database not populated with the json file
        VertexDao vertexDao = VertexDatabase.getSingleton(this).vertexDao();

        //testing purposes of displaying a static list to indicate which animals to add to database
        VertexViewModel viewModel = new ViewModelProvider(this)
                .get(VertexViewModel.class);

        VertexListAdapter adapter = new VertexListAdapter();
        adapter.setHasStableIds(true);
        adapter.setVertices(Vertex.loadJSON(this,"sample_node_info.json"));
        //associated with the wrong activity (?)
        adapter.setOnLayoutClickedHandler(viewModel::toggleClickedAddToArray);

        //launch PathActivity Page
        this.createPlan = this.findViewById(R.id.button);
        this.createPlan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //populate database
                vertexDao.clearAllRows();
                vertexDao.insertAll(viewModel.getAddedAnimals());
                //you can remove this line if you want to go back and still retain state
                viewModel.clearAddedAnimals();

                Intent intent = new Intent(MainActivity.this, PathActivity.class);
                startActivity(intent);
               //System.out.println(viewModel.getAddedAnimals());
            }
        });

        //again testing purposes for recycler view
        recyclerView = findViewById(R.id.vertex_items_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}