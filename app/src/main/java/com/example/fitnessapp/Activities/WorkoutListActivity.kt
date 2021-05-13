package com.example.fitnessapp.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.fitnessapp.Adapters.WorkoutListAdapter
import com.example.fitnessapp.Database.FireStoreClass
import com.example.fitnessapp.Model.Exercise
import com.example.fitnessapp.Model.User
import com.example.fitnessapp.Model.Workout
import com.example.fitnessapp.R
import com.example.fitnessapp.Utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_workout_list.*
import kotlinx.android.synthetic.main.drawer.*
import kotlinx.android.synthetic.main.drawer_header.*
import kotlinx.android.synthetic.main.item_workout.*
import kotlinx.android.synthetic.main.top_bar_main.*
import java.io.Serializable

class WorkoutListActivity : AuxActivity(), NavigationView.OnNavigationItemSelectedListener, Serializable{

    companion object {
        //Why 11? NO idea.
        const val MY_PROFILE_REQUEST_CODE  : Int = 11
        const val CREATE_WORKOUT_REQUEST_CODE : Int = 12
    }

    private lateinit var mUserName: String
    private lateinit var mExercise : Exercise


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer)
        setupActionBar()
        nav_view.setNavigationItemSelectedListener(this@WorkoutListActivity)
        FireStoreClass().loadUserData(this, true)

        fab_create_workout.setOnClickListener{
            //We do it this way beacuse we get the username without doin another db request
            val intent = Intent(this, CreateWorkoutActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            //The paramet request code makes sure that a newly created workout is displayed in the view, beacuse fun workoutCreatedSuccessfully() in CreateWrokoutActivity sets the
            //code as ok, and here in this class in onActivityResult we can execute additional code based on the result of the request code.
            startActivityForResult(intent, CREATE_WORKOUT_REQUEST_CODE)
        }

    }

    //The downloading of the workouts(boards) will happen else where (getWorkoutList in Firestore class), this function will display the workouts ot UI
    fun populateWorkoutListToUI(workoutList: ArrayList<Workout>) {

        hideProgressDialog()
        if (workoutList.size > 0) {
            rv_workout_list.visibility = View.VISIBLE

            no_workouts_available.visibility = View.GONE
            tv_add_exercise_to_workout.visibility= View.GONE
            rv_workout_list.layoutManager = LinearLayoutManager(this)
            rv_workout_list.setHasFixedSize(true)

            val adapter = WorkoutListAdapter(this, workoutList)

            rv_workout_list.adapter = adapter

            if (intent.hasExtra("EXERCISE")) {
                tv_add_exercise_to_workout.visibility= View.VISIBLE
                adapter.activateButtons(true)
            }

            adapter.setOnClickListener(object : WorkoutListAdapter.OnClickListener {
                override fun onClick(position: Int, model: Workout) {
                    if (intent.hasExtra("EXERCISE")) {

                        btn_select_workout.visibility = View.GONE


                        tv_add_exercise_to_workout.text =
                            "Exercise successfully added to: " + model.name + " click on another workout to add or click here to return!"
                        val exercise = intent.extras?.getParcelable<Exercise>("EXERCISE")
                        if (exercise != null && model != null) {
                            /*if (model.exerciseList[position] == exercise) {
                                //"Add a dialog pop up here like when back press during exercise, that asks if you want to still add the exercise"
                                println("Exercise: " + exercise.name  + " is already in workout")
                            }*/
                            mExercise = exercise
                            mExercise.id = model.exerciseList.size.toLong()

                            model.exerciseList.add(0, mExercise)
                            //model.exerciseList.removeAt(model.exerciseList.size - 1)

                            showProgressDialog(resources.getString(R.string.please_wait))
                            FireStoreClass().addUpdateExercise(this@WorkoutListActivity, model)

                            //Uncomment this to send workout.docID and Exercise to ExListActivity
                            /*val intent = Intent(this@WorkoutListActivity, ExerciseListActivity::class.java)
                            intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                            intent.putExtra("EXERCISE", mExercise)
                            startActivity(intent)*/
                            //ExerciseListActivity().addExerciseToWorkout(mExercise)
                        } else {
                            println("Model is null: " + model)
                        }

                    } else {
                        val intent = Intent(
                            this@WorkoutListActivity,
                            WorkoutExerciseListActivity::class.java
                        )
                        intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                        startActivity(intent)
                    }
                }
            })

        }else {
            rv_workout_list.visibility = View.GONE
            tv_add_exercise_to_workout.visibility= View.GONE
            no_workouts_available.visibility = View.VISIBLE
        }
    }

    fun hideButton() {
        if (intent.hasExtra("EXERCISE")) {
            btn_select_workout.visibility= View.GONE
        }
    }

    fun startExercise(workout: Workout) {

        /*val intent = Intent(this@WorkoutListActivity, ExcersiseActivity::class.java)
        intent.putExtra(Constants.DOCUMENT_ID, workout.documentId)
        startActivity(intent)*/

val intent = Intent(this@WorkoutListActivity, MainActivity::class.java)
intent.putExtra(Constants.DOCUMENT_ID, workout.documentId)
startActivity(intent)

}

fun addUpdateExerciseSuccess() {
hideProgressDialog()
println("Exercise added successfully?")

}

private fun setupActionBar() {
setSupportActionBar(toolbar_main_activity)
toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

toolbar_main_activity.setNavigationOnClickListener {
    //Toggle drawer
    toggleDrawer()
}
}

private fun toggleDrawer() {
if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
    drawer_layout.closeDrawer(GravityCompat.START)
} else {
    drawer_layout.openDrawer(GravityCompat.START)
}
}

override fun onBackPressed() {
if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
    drawer_layout.closeDrawer(GravityCompat.START)
} else {
    doubleBackToExit()
}
}

fun updateNavigationUserDetails(user: User, readWorkoutList: Boolean) {

mUserName = user.name

Glide
    .with(this)
    .load(user.image)
    .centerCrop()
    .placeholder(R.drawable.ic_user_place_holder)
    .into(nav_user_image)

tv_username.text = user.name

//Also if I have more data fields they can adjusted, (retirved information for here)

if (readWorkoutList) {
    showProgressDialog(resources.getString(R.string.please_wait))
    FireStoreClass().getWorkoutList(this)


}

}

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
super.onActivityResult(requestCode, resultCode, data)
if (resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE) {
    FireStoreClass().loadUserData(this)
} else if (resultCode == Activity.RESULT_OK && requestCode == CREATE_WORKOUT_REQUEST_CODE) {
    FireStoreClass().getWorkoutList(this)
}

else {
    Log.e("Canceleld", "Cancelled")
}
}

override fun onNavigationItemSelected(item: MenuItem): Boolean {
when (item.itemId) {
    R.id.nav_my_profile -> {
        startActivityForResult(Intent(this@WorkoutListActivity, UpdateProfileActivity::class.java), MY_PROFILE_REQUEST_CODE)
        println("My Profile Pressed")
    }
    R.id.nav_sign_out -> {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, EntryActivity::class.java)
        //Necessaary maybe remove this?
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
drawer_layout.closeDrawer(GravityCompat.START)
return true
}
}