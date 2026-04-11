package com.example.test_gemini

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment

class CalendarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Загружаем наш XML файл
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Находим элементы
        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        val tvDayInfo = view.findViewById<TextView>(R.id.tv_day_info)

        // Слушатель нажатий на дату
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Месяцы в Android считаются от 0 до 11, поэтому добавляем 1
            val selectedDate = "$dayOfMonth.${month + 1}.$year"

            // Пока просто выводим дату. Позже здесь будет поиск задач в базе данных
            tvDayInfo.text = "Данные для даты $selectedDate:\n\n• Задач пока нет\n• Тренировок пока нет"
        }
    }
}