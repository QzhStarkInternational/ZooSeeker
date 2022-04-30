package com.example.sandiegozooseeker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {Vertex.class}, version = 1)
@TypeConverters({DataConverter.class})
public abstract class VertexDatabase extends RoomDatabase {
    private static VertexDatabase singleton = null;
    public abstract VertexDao vertexDao();

    public synchronized static VertexDatabase getSingleton(Context context) {
        if (singleton == null) {
            singleton = VertexDatabase.makeDatabase(context);
        }
        return singleton;
    }

    //populate the database with 'sample_node_info.json'
    //add type converter -- omg finally fixed the app failing!
    //should i create an empty database?
    private static VertexDatabase makeDatabase(Context context) {
        return Room.databaseBuilder(context, VertexDatabase.class, "vertex_animals.db")
                .allowMainThreadQueries()
                .addTypeConverter(new DataConverter())
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
//                        Executors.newSingleThreadScheduledExecutor().execute(()-> {
//                            List<Vertex> vertex = Vertex
//                                    .loadJSON(context, "sample_node_info.json");
//                            getSingleton(context).vertexDao().insertAll(vertex);
//                        });
                    }
                })
                .build();
    }
}
