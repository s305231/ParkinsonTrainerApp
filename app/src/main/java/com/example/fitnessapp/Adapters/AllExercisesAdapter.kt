package com.example.fitnessapp.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitnessapp.Activities.ViewAllExercises
import com.example.fitnessapp.Activities.WorkoutListActivity
import com.example.fitnessapp.Model.Exercise
import com.example.fitnessapp.R
import kotlinx.android.synthetic.main.item_all_exercises.*
import kotlinx.android.synthetic.main.item_all_exercises.view.*


open class AllExercisesAdapter (private val context: Context, private var list: ArrayList<Exercise>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_all_exercises, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_exercise_placeholder)
                .into(holder.itemView.civ_exercise_image)


            holder.itemView.tv_name.text = model.name
            holder.itemView.tv_duration.text = "Duration: " + model.duration
            holder.itemView.tv_created_by.text = "Created by: " + model.createdBy

            holder.itemView.btn_add_to_workout.setOnClickListener{
                println("Add To Workout Button Pressed")
                if (context is ViewAllExercises) {
                    context.addExerciceToWorkout(model)
                }
            }

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }

            }


        }

    }

    interface OnClickListener{
        fun onClick(position: Int, model: Exercise)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}