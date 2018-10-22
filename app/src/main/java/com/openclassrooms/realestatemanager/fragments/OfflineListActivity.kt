package com.openclassrooms.realestatemanager.fragments

import android.arch.persistence.room.Room
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.adapters.PropertiesListAdapter
import com.openclassrooms.realestatemanager.database.AppDatabase
import com.openclassrooms.realestatemanager.models.Property
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.util.ArrayList

/**
 * Created by Mickael Hernandez on 17/10/2018.
 */

/** Database object */
private lateinit var db : AppDatabase

/** List of workmates */
private var propertiesList = ArrayList<Property>()

private lateinit var adapter : PropertiesListAdapter

private lateinit var snackbar: Snackbar

class OfflineListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline_list)
        db = Room.databaseBuilder(this,
                AppDatabase::class.java, getString(R.string.app_name)).build()

        // Adapter
        adapter = PropertiesListAdapter(this.applicationContext, R.layout.row_property, propertiesList)

        val recyclerView = this.findViewById<RecyclerView>(R.id.recyclerView)

        //Layout manager
        val llm = LinearLayoutManager(this.applicationContext)
        llm.orientation = LinearLayoutManager.VERTICAL

        // List
        recyclerView.layoutManager = llm
        recyclerView.adapter = adapter

        snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.offline_backup, Snackbar.LENGTH_INDEFINITE)
        snackbar.show()

        // Add all database properties to the list
        runBlocking {
            propertiesList.clear()
            val dataAdd = launch {
                propertiesList.addAll(db.propertyDao().all)
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        snackbar.dismiss()
        super.onDestroy()
    }

}