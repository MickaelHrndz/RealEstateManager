package com.openclassrooms.realestatemanager.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by Mickaël Hernandez on 09/05/2018.
 */
@Parcelize
@Entity
data class Property(
        @PrimaryKey var pid: String, // Property ID generated randomly
        var type: String, // Type (apartment, loft, mansion, etc...)
        var location: String, // Location identifier (City, neighbourhood...)
        var price: Int, // Price (in US Dollars)
        var surface: Int, // Surface (in square meters)
        var roomsCount: Int, // Rooms count
        var description: String, // Full description of the property
        @Ignore var pictures: List<String>, // List of pictures urls
        var status: Boolean, // Status (True is available, False is sold)
        var entryDate: Date, // Date of entry on the market
        var saleDate: Date, // Date of sale, if sold
        var salesmanUid: String // Salesman Firebase uid, if sold
            //var poiList: ArrayList<MarkerOptions> // List of nearby points of interest
        ) : Parcelable {
    constructor() : this("", "", "", 0, 0, 0, "", arrayListOf<String>(), false, Calendar.getInstance().time, Date(), "")
}