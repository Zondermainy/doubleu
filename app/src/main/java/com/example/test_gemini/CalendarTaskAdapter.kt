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

class CalendarTaskAdapter : ListAdapter<TaskEntity, CalendarTaskAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)  // используем существующий макет
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_task_title)
        private val tvDescription: TextView = itemView.findViewById(R.id.tv_task_description)
        private val cbCompleted: CheckBox = itemView.findViewById(R.id.cb_task_completed)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete_task)

        fun bind(task: TaskEntity) {
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

            // Никаких слушателей не вешаем – просто просмотр
            itemView.setOnClickListener(null)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity) = oldItem == newItem
    }
}