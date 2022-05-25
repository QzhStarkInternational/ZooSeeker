package com.example.sandiegozooseeker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.sandiegozooseeker.AnimalDB.DataConverter;
import com.example.sandiegozooseeker.AnimalDB.Vertex;
import com.example.sandiegozooseeker.AnimalDB.VertexDao;
import com.example.sandiegozooseeker.AnimalDB.VertexDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class VertexDatabaseTest {
    private VertexDao dao;
    private VertexDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, VertexDatabase.class)
                .addTypeConverter(new DataConverter())
                .allowMainThreadQueries()
                .build();
        dao = db.vertexDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    //insert for testing purposes only
    @Test
    public void testInsert() {
        List<String> tag1 = new ArrayList<>(Arrays.asList("rabbit","mammal"));
        List<String> tag2 = new ArrayList<>(Arrays.asList("wolves","mammal"));
        Vertex v1 = new Vertex("rabbit habitat", Vertex.Kind.EXHIBIT,"Rabbits In the Wild", tag1, 31.2, 32.2);
        Vertex v2 = new Vertex("wolf habitat", Vertex.Kind.EXHIBIT,"Wolves", tag2, 30.0, 35.2);

        dao.insert(v1);
        dao.insert(v2);
        Vertex test1 = dao.get("rabbit habitat");
        Vertex test2 = dao.get("wolf habitat");
        //check inserted with unique IDs (string)
        assertNotEquals(test1.name,test2.name);
    }
    @Test
    public void testGet() {
        List<String> tags = new ArrayList<>(Arrays.asList("rabbit","mammal"));
        Vertex insertedAnimal = new Vertex("rabbit habitat", Vertex.Kind.EXHIBIT,"Rabbits In the Wild", tags, 31.2, 32.2);
        dao.insert(insertedAnimal);

        Vertex vertex = dao.get("rabbit habitat");
        //not auto-generated ID
        assertEquals(insertedAnimal.id, vertex.id);
        assertEquals(insertedAnimal.name,vertex.name);
        assertEquals(insertedAnimal.kind,vertex.kind);
        assertEquals(insertedAnimal.tags,vertex.tags);
    }
    @Test
    public void testDelete() {
        List<String> tags = new ArrayList<>(Arrays.asList("rabbit","mammal"));
        Vertex vertex = new Vertex("rabbit habitat", Vertex.Kind.EXHIBIT,"Rabbits In the Wild", tags, 31.2, 32.2);
        dao.insert(vertex);

        vertex = dao.get("rabbit habitat");
        int numberDeleted = dao.delete(vertex);
        assertEquals(1,numberDeleted);
        assertNull(dao.get("rabbit habitat"));
    }
}
