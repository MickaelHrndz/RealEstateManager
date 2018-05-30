package com.openclassrooms.realestatemanager.fragments

import android.graphics.Color
import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.dmallcott.dismissibleimageview.DismissibleImageView
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.activities.MainActivity
import com.openclassrooms.realestatemanager.models.Property
import kotlinx.android.synthetic.main.fragment_property.*
import java.text.DateFormat
import android.R.attr.button
import android.view.Gravity
import android.R.attr.gravity
import android.view.ViewGroup.LayoutParams.FILL_PARENT
import android.widget.LinearLayout




/**
 * Created by Mickael Hernandez on 09/05/2018.
 */
class PropertyFragment : Fragment() {

    private lateinit var dateFormat: DateFormat

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dateFormat = android.text.format.DateFormat.getDateFormat(context?.applicationContext)
        return inflater.inflate(R.layout.fragment_property, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prop = arguments?.getParcelable<Property>("property")
        if(prop != null){
            property_type.text = prop.type
            property_address.text = prop.address
            property_location.text = prop.location
            property_desc.text = prop.description
            property_surface.text = getString(R.string.square_meters, prop.surface)
            property_rooms.text = prop.roomsCount.toString()
            property_price.text = this.getString(R.string.price, prop.price)
            property_entryDate.text = dateFormat.format(prop.entryDate)

            // Status
            if(prop.status){
                property_status.text = this.getString(R.string.available)
                property_status.setTextColor(Color.parseColor("#4caf50"))
            } else {
                property_status.text = this.getString(R.string.unavailable)
                property_status.setTextColor(Color.RED)
            }

            for(url in prop.picturesList){
                val img = DismissibleImageView(context)
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 800)
                //params.weight = 1.0f
                //params.gravity = Gravity.FILL
                params.setMargins(0, 0, 0, 0)
                //img.layoutParams = params
                pictures_layout.addView(img)
                Glide.with(context!!).load(url).into(img)
            }
        }
        /*overlay.setOnClickListener {
                activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }
        card_view.setOnClickListener {  }

        fab_edit.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            (activity as MainActivity).displayEditProperty(prop!!)
        }*/
    }
    fun newInstance(prop: Property): PropertyFragment {
        val myFragment = PropertyFragment()
        val args = Bundle()
        args.putParcelable("property", prop)
        myFragment.arguments = args
        return myFragment
    }
}