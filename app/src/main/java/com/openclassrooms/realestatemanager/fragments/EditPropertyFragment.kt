package com.openclassrooms.realestatemanager.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.cielyang.android.clearableedittext.ClearableEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.activities.MainActivity
import com.openclassrooms.realestatemanager.models.Property
import kotlinx.android.synthetic.main.fragment_editproperty.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.storage.FirebaseStorage
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.content.Intent
import android.provider.MediaStore
import android.R.attr.data
import android.app.Activity.RESULT_OK
import android.net.Uri
import android.support.annotation.NonNull
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.android.synthetic.main.abc_activity_chooser_view.*
import com.google.firebase.storage.UploadTask
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageReference
import java.io.File


/**
 * Created by Mickael Hernandez on 17/05/2018.
 */
class EditPropertyFragment : Fragment() {

    companion object {
        const val RESULT_IMAGE = 9
    }

    private lateinit var dateFormat: DateFormat

    /** Firestore instance */
    private val firestore = FirebaseFirestore.getInstance()

    /** Firebase storage instance */
    var storage = FirebaseStorage.getInstance().reference

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
                    val editText = ClearableEditText(context)
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

        btn_addpicture.setOnClickListener {
            val builder = AlertDialog.Builder(context!!)
            builder.setTitle("Pick a color")
            builder.setItems(arrayOf("On Internet", "On my phone"), (DialogInterface.OnClickListener { dialogInterface, i ->
                when(i){
                    // Internet URL
                    0 -> {
                        addUrlField()
                    }
                    // Phone
                    1 -> {
                        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(intent, RESULT_IMAGE)
                    }
                }
            }))
            builder.show()
        }

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
                // Populate data map with user input
                data["type"] = editprop_type.text.toString()
                data["location"] = editprop_location.text.toString()
                data["address"] = editprop_address.text.toString()
                data["description"] = editprop_desc.text.toString()
                data["surface"] = Integer.parseInt(editprop_surface.text.toString())
                data["roomsCount"] = Integer.parseInt(editprop_rooms.text.toString())
                data["entryDate"] = df.parse(editprop_entryDate.text.toString())
                data["price"] = Integer.parseInt(editprop_price.text.toString())
                data["status"] = editprop_checkbox.isChecked

                val pList = ArrayList<String>()
                for(i in 0 until editpictures_layout.childCount){
                    pList.add((editpictures_layout.getChildAt(i) as EditText).text.toString())
                }
                data["picturesList"] = pList

                // If the property exists
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
            catch(e: Exception) {
                Toast.makeText(context, "Something went wrong. Please make sure that all value entered is valid.", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val selectedMediaUri = data?.data
            if (selectedMediaUri.toString().contains("image")) {
                //handle image
                val file = Uri.fromFile(File(selectedMediaUri.toString()))
                val imgRef = storage.child("images/" + file.lastPathSegment)

                // Register observers to listen for when the download is done or if it fails
                val uploadTask = imgRef.putFile(file)
                uploadTask.continueWithTask {
                    if (!it.isSuccessful) {
                        throw it.exception!!
                    }
                    // Continue with the task to get the download URL
                    imgRef.downloadUrl

                }.addOnCompleteListener {
                    if(it.isSuccessful){
                        addUrlField(it.result.toString())
                    }
                }

                /*val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    if(!it.isSuccessful){
                        throw it.exception!!
                    }
            }).addOnCompleteListener {
                    addUrlField(it.result.toString())
            }*/
            }
        }
    }

    private fun addUrlField(url: String = ""){
        val editText = ClearableEditText(context)
        if(url != ""){
            editText.setText(url)
        }
        editText.hint = "Image URL"
        editpictures_layout.addView(editText)
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