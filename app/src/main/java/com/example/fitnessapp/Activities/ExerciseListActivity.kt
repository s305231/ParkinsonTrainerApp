package com.example.fitnessapp.Activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.fitnessapp.Adapters.ExerciseListAdapter
import com.example.fitnessapp.Database.FireStoreClass
import com.example.fitnessapp.Model.Exercise
import com.example.fitnessapp.Model.Workout
import com.example.fitnessapp.R
import com.example.fitnessapp.Utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_exercise_list.*
import java.io.IOException

class ExerciseListActivity : AuxActivity() {

    private lateinit var mWorkoutDetails : Workout
    private lateinit var mExercise : Exercise
    var idNr : Long = 0
    var workoutDocumentId = ""


    private var mSelectedImageFileUri : Uri? = null
    private var mWorkoutImageFileURL : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_list)
        //Extra is sent from MainActicty -> populateUI

        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            workoutDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
            println("Intent has extra doc id: " + workoutDocumentId)
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getWorkOutDetails(this, workoutDocumentId)

        iv_exercise_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERIMISSION_CODE
                )
            }
        }




        /*fab_create_exercise.setOnClickListener{

            val intent = Intent(this@ExerciseListActivity, CreateExerciseActivity::class.java)
            intent.putExtra(Constants.DOCUMENT_ID, workoutDocumentId)
            startActivity(intent)





        }*/
    }


    //This function basically gets the details for a spesicif workout, i.e the name when
    //setUp actionbar is called, the workout is passed from firestore getWorkoutDetails, and
    //the wokorut is identified beacuse of the doucement id which is passed from mainactivity onitemClickListener
    fun workoutDetails(workout: Workout) {

        mWorkoutDetails = workout

        //Somewhat works, Exercie gets added continously, beacuse of addUpdateExerciseSuccess Method
        /*if (intent.hasExtra("EXERCISE")) {
            val exercise = intent.extras?.getParcelable<Exercise>("EXERCISE")
            if (exercise != null) {
                mExercise = exercise
                exTestMethod()
            }
        }*/

        hideProgressDialog()
        setupActionBar()

        val addExerciseList = Exercise("Add List")
        workout.exerciseList.add(addExerciseList)

        rv_exercise_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_exercise_list.setHasFixedSize(false)

        val adapter = ExerciseListAdapter(this, workout.exerciseList)
        rv_exercise_list.adapter = adapter

        tv_select_workout.setOnClickListener {
            getExerciseList()
        }


        //watch pre 18:00 for this line, prepering tasklistadapter
        /*val addExerciseList = Exercise("Added Task")
        workout.exercieList.add(addExerciseList)

        rv_exercise_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_exercise_list.setHasFixedSize(true)

        val adapter = ExerciseListItemsAdapter(this, workout.exercieList)
        rv_exercise_list.adapter = adapter*/

    }

    fun addUpdateExerciseSuccess() {

        /*if (intent.hasExtra("EXERCISE")) {
            intent.removeExtra("EXERCISE")
        }*/

        hideProgressDialog()
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getWorkOutDetails(this, mWorkoutDetails.documentId)


    }

    fun createExerciseList(exerciseListName: String, duration: String) {


        var idNr = mWorkoutDetails.exerciseList.size.toLong()
        val exercise = Exercise(exerciseListName, FireStoreClass().getCurrentUserId(), duration, idNr, mWorkoutImageFileURL)
        //Add it to the workout
        mWorkoutDetails.exerciseList.add(0, exercise)
        mWorkoutDetails.exerciseList.removeAt(mWorkoutDetails.exerciseList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateExercise(this, mWorkoutDetails)

    }

    //This method will not create a new entry in the exerciseList it will overwrite the current entries with this new entry
    fun exTestMethod() {

        mWorkoutDetails.exerciseList.add(0, mExercise)
        mWorkoutDetails.exerciseList.removeAt(mWorkoutDetails.exerciseList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateExercise(this, mWorkoutDetails)
        println("Workout is: " + mWorkoutDetails.name + " Exercise is: " + mExercise)
    }

    //This method also needs the workoutDetail() method and and addupdateExerciseSuccess() .
    //This method is able to get the exercise and workout from WorkoutListActivity, however since the workout is sent directly as an object
    //and not only the worout document the other methods i.e getWorkoutDetails and addUpdateExercise does not work beacuse of mWorkoutDetails and so on.
    fun addExerciseToWorkout(exercise: Exercise) {

        //Add it to the workout

        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            var workoutDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
            println("Document id is: " + workoutDocumentId)
        }

       /* if (exercise != null) {

            /*workout.exerciseList.add(0, exercise)
            workout.exerciseList.removeAt(workout.exerciseList.size - 1)

            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().addUpdateExercise(this, workout)*/

            println("We are In addExerCiseToWorkout method exercise is: " + exercise + " and workoutplan is: " + mWorkoutDetails.name)
        } else println("Something is null workout: " + mWorkoutDetails.name + " exercise: " + exercise)*/



    }




    private fun setupActionBar() {
        setSupportActionBar(toolbar_task_list_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
            actionBar.title = mWorkoutDetails.name
        }
        toolbar_task_list_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun getExerciseList() {

        //uploadExerciseImage()
        println("Select Button pressed")
        var exerList : ArrayList<Exercise>
        exerList = mWorkoutDetails.exerciseList
        //exerList.removeAt(mWorkoutDetails.exerciseList.size-1)
        //println(exerList).toString()

        println(mWorkoutImageFileURL)


        val intent = Intent(this@ExerciseListActivity, ExcersiseActivity::class.java)
        intent.putExtra(Constants.DOCUMENT_ID, workoutDocumentId)
        startActivity(intent)

        //This works it sends the exerciseList to the next activity
        /*
        val intent = Intent(this@ExerciseListActivity, ExcersiseActivity::class.java)
        intent.putExtra("EXERLIST", exerList)
        startActivity(intent)*/
    }

    fun getDocumentId() : String {
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            val workoutDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
            return workoutDocumentId
        }
        else return ""
    }

    fun uploadExerciseImage(name: String, duration: String) {
        //showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null) {
            //This done to give image a unique name. For ex. It will be called user_image + the time + the fileExtension at the end
            val sRef : StorageReference = FirebaseStorage.getInstance().reference.child("WORKOUT_IMAGE" + System.currentTimeMillis()
                    + "." + getFileExtenstion(this, mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                    taskSnapshot ->
                Log.e("Workout Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl!!.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->
                    Log.i("Downloadable Image Url", uri.toString())
                    mWorkoutImageFileURL = uri.toString()
                    createExerciseList(name,duration)
                }.addOnFailureListener {
                        exception ->
                    Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                    hideProgressDialog()
                }
            }
        } else Toast.makeText(this, "Please enter a image for the exercise", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERIMISSION_CODE) {
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
                .placeholder(R.drawable.ic_exercise_placeholder)
                .into(iv_exercise_image)
        }catch (e: IOException) {
            e.printStackTrace()
        }
    }
}