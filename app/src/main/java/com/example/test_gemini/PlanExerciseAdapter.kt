package com.example.test_gemini

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.test_gemini.data.ExerciseWithDetails

class PlanExerciseAdapter(
    private val onItemClick: (ExerciseWithDetails) -> Unit,
    private val onRemoveClick: (ExerciseWithDetails) -> Unit
) : ListAdapter<ExerciseWithDetails, PlanExerciseAdapter.ExerciseViewHolder>(ExerciseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plan_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_exercise_name)
        private val tvSetsReps: TextView = itemView.findViewById(R.id.tv_sets_reps)
        private val btnRemove: ImageButton = itemView.findViewById(R.id.btn_remove_exercise)

        fun bind(exercise: ExerciseWithDetails) {
            tvName.text = exercise.name
            tvSetsReps.text = "${exercise.sets} подходов x ${exercise.reps} повторений"
            itemView.setOnClickListener { onItemClick(exercise) }
            btnRemove.setOnClickListener { onRemoveClick(exercise) }
        }
    }

    class ExerciseDiffCallback : DiffUtil.ItemCallback<ExerciseWithDetails>() {
        override fun areItemsTheSame(oldItem: ExerciseWithDetails, newItem: ExerciseWithDetails) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ExerciseWithDetails, newItem: ExerciseWithDetails) =
            oldItem == newItem
    }
}