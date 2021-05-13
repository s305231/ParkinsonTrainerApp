package com.example.fitnessapp.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitnessapp.Activities.WorkoutListActivity
import com.example.fitnessapp.Database.FireStoreClass
import com.example.fitnessapp.Model.User
import com.example.fitnessapp.Model.Workout
import com.example.fitnessapp.R
import com.example.fitnessapp.Utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.item_workout.view.*

open class WorkoutListAdapter(private val context: Context, private var list: ArrayList<Workout>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener : OnClickListener? = null
    private var activate: Boolean = false
    var name: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_workout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getName()
        val model = list[position]
        if (holder is MyViewHolder) {
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_workout_place_holder)
                .into(holder.itemView.civ_workout_image)

            holder.itemView.tv_name.text = model.name
            holder.itemView.tv_created_by.text = "Created by:  ${model.createdBy}"

            if (model.createdBy == name)
            {
                holder.itemView.setBackgroundColor(Color.LTGRAY)
            }

            if (activate) {
                holder.itemView.btn_select_workout.visibility = View.GONE
            }


            holder.itemView.btn_select_workout.setOnClickListener{
                println("model is:" + model.createdBy + " |username is:" + name)


                if (context is WorkoutListActivity) {
                    context.startExercise(model)
                }
            }

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }


        }

    }

    fun getName() {
          FirebaseFirestore.getInstance().collection(Constants.USERS)
                  .document(FireStoreClass().getCurrentUserId()).get()
                  .addOnSuccessListener {
                      documentSnapshot ->
                      val loggedInUser = documentSnapshot.toObject(User::class.java)!!
                      name = loggedInUser.name
                      println("From Adapter: " + name)
                  }
    }

    open fun activateButtons(activate: Boolean) {
        this.activate = activate
        notifyDataSetChanged()
    }

    interface OnClickListener{
        fun onClick(position: Int, model: Workout)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}