package com.example.test_gemini

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test_gemini.data.WorkoutExerciseItem

class WorkoutExerciseAdapter(
    private val onExerciseCheckChanged: (WorkoutExerciseItem, Boolean) -> Unit
) : RecyclerView.Adapter<WorkoutExerciseAdapter.ExerciseViewHolder>() {

    private var exercises: List<WorkoutExerciseItem> = emptyList()

    fun submitList(list: List<WorkoutExerciseItem>) {
        exercises = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(exercises[position])
    }

    override fun getItemCount(): Int = exercises.size

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cbCompleted: CheckBox = itemView.findViewById(R.id.cb_exercise_completed)
        private val tvName: TextView = itemView.findViewById(R.id.tv_exercise_name)
        private val tvDetails: TextView = itemView.findViewById(R.id.tv_exercise_details)

        fun bind(exercise: WorkoutExerciseItem) {
            tvName.text = exercise.exerciseName
            tvDetails.text = "${exercise.sets} подходов x ${exercise.reps} повторений (${exercise.muscleGroup})"

            cbCompleted.setOnCheckedChangeListener(null)
            cbCompleted.isChecked = exercise.isCompleted

            cbCompleted.setOnCheckedChangeListener { _, isChecked ->
                onExerciseCheckChanged(exercise, isChecked)
            }
        }
    }
}