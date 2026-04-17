package com.example.test_gemini

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.test_gemini.data.AppDatabase
import com.example.test_gemini.data.AppRepository
import com.example.test_gemini.viewmodels.ProfileViewModel
import com.example.test_gemini.viewmodels.ProfileViewModelFactory
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.test_gemini.utils.ExportImportHelper

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var tvMemberSince: TextView
    private lateinit var tvTasksCompletedWeek: TextView
    private lateinit var tvTasksCreatedTotal: TextView
    private lateinit var tvCurrentStreak: TextView
    private lateinit var tvWorkoutsCompletedTotal: TextView
    private lateinit var tvCaloriesTotal: TextView
    private lateinit var tvAchievements: TextView
    private lateinit var btnExport: Button
    private lateinit var btnImport: Button

    private val importFileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                ExportImportHelper.importDatabase(requireContext(), uri)
                viewModel.loadStats() // обновить статистику после импорта
                Toast.makeText(requireContext(), "Данные импортированы. Рекомендуем перезапустить приложение.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Инициализация базы данных и репозитория ---
        val database = AppDatabase.getDatabase(requireContext())
        val repository = AppRepository(
            database.taskDao(),
            database.workoutDao(),
            database.dailyHistoryDao(),
            database.exerciseDao(),
            database.trainingPlanDao()
        )

        // --- Создание ViewModel ---
        val factory = ProfileViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        // --- Привязка UI (уже было) ---
        tvMemberSince = view.findViewById(R.id.tv_member_since)
        tvTasksCompletedWeek = view.findViewById(R.id.tv_tasks_completed_week)
        tvTasksCreatedTotal = view.findViewById(R.id.tv_tasks_created_total)
        tvCurrentStreak = view.findViewById(R.id.tv_current_streak)
        tvWorkoutsCompletedTotal = view.findViewById(R.id.tv_workouts_completed_total)
        tvCaloriesTotal = view.findViewById(R.id.tv_calories_total)
        tvAchievements = view.findViewById(R.id.tv_achievements)
        btnExport = view.findViewById(R.id.btn_export)
        btnImport = view.findViewById(R.id.btn_import)

        // --- Установка даты установки (уже было) ---
        val installDate = getInstallDate()
        tvMemberSince.text = "В приложении с $installDate"

        // --- Подписка на статистику ---
        viewModel.stats.observe(viewLifecycleOwner) { stats ->
            tvTasksCompletedWeek.text = "${stats.completedTasksThisWeek} на этой неделе"
            tvTasksCreatedTotal.text = "${stats.totalTasksCreated} всего"
            tvCurrentStreak.text = "${stats.currentStreak} ${getDaysWord(stats.currentStreak)}"
            tvWorkoutsCompletedTotal.text = "${stats.completedWorkouts} всего"
            tvCaloriesTotal.text = "${stats.totalCalories} ккал"
            updateAchievements(stats)
        }

        // --- Загрузка статистики ---
        viewModel.loadStats()

        btnExport.setOnClickListener {
            ExportImportHelper.exportDatabase(requireContext())
        }

        btnImport.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
            importFileLauncher.launch(intent)
        }
    }

    // --- Добавь вспомогательные методы (в конец класса) ---
    private fun getDaysWord(days: Int): String {
        return when {
            days % 10 == 1 && days % 100 != 11 -> "день"
            days % 10 in 2..4 && days % 100 !in 12..14 -> "дня"
            else -> "дней"
        }
    }

    private fun updateAchievements(stats: ProfileViewModel.Stats) {
        val achievementsList = mutableListOf<String>()
        if (stats.totalTasksCreated >= 10) achievementsList.add("• Первые шаги (Создано 10 задач)")
        if (stats.currentStreak >= 7) achievementsList.add("• Стайер (Стрик 7 дней)")
        if (stats.completedTasksTotal >= 20) achievementsList.add("• Мастер планирования (20 выполненных задач)")

        tvAchievements.text = if (achievementsList.isEmpty()) {
            "• Пока нет достижений. Продолжай активность!"
        } else {
            achievementsList.joinToString("\n")
        }
    }

    // ... остальной код (getInstallDate) остаётся без изменений

    private fun getInstallDate(): String {
        val prefs = requireContext().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        return prefs.getString("install_date", null) ?: run {
            val newDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
            prefs.edit().putString("install_date", newDate).apply()
            newDate
        }
    }
}