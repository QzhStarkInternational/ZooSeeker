package com.example.sandiegozooseeker;

import androidx.room.ProvidedTypeConverter;
import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

@ProvidedTypeConverter
public class DataConverter {
    //list to json
    @TypeConverter
    public String tags(List<String> tag) {
        if (tag == null) return null;
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        String json = gson.toJson(tag,type);
        return json;
    }
    //json to list
    @TypeConverter
    public List<String> toTagList(String tags) {
        if (tags == null) return null;
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> tagList = gson.fromJson(tags,type);
        return tagList;
    }
}
