package com.example.fitnessapp.Activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitnessapp.Adapters.AllExercisesAdapter
import com.example.fitnessapp.Adapters.WorkoutListAdapter
import com.example.fitnessapp.Database.FireStoreClass
import com.example.fitnessapp.Model.Exercise
import com.example.fitnessapp.Model.User
import com.example.fitnessapp.Model.Workout
import com.example.fitnessapp.R
import com.example.fitnessapp.Utils.Constants
import kotlinx.android.synthetic.main.activity_exercise_list.*
import kotlinx.android.synthetic.main.activity_view_all_exercises.*
import kotlinx.android.synthetic.main.activity_workout_list.*
import kotlinx.android.synthetic.main.drawer.*
import kotlinx.android.synthetic.main.item_all_exercises.*
import kotlinx.android.synthetic.main.top_bar_main.*

class ViewAllExercises : AuxActivity() {


    companion object {
        const val CREATE_EXERCISE_REQUEST_CODE : Int = 13
    }

    private lateinit var mUserName: String
    private lateinit var mExercise: Exercise



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_exercises)
        setupActionBar()
        FireStoreClass().getExerciseList(this)
        FireStoreClass().loadUserData(this)


        fab_create_exercise.setOnClickListener {
            //We do it this way beacuse we get the username without doin another db request
            val intent = Intent(this, CreateExerciseActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)

            //The paramet request code makes sure that a newly created workout is displayed in the view, beacuse fun workoutCreatedSuccessfully() in CreateWrokoutActivity sets the
            //code as ok, and here in this class in onActivityResult we can execute additional code based on the result of the request code.
            startActivityForResult(intent, CREATE_EXERCISE_REQUEST_CODE)
        }





    }

    fun getUserName(user: User) {

        mUserName = user.name

    }

    //The downloading of the workouts(boards) will happen else where (getWorkoutList in Firestore class), this function will display the workouts ot UI
    fun populateExerciseList(exerciseList: ArrayList<Exercise>) {


//        hideProgressDialog()
        if (exerciseList.size > 0) {
            println("Exercise List size greater than 0: " + exerciseList.size)
            rv_all_exercise_list.visibility = View.VISIBLE
            no_exercises_available.visibility = View.GONE

            rv_all_exercise_list.layoutManager = LinearLayoutManager(this)
            rv_all_exercise_list.setHasFixedSize(true)

            val adapter = AllExercisesAdapter(this, exerciseList)
            rv_all_exercise_list.adapter = adapter

            adapter.setOnClickListener(object : AllExercisesAdapter.OnClickListener {
                override fun onClick(position: Int, model: Exercise) {
                    println("Something will happen when an exercise is pressed, however position is: " + position + " and model is: " + model)
                    mExercise = model

                    val intent = Intent(this@ViewAllExercises, WorkoutListActivity::class.java)
                    intent.putExtra("EXERCISE", model)
                    startActivity(intent)

                }

                /* override fun onClick(position: Int, model: Workout) {
                     val intent = Intent(this@WorkoutListActivity, ExerciseListActivity::class.java)
                     intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                     startActivity(intent)
                 }*/
            })

        } else {
            println("Exercise List size less than 0: " + exerciseList.size)
            rv_all_exercise_list.visibility = View.GONE
            no_exercises_available.visibility = View.VISIBLE
        }


    }

    fun addExerciceToWorkout(exercise: Exercise) {
            val intent = Intent(this@ViewAllExercises, WorkoutListActivity::class.java)
            intent.putExtra("EXERCISE", exercise)
            startActivity(intent)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == ViewAllExercises.CREATE_EXERCISE_REQUEST_CODE) {
            println("Activity result: " + resultCode)
            FireStoreClass().getExerciseList(this)
        }
        else {
            println("Did not enter if statemtn, resultcode: " + resultCode + " request code: " + requestCode)
            Log.e("Canceleld", "Cancelled")
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_all_exercise_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
            actionBar.title = "All Exercises"
        }
        toolbar_all_exercise_activity.setNavigationOnClickListener { onBackPressed() }
    }


}