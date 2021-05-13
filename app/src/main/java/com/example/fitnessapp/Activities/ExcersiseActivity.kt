package com.example.fitnessapp.Activities

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.fitnessapp.Adapters.ExerciseStatusAdapter
import com.example.fitnessapp.Database.FireStoreClass
import com.example.fitnessapp.Model.Exercise
import com.example.fitnessapp.Model.Workout
import com.example.fitnessapp.R
import com.example.fitnessapp.Utils.Constants
import kotlinx.android.synthetic.main.activity_excersise.*
import kotlinx.android.synthetic.main.activity_exercise_list.*
import kotlinx.android.synthetic.main.dialog_custom_back_confirmation.*
import java.util.*


class ExcersiseActivity : AuxActivity(), TextToSpeech.OnInitListener {

    //private lateinit var db: FirebaseFirestore


    private lateinit var mWorkoutDetails : Workout
    private lateinit var model: ArrayList<Exercise>
    var idNr : Long = 0
    var workoutDocumentId = ""

    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null

    private var restTimer: CountDownTimer? = null
    private var restProgress = 0
    private var restTimeDuration : Long = 4

    private var exerTimer: CountDownTimer? = null
    private var exerProgress = 0
    private var excersiceDuration: Long = 10

    private var currentExercisePosition = -1

