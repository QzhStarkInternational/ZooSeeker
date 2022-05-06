package com.example.sandiegozooseeker;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.sandiegozooseeker.AnimalDB.DataConverter;
import com.example.sandiegozooseeker.AnimalDB.Vertex;
import com.example.sandiegozooseeker.AnimalDB.VertexDao;
import com.example.sandiegozooseeker.AnimalDB.VertexDatabase;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class DatabaseAddFragmentVersionTest {
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

//    @Rule
//    public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);
//
//    @Before
//    public void init(){
//        activityActivityTestRule.getActivity()
//                .getSupportFragmentManager().beginTransaction();
//    }

    @Test
    public void databaseAddFragmentVersionTest() {
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

        onView(withId(R.id.pathFragment)).perform(click());

        List<Vertex> populatedDb = vertexDao.getSelectedExhibits(Vertex.Kind.EXHIBIT);
        assertEquals(2, populatedDb.size());
    }


    @Test
    public void databaseSearchFragmentVersionTest() {
        ActivityScenario<MainActivity> scenario
                = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            activity.getSupportFragmentManager().beginTransaction();
        });

        //should have 4 mammal exhibits
        onView(withId(R.id.textInputEditText)).perform(clearText(),typeText("mammal"));

    }
}
