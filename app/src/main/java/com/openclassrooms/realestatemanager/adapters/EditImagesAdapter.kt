package com.openclassrooms.realestatemanager.adapters

import android.content.Context
import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.cielyang.android.clearableedittext.ClearableEditText
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.activities.MainActivity
import com.openclassrooms.realestatemanager.fragments.EditPropertyFragment
import kotlinx.android.synthetic.main.row_edit_image.view.*
import kotlin.concurrent.thread


/**
 * Created by Mickael Hernandez on 08/02/2018.
 */

/** Custom adapter for the workmates RecyclerView */
open class EditImagesAdapter(context: Context, resource: Int, list: ArrayList<String>) : RecyclerView.Adapter<EditImagesAdapter.ViewHolder>() {

    private var mContext = context
    private var mResource = resource
    private var mList = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(mResource, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Item (row)
        val url = mList[position]

        // Property image
        Glide.with(mContext).load(url).into(holder.imageView)

        // Property url
        holder.editUrlView.setText(url)

        holder.editUrlView.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mList[position] = text.toString()
            }

        })
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.row_edit_image!!
        val editUrlView = itemView.row_edit_url!!
    }

}