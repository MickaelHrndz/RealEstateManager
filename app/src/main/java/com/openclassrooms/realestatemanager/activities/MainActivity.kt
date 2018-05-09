package com.openclassrooms.realestatemanager.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.adapters.PropertiesListAdapter
import com.openclassrooms.realestatemanager.models.Property
import java.util.*

open class MainActivity : AppCompatActivity() {

    /** List of workmates */
    private var propertiesList = ArrayList<Property>()

    /** Adapter between workmates list and ListView */
    private lateinit var mAdapter: PropertiesListAdapter

    /** RecyclerView */
    private lateinit var mRecyclerView: RecyclerView

    /** Firestore instance */
    private val firestore = FirebaseFirestore.getInstance()

    private var colRef = firestore.collection("properties")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAdapter = PropertiesListAdapter(applicationContext, R.layout.row_property, propertiesList)
        val llm = LinearLayoutManager(applicationContext)
        llm.orientation = LinearLayoutManager.VERTICAL

        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView.layoutManager = llm
        mRecyclerView.adapter = mAdapter

        // Row separator
        mRecyclerView.addItemDecoration(DividerItemDecoration(mRecyclerView.context, llm.orientation))

        colRef.get().addOnCompleteListener {
            if(it.isSuccessful) {
                val res = it.result.documents
                for(doc in res){
                    val prop = doc.toObject(Property::class.java)
                    if(prop != null){
                        propertiesList.add(prop)
                        mAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}
