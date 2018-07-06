package com.openclassrooms.realestatemanager

import android.app.Application
import android.arch.lifecycle.*
import android.databinding.Bindable

/**
 * Created by Mickael Hernandez on 03/07/2018.
 */
class FiltersViewModel(application: Application) : AndroidViewModel(application) {

    // Type
    var type = MutableLiveData<String>()

    // Location
    var location = MutableLiveData<String>()


    //// Bounds

    // Price
    var price = MutableLiveData<Pair<Int, Int>>()
    var priceText : LiveData<String> = Transformations.map(price) {
        "\$" + price.value?.first + " to \$" + price.value?.second.toString()
    }

    // Surface
    var surface = MutableLiveData<Pair<Int, Int>>()
    var surfaceText : LiveData<String> = Transformations.map(surface) {
        surface.value?.first.toString() + "m² to " + surface.value?.second.toString() + "m²"
    }

    // Rooms
    var rooms = MutableLiveData<Pair<Int, Int>>()
    var roomsText : LiveData<String> = Transformations.map(rooms) {
        rooms.value?.first.toString() + " to " + rooms.value?.second.toString() + " rooms"
    }

    // Pictures
    var pictures = MutableLiveData<Pair<Int, Int>>()
    var picturesText : LiveData<String> = Transformations.map(pictures) {
        pictures.value?.first.toString() + " to " + pictures.value?.second.toString() + " pictures"
    }
}