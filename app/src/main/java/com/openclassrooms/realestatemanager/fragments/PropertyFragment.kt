package com.openclassrooms.realestatemanager.fragments

import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Property
import kotlinx.android.synthetic.main.fragment_property.*


/**
 * Created by Mickael Hernandez on 09/05/2018.
 */
class PropertyFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_property, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prop = arguments?.getParcelable<Property>("property")
        if(prop != null){
            property_desc.text = prop.description
            property_location.text = prop.location
            property_surface.text = getString(R.string.square_meters, prop.surface)
            property_type.text = prop.type
        }
    }
    fun newInstance(prop: Property): PropertyFragment {
        val myFragment = PropertyFragment()
        val args = Bundle()
        args.putParcelable("property", prop)
        myFragment.arguments = args
        return myFragment
    }
}