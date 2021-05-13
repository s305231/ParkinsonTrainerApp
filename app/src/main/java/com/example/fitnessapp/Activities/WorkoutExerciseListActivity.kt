package com.example.fitnessapp.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitnessapp.Adapters.WorkoutExerciseListAdapter
import com.example.fitnessapp.Database.FireStoreClass
import com.example.fitnessapp.Model.Exercise
import com.example.fitnessapp.Model.Workout
import com.example.fitnessapp.R
import com.example.fitnessapp.Utils.Constants
import kotlinx.android.synthetic.main.activity_exercise_list.*
import kotlinx.android.synthetic.main.activity_view_all_exercises.*
import kotlinx.android.synthetic.main.activity_workout_exercise_list.*
import kotlinx.android.synthetic.main.item_workout_exercise_list.*

class WorkoutExerciseListActivity : AuxActivity() {

    var workoutDocumentId = ""
    private lateinit var mWorkoutDetails: Workout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_exercise_list)

        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            workoutDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
            println("Intent has extra doc id: " + workoutDocumentId)
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getWorkOutDetails(this, workoutDocumentId)
    }

    fun workoutDetails(workout: Workout) {

        mWorkoutDetails = workout

        val addExerciseList = Exercise("Add List")
        workout.exerciseList.add(addExerciseList)

        if (workout.exerciseList.size > 0) {
            println("Exercise List size greater than 0: " + workout.exerciseList.size)
            rv_workout_exercise_list.visibility = View.VISIBLE
            no_exercises_inside.visibility = View.GONE

            rv_workout_exercise_list.layoutManager = LinearLayoutManager(this)
            rv_workout_exercise_list.setHasFixedSize(true)

            val adapter = WorkoutExerciseListAdapter(this, workout.exerciseList)
            rv_workout_exercise_list.adapter = adapter
        }

        else {
            println("Exercise List size less than 0: " + workout.exerciseList.size)
            rv_workout_exercise_list.visibility = View.GONE
            no_exercises_inside.visibility = View.VISIBLE
        }



        hideProgressDialog()
        setupActionBar()


    }

    fun createNewExercise() {
        val intent = Intent(this@WorkoutExerciseListActivity, CreateExerciseActivity::class.java)
        startActivity(intent)
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_workout_exercise_list_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
            actionBar.title = mWorkoutDetails.name
        }
        toolbar_workout_exercise_list_activity.setNavigationOnClickListener { onBackPressed() }
    }

}