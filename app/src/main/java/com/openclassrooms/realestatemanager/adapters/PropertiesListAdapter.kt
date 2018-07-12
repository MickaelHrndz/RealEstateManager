package com.openclassrooms.realestatemanager.adapters

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.FiltersViewModel
import com.openclassrooms.realestatemanager.PropertyFilter
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.activities.MainActivity
import com.openclassrooms.realestatemanager.fragments.PropertyFragment
import com.openclassrooms.realestatemanager.models.Property
import kotlinx.android.synthetic.main.row_property.view.*
import android.app.LauncherActivity.ListItem




/**
 * Created by Mickael Hernandez on 08/02/2018.
 */

/** Custom adapter for the workmates RecyclerView */
open class PropertiesListAdapter(context: Context, resource: Int, list: ArrayList<Property>) : RecyclerView.Adapter<PropertiesListAdapter.ViewHolder>() {

    private var mContext = context
    private var mResource = resource
    private var mList = list
    private var mFilteredList = list


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_property, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mFilteredList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Item (row)
        val item = mFilteredList[position]

        // Property image
        if(item.picturesList.isNotEmpty()){
            Glide.with(mContext).load(item.picturesList[0]).into(holder.itemView.findViewById(R.id.prop_image))
        }

        // Type
        holder.typeTextView.text = item.type

        // Location
        holder.locationTextView.text = item.location

        // Status
        if(item.status){
            holder.statusTextView.text = mContext.getString(R.string.available)
            holder.statusTextView.setTextColor(Color.parseColor("#4caf50"))
        } else {
            holder.statusTextView.text = mContext.getString(R.string.unavailable)
            holder.statusTextView.setTextColor(Color.RED)
        }
        
        // Price
        holder.priceTextView.text = mContext.getString(R.string.price_tag, item.price)
        
        // Item click listener
        holder.itemView.setOnClickListener {
            (mContext as MainActivity).displayFragment(PropertyFragment.newInstance(item))
        }

    }

    /** Sets the list to a filtered list based on the filter and original list provided */
    fun filter(originalList: ArrayList<Property>, filter: PropertyFilter){
        // Resetting the filtered list as a copy of the original
        mFilteredList.clear()
        mFilteredList.addAll(originalList)

        // Type filter
        if(filter.type.value != null && filter.type.value != ""){
            mFilteredList.removeAll(mFilteredList.filter { !it.type.contains(filter.type.value!!, true)})
        }

        // Location filter
        if(filter.location.value != null && filter.location.value != ""){
            mFilteredList.removeAll(mFilteredList.filter { !it.location.contains(filter.location.value!!, true)})
        }

        // Price filter
        mFilteredList.removeAll(mFilteredList.filter { it.price < filter.price.value!!.first && it.price > filter.price.value!!.second })

        notifyDataSetChanged()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeTextView = itemView.prop_type!!
        val locationTextView = itemView.prop_location!!
        val priceTextView = itemView.prop_price!!
        val statusTextView = itemView.prop_status!!
    }

}