package com.openclassrooms.realestatemanager.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.activities.MainActivity
import com.openclassrooms.realestatemanager.adapters.EditImagesAdapter
import com.openclassrooms.realestatemanager.models.Property
import kotlinx.android.synthetic.main.fragment_editproperty.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Mickael Hernandez on 17/05/2018.
 */
class EditPropertyFragment : Fragment() {

    companion object {
        const val REQUEST_READ_EXTERNAL_STORAGE = 8
        const val REQUEST_CAMERA = 9

        const val REQUEST_IMAGE = 9
        const val datePattern = "dd/MM/yyyy"
        /** Creates a new instance of this fragment */
        fun newInstance(pid: String): EditPropertyFragment {
            val myFragment = EditPropertyFragment()
            val args = Bundle()
            args.putString(PropertyFragment.PROPERTY_PID_KEY, pid)
            myFragment.arguments = args
            return myFragment
        }
    }

    private lateinit var dateFormat: DateFormat

    /** Firestore instance */
    private val firestore = FirebaseFirestore.getInstance()

    /** Firebase storage instance */
    var storage = FirebaseStorage.getInstance().reference

    private val df = SimpleDateFormat(datePattern, Locale.getDefault())

    private var colRef = firestore.collection("properties")

    /** If the fragment is used to add a new property */
    private var isNew = false

    private lateinit var editImagesAdapter: EditImagesAdapter

    private var imagesList = ArrayList<String>()

    private var pid = ""

    private lateinit var prop : Property


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retainInstance = true
        dateFormat = android.text.format.DateFormat.getDateFormat(context?.applicationContext)
        return inflater.inflate(R.layout.fragment_editproperty, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PropertyFragment.PROPERTY_PID_KEY, pid)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Get property PID from saved instance or arguments
        if(savedInstanceState != null){
            pid = savedInstanceState.getString(PropertyFragment.PROPERTY_PID_KEY)
        } else {
            pid = arguments?.getString(PropertyFragment.PROPERTY_PID_KEY)!!
            arguments?.remove(PropertyFragment.PROPERTY_PID_KEY)
        }

        // If pid is not found, the user wants to add a new property
        if(pid == "") {
            isNew = true
        } else {
            // Get the property's data according to its pid
            MainActivity.colRef.document(pid).addSnapshotListener { doc, _ ->
                if(doc != null){
                    prop = doc.toObject(Property::class.java)!!
                    updateUIFromProperty()
                }
            }
        }

        // Set up images list adapter
        editImagesAdapter = EditImagesAdapter(context!!, R.layout.row_edit_image, imagesList)
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        list_pictures.adapter = editImagesAdapter
        list_pictures.layoutManager = llm

        //list_pictures.isNestedScrollingEnabled = false

        // Clicking out of the cardView finishes the fragment
        editoverlay.setOnClickListener {
            finish()
        }
        // Overriding click listener on card to do nothing
        card_view_edit.setOnClickListener {  }

