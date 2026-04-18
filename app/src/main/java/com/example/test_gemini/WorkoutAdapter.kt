package com.example.test_gemini

import android.animation.ValueAnimator
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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
    private val expandedItems = mutableSetOf<Int>()

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
        holder.bind(workouts[position], position)
    }

    override fun getItemCount(): Int = workouts.size

    inner class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivExpandArrow: ImageView = itemView.findViewById(R.id.iv_expand_arrow)
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

        fun bind(workout: WorkoutItem, position: Int) {
            val w = workout.workout
            tvName.text = w.name
            tvProgress.text = "${workout.completedCount}/${workout.totalCount}"

            val isExpanded = expandedItems.contains(position)
            rvExercises.visibility = if (isExpanded) View.VISIBLE else View.GONE

            val targetRotation = if (isExpanded) 90 else 0
            ivExpandArrow.rotation = targetRotation.toFloat()

            if (w.isCompleted) {
                tvName.paintFlags = tvName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvName.alpha = 0.5f
            } else {
                tvName.paintFlags = tvName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                tvName.alpha = 1f
            }

            exerciseAdapter.submitList(workout.exercises)

            itemView.setOnClickListener {
                val newExpanded = !expandedItems.contains(position)
                if (newExpanded) {
                    expandedItems.add(position)
                } else {
                    expandedItems.remove(position)
                }

                rvExercises.visibility = if (newExpanded) View.VISIBLE else View.GONE

                ValueAnimator.ofFloat(ivExpandArrow.rotation, if (newExpanded) 90f else 0f).apply {
                    duration = 200
                    addUpdateListener { ivExpandArrow.rotation = it.animatedValue as Float }
                    start()
                }
            }

            btnDelete.setOnClickListener { onDeleteClick(workout) }
        }
    }
}