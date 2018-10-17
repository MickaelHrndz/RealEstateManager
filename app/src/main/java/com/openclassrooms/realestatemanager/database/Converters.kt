package com.openclassrooms.realestatemanager.database

import android.arch.persistence.room.TypeConverter
import com.google.firebase.firestore.GeoPoint
import com.google.gson.Gson
import java.util.*


class Converters {
    // List
    @TypeConverter
    fun listToJson(value: List<String>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String): List<String>? {
        val objects = Gson().fromJson(value, Array<String>::class.java) as Array<String>
        return objects.toList()
    }

    // Date
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // GeoPoint
    @TypeConverter
    fun stringToGeoPoint(string: String): GeoPoint {
        val lat = string.substring(string.indexOfFirst { it == "="[0] }+1, string.indexOfLast { it == ","[0] }).toDouble()
        val lng = string.substring(string.indexOfLast { it == "="[0] }+1, string.indexOfLast { it == "}"[0] }-1).toDouble()
        return GeoPoint(lat, lng)
    }

    @TypeConverter
    fun geoPointToString(geoPoint: GeoPoint): String {
        return geoPoint.toString()
    }
}