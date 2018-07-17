package com.openclassrooms.realestatemanager.models

import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*

/**
 * Created by Mickaël Hernandez on 09/05/2018.
 */
@Parcelize
data class Property(
        var pid: String, // Property ID generated randomly
        var type: String, // Type (apartment, loft, mansion, etc...)
        var location: String, // Location identifier (City, neighbourhood...)
        var geopoint: @RawValue GeoPoint,
        var address: String, // Full address of the property
        var price: Int, // Price (in US Dollars)
        var surface: Int, // Surface (in square meters)
        var roomsCount: Int, // Rooms count
        var description: String, // Full description of the property
        var picturesList: List<String>, // List of pictures urls
        var status: Boolean, // Status (True is available, False is sold)
        var entryDate: Date, // Date of entry on the market
        var saleDate: Date, // Date of sale, if sold
        var agent: String // Full name of the real estate agent in charge of this property
            //var poiList: ArrayList<MarkerOptions> // List of nearby points of interest
        ) : Parcelable {
    constructor() : this("", "", "", GeoPoint(.0, .0),"", 0, 0, 0, "", arrayListOf<String>(), false, Calendar.getInstance().time, Date(), "")
}