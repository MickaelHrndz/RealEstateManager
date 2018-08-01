package com.openclassrooms.realestatemanager.viewmodels

import android.app.Application
import android.arch.lifecycle.*
import com.openclassrooms.realestatemanager.PropertyFilter

/**
 * Created by Mickael Hernandez on 03/07/2018.
 */
class FiltersViewModel(application: Application) : AndroidViewModel(application) {
    var filter = PropertyFilter()
}