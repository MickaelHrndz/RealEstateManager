package com.openclassrooms.realestatemanager.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.activities.MainActivity
import com.openclassrooms.realestatemanager.models.Property

class PropertiesMapFragment : SupportMapFragment() {
    companion object {
        fun newInstance(propertyPid: String): PropertiesMapFragment {
            val myFragment = PropertiesMapFragment()
            val args = Bundle()
            args.putString(PropertyFragment.PID_KEY, propertyPid)
            myFragment.arguments = args
            return myFragment
        }
    }

    // Camera position (initialized by default to show NYC)
    val cameraLatLng = LatLng(40.730610, -73.935242)

    // Default zoom level
    val defaultZoom = 6f

    // Default zoom level when showing a marker
    val markerZoom = 14f

    // Markers list
    val markers = mutableMapOf<Marker, Property>()

    override fun onActivityCreated(p0: Bundle?) {
        super.onActivityCreated(p0)
        if(context != null) {
            this.getMapAsync { map ->
                // Get property pid from the arguments
                val pid = arguments?.getString(PropertyFragment.PID_KEY)

                // Set default
                if (pid == null) {
                    map.moveCamera(CameraUpdateFactory.newLatLng(cameraLatLng))
                    map.animateCamera(CameraUpdateFactory.zoomTo(defaultZoom))
                }

                // Check permissions
                if (context != null && ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Enables location button
                    map.isMyLocationEnabled = true
                    map.uiSettings.isMyLocationButtonEnabled = true
                    // Enables map toolbar
                    map.uiSettings.isMapToolbarEnabled = true
                    // Enables defaultZoom buttons
                    map.uiSettings.isZoomControlsEnabled = true
                } else {
                    // Request permission
                    ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
                }

                // Download data from Firestore
                MainActivity.colRef.addSnapshotListener { querySnapshot, _ ->

                    // For each document in properties list
                    Utils.documentsToPropertyList(querySnapshot?.documents).forEach {

                        // Marker set-up
                        val marker = map.addMarker(MarkerOptions()
                                .position(Utils.geoPointToLatLng(it.geopoint))
                                .title(it.type + " - " + it.location)
                                .snippet("$" + it.price + " - " + getString(it.getStateStringId())))

                        // Add marker to the list
                        markers[marker] = it

                        // If the property should be focused
                        if (pid != null && it.pid == pid) {
                            map.moveCamera(CameraUpdateFactory.newLatLng(Utils.geoPointToLatLng(it.geopoint)))
                            map.animateCamera(CameraUpdateFactory.zoomTo(markerZoom))
                        }
                    }
                }

                // Markers info click listener
                map.setOnInfoWindowClickListener {
                    (context as MainActivity).displayFragment(PropertyFragment.newInstance(markers[it]?.pid!!))
                }
            }
        }
    }
}
