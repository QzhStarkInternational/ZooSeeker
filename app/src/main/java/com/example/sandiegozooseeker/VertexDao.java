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

    @Query("SELECT * FROM `animal_vertex` WHERE `isSelected`=:isSelected")
    List<Vertex> getSelectedAll(Boolean isSelected);

    @Query("SELECT * FROM `animal_vertex` WHERE `isSelected`")
    LiveData<List<Vertex>> getSelectedLive();

    @Query("SELECT * FROM `animal_vertex` ORDER BY `kind`")
    LiveData<List<Vertex>> getAllLive();

    @Update
    int update(Vertex vertex);

    @Delete
    int delete(Vertex vertex);

    @Query("DELETE FROM animal_vertex")
    void clearAllRows();
}
