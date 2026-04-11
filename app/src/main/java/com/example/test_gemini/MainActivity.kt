package com.example.test_gemini

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Находим наше меню по его ID
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // При запуске приложения сразу открываем вкладку Today
        replaceFragment(TodayFragment())

        // Обрабатываем нажатия на пункты меню
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_today -> replaceFragment(TodayFragment())
                R.id.nav_calendar -> replaceFragment(CalendarFragment())
                R.id.nav_notes -> replaceFragment(NotesFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
            }
            true
        }
    }

    // Функция, которая подменяет экраны
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}