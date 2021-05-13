package com.example.fitnessapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.fitnessapp.R
import kotlinx.android.synthetic.main.activity_end.*
import java.text.SimpleDateFormat
import java.util.*

class EndActivity : AppCompatActivity() {

    private var currentExercisePosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end)


        setSupportActionBar(toolbar_end_activity)
        val actionbar = supportActionBar//actionbar

        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true) //set back button
        }

        toolbar_end_activity.setNavigationOnClickListener {
            onBackPressed()
        }

        btnFinish.setOnClickListener {
            finish() //goes to start activitcy becuase this the las one on the stack
        }

        //addDateToDatabase()
        //addExerciseNameToDatabase()

        //val dbHandler = SqliteOpenHelper(this, null)
        //println(dbHandler.getAllCompletedDatesList()[1])


    }
/*
    private fun addDateToDatabase() {
        val calendar = Calendar.getInstance()
        val dateTime = calendar.time
        Log.i("DATE", "" + dateTime)

        val sdf = SimpleDateFormat("dd MM yyyy HH:mm:ss", Locale.getDefault())
        val date = sdf.format(dateTime)

        val dbHandler = SqliteOpenHelper(this, null)
        dbHandler.addDate(date) //addDate func from SqliteOpenHelper class
        Log.i("DATE: ", "Added")
    }

    //if(exercise.getIscompleted == true) {
    //dbHandler.addExercise(exercise)

   private fun addExerciseNameToDatabase() {

        val exercise = "Yoga"

        val dbHandler = SqliteOpenHelper(this, null)
        dbHandler.addExercise(exercise) //addDate func from SqliteOpenHelper class
        Log.i("BExercise: ", "Added")

    }*/

}