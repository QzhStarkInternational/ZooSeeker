package com.example.sandiegozooseeker;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.sandiegozooseeker.AnimalDB.DataConverter;
import com.example.sandiegozooseeker.AnimalDB.Vertex;
import com.example.sandiegozooseeker.AnimalDB.VertexDao;
import com.example.sandiegozooseeker.AnimalDB.VertexDatabase;
import com.example.sandiegozooseeker.pathfinder.Directions;
import com.example.sandiegozooseeker.pathfinder.IdentifiedWeightedEdge;
import com.example.sandiegozooseeker.pathfinder.Pathfinder;

import org.jgrapht.GraphPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SkipTest {
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
        List<Vertex> vertices = Vertex.loadJSON(context, "zoo_node_info.json");
        vertexDao = vertexDb.vertexDao();
        vertexDao.insertAll(vertices);
    }

    @Test
    public void skipButtonTest() {
        ActivityScenario<MainActivity> scenario
                = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            activity.getSupportFragmentManager().beginTransaction();
        });

        //select two exhibits
        onView(withId(R.id.vertex_items_search)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.vertex_items_search)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        onView(withId(R.id.planFragment)).perform(click());

        onView(withId(R.id.navigateFragment)).perform(click());
        List<String> selectedExhibits = vertexDao.getSelectedExhibitsID(Vertex.Kind.EXHIBIT);
        Pathfinder pf = new Pathfinder(selectedExhibits, ApplicationProvider.getApplicationContext(), "entrance_exit_gate");
        List<String> orderedList = pf.getOrderedList();
        //ignore any pop up alert dialog messages
        onView(withText("Did you take the wrong turn?")).inRoot(isDialog()).check(matches(isDisplayed())).perform(pressBack());
        //click skip button - currently directions displayed should be from entrance/exit gate to first exhibit
        //we want to skip this first exhibit and get directions to the next exhibit
        onView(withId(R.id.skipButton)).perform(click());
        //onView(withText("Replanning")).inRoot(isDialog()).check(matches(isDisplayed())).perform(pressBack());
        onView(withText("Did you take the wrong turn?")).inRoot(isDialog()).check(matches(isDisplayed())).perform(pressBack());
        onView(withText("Replanning")).inRoot(isDialog()).check(matches(isDisplayed())).perform(pressBack());
        //check that text in the next view matches what we expect
        onView(withId(R.id.textView2))
                .check(matches(withText("Entrance and Exit Gate")));

    }
}