        // Add picture listener
        btn_addpicture.setOnClickListener {
            // Building AlertDialog to let the user choose where its picture is
            val builder = AlertDialog.Builder(context!!)
            builder.setTitle("Picture location")
            builder.setItems(arrayOf("On Internet", "On my phone", "Take the picture"), (DialogInterface.OnClickListener { _, i ->
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
                            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            } else {
                                // No explanation needed; request the permission
                                ActivityCompat.requestPermissions(activity!!,
                                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                        REQUEST_READ_EXTERNAL_STORAGE)
                            }
                        } else {
                            startImagePickIntent()
                        }
                    }

                    // Camera
                    2 -> {
                        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) !=
                                PackageManager.PERMISSION_GRANTED) {
                            // Permission is not granted
                            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            } else {
                                // No explanation needed; request the permissions
                                ActivityCompat.requestPermissions(activity!!,
                                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                                        REQUEST_CAMERA)
                            }
                        } else {
                            startCameraIntent()
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
                (activity as MainActivity).displayFragment(PropertyFragment.newInstance(prop?.pid!!))
            }
        }

        // If user validates his edits
        prop_done.setOnClickListener {
                try {
                    // Data map used to update the property data
                    val data = HashMap<String, Any>()

                    if(!editprop_checkbox.isChecked) {
                        assert(df.parse(editprop_saleDate.text.toString()) != null)
                    }

                    // Populate data map with user input
                    data["type"] = editprop_type.text.toString()
                    data["status"] = editprop_checkbox.isChecked
                    data["location"] = editprop_location.text.toString()
                    data["address"] = editprop_address.text.toString()
                    data["description"] = editprop_desc.text.toString()
                    data["surface"] = Integer.parseInt(editprop_surface.text.toString())
                    data["roomsCount"] = Integer.parseInt(editprop_rooms.text.toString())
                    data["entryDate"] = df.parse(editprop_entryDate.text.toString())
                    data["saleDate"] = df.parse(editprop_saleDate.text.toString())
                    data["price"] = Integer.parseInt(editprop_price.text.toString())
                    data["agent"] = editprop_agent.text.toString()

                    editImagesAdapter.notifyDataSetChanged()
                    data["picturesList"] = imagesList.filter { it != "" }

                    // If the property exists
                    if(prop!!.pid != ""){
                        // Update Firestore data
                        colRef.document(prop.pid).update(data as Map<String, Any>)
                        (activity as MainActivity).displayFragment(PropertyFragment.newInstance(prop.pid))
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

    private fun updateUIFromProperty() {
        // lateinit prop must be initialized
        if(::prop.isInitialized){
            // Assert property data and edit layout aren't empty or null
            if(prop.pid != "" && editoverlay != null) {
                editprop_checkbox.isChecked = prop.status
                editprop_type.setText(prop.type)
                editprop_address.setText(prop.address)
                editprop_location.setText(prop.location)
                editprop_desc.setText(prop.description)
                editprop_surface.setText(prop.surface.toString())
                editprop_rooms.setText(prop.roomsCount.toString())
                editprop_price.setText(prop.price.toString())
                editprop_entryDate.setText(dateFormat.format(prop.entryDate))
                editprop_saleDate.setText(dateFormat.format(prop.saleDate))
                editprop_agent.setText(prop.agent)
                imagesList.clear()
                imagesList.addAll(prop.picturesList)
                editImagesAdapter.notifyDataSetChanged()
            } else {
                isNew = true
            }
        } else {
            finish()
        }
    }

    private val REQUEST_CAMERA = 7256

    private fun startCameraIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context?.packageManager) != null)
        {
            // Create the File where the photo should go
            var photoFile:File? = null
            try
            {
                photoFile = createImageFile()
            }
            catch (ex:IOException) {}// Error occurred while creating the File
            // Continue only if the File was successfully created
            if (photoFile != null)
            {
                val photoURI = FileProvider.getUriForFile(context!!,
                        "com.example.android.fileprovider",
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_CAMERA)
            }
        }
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
                    // permission was granted
                    startImagePickIntent()
                } else {
                    // permission denied
                }
                return
            }
            REQUEST_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted
                    startCameraIntent()
                } else {
                    // permission denied
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
        if (resultCode == RESULT_OK && Utils.isExternalStorageReadable()) {
            if(requestCode == REQUEST_IMAGE){
                val selectedMediaUri = data?.data
                    uploadImageFromUri(selectedMediaUri!!)

            } else if(requestCode == REQUEST_CAMERA){
                uploadImage(Uri.fromFile(currentPicture))
            }
        }
    }

    private lateinit var currentPhotoPath: String
    private lateinit var currentPicture: File

    @Throws(IOException::class)
    private fun createImageFile():File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        )
        image.setReadable(true)
        image.setWritable(true)
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        currentPicture = image
        return image
    }

    /** Uploads an image to the Firebase Storage based on its uri */
    private fun uploadImageFromUri(uri: Uri){
        //get image path from uri
        val file = Uri.fromFile(File(Utils.getRealPathFromUri(context, uri)))
        uploadImage(file)
    }

    private fun uploadImage(file: Uri){
        val progressBar = view?.findViewById<ProgressBar>(R.id.progressBar)
        progressBar?.visibility = View.VISIBLE

        val imgRef = storage.child("images/" + file.lastPathSegment)
        // Register observers to listen for when the download is done or if it fails
        imgRef.putStream(FileInputStream(file.path)).continueWithTask {
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

}