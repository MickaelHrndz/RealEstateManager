package com.openclassrooms.realestatemanager.fragments

import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.dmallcott.dismissibleimageview.DismissibleImageView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.activities.MainActivity
import com.openclassrooms.realestatemanager.models.Property
import kotlinx.android.synthetic.main.fragment_property.*
import java.io.IOException
import java.text.DateFormat


/**
 * Created by Mickael Hernandez on 09/05/2018.
 */
class PropertyFragment : Fragment() {

    companion object {
        const val MAP_ZOOM = 10f
        const val PROPERTY_PID_KEY = "propertypid"
        fun newInstance(propertyPid: String): PropertyFragment {
            val myFragment = PropertyFragment()
            val args = Bundle()
            args.putString(PROPERTY_PID_KEY, propertyPid)
            myFragment.arguments = args
            return myFragment
        }
    }

    private lateinit var dateFormat: DateFormat

    private lateinit var pid: String

    private lateinit var fragmentView: View

    private lateinit var prop : Property

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retainInstance = true
        dateFormat = android.text.format.DateFormat.getDateFormat(context?.applicationContext)
        return inflater.inflate(R.layout.fragment_property, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(PROPERTY_PID_KEY, pid)
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentView = view
        if(savedInstanceState != null){
            pid = savedInstanceState.getString(PROPERTY_PID_KEY)
        } else {
            pid = arguments?.getString(PROPERTY_PID_KEY)!!
            arguments?.remove(PROPERTY_PID_KEY)
        }
        //updateUIFromProperty(prop)
        MainActivity.colRef.document(pid).addSnapshotListener { doc, _ ->
            if(doc != null){
                val property = doc.toObject(Property::class.java)
                if(property != null){
                    updateUIFromProperty(property)
                }
            }
        }
        property_overlay.setOnClickListener {
                activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }

        fab_edit.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            (activity as MainActivity).displayFragment(EditPropertyFragment.newInstance(prop.pid))
        }

        property_close_button.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }
    }

    /** Update UI based on a Property object */
    private fun updateUIFromProperty(prop: Property) {
        this.prop = prop
        if(::fragmentView.isInitialized){
            try {
                // Setting UI elements according to the property object
                fragmentView.findViewById<TextView>(R.id.property_location).text = prop.location
                fragmentView.findViewById<TextView>(R.id.property_type).text = prop.type
                fragmentView.findViewById<TextView>(R.id.property_address).text = prop.address
                fragmentView.findViewById<TextView>(R.id.property_desc).text = prop.description
                fragmentView.findViewById<TextView>(R.id.property_surface).text = getString(R.string.sqm_value, prop.surface)
                fragmentView.findViewById<TextView>(R.id.property_rooms).text = getString(R.string.rooms_count, prop.roomsCount)
                fragmentView.findViewById<TextView>(R.id.property_price).text = getString(R.string.price_tag, prop.price)
                fragmentView.findViewById<TextView>(R.id.property_entryDate).text = dateFormat.format(prop.entryDate)
                fragmentView.findViewById<TextView>(R.id.property_saleDate).text = dateFormat.format(prop.saleDate)
                fragmentView.findViewById<TextView>(R.id.property_agent).text = prop.agent

                // Status
                val statusView = fragmentView.findViewById<TextView>(R.id.property_status)
                if(prop.status){
                    statusView.text = this.getString(R.string.available)
                    statusView.setTextColor(Color.parseColor("#4caf50"))
                } else {
                    statusView.text = this.getString(R.string.unavailable)
                    statusView.setTextColor(Color.RED)
                }

                // Pictures
                val picturesLayout = fragmentView.findViewById<LinearLayout>(R.id.pictures_layout)
                if(picturesLayout != null){
                    picturesLayout.removeAllViews()
                    for(url in prop.picturesList){
                        val img = DismissibleImageView(context)
                        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 800)
                        //params.weight = 1.0f
                        //params.gravity = Gravity.FILL
                        params.setMargins(0, 0, 0, 0)
                        //img.layoutParams = params
                        picturesLayout.addView(img)
                        Glide.with(context!!).load(url).into(img)
                    }
                }

                // Map
                val mapFragment = childFragmentManager.findFragmentById(R.id.property_map) as? SupportMapFragment
                if(mapFragment != null){
                    if(prop.geopoint.latitude != 0.0){
                        setMapWithGeopoint(mapFragment, prop.geopoint)
                    } else {
                        setMapWithProperty(mapFragment, prop)
                    }
                }

            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    /** Get geopoint from address, set up map and upload geopoint into firestore */
    private fun setMapWithProperty(map: SupportMapFragment, prop: Property) {
        map.getMapAsync {
            val coder = Geocoder(context)
            val addresses: List<Address>?
            var latlng: LatLng? = null
            try {
                addresses = coder.getFromLocationName(prop.address, 5)
                if (addresses != null && addresses.isNotEmpty()) {
                    latlng = LatLng(addresses[0].latitude, addresses[0].longitude)
                    val geoPoint = GeoPoint(addresses[0].latitude, addresses[0].longitude)
                    setUpMap(it, latlng)
                    FirebaseFirestore.getInstance().collection("properties").document(prop.pid).update("geopoint", geoPoint)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun setMapWithGeopoint(map: SupportMapFragment, geo: GeoPoint) {
        val latlng = LatLng(geo.latitude, geo.longitude)
        map.getMapAsync {
            setUpMap(it, latlng)
        }
    }

    private fun setUpMap(map: GoogleMap, latlng: LatLng){
        map.addMarker(MarkerOptions().position(latlng))
        map.moveCamera(CameraUpdateFactory.newLatLng(latlng))
        map.setMinZoomPreference(MAP_ZOOM)
        map.setOnMapClickListener {
            displayPropertyMap(prop.pid)
        }
        map.setOnMarkerClickListener {
            displayPropertyMap(prop.pid)
            true
        }
    }

    private fun displayPropertyMap(pid: String){
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        (activity as MainActivity).displayFragment(PropertiesMapFragment.newInstance(pid))
    }
}