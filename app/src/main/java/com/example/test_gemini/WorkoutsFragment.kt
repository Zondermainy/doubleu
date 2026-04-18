package com.example.test_gemini

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.test_gemini.data.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WorkoutsFragment : Fragment() {

    private lateinit var repository: AppRepository
    private lateinit var adapter: WorkoutAdapter
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_workouts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = requireActivity() as MainActivity
        repository = mainActivity.repository

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_workouts)
        val tvEmpty = view.findViewById<TextView>(R.id.tv_workouts_empty)
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fab_add_workout)

        adapter = WorkoutAdapter(
            onWorkoutClick = { workout -> showWorkoutDetails(workout) },
            onDeleteClick = { workout -> deleteWorkout(workout) },
            onWorkoutCheckChanged = { workout, isChecked -> toggleWorkoutCompletion(workout, isChecked) },
            onExerciseCheckChanged = { exerciseId, isChecked -> toggleExerciseCompletion(exerciseId, isChecked) }
        )

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        loadWorkouts()

        fabAdd.setOnClickListener {
            showAddWorkoutDialog()
        }
    }

    private fun loadWorkouts() {
        val today = dateFormat.format(Date())
        lifecycleScope.launch {
            repository.getWorkoutsByDateOrdered(today).collect { workouts ->
                val workoutItems = workouts.map { workout ->
                    val exercises = getExercisesForWorkout(workout.id)
                    val (completed, total) = repository.getWorkoutProgress(workout.id)
                    WorkoutItem(workout, exercises, completed, total)
                }
                adapter.submitList(workoutItems)
            }
        }
    }

    private suspend fun getExercisesForWorkout(workoutId: Long): List<WorkoutExerciseItem> {
        val workoutExercises = repository.getExercisesForWorkoutList(workoutId)
        return workoutExercises.mapNotNull { we ->
            val exercise = repository.getExerciseById(we.exerciseId)
            exercise?.let {
                WorkoutExerciseItem(
                    id = we.id,
                    workoutId = we.workoutId,
                    exerciseId = we.exerciseId,
                    exerciseName = it.name,
                    muscleGroup = it.muscleGroup,
                    sets = we.sets,
                    reps = we.reps,
                    isCompleted = we.isCompleted
                )
            }
        }
    }

    private fun showAddWorkoutDialog() {
        lifecycleScope.launch {
            val exercises = repository.getAllExercisesSync()
            val plans = repository.getAllTrainingPlansSync()

            if (exercises.isEmpty()) {
                Toast.makeText(context, "Нет упражнений. Сначала добавьте в Notes -> Упражнения", Toast.LENGTH_LONG).show()
                return@launch
            }

            val selectedExercises = mutableSetOf<Long>()

            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_workout_v2, null)
            val etName = dialogView.findViewById<EditText>(R.id.et_workout_name)
            val etSearch = dialogView.findViewById<EditText>(R.id.et_search)
            val llExercises = dialogView.findViewById<LinearLayout>(R.id.ll_exercises)

            // Store all exercises for filtering
            val allExercises = exercises.toList()

            // Create checkboxes for each exercise
            val exerciseSets = mutableMapOf<Long, EditText>()
            val exerciseReps = mutableMapOf<Long, EditText>()

            val refreshExerciseList = {
                val query = etSearch.text.toString().lowercase()
                llExercises.removeAllViews()

                val filteredExercises = if (query.isEmpty()) {
                    allExercises
                } else {
                    allExercises.filter { it.name.lowercase().contains(query) }
                }

                for (exercise in filteredExercises) {
                    val itemView = LayoutInflater.from(context).inflate(R.layout.item_exercise_select, null)
                    val cb = itemView.findViewById<CheckBox>(R.id.cb_select_exercise)
                    val tvName = itemView.findViewById<TextView>(R.id.tv_exercise_name)
                    val etSets = itemView.findViewById<EditText>(R.id.et_sets)
                    val etReps = itemView.findViewById<EditText>(R.id.et_reps)

                    tvName.text = exercise.name
                    val isSelected = selectedExercises.contains(exercise.id)
                    cb.isChecked = isSelected

                    if (isSelected) {
                        etSets.setText(exerciseSets[exercise.id]?.text.toString() ?: "3")
                        etReps.setText(exerciseReps[exercise.id]?.text.toString() ?: "10")
                    } else {
                        etSets.setText("3")
                        etReps.setText("10")
                    }

                    cb.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            selectedExercises.add(exercise.id)
                        } else {
                            selectedExercises.remove(exercise.id)
                        }
                    }

                    exerciseSets[exercise.id] = etSets
                    exerciseReps[exercise.id] = etReps

                    llExercises.addView(itemView)
                }
            }

            // Initial load
            refreshExerciseList()

            // Search listener
            etSearch.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: android.text.Editable?) {
                    refreshExerciseList()
                }
            })

            AlertDialog.Builder(requireContext())
                .setTitle("Новая тренировка")
                .setView(dialogView)
                .setPositiveButton("Добавить") { _, _ ->
                    val name = etName.text.toString().trim()
                    if (name.isEmpty() || selectedExercises.isEmpty()) {
                        Toast.makeText(context, "Введите название и выберите упражнения", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    addWorkout(name, selectedExercises.toList(), exerciseSets, exerciseReps)
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }

    private fun addWorkout(
        name: String,
        exerciseIds: List<Long>,
        setsMap: MutableMap<Long, EditText>,
        repsMap: MutableMap<Long, EditText>
    ) {
        lifecycleScope.launch {
            val today = dateFormat.format(Date())
            val workout = WorkoutEntity(name = name, date = today)
            val workoutId = repository.insertWorkout(workout)

            val workoutExercises = exerciseIds.mapIndexed { index, exerciseId ->
                WorkoutExerciseEntity(
                    workoutId = workoutId,
                    exerciseId = exerciseId,
                    sets = setsMap[exerciseId]?.text.toString().toIntOrNull() ?: 3,
                    reps = repsMap[exerciseId]?.text.toString().toIntOrNull() ?: 10,
                    orderIndex = index
                )
            }

            repository.addExercisesToWorkout(workoutExercises)
            Toast.makeText(context, "Тренировка добавлена", Toast.LENGTH_SHORT).show()
            loadWorkouts()
        }
    }

    private fun showWorkoutDetails(workout: WorkoutItem) {
        lifecycleScope.launch {
            val exercises = repository.getAllExercisesSync()
            val currentExercises = workout.exercises

            if (exercises.isEmpty()) {
                Toast.makeText(context, "Нет упражнений", Toast.LENGTH_LONG).show()
                return@launch
            }

            val selectedExercises = mutableSetOf<Long>()
            currentExercises.forEach { selectedExercises.add(it.exerciseId) }

            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_workout_v2, null)
            val etName = dialogView.findViewById<EditText>(R.id.et_workout_name)
            val etSearch = dialogView.findViewById<EditText>(R.id.et_search)
            val llExercises = dialogView.findViewById<LinearLayout>(R.id.ll_exercises)

            etName.setText(workout.workout.name)

            val exerciseSets = mutableMapOf<Long, EditText>()
            val exerciseReps = mutableMapOf<Long, EditText>()
            val allExercises = exercises.toList()

            val refreshExerciseList = {
                val query = etSearch.text.toString().lowercase()
                llExercises.removeAllViews()

                val filteredExercises = if (query.isEmpty()) {
                    allExercises
                } else {
                    allExercises.filter { it.name.lowercase().contains(query) }
                }

                for (exercise in filteredExercises) {
                    val itemView = LayoutInflater.from(context).inflate(R.layout.item_exercise_select, null)
                    val cb = itemView.findViewById<CheckBox>(R.id.cb_select_exercise)
                    val tvName = itemView.findViewById<TextView>(R.id.tv_exercise_name)
                    val etSets = itemView.findViewById<EditText>(R.id.et_sets)
                    val etReps = itemView.findViewById<EditText>(R.id.et_reps)

                    tvName.text = exercise.name

                    val isSelected = selectedExercises.contains(exercise.id)
                    cb.isChecked = isSelected

                    val existingExercise = currentExercises.find { it.exerciseId == exercise.id }
                    if (isSelected && existingExercise != null) {
                        etSets.setText(existingExercise.sets.toString())
                        etReps.setText(existingExercise.reps.toString())
                    } else if (isSelected) {
                        etSets.setText("3")
                        etReps.setText("10")
                    } else {
                        etSets.setText("3")
                        etReps.setText("10")
                    }

                    cb.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            selectedExercises.add(exercise.id)
                        } else {
                            selectedExercises.remove(exercise.id)
                        }
                    }

                    exerciseSets[exercise.id] = etSets
                    exerciseReps[exercise.id] = etReps

                    llExercises.addView(itemView)
                }
            }

            refreshExerciseList()

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: android.text.Editable?) {
                    refreshExerciseList()
                }
            })

            AlertDialog.Builder(requireContext())
                .setTitle("Редактировать тренировку")
                .setView(dialogView)
                .setPositiveButton("Сохранить") { _, _ ->
                    val name = etName.text.toString().trim()
                    if (name.isEmpty() || selectedExercises.isEmpty()) {
                        Toast.makeText(context, "Введите название и выберите упражнения", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    updateWorkout(workout.workout, name, selectedExercises.toList(), exerciseSets, exerciseReps)
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }

    private fun updateWorkout(
        workout: WorkoutEntity,
        name: String,
        exerciseIds: List<Long>,
        setsMap: MutableMap<Long, EditText>,
        repsMap: MutableMap<Long, EditText>
    ) {
        lifecycleScope.launch {
            val updatedWorkout = workout.copy(name = name)
            repository.updateWorkout(updatedWorkout)

            repository.deleteAllExercisesForWorkout(workout.id)

            val workoutExercises = exerciseIds.mapIndexed { index, exerciseId ->
                val existingExercise = repository.getExercisesForWorkoutList(workout.id).find { it.exerciseId == exerciseId }
                WorkoutExerciseEntity(
                    workoutId = workout.id,
                    exerciseId = exerciseId,
                    sets = setsMap[exerciseId]?.text.toString().toIntOrNull() ?: 3,
                    reps = repsMap[exerciseId]?.text.toString().toIntOrNull() ?: 10,
                    orderIndex = index
                )
            }

            repository.addExercisesToWorkout(workoutExercises)
            Toast.makeText(context, "Тренировка обновлена", Toast.LENGTH_SHORT).show()
            loadWorkouts()
        }
    }

    private fun deleteWorkout(workout: WorkoutItem) {
        lifecycleScope.launch {
            repository.deleteAllExercisesForWorkout(workout.workout.id)
            repository.deleteWorkout(workout.workout)
            Toast.makeText(context, "Тренировка удалена", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleWorkoutCompletion(workout: WorkoutItem, completed: Boolean) {
        lifecycleScope.launch {
            repository.setWorkoutCompleted(workout.workout.id, completed, workout.workout.date)
            loadWorkouts()
        }
    }

    private fun toggleExerciseCompletion(exerciseId: Long, completed: Boolean) {
        lifecycleScope.launch {
            repository.setExerciseCompleted(exerciseId, completed)
            loadWorkouts()
        }
    }
}