package com.example.test_gemini

import androidx.lifecycle.*
import com.example.test_gemini.data.*
import kotlinx.coroutines.launch

class NotesViewModel(private val repository: AppRepository) : ViewModel() {

    val allExercises: LiveData<List<ExerciseEntity>> = repository.getAllExercises().asLiveData()
    val allPlans: LiveData<List<TrainingPlanEntity>> = repository.getAllTrainingPlans().asLiveData()

    private val _selectedPlanId = MutableLiveData<Long?>()
    val selectedPlanExercises: LiveData<List<ExerciseWithDetails>> = _selectedPlanId.switchMap { planId ->
        if (planId != null) {
            repository.getExercisesForPlan(planId).asLiveData()
        } else {
            MutableLiveData(emptyList())
        }
    }

    fun addExercise(name: String, description: String, muscleGroup: String) {
        viewModelScope.launch {
            repository.insertExercise(
                ExerciseEntity(
                    name = name,
                    description = description,
                    muscleGroup = muscleGroup,
                    isDefault = false
                )
            )
        }
    }

    fun updateExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            repository.updateExercise(exercise)
        }
    }

    fun deleteExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            repository.deleteExercise(exercise)
        }
    }

    fun createPlan(name: String, description: String?) {
        viewModelScope.launch {
            repository.createTrainingPlan(name, description)
        }
    }

    fun deletePlan(plan: TrainingPlanEntity) {
        viewModelScope.launch {
            repository.deleteTrainingPlan(plan)
        }
    }

    fun selectPlan(planId: Long?) {
        _selectedPlanId.value = planId
    }

    fun addExerciseToPlan(planId: Long, exerciseId: Long, sets: Int, reps: Int) {
        viewModelScope.launch {
            repository.addExerciseToPlan(planId, exerciseId, sets, reps)
        }
    }

    fun removeExerciseFromPlan(planId: Long, exerciseId: Long) {
        viewModelScope.launch {
            repository.removeExerciseFromPlan(planId, exerciseId)
        }
    }

    fun updateExerciseInPlan(planId: Long, exerciseId: Long, sets: Int, reps: Int) {
        viewModelScope.launch {
            repository.updateExerciseInPlan(planId, exerciseId, sets, reps)
        }
    }
}

class NotesViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
