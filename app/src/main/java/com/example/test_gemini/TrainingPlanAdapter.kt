package com.example.test_gemini

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.test_gemini.data.TrainingPlanEntity

class TrainingPlanAdapter(
    private val onItemClick: (TrainingPlanEntity) -> Unit
) : ListAdapter<TrainingPlanEntity, TrainingPlanAdapter.PlanViewHolder>(PlanDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_training_plan, parent, false)
        return PlanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_plan_name)
        private val tvDescription: TextView = itemView.findViewById(R.id.tv_plan_description)
        private val tvExercisesCount: TextView = itemView.findViewById(R.id.tv_plan_exercises_count)

        fun bind(plan: TrainingPlanEntity) {
            tvName.text = plan.name
            tvDescription.text = plan.description ?: "Без описания"
            tvExercisesCount.text = "Упражнений в плане"
            itemView.setOnClickListener { onItemClick(plan) }
        }
    }

    class PlanDiffCallback : DiffUtil.ItemCallback<TrainingPlanEntity>() {
        override fun areItemsTheSame(oldItem: TrainingPlanEntity, newItem: TrainingPlanEntity) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: TrainingPlanEntity, newItem: TrainingPlanEntity) =
            oldItem == newItem
    }
}
