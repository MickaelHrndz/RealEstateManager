package com.openclassrooms.realestatemanager

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.Bindable

/**
 * Created by Mickael Hernandez on 03/07/2018.
 */
class FiltersViewModel : ViewModel() {

    // Type
    var type = MutableLiveData<String>()

    // Location
    var location = MutableLiveData<String>()


    //// Bounds

    // Price
    var minPrice = MutableLiveData<Int>()
    var maxPrice = MutableLiveData<Int>()
    fun setPriceBounds(min: Int, max: Int){
        minPrice.value = min
        maxPrice.value = max
    }

    // Surface
    var minSurface = MutableLiveData<Int>()
    var maxSurface = MutableLiveData<Int>()

    // Rooms
    var minRooms = MutableLiveData<Int>()
    var maxRooms = MutableLiveData<Int>()

    // Photos
    var minPhotos = MutableLiveData<Int>()
    var maxPhotos = MutableLiveData<Int>()
}