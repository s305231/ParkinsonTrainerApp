package com.example.fitnessapp.Activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.fitnessapp.Database.FireStoreClass
import com.example.fitnessapp.Model.User
import com.example.fitnessapp.R
import com.example.fitnessapp.Utils.Constants
import com.example.fitnessapp.Utils.Constants.READ_STORAGE_PERIMISSION_CODE
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_update_profile.*
import java.io.IOException

class UpdateProfileActivity : AuxActivity() {

    private var mSelectedImageFileUri: Uri? = null
    private var mProfileImageURL : String = ""
    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)
        setupActionBar()

        FireStoreClass().loadUserData(this)

        iv_profile_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
                showImageChooser(this)
            }else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERIMISSION_CODE
                )
            }
        }

        //updateUserData is also called inside uploadUserImage() so its called in both ttrue or false case
        //I think the main reason for this is beacuse when changeing profile pic we first need to upload that image to
        //Firestore storage, thats how we set a new mPofileImageURL, and then call updateUserData() inisde the uploadUserImage()
        // in order to set the new mProfileImageURL
        btn_update.setOnClickListener {
            if (mSelectedImageFileUri != null) {
                uploadUserImage()
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERIMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                showImageChooser(this)

            } else {
                Toast.makeText(this, "Oops, you just denied the permission for storage. You can also allow it for settings", Toast.LENGTH_LONG).show()

            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null) {
            mSelectedImageFileUri = data.data        }

        try {
            Glide
                .with(this)
                .load(mSelectedImageFileUri)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(iv_profile_image)
        }catch (e: IOException) {
            e.printStackTrace()
        }


    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_my_profile_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
            actionBar.title = resources.getString(R.string.my_profile)
        }
        toolbar_my_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun setUserDataInUI(user: User) {

        //The logged in user gets passed from Firestore class and intialized here
        mUserDetails = user

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_profile_image)

        et_name.setText(user.name)
        et_email.setText(user.email)
        if (user.mobile != 0L) {
            et_mobile.setText(user.mobile.toString())
        }
    }

    //This will update the values in the actual Firestore db
    private fun updateUserProfileData() {
        //Creating user hashmap
        val userHashMap = HashMap<String, Any>()
        var changesMade : Boolean = false

        //Adding values to the hashmap, the values are: image, name, number
        if (mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image) {
            userHashMap[Constants.IMAGE] = mProfileImageURL
            changesMade = true
        }

        if (et_name.text.toString() != mUserDetails.name) {
            userHashMap[Constants.NAME] = et_name.text.toString()
            changesMade = true
        }

        if (et_mobile.text.toString() != mUserDetails.mobile.toString()) {
            //its toLong() beacuse our userHashmap expects a Long here (Firebase mobile field)
            userHashMap[Constants.MOBILE] = et_mobile.text.toString().toLong()
            changesMade =true
        }

        if (changesMade == true) {
            FireStoreClass().updateUserProfileData(this,userHashMap)
        }

    }

    //Function to upload to the cloud storage, this one is for images
    private fun uploadUserImage() {
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null) {
            //This done to give image a unique name. For ex. It will be called user_image + the time + the fileExtension at the end
            val sRef : StorageReference = FirebaseStorage.getInstance().reference.child("USER_IMAGE" + System.currentTimeMillis()
                    + "." + getFileExtenstion(this, mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                    taskSnapshot ->
                Log.e("Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl!!.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->
                    Log.i("Downloadable Image Url", uri.toString())
                    mProfileImageURL = uri.toString()
                    updateUserProfileData()
                }.addOnFailureListener {
                        exception ->
                    Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                    hideProgressDialog()
                }
            }
        }
    }



    //After update activity will be finsihed and user will return
    fun profileUpdateSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}