package com.example.sandiegozooseeker.utils;

import android.content.Context;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

import com.example.sandiegozooseeker.graph.GraphEdge;
import com.example.sandiegozooseeker.graph.GraphVertex;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JSONLoader {
    public static List<GraphVertex> loadVertices(Context context){
        try{
            InputStream inputStream = context.getAssets().open(Constants.VERTEX_JSON_PATH);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            Type type = new TypeToken<List<GraphVertex>>(){}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static List<GraphEdge> loadEdges(Context context){
        try{
            InputStream inputStream = context.getAssets().open(Constants.EDGE_JSON_PATH);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            Type type = new TypeToken<List<GraphEdge>>(){}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}