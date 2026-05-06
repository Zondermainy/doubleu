package com.example.test_gemini.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test_gemini.data.AppRepository
import com.example.test_gemini.data.DailyHistoryEntity
import com.example.test_gemini.data.TaskEntity
import com.example.test_gemini.data.WorkoutEntity
import kotlinx.coroutines.launch

class CalendarViewModel(private val repository: AppRepository) : ViewModel() {

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> = _selectedDate

    private val _tasksWithTime = MediatorLiveData<List<TaskEntity>>()
    val tasksWithTime: LiveData<List<TaskEntity>> = _tasksWithTime

    private val _tasksWithoutTime = MediatorLiveData<List<TaskEntity>>()
    val tasksWithoutTime: LiveData<List<TaskEntity>> = _tasksWithoutTime

    private val _workouts = MediatorLiveData<List<WorkoutEntity>>()
    val workouts: LiveData<List<WorkoutEntity>> = _workouts

    init {
        _tasksWithTime.addSource(_selectedDate) { date ->
            viewModelScope.launch {
                repository.getTasksWithTimeByDate(date).collect { list ->
                    _tasksWithTime.postValue(list)
                }
            }
        }
        _tasksWithoutTime.addSource(_selectedDate) { date ->
            viewModelScope.launch {
                repository.getTasksWithoutTimeByDate(date).collect { list ->
                    _tasksWithoutTime.postValue(list)
                }
            }
        }
        _workouts.addSource(_selectedDate) { date ->
            viewModelScope.launch {
                repository.getWorkoutsByDate(date).collect { list ->
                    _workouts.postValue(list)
                }
            }
        }
    }

    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    fun getHistoryForDate(date: String, onResult: (DailyHistoryEntity?) -> Unit) {
        viewModelScope.launch {
            val history = repository.getHistoryForDate(date)
            onResult(history)
        }
    }

    fun toggleTaskCompletion(task: TaskEntity, completed: Boolean) {
        viewModelScope.launch {
            repository.setTaskCompleted(task.id, completed, task.date)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }
}