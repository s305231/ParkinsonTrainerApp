package com.example.fitnessapp.Adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.Activities.AuxActivity
import com.example.fitnessapp.Activities.ExerciseListActivity
import com.example.fitnessapp.Model.Exercise
import com.example.fitnessapp.R
import kotlinx.android.synthetic.main.item_exercise.view.*

open class ExerciseListAdapter(private val context: Context, private var list: ArrayList<Exercise> ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener : ExerciseListAdapter.OnClickListener? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_exercise, parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(15.toDp().toPx(), 0, (40.toDp().toPx()), 0)
        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            if (position == list.size - 1) {
                //If there are no exercises we want to view the add list textview
                holder.itemView.tv_add_task_list.visibility = View.VISIBLE
                holder.itemView.ll_task_item.visibility = View.GONE

            } else {
                holder.itemView.tv_add_task_list.visibility = View.GONE
                holder.itemView.ll_task_item.visibility = View.VISIBLE
            }
            holder.itemView.tv_task_list_title.text = model.name
            holder.itemView.tv_add_task_list.setOnClickListener {

                holder.itemView.tv_add_task_list.visibility = View.GONE
                //When pressing the add task list the text field view appears
                holder.itemView.cv_add_task_list_name.visibility = View.VISIBLE

            }

            //This when the cancel x button is pressed
            holder.itemView.ib_close_list_name.setOnClickListener {
                holder.itemView.tv_add_task_list.visibility = View.VISIBLE
                holder.itemView.cv_add_task_list_name.visibility = View.GONE

            }
            // This is when the ceckmark or done button is pressed, we want to add the name to the DB.
            holder.itemView.ib_done_list_name.setOnClickListener{

                println("Checkmark clicked")

                val listName = holder.itemView.et_task_list_name.text.toString()
                val duration = holder.itemView.et_duration.text.toString()

                if (listName.isNotEmpty() && duration.isNotEmpty()) {
                    if (context is ExerciseListActivity) {
                        //context.createExerciseList(listName, duration)
                            //This will only create a exercise if there is a image chosen
                        //Needs to be uncommented
                        context.uploadExerciseImage(listName,duration)
                    }
                } else {
                    Toast.makeText(context, "Please enter listname", Toast.LENGTH_SHORT).show()
                }

            }


        }


        //This is for image and to have a view similar to the workouts view, want to implement this
        /*val model = list[position]
        if (holder is MyViewHolder) {
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_exercise_placeholder)
                .into(holder.itemView.civ_exercise_image)

            holder.itemView.tv_name.text = model.name
            holder.itemView.tv_duration.text = model.duration.toString()
            holder.itemView.tv_created_by.text = "Created by:  ${model.createdBy}"

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }


        }*/

    }

    private fun Int.toDp():
            Int= (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx():
            Int= (this * Resources.getSystem().displayMetrics.density).toInt()

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