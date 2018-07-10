package com.openclassrooms.realestatemanager

import android.app.Application
import android.arch.lifecycle.*
import android.databinding.Bindable

/**
 * Created by Mickael Hernandez on 03/07/2018.
 */
class FiltersViewModel(application: Application) : AndroidViewModel(application) {
    var filter = PropertyFilter()
}