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
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.R.id.editprop_entryDate
import com.openclassrooms.realestatemanager.R.id.editprop_price
import com.openclassrooms.realestatemanager.activities.MainActivity
import com.openclassrooms.realestatemanager.models.Property
import kotlinx.android.synthetic.main.fragment_editproperty.*
import kotlinx.android.synthetic.main.fragment_property.*
import java.sql.Date
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*


/**
 * Created by Mickael Hernandez on 17/05/2018.
 */
class EditPropertyFragment : Fragment() {

    private lateinit var dateFormat: DateFormat

    /** Firestore instance */
    private val firestore = FirebaseFirestore.getInstance()

    private val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private var colRef = firestore.collection("properties")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dateFormat = android.text.format.DateFormat.getDateFormat(context?.applicationContext)
        return inflater.inflate(R.layout.fragment_editproperty, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prop = arguments?.getParcelable<Property>("property")
        if(prop != null){
            if(prop.pid != "") {
                editprop_type.text = prop.type
                editprop_address.setText(prop.address)
                editprop_location.text = prop.location
                editprop_desc.setText(prop.description)
                editprop_surface.setText(prop.surface.toString())
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
            val data = HashMap<String, Any>()
            data["address"] = editprop_address.text.toString()
            data["description"] = editprop_desc.text.toString()
            data["surface"] = Integer.parseInt(editprop_surface.text.toString())
            data["roomsCount"] = Integer.parseInt(editprop_rooms.text.toString())
            data["entryDate"] = df.parse(editprop_entryDate.text.toString())
            if(prop!!.pid != ""){
                // Update Firestore data
                colRef.document(prop.pid).update(data as Map<String, Any>)
            } else {
                // Create new document and set its pid as a field after it is successfully created
                colRef.add(data).addOnSuccessListener {
                    colRef.document(it.id).update("pid", it.id)
                }
            }

            (activity as MainActivity).displayProperty(prop)
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