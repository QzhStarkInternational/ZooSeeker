package com.example.sandiegozooseeker.AnimalDB;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

@Entity(tableName = "animal_vertex")
public class Vertex {
    public enum Kind {
        @SerializedName("gate") GATE,
        @SerializedName("exhibit") EXHIBIT,
        @SerializedName("intersection") INTERSECTION
    }

    //contains a unique id
    //1. Public fields
    @NonNull
    @PrimaryKey
    public String id;
    public Kind kind;
    public String name;
    public List<String> tags;
    public boolean isSelected;

    //2. Constructor matching fields above
    public Vertex(String id, Kind kind, String name, List<String> tags) {
        this.id = id;
        this.kind = kind;
        this.name = name;
        this.tags = tags;
        this.isSelected = false;
    }

    public static List<Vertex> loadJSON(Context context, String path) {
        try {
            InputStream input = context.getAssets().open(path);
            Reader reader = new InputStreamReader(input);
            Gson gson = new Gson();
            Type type = new TypeToken<List<Vertex>>(){}.getType();
            return gson.fromJson(reader,type);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }



    @NonNull
    @Override
    public String toString() {
        return "Vertex{" +
                "id='" + id + '\'' +
                ", kind=" + kind +
                ", name='" + name + '\'' +
                ", tags=" + tags +
                ", isSelected=" + isSelected +
                '}';
    }
}
