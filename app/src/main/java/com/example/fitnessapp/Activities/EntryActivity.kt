package com.example.fitnessapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.fitnessapp.R
import kotlinx.android.synthetic.main.activity_entry.*

class EntryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        btn_sign_up.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        btn_sign_in.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        /*if (intent.hasExtra("EXERLIST")) {

            var workoutDocumentId = intent.getStringArrayListExtra("EXERLIST").toString()
            println("Yo we got here from exerlist heres the list that was passed: " + workoutDocumentId)
        }*/



    }




}