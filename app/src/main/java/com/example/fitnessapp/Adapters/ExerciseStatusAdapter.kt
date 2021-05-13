package com.example.fitnessapp.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.Model.Exercise
import com.example.fitnessapp.R
import kotlinx.android.synthetic.main.item_exercise_status.view.*
import kotlin.collections.ArrayList


class ExerciseStatusAdapter(private var list: ArrayList<Exercise>, val context: Context) : RecyclerView.Adapter<ExerciseStatusAdapter.ViewHolder>(){

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){

        val tvItem = view.tvItem

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context
        ).inflate(R.layout.item_exercise_status, parent, false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //val model: ExerciseModel = items[position]
        val model = list[position]
        holder.tvItem.text = model.id.toString().reversed()

        if (model.getIsSelected()) {
            //holder.tvItem.background = ContextCompat.getDrawable(context, R.drawable.item_circular_thin_color_accent_border)
            //holder.tvItem.setTextColor(Color.parseColor("#212121"))
            holder.tvItem.background = ContextCompat.getDrawable(context, R.drawable.item_circular_color_accent_background)
            holder.tvItem.setTextColor(Color.parseColor("#FFFFFF"))
        } else if (model.getIsCompleted()) {
            holder.tvItem.background = ContextCompat.getDrawable(context, R.drawable.item_circular_color_accent_background)
            holder.tvItem.setTextColor(Color.parseColor("#FFFFFF"))
        } /*else {
            holder.tvItem.background = ContextCompat.getDrawable(context, R.drawable.item_circular_color_gray_background)
            holder.tvItem.setTextColor(Color.parseColor("#212121"))
        }*/
    }

    override fun getItemCount(): Int {
        return list.size
    }

}