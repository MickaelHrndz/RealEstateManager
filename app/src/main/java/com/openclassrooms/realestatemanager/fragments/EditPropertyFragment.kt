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

    private val df = SimpleDateFormat(datePattern, Locale.getDefault())

    private var colRef = firestore.collection("properties")

    /** If the fragment is used to add a new property */
    private var isNew = false

    private lateinit var editImagesAdapter: EditImagesAdapter

    private var imagesList = ArrayList<String>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retainInstance = true
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
        editoverlay.setOnClickListener {
            finish()
        }
        card_view_edit.setOnClickListener {  }

        btn_addpicture.setOnClickListener {
            val builder = AlertDialog.Builder(context!!)
            builder.setTitle("Picture location")
            builder.setItems(arrayOf("On Internet", "On my phone", "Take the picture"), (DialogInterface.OnClickListener { dialogInterface, i ->
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
                    2 -> {
                        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) !=
                                PackageManager.PERMISSION_GRANTED) {
                            // Permission is not granted
                            // Should we show an explanation?
                            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                // Show an explanation to the user *asynchronously* -- don't block
                                // this thread waiting for the user's response! After the user
                                // sees the explanation, try again to request the permission.
                            } else {
                                // No explanation needed; request the permissions
                                ActivityCompat.requestPermissions(activity!!,
                                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                                        REQUEST_CAMERA)
                                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                                // app-defined int constant. The callback method gets the
                                // result of the request.
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
                (activity as MainActivity).displayFragment(PropertyFragment.newInstance(prop!!))
            }
        }

        // If user validates his edits
        prop_done.setOnClickListener {
                try {
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

                    /*val pList = ArrayList<String>()
                    for(i in 0 until editpictures_layout.childCount){
                        pList.add((editpictures_layout.getChildAt(i) as EditText).text.toString())
                    }*/

                    // If the property exists
                    if(prop!!.pid != ""){
                        // Update Firestore data
                        colRef.document(prop.pid).update(data as Map<String, Any>)
                        (activity as MainActivity).displayFragment(PropertyFragment.newInstance(prop))
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
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startImagePickIntent()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            REQUEST_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startCameraIntent()
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

    /*private fun uploadImageFromPath(path: String){
        uploadImageFromUri(Uri.parse(path))
    }*/

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

    /** Displays property or removes this fragment depending if the property is created or edited */
    private fun displayOrFinish(prop: Property){
        if(isNew){
            finish()
        } else {
            (activity as MainActivity).displayFragment(newInstance(prop))
        }
    }

}