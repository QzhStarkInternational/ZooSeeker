package com.example.sandiegozooseeker;



import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.LargeTest;
//import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.sandiegozooseeker.AnimalDB.DataConverter;
import com.example.sandiegozooseeker.AnimalDB.VertexDao;
import com.example.sandiegozooseeker.AnimalDB.VertexDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class DatabaseAddTest {
    VertexDatabase vertexDb;
    VertexDao vertexDao;

    private static void forceLayout(RecyclerView recyclerView) {
        recyclerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        recyclerView.layout(0,0,1080,2288);
    }

    @Before
    public void resetDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        vertexDb = Room.inMemoryDatabaseBuilder(context, VertexDatabase.class)
                .allowMainThreadQueries()
                .addTypeConverter(new DataConverter())
                .build();
        VertexDatabase.injectTestDatabase(vertexDb);
        vertexDao = vertexDb.vertexDao();
    }

    @Test
    public void databaseAddTest() {
//        ActivityScenario<MainActivity> scenario
//                = ActivityScenario.launch(MainActivity.class);
//        scenario.moveToState(Lifecycle.State.CREATED);
//        scenario.moveToState(Lifecycle.State.STARTED);
//        scenario.moveToState(Lifecycle.State.RESUMED);
//
//        scenario.onActivity(activity -> {
//            RecyclerView recyclerView = activity.recyclerView;
//            RecyclerView.ViewHolder firstVH = recyclerView.findViewHolderForAdapterPosition(0);
//
//            ConstraintLayout layout1 = firstVH.itemView.findViewById(R.id.add_animal_layout);
//            layout1.performClick();
//            layout1.performClick();
//
//            RecyclerView.ViewHolder secondVH = recyclerView.findViewHolderForAdapterPosition(1);
//
//            ConstraintLayout layout2 =secondVH.itemView.findViewById(R.id.add_animal_layout);
//            layout2.performClick();
//            layout2.performClick();
//
//            //Button planButton = activity.findViewById(R.id.button);
//            //click plan button
//            planButton.performClick();
//
//            List<Vertex> populatedDb = vertexDao.getAll();
//            assertEquals(2,populatedDb.size());
//        });
//    }
    }
}