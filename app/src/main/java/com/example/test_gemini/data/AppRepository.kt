package com.example.test_gemini.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray

class AppRepository(
    private val taskDao: TaskDao,
    private val workoutDao: WorkoutDao,
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val historyDao: DailyHistoryDao,
    private val exerciseDao: ExerciseDao,
    private val trainingPlanDao: TrainingPlanDao
) {

    // ===== Задачи =====
    fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()
    fun getTasksByDate(date: String): Flow<List<TaskEntity>> = taskDao.getTasksByDate(date)
    fun getTasksWithoutTimeByDate(date: String): Flow<List<TaskEntity>> = taskDao.getTasksWithoutTimeByDate(date)
    fun getTasksWithTimeByDate(date: String): Flow<List<TaskEntity>> = taskDao.getTasksWithTimeByDate(date)

    suspend fun insertTask(task: TaskEntity): Long = withContext(Dispatchers.IO) {
        val id = taskDao.insert(task)
        updateDailyHistoryForDate(task.date)
        id
    }

    suspend fun updateTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        taskDao.update(task)
        updateDailyHistoryForDate(task.date)
    }

    suspend fun deleteTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        taskDao.delete(task)
        updateDailyHistoryForDate(task.date)
    }

    suspend fun setTaskCompleted(taskId: Long, completed: Boolean, date: String) = withContext(Dispatchers.IO) {
        taskDao.updateCompletion(taskId, completed)
        updateDailyHistoryForDate(date)
    }

    // ===== Тренировки =====
    fun getAllWorkouts(): Flow<List<WorkoutEntity>> = workoutDao.getAllWorkouts()
    fun getWorkoutsByDate(date: String): Flow<List<WorkoutEntity>> = workoutDao.getWorkoutsByDate(date)
    fun getWorkoutsByDateOrdered(date: String): Flow<List<WorkoutEntity>> = workoutDao.getWorkoutsByDateOrdered(date)

    suspend fun insertWorkout(workout: WorkoutEntity): Long = withContext(Dispatchers.IO) {
        val id = workoutDao.insert(workout)
        updateDailyHistoryForDate(workout.date)
        id
    }

    suspend fun updateWorkout(workout: WorkoutEntity) = withContext(Dispatchers.IO) {
        workoutDao.update(workout)
        updateDailyHistoryForDate(workout.date)
    }

    suspend fun deleteWorkout(workout: WorkoutEntity) = withContext(Dispatchers.IO) {
        workoutDao.delete(workout)
        updateDailyHistoryForDate(workout.date)
    }

    suspend fun setWorkoutCompleted(workoutId: Long, completed: Boolean, date: String) = withContext(Dispatchers.IO) {
        workoutDao.updateCompletion(workoutId, completed)
        updateDailyHistoryForDate(date)
    }

    // ===== Упражнения в тренировке =====
    fun getExercisesForWorkout(workoutId: Long): Flow<List<WorkoutExerciseEntity>> =
        workoutExerciseDao.getExercisesForWorkout(workoutId)

    suspend fun getExercisesForWorkoutList(workoutId: Long): List<WorkoutExerciseEntity> =
        workoutExerciseDao.getExercisesForWorkoutList(workoutId)

    suspend fun getWorkoutProgress(workoutId: Long): Pair<Int, Int> {
        val total = workoutExerciseDao.getTotalExercisesCount(workoutId)
        val completed = workoutExerciseDao.getCompletedExercisesCount(workoutId)
        return Pair(completed, total)
    }

    suspend fun addExerciseToWorkout(workoutExercise: WorkoutExerciseEntity): Long =
        withContext(Dispatchers.IO) {
            workoutExerciseDao.insert(workoutExercise)
        }

    suspend fun addExercisesToWorkout(workoutExercises: List<WorkoutExerciseEntity>) =
        withContext(Dispatchers.IO) {
            workoutExerciseDao.insertAll(workoutExercises)
        }

    suspend fun setExerciseCompleted(exerciseId: Long, completed: Boolean) =
        withContext(Dispatchers.IO) {
            workoutExerciseDao.updateCompletion(exerciseId, completed)
        }

    suspend fun removeExerciseFromWorkout(workoutExercise: WorkoutExerciseEntity) =
        withContext(Dispatchers.IO) {
            workoutExerciseDao.delete(workoutExercise)
        }

    suspend fun deleteAllExercisesForWorkout(workoutId: Long) =
        withContext(Dispatchers.IO) {
            workoutExerciseDao.deleteAllForWorkout(workoutId)
        }

    // ===== История дня =====
    suspend fun getHistoryForDate(date: String): DailyHistoryEntity? = withContext(Dispatchers.IO) {
        historyDao.getHistoryForDate(date)
    }

    suspend fun getAllHistory(): List<DailyHistoryEntity> = withContext(Dispatchers.IO) {
        historyDao.getAllHistory()
    }

    private suspend fun updateDailyHistoryForDate(date: String) = withContext(Dispatchers.IO) {
        val tasks = taskDao.getTasksByDateSuspend(date)
        val workouts = workoutDao.getWorkoutsByDateSuspend(date)

        val completedTaskIds = tasks.filter { it.isCompleted }.map { it.id }
        val completedWorkoutIds = workouts.filter { it.isCompleted }.map { it.id }

        val history = DailyHistoryEntity(
            date = date,
            completedTaskIds = JSONArray(completedTaskIds).toString(),
            completedWorkoutIds = JSONArray(completedWorkoutIds).toString()
        )
        historyDao.insertOrUpdate(history)
    }

    // ===== Статистика =====
    suspend fun getTotalTasksCreated(): Int = taskDao.getTotalTasksCount() ?: 0
    suspend fun getCompletedTasksCount(): Int = taskDao.getCompletedTasksCount() ?: 0
    suspend fun getCompletedTasksCountForWeek(start: String, end: String): Int =
        taskDao.getCompletedTasksCountForWeek(start, end) ?: 0
    suspend fun getCompletedWorkoutsCount(): Int = workoutDao.getCompletedWorkoutsCount() ?: 0

    // ===== Упражнения =====
    fun getAllExercises(): Flow<List<ExerciseEntity>> = exerciseDao.getAllExercises()

    suspend fun getAllExercisesSync(): List<ExerciseEntity> = withContext(Dispatchers.IO) {
        exerciseDao.getAllExercisesList()
    }

    suspend fun getAllTrainingPlansSync(): List<TrainingPlanEntity> = withContext(Dispatchers.IO) {
        trainingPlanDao.getAllPlansList()
    }

    fun getExercisesByMuscleGroup(muscleGroup: String): Flow<List<ExerciseEntity>> =
        exerciseDao.getExercisesByMuscleGroup(muscleGroup)

    suspend fun getExerciseById(id: Long): ExerciseEntity? = withContext(Dispatchers.IO) {
        exerciseDao.getExerciseById(id)
    }

    suspend fun insertExercise(exercise: ExerciseEntity): Long = withContext(Dispatchers.IO) {
        exerciseDao.insert(exercise)
    }

    suspend fun updateExercise(exercise: ExerciseEntity) = withContext(Dispatchers.IO) {
        exerciseDao.update(exercise)
    }

    suspend fun deleteExercise(exercise: ExerciseEntity) = withContext(Dispatchers.IO) {
        exerciseDao.delete(exercise)
    }

    // ===== Тренировочные планы =====
    fun getAllTrainingPlans(): Flow<List<TrainingPlanEntity>> = trainingPlanDao.getAllPlans()
    fun getExercisesForPlan(planId: Long): Flow<List<ExerciseWithDetails>> =
        trainingPlanDao.getExercisesForPlan(planId)

    suspend fun getExercisesForPlanSuspend(planId: Long): List<ExerciseWithDetails> = withContext(Dispatchers.IO) {
        trainingPlanDao.getExercisesForPlanSuspend(planId)
    }

    suspend fun getTrainingPlanById(id: Long): TrainingPlanEntity? = withContext(Dispatchers.IO) {
        trainingPlanDao.getPlanById(id)
    }

    suspend fun createTrainingPlan(name: String, description: String? = null): Long =
        withContext(Dispatchers.IO) {
            trainingPlanDao.insert(TrainingPlanEntity(name = name, description = description))
        }

    suspend fun updateTrainingPlan(plan: TrainingPlanEntity) = withContext(Dispatchers.IO) {
        trainingPlanDao.update(plan)
    }

    suspend fun deleteTrainingPlan(plan: TrainingPlanEntity) = withContext(Dispatchers.IO) {
        trainingPlanDao.delete(plan)
    }

    suspend fun addExerciseToPlan(planId: Long, exerciseId: Long, sets: Int = 3, reps: Int = 10) =
        withContext(Dispatchers.IO) {
            trainingPlanDao.insertPlanExercise(
                TrainingPlanExerciseEntity(
                    planId = planId,
                    exerciseId = exerciseId,
                    sets = sets,
                    reps = reps,
                    orderIndex = 0
                )
            )
        }

    suspend fun removeExerciseFromPlan(planId: Long, exerciseId: Long) = withContext(Dispatchers.IO) {
        trainingPlanDao.removeExerciseFromPlan(planId, exerciseId)
    }

    suspend fun updateExerciseInPlan(planId: Long, exerciseId: Long, sets: Int, reps: Int) =
        withContext(Dispatchers.IO) {
            trainingPlanDao.updateExerciseSetsReps(planId, exerciseId, sets, reps)
        }
}