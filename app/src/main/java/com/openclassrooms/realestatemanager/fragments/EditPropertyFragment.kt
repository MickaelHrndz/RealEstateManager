package com.openclassrooms.realestatemanager.fragments

import android.graphics.Color
import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.R.id.editprop_entryDate
import com.openclassrooms.realestatemanager.R.id.editprop_price
import com.openclassrooms.realestatemanager.activities.MainActivity
import com.openclassrooms.realestatemanager.models.Property
import kotlinx.android.synthetic.main.fragment_editproperty.*
import kotlinx.android.synthetic.main.fragment_property.*
import java.text.DateFormat


/**
 * Created by Mickael Hernandez on 17/05/2018.
 */
class EditPropertyFragment : Fragment() {

    private lateinit var dateFormat: DateFormat

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dateFormat = android.text.format.DateFormat.getDateFormat(context?.applicationContext)
        return inflater.inflate(R.layout.fragment_editproperty, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prop = arguments?.getParcelable<Property>("property")
        if(prop != null){
            editprop_type.text = prop.type
            editprop_location.text = prop.location
            editprop_desc.setText(prop.description)
            editprop_surface.setText(getString(R.string.square_meters, prop.surface))
            editprop_rooms.setText(prop.roomsCount.toString())
            editprop_price.text = this.getString(R.string.price, prop.price)
            editprop_entryDate.setText(dateFormat.format(prop.entryDate))

            // Status
            if(prop.status){
                editprop_status.text = this.getString(R.string.available)
                editprop_status.setTextColor(Color.parseColor("#4caf50"))
            } else {
                editprop_status.text = this.getString(R.string.unavailable)
                editprop_status.setTextColor(Color.RED)
            }

            for(url in prop.picturesList){
                val editText = EditText(context)
                editText.setText(url)
                editpictures_layout.addView(editText)
            }
        }
        editoverlay.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }
        card_view_edit.setOnClickListener {  }

        // If user cancels the edit
        prop_cancel.setOnClickListener {
            (activity as MainActivity).displayProperty(prop!!)
        }

        // If user validates his edits
        prop_done.setOnClickListener {
            // TODO : Update Firestore data
            (activity as MainActivity).displayProperty(prop!!)
        }
    }
    fun newInstance(prop: Property): EditPropertyFragment {
        val myFragment = EditPropertyFragment()
        val args = Bundle()
        args.putParcelable("property", prop)
        myFragment.arguments = args
        return myFragment
    }
}