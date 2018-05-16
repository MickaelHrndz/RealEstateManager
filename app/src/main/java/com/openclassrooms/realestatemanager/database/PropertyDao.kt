package com.openclassrooms.realestatemanager.database

import android.arch.persistence.room.*
import com.openclassrooms.realestatemanager.models.Property


/**
 * Created by Mickael Hernandez on 15/05/2018.
 */
@Dao
interface PropertyDao {

    @Query("SELECT * FROM property WHERE pid IN (:propertyIds)")
    fun loadAllByIds(propertyIds: IntArray): List<Property>

    @Query("SELECT * FROM property WHERE pid = :pid LIMIT 1")
    fun findById(pid: String): Property

    @Update
    fun update(property: Property)

    @Insert
    fun insertAll(vararg properties: Property)

    @Delete
    fun delete(property: Property)


}