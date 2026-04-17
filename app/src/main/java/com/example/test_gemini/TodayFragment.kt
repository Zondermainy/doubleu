package com.example.test_gemini

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.example.test_gemini.data.AppDatabase
import com.example.test_gemini.data.AppRepository
import com.example.test_gemini.data.TaskEntity
import com.example.test_gemini.data.WorkoutEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TodayFragment : Fragment() {

    private lateinit var repository: AppRepository
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_today, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация базы и репозитория
        val database = AppDatabase.getDatabase(requireContext())
        repository = AppRepository(
            database.taskDao(),
            database.workoutDao(),
            database.dailyHistoryDao(),
            database.exerciseDao(),
            database.trainingPlanDao()
        )

        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = view.findViewById<ViewPager2>(R.id.view_pager)
        val btnAddTestData = view.findViewById<Button>(R.id.btn_add_test_data)

        val adapter = TodayPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Задачи"
                1 -> tab.text = "Расписание"
                2 -> tab.text = "Тренировки"
            }
        }.attach()

        // Обработчик кнопки тестовых данных
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

                // Задачи
                val task1 = TaskEntity(title = "Пробежка", description = "5 км", isCompleted = true, date = today)
                val task2 = TaskEntity(title = "Прочитать книгу", description = "30 мин", isCompleted = false, date = today)
                val task3 = TaskEntity(title = "Купить продукты", isCompleted = true, date = yesterday)
                val task4 = TaskEntity(title = "Позвонить маме", isCompleted = true, date = yesterday)
                val task5 = TaskEntity(title = "Сходить в спортзал", isCompleted = true, date = twoDaysAgo)

                repository.insertTask(task1)
                repository.insertTask(task2)
                repository.insertTask(task3)
                repository.insertTask(task4)
                repository.insertTask(task5)

                // Тренировки
                val workout1 = WorkoutEntity(name = "Отжимания", durationMinutes = 15, caloriesBurned = 120, isCompleted = true, date = today)
                val workout2 = WorkoutEntity(name = "Пресс", durationMinutes = 10, caloriesBurned = 80, isCompleted = true, date = today)
                val workout3 = WorkoutEntity(name = "Приседания", durationMinutes = 20, caloriesBurned = 150, isCompleted = true, date = yesterday)

                repository.insertWorkout(workout1)
                repository.insertWorkout(workout2)
                repository.insertWorkout(workout3)

                Toast.makeText(requireContext(), "Тестовые данные добавлены! Проверьте Календарь и Профиль.", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getDateDaysAgo(days: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        return dateFormat.format(calendar.time)
    }
}

// Адаптер остаётся без изменений
class TodayPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TasksFragment()
            1 -> ScheduleFragment()
            2 -> WorkoutsFragment()
            else -> TasksFragment()
        }
    }
}