package com.openclassrooms.realestatemanager.models

import android.graphics.Bitmap
import android.os.Parcelable
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by Mickaël Hernandez on 09/05/2018.
 */
@Parcelize
data class Property(
            var type: String, // Type (apartment, loft, mansion, etc...)
            var location: String, // Location identifier (City, neighbourhood...)
            var price: Double, // Price (in US Dollars)
            var surface: Double, // Surface (in square meters)
            var roomsCount: Int, // Rooms count
            var description: String, // Full description of the property
            var picturesList: ArrayList<String>, // List of pictures urls
            var status: Boolean, // Status (True is available, False is sold)
            var entryDate: Date, // Date of entry on the market
            var saleDate: Date?, // Date of sale, if sold
            var salesmanUid: String? // Salesman Firebase uid, if sold
            //var poiList: ArrayList<MarkerOptions> // List of nearby points of interest
        ) : Parcelable {
    constructor() : this("", "", .0, .0, 0, "", arrayListOf<String>(), false, Calendar.getInstance().time, null, null)
}