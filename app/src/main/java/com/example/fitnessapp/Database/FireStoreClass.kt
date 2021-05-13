package com.example.fitnessapp.Database

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.fitnessapp.Activities.*
import com.example.fitnessapp.Model.Exercise
import com.example.fitnessapp.Model.User
import com.example.fitnessapp.Model.Workout
import com.example.fitnessapp.Utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStoreClass {

    private val mFirestore = FirebaseFirestore.getInstance()
    public var name: String = ""


    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
    }

    //Not Used, to mark the end of non used funtions $$ will be used//
    /*fun createExercise(activity: CreateExerciseActivity, documentId: String, exercise: Exercise) {
        mFirestore.collection(Constants.EXERCISE_LIST)
            .document(documentId)
            .set(exercise, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Exercise Created successfully")
                Toast.makeText(activity,"Exercise Created Successfully!", Toast.LENGTH_SHORT).show()
                activity.exerciseCreatedSuccessfully()
            }.addOnFailureListener {
                    exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating exercise", exception)
            }

    }

    fun getExerciseList(activity: ExerciseListActivity) {
        mFirestore.collection(Constants.WORKOUTS)
            .whereArrayContains(Constants.DOCUMENT_ID, activity.getDocumentId())
            .get()
            .addOnSuccessListener {
                    document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val exerciseList: ArrayList<Exercise> = ArrayList()
                for (i in document.documents) {
                    val exercise = i.toObject(Exercise::class.java)!!
                      exerciseList.add(exercise)
                }
                activity.populateWorkoutListToUI(exerciseList)
            }.addOnFailureListener {
                    e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating workoutList")
            }
    }*/ //$$//

    //Change name to getWorkoutDetails

    fun getWorkOutDetails(activity: Activity, documentId: String) {
        mFirestore.collection(Constants.WORKOUTS)
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())
                val workout = document.toObject(Workout::class.java)!!
                workout.documentId = document.id
                when (activity) {
                    is ExerciseListActivity -> {
                        activity.workoutDetails(workout)
                    }
                    is ExcersiseActivity -> {
                        activity.workoutDetails(workout)
                    }
                    is WorkoutExerciseListActivity -> {
                        activity.workoutDetails(workout)
                    }
                    is MainActivity -> {
                        activity.workoutDetails(workout)
                    }
                }
            }.addOnFailureListener { e ->
                when (activity) {
                    is ExerciseListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is ExcersiseActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while creating workoutList")
            }

    }

    fun createWorkout(activity: CreateWorkoutActivity, workout: Workout) {
        mFirestore.collection(Constants.WORKOUTS)
            .document()
            .set(workout, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Workout Created successfully")
                Toast.makeText(activity, "Workout Created Successfully!", Toast.LENGTH_SHORT).show()
                activity.workoutCreatedSuccessfully()
            }.addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating board", exception)
            }
    }

    fun createExercise(activity: CreateExerciseActivity, exercise: Exercise) {
        mFirestore.collection(Constants.EXERCISES)
            .document()
            .set(exercise, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Exercise Created successfully")
                Toast.makeText(activity, "Exercise Created Successfully!", Toast.LENGTH_SHORT)
                    .show()
                activity.exerciseCreatedSuccessfully()
            }.addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating exercise", exception)
            }
    }

    //This function retrives the workout list from Firebase
    fun getWorkoutList(activity: WorkoutListActivity) {
        mFirestore.collection(Constants.WORKOUTS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val workoutList: ArrayList<Workout> = ArrayList()
                for (i in document.documents) {
                    val workout = i.toObject(Workout::class.java)!!
                    workout.documentId = i.id
                    workoutList.add(workout)
                }
                activity.populateWorkoutListToUI(workoutList)
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating workoutList")
            }
    }

    fun getExerciseList(activity: ViewAllExercises) {
        mFirestore.collection(Constants.EXERCISES)
            //If physios and other can also create a exercise for users then this needs to be changed from CREATED_BY
            //.whereArrayContains(Constants.CREATED_BY, getCurrentUserId())
            .get()
            .addOnSuccessListener { result ->
                println("From FireStoreClass document: " + result.size())
                Log.e(activity.javaClass.simpleName, result.documents.toString())
                val exerciseList: ArrayList<Exercise> = ArrayList()
                for (document in result) {
                    println("Entered for loop")
                    val exercise = document.toObject(Exercise::class.java)!!
                    exerciseList.add(exercise)
                    println("Exex list: " + exerciseList)
                }
                activity.populateExerciseList(exerciseList)
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating exerciselist")
            }
    }


    //Creates the entry in FireBase the DB
    fun addUpdateExercise(activity: Activity, workout: Workout) {
        //from 12:47
        val exerciseListHashMap = HashMap<String, Any>()
        exerciseListHashMap[Constants.EXERCISE_LIST] = workout.exerciseList

        mFirestore.collection(Constants.WORKOUTS)
            //We are passing in an document.id so it will look for that doc id and overwrite whatever is in there
            //if we did not pass doc id it would have created a new one
            .document(workout.documentId)
            .update(exerciseListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "ExerciseList updated successfully")
                //Gets the workout
                when (activity) {
                    is ExerciseListActivity -> {
                        activity.addUpdateExerciseSuccess()
                    }
                    is ExcersiseActivity -> {
                        activity.addUpdateExerciseSuccess()
                    }
                    is WorkoutListActivity -> {
                        activity.addUpdateExerciseSuccess()
                    }
                }
            }.addOnFailureListener { exception ->
                when (activity) {
                    is ExerciseListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is ExcersiseActivity -> {
                        activity.hideProgressDialog()
                    }
                    is WorkoutListActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while creating exercise")

            }
    }

    fun updateUserProfileData(activity: UpdateProfileActivity, userHashMap: HashMap<String, Any>) {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId()).update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Profile Data updated successfully")
                Toast.makeText(activity, "Profile updated succcessfully!", Toast.LENGTH_SHORT)
                    .show()
                activity.profileUpdateSuccess()


            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while updating profile")
            }
    }

    //readWorkoutList parameter is added for optimaztion purposes later on it was not there initially, its desgin desicion can be left out
    fun loadUserData(activity: Activity, readWorkoutList: Boolean = false) {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId()).get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)!!
                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is WorkoutListActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser, readWorkoutList)
                    }
                    is UpdateProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                    is ViewAllExercises -> {
                        activity.getUserName(loggedInUser)
                    }
                }

            }.addOnFailureListener { e ->
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e("FirestoreClassSignInUse", "Error while reading document")
            }
    }

    fun getCurrentUserId(): String {

        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""

        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun getCurrentName(): String {

        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserName = ""

        if (currentUser != null) {
            currentUserName = currentUser.displayName
        }

        return currentUserName
    }

    fun getUserName(): String {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId()).get()
            .addOnSuccessListener {
                    document ->
                val loggedInUser = document.toObject(User::class.java)!!
                name = loggedInUser.name
                println("From FireStore: " + name)
                return@addOnSuccessListener
            }.addOnFailureListener {
                    e ->
                Log.e("FirestoreClassSignInUse", "Error while reading document")

            }
        return name
    }
}