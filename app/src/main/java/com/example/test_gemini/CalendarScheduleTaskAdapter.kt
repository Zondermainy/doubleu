package com.example.test_gemini

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.test_gemini.data.TaskEntity

class CalendarScheduleTaskAdapter : ListAdapter<TaskEntity, CalendarScheduleTaskAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTime: TextView = itemView.findViewById(R.id.tv_task_time)
        private val tvEndTime: TextView = itemView.findViewById(R.id.tv_end_time)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_schedule_title)
        private val tvDescription: TextView = itemView.findViewById(R.id.tv_schedule_description)
        private val cbCompleted: CheckBox = itemView.findViewById(R.id.cb_schedule_completed)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete_schedule)

        fun bind(task: TaskEntity) {
            tvTime.text = task.time ?: ""
            if (task.endTime != null) {
                tvEndTime.text = "– ${task.endTime}"
                tvEndTime.visibility = View.VISIBLE
            } else {
                tvEndTime.visibility = View.GONE
            }
            tvTitle.text = task.title
            if (!task.description.isNullOrBlank()) {
                tvDescription.text = task.description
                tvDescription.visibility = View.VISIBLE
            } else {
                tvDescription.visibility = View.GONE
            }

            // Скрываем элементы управления
            cbCompleted.visibility = View.GONE
            btnDelete.visibility = View.GONE

            // Никаких действий по клику
            itemView.setOnClickListener(null)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity) = oldItem == newItem
    }
}