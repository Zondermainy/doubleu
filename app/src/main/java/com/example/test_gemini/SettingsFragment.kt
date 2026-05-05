package com.example.test_gemini

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.test_gemini.data.AppDatabase
import com.example.test_gemini.data.AppRepository
import com.example.test_gemini.data.TaskEntity
import com.example.test_gemini.utils.ExportImportHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : Fragment() {

    private lateinit var repository: AppRepository
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val importFileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                ExportImportHelper.importDatabase(requireContext(), uri)
                Toast.makeText(requireContext(), "Данные импортированы. Рекомендуем перезапустить приложение.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация репозитория
        val database = AppDatabase.getDatabase(requireContext())
        repository = AppRepository(
            database.taskDao(),
            database.workoutDao(),
            database.workoutExerciseDao(),
            database.dailyHistoryDao(),
            database.exerciseDao(),
            database.trainingPlanDao()
        )

        val btnBack = view.findViewById<Button>(R.id.btn_back)
        val btnClearAll = view.findViewById<Button>(R.id.btn_clear_all_data)
        val btnExport = view.findViewById<Button>(R.id.btn_export)
        val btnImport = view.findViewById<Button>(R.id.btn_import)
        val btnAddTestData = view.findViewById<Button>(R.id.btn_add_test_data)

        btnBack.setOnClickListener {
            requireActivity().findViewById<TextView>(R.id.tv_tab_title).text = "Profile"
            requireActivity().findViewById<ImageView>(R.id.iv_settings).visibility = View.VISIBLE
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .commit()
        }

        btnClearAll.setOnClickListener {
            showClearConfirmationDialog()
        }

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

        btnAddTestData.setOnClickListener {
            addTestData()
        }
    }

    private fun addTestData() {
        lifecycleScope.launch {
            try {
                val today = dateFormat.format(Date())
                val yesterday = getDateDaysAgo(1)
                val twoDaysAgo = getDateDaysAgo(2)

                val tasks = listOf(
                    TaskEntity(title = "Пробежка", description = "5 км", isCompleted = true, date = today),
                    TaskEntity(title = "Прочитать книгу", description = "30 мин", isCompleted = false, date = today),
                    TaskEntity(title = "Купить продукты", isCompleted = true, date = yesterday),
                    TaskEntity(title = "Позвонить маме", isCompleted = true, date = yesterday),
                    TaskEntity(title = "Сходить в спортзал", isCompleted = true, date = twoDaysAgo)
                )

                withContext(Dispatchers.IO) {
                    tasks.forEach { repository.insertTask(it) }
                }

                Toast.makeText(requireContext(), "Тестовые данные добавлены!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getDateDaysAgo(days: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        return dateFormat.format(calendar.time)
    }

    private fun showClearConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Сброс всех данных")
            .setMessage("Вы уверены? Это действие нельзя отменить.")
            .setPositiveButton("Удалить") { _, _ -> clearAllData() }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun clearAllData() {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    AppDatabase.getDatabase(requireContext()).clearAllData()
                }
                Toast.makeText(requireContext(), "Все данные удалены", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}