package com.example.test_gemini.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test_gemini.data.AppRepository
import com.example.test_gemini.data.DailyHistoryEntity
import kotlinx.coroutines.launch

class CalendarViewModel(private val repository: AppRepository) : ViewModel() {
    fun getHistoryForDate(date: String, onResult: (DailyHistoryEntity?) -> Unit) {
        viewModelScope.launch {
            val history = repository.getHistoryForDate(date)
            onResult(history)
        }
    }
}