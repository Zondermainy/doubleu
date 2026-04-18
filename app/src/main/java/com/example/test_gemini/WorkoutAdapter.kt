package com.example.test_gemini

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test_gemini.data.WorkoutItem

class WorkoutAdapter(
    private val onWorkoutClick: (WorkoutItem) -> Unit,
    private val onDeleteClick: (WorkoutItem) -> Unit,
    private val onWorkoutCheckChanged: (WorkoutItem, Boolean) -> Unit,
    private val onExerciseCheckChanged: (Long, Boolean) -> Unit
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    private var workouts: List<WorkoutItem> = emptyList()

    fun submitList(list: List<WorkoutItem>) {
        workouts = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(workouts[position])
    }

    override fun getItemCount(): Int = workouts.size

    inner class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cbCompleted: CheckBox = itemView.findViewById(R.id.cb_workout_completed)
        private val tvName: TextView = itemView.findViewById(R.id.tv_workout_name)
        private val tvProgress: TextView = itemView.findViewById(R.id.tv_workout_progress)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete_workout)
        private val rvExercises: RecyclerView = itemView.findViewById(R.id.rv_exercises)

        private val exerciseAdapter = WorkoutExerciseAdapter { exercise, isChecked ->
            onExerciseCheckChanged(exercise.id, isChecked)
        }

        init {
            rvExercises.layoutManager = LinearLayoutManager(itemView.context)
            rvExercises.adapter = exerciseAdapter
        }

        fun bind(workout: WorkoutItem) {
            val w = workout.workout
            tvName.text = w.name
            tvProgress.text = "${workout.completedCount}/${workout.totalCount}"

            cbCompleted.setOnCheckedChangeListener(null)
            cbCompleted.isChecked = w.isCompleted

            if (w.isCompleted) {
                tvName.paintFlags = tvName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvName.alpha = 0.5f
            } else {
                tvName.paintFlags = tvName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                tvName.alpha = 1f
            }

            exerciseAdapter.submitList(workout.exercises)

            cbCompleted.setOnCheckedChangeListener { _, isChecked ->
                onWorkoutCheckChanged(workout, isChecked)
            }

            itemView.setOnClickListener { onWorkoutClick(workout) }
            btnDelete.setOnClickListener { onDeleteClick(workout) }
        }
    }
}