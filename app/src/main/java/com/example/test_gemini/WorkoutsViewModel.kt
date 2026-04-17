package com.example.test_gemini

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.test_gemini.data.AppRepository
import com.example.test_gemini.data.TaskEntity
import kotlinx.coroutines.launch

class WorkoutsViewModel (private val repository: AppRepository) : ViewModel() {
    val allTasks = repository.getAllTasks().asLiveData()

    fun addTask(task: TaskEntity) = viewModelScope.launch {
        repository.insertTask(task)
    }

    fun toggleTaskCompletion(taskId: Long, completed: Boolean, date: String) = viewModelScope.launch {
        repository.setTaskCompleted(taskId, completed, date)
    }
}