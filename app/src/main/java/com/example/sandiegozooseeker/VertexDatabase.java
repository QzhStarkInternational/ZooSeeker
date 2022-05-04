package com.example.sandiegozooseeker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
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

    private static VertexDatabase makeDatabase(Context context) {
        return Room.databaseBuilder(context, VertexDatabase.class, "vertex_animals.db")
                .allowMainThreadQueries()
                .addTypeConverter(new DataConverter())
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                    }
                })
                .build();
    }

    @VisibleForTesting
    public static void injectTestDatabase(VertexDatabase vertexDatabase) {
        if (singleton != null) {
            singleton.close();
        }
        singleton = vertexDatabase;
    }
}
