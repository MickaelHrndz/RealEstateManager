package com.openclassrooms.realestatemanager.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
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
        // Property url
        holder.editUrlView.setText(mList[position])
        holder.updateUI(mList[position])
        holder.editUrlView.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
                holder.updateUI(str.toString())
                mList[holder.adapterPosition] = str.toString()
            }

        })
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.row_edit_image!!
        val editUrlView = itemView.row_edit_url!!
        private var imagesValid = true

        fun updateUI(url: String){
            // Property image
            Glide.with(itemView.context).load(url).listener(object : RequestListener<Drawable> {
                override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    imagesValid = true
                    return false
                }
                override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    if(url != ""){ imagesValid = false }
                    return true
                }
            }).into(imageView)
        }
    }

}