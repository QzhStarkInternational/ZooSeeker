package com.example.sandiegozooseeker;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
//dao stands for data access object and represents the ways in which we may interact with entities
@Dao
public interface VertexDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insert(Vertex vertex);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Vertex> vertex);

    @Query("SELECT * FROM `animal_vertex` WHERE `id`=:id")
    Vertex get(String id);

    @Query("SELECT * FROM `animal_vertex`")
    List<Vertex> getAll();

    @Query("SELECT * FROM `animal_vertex` WHERE `isSelected` AND `kind`=:kind")
    List<Vertex> getSelectedExhibits(Vertex.Kind kind);

    @Query("SELECT * FROM `animal_vertex` WHERE `kind`=:kind")
    List<Vertex> getAllOfKind(Vertex.Kind kind);

    @Query("SELECT * FROM `animal_vertex` WHERE `isSelected` AND `kind`=:kind")
    LiveData<List<Vertex>> getSelectedOfKindLive(Vertex.Kind kind);

    @Query("SELECT * FROM `animal_vertex` WHERE `kind`=:kind")
    LiveData<List<Vertex>> getAllOfKindLive(Vertex.Kind kind);

    @Update
    int update(Vertex vertex);

    @Delete
    int delete(Vertex vertex);
}
