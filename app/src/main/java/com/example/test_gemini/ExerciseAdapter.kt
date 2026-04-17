package com.example.test_gemini

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.test_gemini.data.ExerciseEntity

class ExerciseAdapter(
    private val onItemClick: (ExerciseEntity) -> Unit
) : ListAdapter<ExerciseEntity, ExerciseAdapter.ExerciseViewHolder>(ExerciseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_exercise_name)
        private val tvMuscleGroup: TextView = itemView.findViewById(R.id.tv_exercise_muscle_group)
        private val tvDescription: TextView = itemView.findViewById(R.id.tv_exercise_description)

        fun bind(exercise: ExerciseEntity) {
            tvName.text = exercise.name
            tvMuscleGroup.text = exercise.muscleGroup
            tvDescription.text = exercise.description
            itemView.setOnClickListener { onItemClick(exercise) }
        }
    }

    class ExerciseDiffCallback : DiffUtil.ItemCallback<ExerciseEntity>() {
        override fun areItemsTheSame(oldItem: ExerciseEntity, newItem: ExerciseEntity) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ExerciseEntity, newItem: ExerciseEntity) =
            oldItem == newItem
    }
}
