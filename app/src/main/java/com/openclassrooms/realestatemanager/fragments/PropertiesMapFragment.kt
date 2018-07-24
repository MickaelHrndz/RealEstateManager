package com.openclassrooms.realestatemanager.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.activities.MainActivity
import com.openclassrooms.realestatemanager.models.Property

class PropertiesMapFragment : SupportMapFragment() {
    val cameraLatLng = LatLng(40.730610, -73.935242)
    val zoom = 6f
    val markers = mutableMapOf<Marker, Property>()
    override fun onActivityCreated(p0: Bundle?) {
        super.onActivityCreated(p0)
        this.getMapAsync { map ->
            if (context != null && ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
                map.uiSettings.isMyLocationButtonEnabled = true
                map.uiSettings.isMapToolbarEnabled = true
            } else {
                // Request permission
                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
            }
            map.moveCamera(CameraUpdateFactory.newLatLng(cameraLatLng))
            map.animateCamera(CameraUpdateFactory.zoomTo(zoom))
            MainActivity.colRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                Utils.documentsToPropertyList(querySnapshot?.documents).forEach {
                    val marker = map.addMarker(MarkerOptions()
                            .position(Utils.geoPointToLatLng(it.geopoint))
                            .title(it.type + " - " + it.location)
                            .snippet("$" + it.price + " - " + getString(it.getStateStringId())))
                    markers[marker] = it
                }
            }
            map.setOnInfoWindowClickListener {
                (context as MainActivity).displayFragment(PropertyFragment.newInstance(markers[it]!!))
            }
        }
    }
}
