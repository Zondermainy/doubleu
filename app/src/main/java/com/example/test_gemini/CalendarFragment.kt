package com.example.test_gemini

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.test_gemini.data.AppDatabase
import com.example.test_gemini.data.AppRepository
import com.example.test_gemini.viewmodels.CalendarViewModel
import com.example.test_gemini.viewmodels.CalendarViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var viewModel: CalendarViewModel
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

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
            database.dailyHistoryDao(),
            database.exerciseDao(),
            database.trainingPlanDao()
        )

        val factory = CalendarViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(CalendarViewModel::class.java)

        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        val tvDayInfo = view.findViewById<TextView>(R.id.tv_day_info)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            loadDataForDate(selectedDate, tvDayInfo)
        }

        val today = dateFormat.format(Date())
        loadDataForDate(today, tvDayInfo)
    }

    private fun loadDataForDate(date: String, tvDayInfo: TextView) {
        viewModel.getHistoryForDate(date) { history ->
            val displayText = if (history != null) {
                val tasksCount = extractIdsCount(history.completedTaskIds)
                val workoutsCount = extractIdsCount(history.completedWorkoutIds)
                "Дата: $date\n✅ Выполнено задач: $tasksCount\n💪 Выполнено тренировок: $workoutsCount"
            } else {
                "Нет данных за $date"
            }
            tvDayInfo.text = displayText
        }
    }

    private fun extractIdsCount(jsonArray: String): Int {
        return if (jsonArray.isNotBlank() && jsonArray != "[]") {
            jsonArray.removeSurrounding("[", "]").split(",").filter { it.isNotBlank() }.size
        } else 0
    }
}