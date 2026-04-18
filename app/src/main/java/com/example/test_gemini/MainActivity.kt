package com.example.test_gemini

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.test_gemini.data.AppDatabase
import com.example.test_gemini.data.AppRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    val database: AppDatabase by lazy { AppDatabase.getDatabase(applicationContext) }
    val repository: AppRepository by lazy {
        AppRepository(
            database.taskDao(),
            database.workoutDao(),
            database.workoutExerciseDao(),
            database.dailyHistoryDao(),
            database.exerciseDao(),
            database.trainingPlanDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Находим элементы на экране
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val tvTabTitle = findViewById<TextView>(R.id.tv_tab_title)
        val tvDate = findViewById<TextView>(R.id.tv_date)
        val ivSettings = findViewById<ImageView>(R.id.iv_settings)

        // Получаем и форматируем текущую дату
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        tvDate.text = sdf.format(Date())

        // При запуске приложения сразу настраиваем всё для вкладки Today
        replaceFragment(TodayFragment())
        tvTabTitle.text = "Today"
        tvDate.visibility = View.VISIBLE
        ivSettings.visibility = View.GONE

        // Обрабатываем нажатия на пункты нижнего меню
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_today -> {
                    replaceFragment(TodayFragment())
                    tvTabTitle.text = "Today"
                    tvDate.visibility = View.VISIBLE
                    ivSettings.visibility = View.GONE
                }
                R.id.nav_calendar -> {
                    replaceFragment(CalendarFragment())
                    tvTabTitle.text = "Calendar"
                    tvDate.visibility = View.GONE
                    ivSettings.visibility = View.GONE
                }
                R.id.nav_notes -> {
                    replaceFragment(NotesFragment())
                    tvTabTitle.text = "Notes"
                    tvDate.visibility = View.GONE
                    ivSettings.visibility = View.GONE
                }
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                    tvTabTitle.text = "Profile"
                    tvDate.visibility = View.GONE
                    ivSettings.visibility = View.VISIBLE // Показываем шестерёнку
                }
            }
            true
        }

        // Обработка клика по шестерёнке
        ivSettings.setOnClickListener {
            replaceFragment(SettingsFragment())
            tvTabTitle.text = "Settings"
            ivSettings.visibility = View.GONE // Скрываем шестерёнку в самих настройках
        }
    }

    // Функция для смены фрагментов
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}