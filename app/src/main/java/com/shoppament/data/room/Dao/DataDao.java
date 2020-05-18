package com.shoppament.data.room.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.shoppament.data.room.entity.DataEntity;

@Dao
public interface DataDao {

    @Query("SELECT * FROM data")
    DataEntity getData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inset(DataEntity entity);

}
