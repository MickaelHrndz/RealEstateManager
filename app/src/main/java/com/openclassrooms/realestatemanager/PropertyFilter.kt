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
    var price = MutableLiveData<Pair<Int, Int>>()
    val minPrice = 0
    val maxPrice = 99999999
    var priceText : LiveData<String> = Transformations.map(price) {
        "\$" + price.value?.first + " to \$" + price.value?.second.toString()
    }

    // Surface
    var surface = MutableLiveData<Pair<Int, Int>>()
    val minSurface = 0
    val maxSurface = 999999
    var surfaceText : LiveData<String> = Transformations.map(surface) {
        surface.value?.first.toString() + "m² to " + surface.value?.second.toString() + "m²"
    }

    // Rooms
    var rooms = MutableLiveData<Pair<Int, Int>>()
    var minRooms = 0
    var maxRooms = 100
    var roomsText : LiveData<String> = Transformations.map(rooms) {
        rooms.value?.first.toString() + " to " + rooms.value?.second.toString() + " rooms"
    }

    // Pictures
    var pictures = MutableLiveData<Pair<Int, Int>>()
    var minPictures = 0
    var maxPictures = 100
    var picturesText : LiveData<String> = Transformations.map(pictures) {
        pictures.value?.first.toString() + " to " + pictures.value?.second.toString() + " pictures"
    }

    // Date of entry on the market
    var entryDate : MutableLiveData<Date>? = MutableLiveData()

    // Date of the sale
    var saleDate : MutableLiveData<Date>? = MutableLiveData()

    fun getAllFilters(): Collection<MutableLiveData<*>?>{
        return arrayListOf<MutableLiveData<*>?>(type, location, availability, price, surface, rooms, pictures, entryDate, saleDate)
    }

    // Handy function to set default value
    private fun <T : Any?> MutableLiveData<T>.default(initialValue: T) = apply { setValue(initialValue) }

    fun reset() {
        type.value = ""
        location.value = ""
        availability.value = defaultAvailability
        price.value = Pair(minPrice, maxPrice)
        surface.value = Pair(minSurface, maxSurface)
        rooms.value = Pair(minRooms, maxRooms)
        pictures.value = Pair(minPictures, maxPictures)
        entryDate?.value = null
        saleDate?.value = null
    }
}