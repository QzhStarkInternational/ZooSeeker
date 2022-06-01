package com.example.sandiegozooseeker.graph;

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
import java.util.Objects;

@Entity(tableName = "animal_vertex")
public class GraphVertex {
    public enum Kind {
        @SerializedName("gate") GATE,
        @SerializedName("exhibit") EXHIBIT,
        @SerializedName("intersection") INTERSECTION,
        @SerializedName("exhibit_group") EXHIBIT_GROUP
    }

    @NonNull
    @PrimaryKey
    public String id;
    public String group_id;
    public Kind kind;
    public String name;
    public List<String> tags;
    public boolean isSelected;
    public double lat;
    public double lng;

    //2. Constructor matching fields above
    public GraphVertex(String id, String group_id, Kind kind, String name, List<String> tags, double lat, double lng) {
        this.id = id;
        this.group_id = group_id;
        this.kind = kind;
        this.name = name;
        this.tags = tags;
        this.isSelected = false;
        this.lat = lat;
        this.lng = lng;
    }

    public void setId(String id){ this.id = id; }
    public void setGroup_id(String groupId) { this.group_id = groupId; }
    public void setName(String name) { this.name = name; }
    public void setKind(Kind kind) { this.kind = kind; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public void setLat(double lat) { this.lat = lat; }
    public void setLng(double lng) { this.lng = lng; }
    public void setSelected(boolean isSelected) { this.isSelected = isSelected; }

    public String getId() {
        return id;
    }
    public String getGroup_id() { return group_id; }
    public String getName() {
        return this.name;
    }
    public Kind getKind(){return this.kind;}
    public List<String> getTags(){return this.tags;}
    public double getLat() { return this.lat; }
    public double getLng() { return this.lng; }
    public boolean getIsSelected(){ return this.isSelected; }

    public int compareTo(GraphVertex graphVertex){
        if(Objects.equals(this.id, graphVertex.id)){
            return 0;
        }

        return -1;
    }

    @NonNull
    @Override
    public String toString() {
        return "Vertex{" +
                "id='" + id + '\'' +
                ", group_id='" + group_id + '\'' +
                ", kind=" + kind +
                ", name='" + name + '\'' +
                ", tags=" + tags +
                ", isSelected=" + isSelected +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