    private var exercisesAdapter: ExerciseStatusAdapter? = null



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_excersise)

        val strtBtn = findViewById<Button>(R.id.startEx)


        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            workoutDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        FireStoreClass().getWorkOutDetails(this, workoutDocumentId)


        tts = TextToSpeech(this, this)

        //("Two different sets of workout plans, create an option choose plan via buttons, and then activate the corresponding plan")
        //exerciseList = Exercises.pdExerciseList()

        if (intent.hasExtra("EXERLIST")) {

            var fireExercise = intent.getStringArrayListExtra("EXERLIST")
            println("Yo we got here from exerlist heres the list that was passed: " + fireExercise.toString())
        }





        changeExercise()

        strtBtn.setOnClickListener() {

            if (model.size > 0) {
                setupExerciseStatusRecyclerView()
                setupRestView()
                println("Button clikced yea")
            } else {
                Toast.makeText(this, "The selected workout does not contain any exercises", Toast.LENGTH_SHORT).show()
            }



            /*val citiesRef = db.collection("workouts")
            val query = citiesRef.whereEqualTo("createdBy", "danial")
            println("Query result:" + query)*/

            /*val model =  mWorkoutDetails.exerciseList[1]
            println("Size of list is: " + mWorkoutDetails.exerciseList.size)
            println(model.name)*/

        }




    }

    fun workoutDetails(workout: Workout) {
        mWorkoutDetails = workout

         model =  mWorkoutDetails.exerciseList

        setupActionBar()


    }


    private fun setupActionBar() {
        setSupportActionBar(toolbar_exercise_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
            actionBar.title = mWorkoutDetails.name
        }
        toolbar_exercise_activity.setNavigationOnClickListener { customDialogForBackButton() }
    }

    fun addUpdateExerciseSuccess() {

        //hideProgressDialog()
        //showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getWorkOutDetails(this, mWorkoutDetails.documentId)
    }


    private fun changeExercise () {

/*val switch = findViewById<Switch>(R.id.switchExercise)

switch?.setOnCheckedChangeListener { _, isChecked ->
    val message = if (isChecked) "Switch1:ON" else "Switch1:OFF"
    println(message)

    if (isChecked) {
        exerciseList = Exercises.pdExerciseList()

    } else {
        exerciseList = Exercises.defaultExerciseList()
    }

}*/

}
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setupRestView() {
    try {
        player = MediaPlayer.create(applicationContext, R.raw.press_start)
        player!!.isLooping = false
        player!!.start()
    }catch (e: Exception) {
        e.printStackTrace()
    }

    val llRestView = findViewById(R.id.llRestView) as LinearLayout
    val llExerView = findViewById(R.id.llExcersiceView) as LinearLayout
    val tvUpcoming = findViewById(R.id.tvUpcomingExerciseName) as TextView
        val strtBtn = findViewById<Button>(R.id.startEx)

        var nameHolder: String



    llRestView.visibility = View.VISIBLE
    llExerView.visibility = View.GONE
        strtBtn.visibility = View.GONE


    if (restTimer != null) {
        restTimer!!.cancel()
        restProgress = 0
    }

    setRestProgressBar()
    //nameHolder = exerciseList!![currentExercisePosition + 1].getName()
    nameHolder = model[currentExercisePosition + 1].name

    tvUpcoming.text = nameHolder
    speakOut(nameHolder)



    }

    /*fun getBitmapFromURL(src: String?): Bitmap? {
    return try {
        Log.e("src", src!!)
        val url = URL(src)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.setDoInput(true)
        connection.connect()
        val input: InputStream = connection.getInputStream()
        val myBitmap = BitmapFactory.decodeStream(input)
        Log.e("Bitmap", "returned")
        myBitmap
    } catch (e: IOException) {
        e.printStackTrace()
        Log.e("Exception", "Failed to load image from URL")
        null
    }
    }*/



    private fun setupExerView() {

    val llRestView = findViewById<LinearLayout>(R.id.llRestView)
    val llExerView = findViewById(R.id.llExcersiceView) as LinearLayout
    val imageView = findViewById(R.id.ivImage) as ImageView

    val tvEx = findViewById(R.id.tvExercise) as TextView


    llRestView.visibility = View.GONE
    llExerView.visibility = View.VISIBLE


    if (exerTimer != null) {
        exerTimer!!.cancel()
        exerProgress = 0
    }

    setExerProgressBar()

    //Code for hardoced workouts
    /*imageView.setImageResource(exerciseList!![currentExercisePosition].getImage())
    tvEx.text = exerciseList!![currentExercisePosition].getName()*/

    //imageView.setImageResource(model!![currentExercisePosition].image.toInt())
    //imageView.setImageBitmap(getBitmapFromURL(model!![currentExercisePosition].image))
    Glide.with(this).load(model!![currentExercisePosition].image).into(imageView)
    tvEx.text = model!![currentExercisePosition].name

    }

    private fun setExerProgressBar() {

    val exerBar = findViewById(R.id.excersiceprogressBar) as ProgressBar
    val exTimer = findViewById(R.id.excersiceTimer) as TextView

    exerBar.progress = exerProgress
    exerTimer = object : CountDownTimer(excersiceDuration * 1000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            exerProgress++
            exerBar.progress = excersiceDuration.toInt() - exerProgress
            exTimer.text = (excersiceDuration.toInt() - exerProgress).toString()
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onFinish() {
            //if(currentExercisePosition < exerciseList?.size!! - 1)
            if(currentExercisePosition < model?.size!! - 1)
            {
                /*val updatedExercise = Exercise(model[currentExercisePosition].name, FireStoreClass().getCurrentUserId(), model[currentExercisePosition].duration, model[currentExercisePosition].id, model[currentExercisePosition].image, true, isSelected = false)
                //Figure how to find oout which index
                model!!.removeAt(currentExercisePosition)
                mWorkoutDetails.exerciseList.add(currentExercisePosition, updatedExercise)
                FireStoreClass().addUpdateExercise(this@ExcersiseActivity, mWorkoutDetails)*/
                    model[currentExercisePosition].setIsCompleted(true)
                exercisesAdapter!!.notifyDataSetChanged()
                setupRestView()
            }
            else {
                /*val updatedExercise = Exercise(model[currentExercisePosition].name, FireStoreClass().getCurrentUserId(), model[currentExercisePosition].duration, model[currentExercisePosition].id, model[currentExercisePosition].image, false, isSelected = false)
                //Figure how to find oout which index
                model!!.removeAt(currentExercisePosition)
                mWorkoutDetails.exerciseList.add(currentExercisePosition, updatedExercise)
                FireStoreClass().addUpdateExercise(this@ExcersiseActivity, mWorkoutDetails)*/
                finish() //finishes this current activity
                val intent = Intent(this@ExcersiseActivity, EndActivity::class.java) //start new intent acticity
                startActivity(intent)
            }
        }
    }.start()
    }


    private fun setRestProgressBar() {

    val norgesBar = findViewById(R.id.progressBar) as ProgressBar
    val nTimer = findViewById(R.id.tvTimer) as TextView

    norgesBar.progress = restProgress
    restTimer = object : CountDownTimer(restTimeDuration * 1000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            restProgress++
            norgesBar.progress = restTimeDuration.toInt() - restProgress
            nTimer.text = (restTimeDuration.toInt() - restProgress).toString()
        }

        override fun onFinish() {
            currentExercisePosition++

            /*val updatedExercise = Exercise(model[currentExercisePosition].name, FireStoreClass().getCurrentUserId(), model[currentExercisePosition].duration, model[currentExercisePosition].id, model[currentExercisePosition].image, false, isSelected = true)
            //Figure how to find oout which index
            model!!.removeAt(currentExercisePosition)
            mWorkoutDetails.exerciseList.add(currentExercisePosition, updatedExercise)
            FireStoreClass().addUpdateExercise(this@ExcersiseActivity, mWorkoutDetails)
            //exerciseList!![currentExercisePosition].setIsSelected(true)
            model!![currentExercisePosition].isSelected(true)*/
            model[currentExercisePosition].setIsSelected(true)
            exercisesAdapter!!.notifyDataSetChanged()
            setupExerView()

        }
    }.start()
    }



    override fun onDestroy() {
    if (restTimer != null) {
        restTimer!!.cancel()
        restProgress = 0
    }

    if (exerTimer != null) {
        exerTimer!!.cancel()
        exerProgress = 0
    }

    if (tts != null) {
        tts!!.stop()
        tts!!.shutdown()
    }

    if (player != null) {
        player!!.stop()
    }

    super.onDestroy()


    }

    override fun onInit(status: Int) {

    if (status == TextToSpeech.SUCCESS) {
        //set US English as language for tts
        val result = tts!!.setLanguage(Locale.US)

        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("TTS", "The specified language is not supported")
        } else {
            Log.e("TTS", "Initialization failed")
        }
    }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun speakOut(text: String) {
    tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun setupExerciseStatusRecyclerView() {
    rvExerciseStatus.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    //exercisesAdapter = ExerciseStatusAdapter(exerciseList!!, this)
    exercisesAdapter = ExerciseStatusAdapter(model!!, this)
    rvExerciseStatus.adapter = exercisesAdapter
    }

    private fun customDialogForBackButton() {
    val customDialog = Dialog(this)
    //Null Pointer exception bug
    exerTimer!!.cancel()

    customDialog.setContentView(R.layout.dialog_custom_back_confirmation)
    customDialog.tvYes.setOnClickListener {
        finish()
        //onBackPressed()
        customDialog.dismiss()
    }

    customDialog.tvNo.setOnClickListener {
        customDialog.dismiss()
        exerTimer!!.start()
    }
    customDialog.show()
    }


    }

    private operator fun Boolean.invoke(b: Boolean) {

    }
