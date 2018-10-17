package com.openclassrooms.realestatemanager.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.openclassrooms.realestatemanager.models.Property;

import java.util.List;

@Dao
public interface PropertyDao {
    @Query("SELECT * FROM property")
    List<Property> getAll();

    @Query("SELECT * FROM property WHERE pid IN (:propertyIds)")
    List<Property> loadAllByIds(int[] propertyIds);

    /*@Query("SELECT * FROM property WHERE first_name LIKE :first AND "
           + "last_name LIKE :last LIMIT 1")
    Property findByName(String first, String last);*/

    @Insert
    void insert(Property property);

    // Upsert (update or insert)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Property> propertys);

    @Delete
    void delete(Property property);

    @Query("DELETE FROM property where 1=1")
    void deleteAll();
}