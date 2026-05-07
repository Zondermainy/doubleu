package com.example.test_gemini

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test_gemini.data.AppDatabase
import com.example.test_gemini.data.AppRepository
import com.example.test_gemini.viewmodels.CalendarViewModel
import com.example.test_gemini.viewmodels.CalendarViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var viewModel: CalendarViewModel
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private lateinit var scheduleAdapter: CalendarScheduleTaskAdapter
    private lateinit var taskAdapter: CalendarTaskAdapter
    private lateinit var workoutDisplayAdapter: WorkoutDisplayAdapter
    private lateinit var concatAdapter: ConcatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val repository = AppRepository(
            database.taskDao(),
            database.workoutDao(),
            database.workoutExerciseDao(),
            database.dailyHistoryDao(),
            database.exerciseDao(),
            database.trainingPlanDao()
        )

        val factory = CalendarViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(CalendarViewModel::class.java)

        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_calendar_tasks)

        recyclerView.layoutManager = LinearLayoutManager(context)

        // Новые read‑only адаптеры
        scheduleAdapter = CalendarScheduleTaskAdapter()
        taskAdapter = CalendarTaskAdapter()
        workoutDisplayAdapter = WorkoutDisplayAdapter(mutableListOf())  // остаётся без изменений

        // Порядок: задачи с временем, задачи без времени, тренировки
        concatAdapter = ConcatAdapter(scheduleAdapter, taskAdapter, workoutDisplayAdapter)
        recyclerView.adapter = concatAdapter

        // Наблюдение за данными
        viewModel.tasksWithTime.observe(viewLifecycleOwner) { tasks ->
            scheduleAdapter.submitList(tasks)
        }
        viewModel.tasksWithoutTime.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.submitList(tasks)
        }
        viewModel.workouts.observe(viewLifecycleOwner) { workouts ->
            workoutDisplayAdapter.updateList(workouts)
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            viewModel.selectDate(selectedDate)
        }

        // Загрузка сегодняшней даты
        val today = dateFormat.format(Date())
        viewModel.selectDate(today)
    }
}