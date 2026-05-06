package com.example.test_gemini

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test_gemini.data.WorkoutEntity

class WorkoutDisplayAdapter(
    private val workouts: List<WorkoutEntity>
) : RecyclerView.Adapter<WorkoutDisplayAdapter.WorkoutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout_display, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(workouts[position])
    }

    override fun getItemCount(): Int = workouts.size

    fun updateList(newList: List<WorkoutEntity>) {
        // Для простоты обновляем через notifyDataSetChanged.
        // В реальном проекте лучше использовать DiffUtil.
        (workouts as? MutableList)?.apply {
            clear()
            addAll(newList)
            notifyDataSetChanged()
        }
    }

    inner class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_workout_name)

        fun bind(workout: WorkoutEntity) {
            tvName.text = "🏋️ ${workout.name}"
            // Можно добавить другие данные, например, время, если нужно.
        }
    }
}
