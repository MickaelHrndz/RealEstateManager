package com.openclassrooms.realestatemanager.fragments

import android.Manifest
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
import android.app.Activity.RESULT_OK
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.adapters.EditImagesAdapter
import java.io.File


/**
 * Created by Mickael Hernandez on 17/05/2018.
 */
class EditPropertyFragment : Fragment() {

    companion object {
        const val REQUEST_READ_EXTERNAL_STORAGE = 8
        const val REQUEST_IMAGE = 9
        /** Creates a new instance of this fragment */
        fun newInstance(prop: Property): EditPropertyFragment {
            val myFragment = EditPropertyFragment()
            val args = Bundle()
            args.putParcelable("property", prop)
            myFragment.arguments = args
            return myFragment
        }
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

    private lateinit var editImagesAdapter: EditImagesAdapter

    private var imagesList = ArrayList<String>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dateFormat = android.text.format.DateFormat.getDateFormat(context?.applicationContext)
        return inflater.inflate(R.layout.fragment_editproperty, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prop = arguments?.getParcelable<Property>("property")
        arguments?.remove("property")
        editImagesAdapter = EditImagesAdapter(context!!, R.layout.row_edit_image, imagesList)
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        list_pictures.adapter = editImagesAdapter
        list_pictures.layoutManager = llm
        //list_pictures.isNestedScrollingEnabled = false
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
                imagesList.clear()
                imagesList.addAll(prop.picturesList)
                editImagesAdapter.notifyDataSetChanged()
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
                        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            // Permission is not granted
                            // Should we show an explanation?
                            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                // Show an explanation to the user *asynchronously* -- don't block
                                // this thread waiting for the user's response! After the user
                                // sees the explanation, try again to request the permission.
                            } else {
                                // No explanation needed; request the permission
                                ActivityCompat.requestPermissions(activity!!,
                                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                        REQUEST_READ_EXTERNAL_STORAGE)

                                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                                // app-defined int constant. The callback method gets the
                                // result of the request.
                            }
                        } else {
                            startImagePickIntent()
                        }
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
                (activity as MainActivity).displayFragment(PropertyFragment.newInstance(prop!!))
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
                    data["picturesList"] = imagesList.filter { it != "" }

                    /*val pList = ArrayList<String>()
                    for(i in 0 until editpictures_layout.childCount){
                        pList.add((editpictures_layout.getChildAt(i) as EditText).text.toString())
                    }*/

                    // If the property exists
                    if(prop!!.pid != ""){
                        // Update Firestore data
                        colRef.document(prop.pid).update(data as Map<String, Any>)
                        (activity as MainActivity).displayFragment(newInstance(prop))
                    } else {
                        // Create new document and set its pid as a field after it is successfully created
                        colRef.add(data).addOnSuccessListener {
                            colRef.document(it.id).update("pid", it.id).addOnCompleteListener {
                                if(it.isSuccessful){
                                    Toast.makeText(context, getString(R.string.property_created), Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                            }
                        }
                    }
                }
                catch(e: Exception) {
                    Toast.makeText(context, getString(R.string.edit_error), Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            } /*else {
                Toast.makeText(context, "One or more images url are invalid. Leave it empty to delete.", Toast.LENGTH_LONG).show()
            }

        }*/
    }
    private fun startImagePickIntent(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_READ_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startImagePickIntent()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

        // Add other 'when' lines to check for other
        // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE) {
            val selectedMediaUri = data?.data
            if (Utils.isExternalStorageReadable()) {
                uploadImageFromUri(selectedMediaUri!!)
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

    /** Uploads an image to the Firebase Storage based on its uri */
    private fun uploadImageFromUri(uri: Uri){
        //get image path from uri
        val file = Uri.fromFile(File(Utils.getRealPathFromUri(context, uri)))
        val imgRef = storage.child("images/" + file.lastPathSegment)
        val progressBar = view?.findViewById<ProgressBar>(R.id.progressBar)
        // Register observers to listen for when the download is done or if it fails
        val uploadTask = imgRef.putFile(file).addOnProgressListener {
            val progress = 100.0 * (it.bytesTransferred / it.totalByteCount)
                progressBar?.visibility = View.VISIBLE
                progressBar?.progress = progress.toInt()
        }.continueWithTask {
            if (!it.isSuccessful) {
                throw it.exception!!
            }
            // Continue with the task to get the download URL
            imgRef.downloadUrl

        }.addOnCompleteListener {
            if(it.isSuccessful){
                addUrlField(it.result.toString())
            }
            progressBar?.visibility = View.GONE
        }
    }

    /** Adds an image url field to the UI */
    private fun addUrlField(url: String = ""){
        imagesList.add(url)
        editImagesAdapter.notifyDataSetChanged()
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
            (activity as MainActivity).displayFragment(newInstance(prop))
        }
    }

}