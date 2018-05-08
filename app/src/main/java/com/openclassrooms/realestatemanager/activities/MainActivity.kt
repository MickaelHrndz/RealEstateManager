package com.openclassrooms.realestatemanager.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView

import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.adapters.PropertiesListAdapter
import com.openclassrooms.realestatemanager.models.Property
import java.util.*

open class MainActivity : AppCompatActivity() {

    /** List of workmates */
    private var propertiesList = ArrayList<Property>()

    /** Adapter between workmates list and ListView */
    private lateinit var mAdapter: PropertiesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAdapter = PropertiesListAdapter(applicationContext, R.layout.row_property, propertiesList)
        findViewById<RecyclerView>(R.id.RecyclerView)
    }
}
