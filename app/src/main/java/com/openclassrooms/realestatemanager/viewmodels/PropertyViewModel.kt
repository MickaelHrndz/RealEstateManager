package com.openclassrooms.realestatemanager.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.models.Property

/**
 * Created by Mickael Hernandez on 01/08/2018.
 */
class PropertyViewModel : ViewModel() {
    var property = MutableLiveData<Property>()
}