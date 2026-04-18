package com.example.test_gemini

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.test_gemini.data.AppRepository
import com.example.test_gemini.data.TaskEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ScheduleFragment : Fragment() {

    private lateinit var repository: AppRepository
    private lateinit var adapter: ScheduleTaskAdapter
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = requireActivity() as MainActivity
        repository = mainActivity.repository

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_schedule)
        val tvEmpty = view.findViewById<TextView>(R.id.tv_schedule_empty)
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fab_add_schedule)

        adapter = ScheduleTaskAdapter(
            onTaskClick = { task -> showEditTaskDialog(task) },
            onDeleteClick = { task -> deleteTask(task) },
            onCheckChanged = { task, isChecked -> toggleTaskCompletion(task, isChecked) }
        )

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        val today = dateFormat.format(Date())
        lifecycleScope.launch {
            repository.getTasksWithTimeByDate(today).collect { tasks ->
                adapter.submitList(tasks)
                tvEmpty.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        fabAdd.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_schedule_task, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.et_schedule_title)
        val etDescription = dialogView.findViewById<EditText>(R.id.et_schedule_description)
        val rgTimeType = dialogView.findViewById<RadioGroup>(R.id.rg_time_type)
        val llSingleTime = dialogView.findViewById<LinearLayout>(R.id.ll_single_time)
        val llIntervalTime = dialogView.findViewById<LinearLayout>(R.id.ll_interval_time)
        val tvTime = dialogView.findViewById<TextView>(R.id.tv_selected_time)
        val tvStartTime = dialogView.findViewById<TextView>(R.id.tv_start_time)
        val tvEndTime = dialogView.findViewById<TextView>(R.id.tv_end_time)

        var selectedTime: String? = null
        var startTime: String? = null
        var endTime: String? = null

        // Toggle time type visibility
        rgTimeType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_time_point -> {
                    llSingleTime.visibility = View.VISIBLE
                    llIntervalTime.visibility = View.GONE
                }
                R.id.rb_time_interval -> {
                    llSingleTime.visibility = View.GONE
                    llIntervalTime.visibility = View.VISIBLE
                }
            }
        }

        tvTime.setOnClickListener {
            showTimePicker { time ->
                selectedTime = time
                tvTime.text = time
            }
        }

        tvStartTime.setOnClickListener {
            showTimePicker { time ->
                startTime = time
                tvStartTime.text = time
            }
        }

        tvEndTime.setOnClickListener {
            showTimePicker { time ->
                endTime = time
                tvEndTime.text = time
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Новая задача с временем")
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val title = etTitle.text.toString().trim()
                val description = etDescription.text.toString().trim()
                val isInterval = rgTimeType.checkedRadioButtonId == R.id.rb_time_interval

                if (title.isEmpty()) {
                    Toast.makeText(context, "Введите название", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (isInterval) {
                    if (startTime == null || endTime == null) {
                        Toast.makeText(context, "Выберите время начала и окончания", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    if (startTime!!.compareTo(endTime!!) > 0) {
                        Toast.makeText(context, "Время окончания должно быть позже времени начала", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    addTask(title, description, startTime!!, endTime!!)
                } else {
                    if (selectedTime == null) {
                        Toast.makeText(context, "Выберите время", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    addTask(title, description, selectedTime!!)
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                onTimeSelected(String.format(Locale.US, "%02d:%02d", hourOfDay, minute))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun showEditTaskDialog(task: TaskEntity) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_schedule_task, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.et_schedule_title)
        val etDescription = dialogView.findViewById<EditText>(R.id.et_schedule_description)
        val rgTimeType = dialogView.findViewById<RadioGroup>(R.id.rg_time_type)
        val llSingleTime = dialogView.findViewById<LinearLayout>(R.id.ll_single_time)
        val llIntervalTime = dialogView.findViewById<LinearLayout>(R.id.ll_interval_time)
        val tvTime = dialogView.findViewById<TextView>(R.id.tv_selected_time)
        val tvStartTime = dialogView.findViewById<TextView>(R.id.tv_start_time)
        val tvEndTime = dialogView.findViewById<TextView>(R.id.tv_end_time)

        var selectedTime: String? = task.time
        var startTime: String? = task.time
        var endTime: String? = task.endTime

        // Set initial state
        if (task.endTime != null) {
            rgTimeType.check(R.id.rb_time_interval)
            llSingleTime.visibility = View.GONE
            llIntervalTime.visibility = View.VISIBLE
            tvStartTime.text = task.time ?: "Выберите время"
            tvEndTime.text = task.endTime
        } else {
            rgTimeType.check(R.id.rb_time_point)
            llSingleTime.visibility = View.VISIBLE
            llIntervalTime.visibility = View.GONE
            tvTime.text = task.time ?: "Выберите время"
        }

        rgTimeType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_time_point -> {
                    llSingleTime.visibility = View.VISIBLE
                    llIntervalTime.visibility = View.GONE
                }
                R.id.rb_time_interval -> {
                    llSingleTime.visibility = View.GONE
                    llIntervalTime.visibility = View.VISIBLE
                }
            }
        }

        tvTime.setOnClickListener {
            showTimePicker { time ->
                selectedTime = time
                tvTime.text = time
            }
        }

        tvStartTime.setOnClickListener {
            val currentTime = startTime ?: "12:00"
            showTimePickerWithDefault(currentTime) { time ->
                startTime = time
                tvStartTime.text = time
            }
        }

        tvEndTime.setOnClickListener {
            val currentTime = endTime ?: "12:00"
            showTimePickerWithDefault(currentTime) { time ->
                endTime = time
                tvEndTime.text = time
            }
        }

        etTitle.setText(task.title)
        etDescription.setText(task.description ?: "")

        AlertDialog.Builder(requireContext())
            .setTitle("Редактировать задачу")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val title = etTitle.text.toString().trim()
                val description = etDescription.text.toString().trim()
                val isInterval = rgTimeType.checkedRadioButtonId == R.id.rb_time_interval

                if (title.isEmpty()) return@setPositiveButton

                if (isInterval) {
                    if (startTime == null || endTime == null) {
                        Toast.makeText(context, "Выберите время начала и окончания", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    if (startTime!!.compareTo(endTime!!) > 0) {
                        Toast.makeText(context, "Время окончания должно быть позже времени начала", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    updateTask(task, title, description, startTime, endTime)
                } else {
                    updateTask(task, title, description, selectedTime)
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showTimePickerWithDefault(defaultTime: String, onTimeSelected: (String) -> Unit) {
        val parts = defaultTime.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 12
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute2 ->
                onTimeSelected(String.format(Locale.US, "%02d:%02d", hourOfDay, minute2))
            },
            hour,
            minute,
            true
        ).show()
    }

    private fun addTask(title: String, description: String, time: String, endTime: String? = null) {
        if (endTime != null && time.compareTo(endTime) > 0) {
            Toast.makeText(context, "Время окончания должно быть позже времени начала", Toast.LENGTH_SHORT).show()
            return
        }
        val today = dateFormat.format(Date())
        val task = TaskEntity(
            title = title,
            description = description.ifEmpty { null },
            isCompleted = false,
            date = today,
            time = time,
            endTime = endTime
        )
        lifecycleScope.launch {
            repository.insertTask(task)
            Toast.makeText(context, "Задача добавлена", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTask(task: TaskEntity, title: String, description: String, time: String?, endTime: String? = null) {
        if (endTime != null && time != null && time.compareTo(endTime) > 0) {
            Toast.makeText(context, "Время окончания должно быть позже времени начала", Toast.LENGTH_SHORT).show()
            return
        }
        val updatedTask = task.copy(
            title = title,
            description = description.ifEmpty { null },
            time = time,
            endTime = endTime
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