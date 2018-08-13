package com.openclassrooms.realestatemanager

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.databinding.BaseObservable
import java.util.*

/**
 * Created by Mickael Hernandez on 10/07/2018.
 */
class PropertyFilter {

    // Type
    var type = MutableLiveData<String>()

    // Location
    var location = MutableLiveData<String>()

    // Availability : Id of the radio button
    private val defaultAvailability = R.id.search_radio_all
    var availability = MutableLiveData<Int>().default(defaultAvailability)



    //// Bounds

    // Price
    val minPrice = 0
    val maxPrice = 99999999
    var lowPrice = MutableLiveData<Int>().default(minPrice)
    var highPrice = MutableLiveData<Int>().default(maxPrice)

    // Surface
    val minSurface = 0
    val maxSurface = 999999
    var lowSurface = MutableLiveData<Int>().default(minSurface)
    var highSurface = MutableLiveData<Int>().default(maxSurface)

    // Rooms
    val minRooms = 0
    val maxRooms = 100
    var lowRooms = MutableLiveData<Int>().default(minRooms)
    var highRooms = MutableLiveData<Int>().default(maxRooms)

    // Pictures
    val minPictures = 0
    val maxPictures = 100
    var lowPictures = MutableLiveData<Int>().default(minPictures)
    var highPictures = MutableLiveData<Int>().default(maxPictures)

    // Date of entry on the market
    var entryDate : MutableLiveData<Date>? = MutableLiveData()

    // Date of the sale
    var saleDate : MutableLiveData<Date>? = MutableLiveData()

    fun getAllFilters(): Collection<MutableLiveData<*>?>{
        return arrayListOf<MutableLiveData<*>?>(
                type, location, availability, lowPrice, highPrice, lowSurface, highSurface, lowRooms, highRooms, lowPictures, highPictures, entryDate, saleDate)
    }

    // Handy function to set default value
    private fun <T : Any?> MutableLiveData<T>.default(initialValue: T) = apply { setValue(initialValue) }

    fun reset() {
        type.value = ""
        location.value = ""
        availability.value = defaultAvailability
        lowPrice.value = minPrice
        highPrice.value = maxPrice
        lowSurface.value = minSurface
        highSurface.value = maxSurface
        lowRooms.value = minRooms
        highRooms.value = maxRooms
        lowPictures.value = minPictures
        highPictures.value = maxPictures
        entryDate?.value = null
        saleDate?.value = null
    }
}