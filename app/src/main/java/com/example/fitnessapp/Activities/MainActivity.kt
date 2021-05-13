package com.example.fitnessapp.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.fitnessapp.Database.FireStoreClass
import com.example.fitnessapp.Model.Workout
import com.example.fitnessapp.R
import com.example.fitnessapp.Utils.Constants
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AuxActivity() {
    var workoutDocumentId = ""
    private lateinit var mWorkoutDetails : Workout


    fun workoutDetails(workout: Workout) {
        mWorkoutDetails = workout
        tv_workout_name.text = "Selected workout plan: " + mWorkoutDetails.name
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            workoutDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
            FireStoreClass().getWorkOutDetails(this, workoutDocumentId)

        }








        llStart.setOnClickListener {

            // val intent = Intent(this, WorkoutListActivity::class.java)
            val intent = Intent(this, ExcersiseActivity::class.java)
            intent.putExtra(Constants.DOCUMENT_ID, workoutDocumentId)

            if (workoutDocumentId.isNotEmpty()) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please select a workout first", Toast.LENGTH_SHORT).show()
            }
        }
            llExercise.setOnClickListener {
                val intent = Intent(this, ViewAllExercises::class.java)
                startActivity(intent)
            }

            llWorkouts.setOnClickListener {
                val intent = Intent(this, WorkoutListActivity::class.java)
                startActivity(intent)
            }



    }



}