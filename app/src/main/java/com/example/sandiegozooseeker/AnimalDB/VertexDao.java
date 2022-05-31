package com.example.sandiegozooseeker.AnimalDB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.sandiegozooseeker.graph.GraphVertex;

import java.util.List;

@Dao
public interface VertexDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    public void insert(GraphVertex graphVertex);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    public void insertAll(List<GraphVertex> pathVertices);

    @Query("SELECT * FROM `animal_vertex` WHERE `id`=:id")
    public GraphVertex get(String id);

    @Query("SELECT * FROM `animal_vertex` WHERE `isSelected` AND `kind`=:kind")
    public List<GraphVertex> getSelectedExhibits(GraphVertex.Kind kind);

    @Query("SELECT * FROM `animal_vertex` WHERE `isSelected` AND `kind`=:kind")
    public LiveData<List<GraphVertex>> getSelectedOfKindLive(GraphVertex.Kind kind);

    @Query("SELECT * FROM `animal_vertex` WHERE `kind`=:kind")
    public LiveData<List<GraphVertex>> getAllOfKindLive(GraphVertex.Kind kind);

    @Query("SELECT id FROM `animal_vertex` WHERE `isSelected` AND `kind`=:kind")
    public List<String> getSelectedExhibitsID(GraphVertex.Kind kind);

    @Query("SELECT COUNT(ID) FROM `animal_vertex` WHERE `isSelected` AND `kind`=:kind")
    public LiveData<Integer> getSelectedExhibitsCount(GraphVertex.Kind kind);

    @Query("SELECT name FROM `animal_vertex` WHERE `id`=:id")
    public String getAnimalName(String id);

    @Update
    public void update(GraphVertex graphVertex);

    @Delete
    public int delete(GraphVertex graphVertex);
}
