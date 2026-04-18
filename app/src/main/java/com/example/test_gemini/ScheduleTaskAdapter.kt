package com.example.test_gemini

import android.graphics.Paint
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

class ScheduleTaskAdapter(
    private val onTaskClick: (TaskEntity) -> Unit,
    private val onDeleteClick: (TaskEntity) -> Unit,
    private val onCheckChanged: (TaskEntity, Boolean) -> Unit
) : ListAdapter<TaskEntity, ScheduleTaskAdapter.ScheduleTaskViewHolder>(ScheduleTaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleTaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule_task, parent, false)
        return ScheduleTaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleTaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ScheduleTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTime: TextView = itemView.findViewById(R.id.tv_task_time)
        private val cbCompleted: CheckBox = itemView.findViewById(R.id.cb_schedule_completed)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_schedule_title)
        private val tvDescription: TextView = itemView.findViewById(R.id.tv_schedule_description)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete_schedule)

        fun bind(task: TaskEntity) {
            tvTime.text = if (task.endTime != null) {
                "${task.time} - ${task.endTime}"
            } else {
                task.time ?: ""
            }
            tvTitle.text = task.title
            tvDescription.text = task.description ?: ""

            cbCompleted.setOnCheckedChangeListener(null)
            cbCompleted.isChecked = task.isCompleted

            if (task.isCompleted) {
                tvTitle.paintFlags = tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvTitle.alpha = 0.5f
                tvDescription.alpha = 0.5f
            } else {
                tvTitle.paintFlags = tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                tvTitle.alpha = 1f
                tvDescription.alpha = 1f
            }

            cbCompleted.setOnCheckedChangeListener { _, isChecked ->
                onCheckChanged(task, isChecked)
            }

            itemView.setOnClickListener { onTaskClick(task) }
            btnDelete.setOnClickListener { onDeleteClick(task) }
        }
    }

    class ScheduleTaskDiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity) =
            oldItem == newItem
    }
}