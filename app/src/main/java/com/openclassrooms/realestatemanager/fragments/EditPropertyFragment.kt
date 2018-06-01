package com.openclassrooms.realestatemanager.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.activities.MainActivity
import com.openclassrooms.realestatemanager.models.Property
import kotlinx.android.synthetic.main.fragment_editproperty.*
import java.text.DateFormat
import java.text.SimpleDateFormat
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

    /** If the fragment is used to add a new property */
    private var isNew = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dateFormat = android.text.format.DateFormat.getDateFormat(context?.applicationContext)
        return inflater.inflate(R.layout.fragment_editproperty, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prop = arguments?.getParcelable<Property>("property")
        arguments?.remove("property")
        if(prop != null){
            if(prop.pid != "") {
                editprop_type.setText(prop.type)
                editprop_address.setText(prop.address)
                editprop_location.setText(prop.location)
                editprop_desc.setText(prop.description)
                editprop_surface.setText(prop.surface.toString())
                editprop_rooms.setText(prop.roomsCount.toString())
                editprop_price.setText(prop.price.toString())
                editprop_entryDate.setText(dateFormat.format(prop.entryDate))
                editprop_checkbox.isChecked = prop.status

                for(url in prop.picturesList){
                    val editText = EditText(context)
                    editText.setText(url)
                    editpictures_layout.addView(editText)
                }
            } else {
                isNew = true
            }
        } else {
            finish()
        }
        editoverlay.setOnClickListener {
            finish()
        }
        card_view_edit.setOnClickListener {  }

        // If user cancels the edit
        prop_cancel.setOnClickListener {
            if(isNew){
                finish()
            } else {
                (activity as MainActivity).displayProperty(prop!!)
            }
        }

        // If user validates his edits
        prop_done.setOnClickListener {
            try {
                val data = HashMap<String, Any>()
                data["type"] = editprop_type.text.toString()
                data["location"] = editprop_location.text.toString()
                data["address"] = editprop_address.text.toString()
                data["description"] = editprop_desc.text.toString()
                data["surface"] = Integer.parseInt(editprop_surface.text.toString())
                data["roomsCount"] = Integer.parseInt(editprop_rooms.text.toString())
                data["entryDate"] = df.parse(editprop_entryDate.text.toString())
                data["price"] = Integer.parseInt(editprop_price.text.toString())
                data["status"] = editprop_checkbox.isChecked
                if(prop!!.pid != ""){
                    // Update Firestore data
                    colRef.document(prop.pid).update(data as Map<String, Any>)
                    (activity as MainActivity).displayProperty(prop)
                } else {
                    // Create new document and set its pid as a field after it is successfully created
                    colRef.add(data).addOnSuccessListener {
                        colRef.document(it.id).update("pid", it.id)
                        finish()
                    }
                }
            }
            catch(e: Exception){
                Toast.makeText(context, "Something went wrong. Please make sure that all value entered is valid.", Toast.LENGTH_LONG).show()
            }
        }
    }

    /** Creates a new instance of this fragment */
    fun newInstance(prop: Property): EditPropertyFragment {
        val myFragment = EditPropertyFragment()
        val args = Bundle()
        args.putParcelable("property", prop)
        myFragment.arguments = args
        return myFragment
    }

    /** Removes this fragment */
    private fun finish(){
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
    }

    /** Displays property or removes this fragment depending if the property is created or edited */
    private fun displayOrFinish(prop: Property){
        if(isNew){
            finish()
        } else {
            (activity as MainActivity).displayProperty(prop)
        }
    }

}