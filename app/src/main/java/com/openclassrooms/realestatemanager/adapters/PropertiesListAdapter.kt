package com.openclassrooms.realestatemanager.adapters

import android.content.Context
import android.content.res.Configuration
import android.graphics.Typeface
import android.provider.Settings.System.getConfiguration
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.activities.MainActivity
import com.openclassrooms.realestatemanager.models.Property
import kotlinx.android.synthetic.main.row_property.view.*
import android.content.Intent.getIntent
import com.openclassrooms.realestatemanager.fragments.PropertyFragment


/**
 * Created by Mickael Hernandez on 08/02/2018.
 */

/** Custom adapter for the workmates RecyclerView */
open class PropertiesListAdapter(context: Context, resource: Int, list: ArrayList<Property>) : RecyclerView.Adapter<PropertiesListAdapter.ViewHolder>() {

    private var mContext = context
    private var mResource = resource
    private var mList = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_property, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Item (row)
        val item = mList[position]

        // Profile image
        Glide.with(mContext).load(item.picturesList[0]).into(holder.itemView.findViewById(R.id.prop_image))

        // Type
        holder.typeTextView.text = item.type

        // Location
        holder.locationTextView.text = item.location

        // Price
        holder.priceTextView.text = "$" + item.price

        // Item click listener
        holder.itemView.setOnClickListener {
            (mContext as MainActivity).displayProperty(item)
        }

    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeTextView = itemView.prop_type!!
        val locationTextView = itemView.prop_location!!
        val priceTextView = itemView.prop_price!!
    }

}