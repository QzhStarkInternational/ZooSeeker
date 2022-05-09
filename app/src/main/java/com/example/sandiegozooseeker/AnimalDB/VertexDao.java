package com.example.sandiegozooseeker.AnimalDB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VertexDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    public void insert(Vertex vertex);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    public void insertAll(List<Vertex> vertex);

    @Query("SELECT * FROM `animal_vertex` WHERE `id`=:id")
    public Vertex get(String id);

    @Query("SELECT * FROM `animal_vertex` WHERE `isSelected` AND `kind`=:kind")
    public List<Vertex> getSelectedExhibits(Vertex.Kind kind);

    @Query("SELECT * FROM `animal_vertex` WHERE `isSelected` AND `kind`=:kind")
    public LiveData<List<Vertex>> getSelectedOfKindLive(Vertex.Kind kind);

    @Query("SELECT * FROM `animal_vertex` WHERE `kind`=:kind")
    public LiveData<List<Vertex>> getAllOfKindLive(Vertex.Kind kind);

    @Query("SELECT id FROM `animal_vertex` WHERE `isSelected` AND `kind`=:kind")
    public List<String> getSelectedExhibitsID(Vertex.Kind kind);

    @Query("SELECT COUNT(ID) FROM `animal_vertex` WHERE `isSelected` AND `kind`=:kind")
    public LiveData<Integer> getSelectedExhibitsCount(Vertex.Kind kind);

    @Query("SELECT name FROM `animal_vertex` WHERE `id`=:id")
    public String getAnimalName(String id);

    @Update
    public void update(Vertex vertex);

    @Delete
    public int delete(Vertex vertex);
}
