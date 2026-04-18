package com.example.test_gemini

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.test_gemini.data.AppRepository
import com.example.test_gemini.data.TaskEntity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TasksFragment : Fragment() {

    private lateinit var repository: AppRepository
    private lateinit var adapter: TaskAdapter
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = requireActivity() as MainActivity
        repository = mainActivity.repository

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_tasks)
        val tvEmpty = view.findViewById<TextView>(R.id.tv_empty)
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fab_add_task)

        adapter = TaskAdapter(
            onTaskClick = { task -> showEditTaskDialog(task) },
            onDeleteClick = { task -> deleteTask(task) },
            onCheckChanged = { task, isChecked -> toggleTaskCompletion(task, isChecked) }
        )

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        val today = dateFormat.format(Date())
        lifecycleScope.launch {
            repository.getTasksWithoutTimeByDate(today).collect { tasks ->
                adapter.submitList(tasks)
                tvEmpty.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        fabAdd.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_task, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.et_task_title)
        val etDescription = dialogView.findViewById<EditText>(R.id.et_task_description)

        AlertDialog.Builder(requireContext())
            .setTitle("Новая задача")
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val title = etTitle.text.toString().trim()
                val description = etDescription.text.toString().trim()
                if (title.isNotEmpty()) {
                    addTask(title, description)
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showEditTaskDialog(task: TaskEntity) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_task, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.et_task_title)
        val etDescription = dialogView.findViewById<EditText>(R.id.et_task_description)

        etTitle.setText(task.title)
        etDescription.setText(task.description ?: "")

        AlertDialog.Builder(requireContext())
            .setTitle("Редактировать задачу")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val title = etTitle.text.toString().trim()
                val description = etDescription.text.toString().trim()
                if (title.isNotEmpty()) {
                    updateTask(task, title, description)
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun addTask(title: String, description: String) {
        val today = dateFormat.format(Date())
        val task = TaskEntity(
            title = title,
            description = description.ifEmpty { null },
            isCompleted = false,
            date = today,
            time = null
        )
        lifecycleScope.launch {
            repository.insertTask(task)
            Toast.makeText(context, "Задача добавлена", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTask(task: TaskEntity, title: String, description: String) {
        val updatedTask = task.copy(
            title = title,
            description = description.ifEmpty { null }
        )
        lifecycleScope.launch {
            repository.updateTask(updatedTask)
            Toast.makeText(context, "Задача обновлена", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteTask(task: TaskEntity) {
        lifecycleScope.launch {
            repository.deleteTask(task)
            Toast.makeText(context, "Задача удалена", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleTaskCompletion(task: TaskEntity, completed: Boolean) {
        lifecycleScope.launch {
            repository.setTaskCompleted(task.id, completed, task.date)
        }
    }
}