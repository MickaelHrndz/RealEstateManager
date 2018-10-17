package com.openclassrooms.realestatemanager.fragments

import android.arch.persistence.room.Room
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.activities.MainActivity
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

class OfflineListFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_offline_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = Room.databaseBuilder(context!!,
                AppDatabase::class.java, getString(R.string.app_name)).build()

        // Adapter
        adapter = PropertiesListAdapter(activity!!.applicationContext, R.layout.row_property, propertiesList)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        //Layout manager
        val llm = LinearLayoutManager(activity?.applicationContext)
        llm.orientation = LinearLayoutManager.VERTICAL

        // List
        recyclerView.layoutManager = llm
        recyclerView.adapter = adapter

        snackbar = Snackbar.make(view, R.string.offline_backup, Snackbar.LENGTH_INDEFINITE)
        snackbar.show()

        // Add all database properties to the list
        runBlocking {
            val dataAdd = launch {
                propertiesList.addAll(db.propertyDao().all)
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        snackbar.dismiss()
        super.onDestroyView()
    }

}