package com.example.sandiegozooseeker;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.filters.LargeTest;
import androidx.test.runner.AndroidJUnit4;

import com.example.sandiegozooseeker.AnimalDB.DataConverter;
import com.example.sandiegozooseeker.AnimalDB.Vertex;
import com.example.sandiegozooseeker.AnimalDB.VertexDao;
import com.example.sandiegozooseeker.AnimalDB.VertexDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class DatabaseRemoveFragmentTest {
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
        List<Vertex> vertices = Vertex.loadJSON(context, "sample_node_info.json");
        vertexDao = vertexDb.vertexDao();
        vertexDao.insertAll(vertices);
    }

    @Test
    public void databaseRemoveFragmentVersion() {
        ActivityScenario<MainActivity> scenario
                = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            activity.getSupportFragmentManager().beginTransaction();
        });

        onView(withId(R.id.vertex_items_search)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.vertex_items_search)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.vertex_items_search)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));

        onView(withId(R.id.pathFragment)).perform(click());
        onView(withId(R.id.edit_but)).perform(click());
        onView(withId(R.id.vertex_items_plan)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.vertex_items_plan)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        List<Vertex> populatedDb = vertexDao.getSelectedExhibits(Vertex.Kind.EXHIBIT);
        assertEquals(1, populatedDb.size());
    }
}
