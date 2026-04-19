package com.example.test_gemini.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DefaultDataHelper {

    suspend fun populateDefaultPlans(
        exerciseDao: ExerciseDao,
        trainingPlanDao: TrainingPlanDao
    ) = withContext(Dispatchers.IO) {
        val exercises = exerciseDao.getAllExercisesSync()
        if (exercises.isEmpty()) return@withContext

        val exerciseMap = exercises.associateBy { it.name }

        val plan1 = TrainingPlanEntity(name = "Новичок", description = "Базовая программа для начинающих", isDefault = true)
        val plan2 = TrainingPlanEntity(name = "Набор массы", description = "Программа для набора мышечной массы", isDefault = true)
        val plan3 = TrainingPlanEntity(name = "Сжигание жира", description = "Интенсивная программа для сжигания жира", isDefault = true)

        trainingPlanDao.insert(plan1)
        trainingPlanDao.insert(plan2)
        trainingPlanDao.insert(plan3)

        val plans = trainingPlanDao.getAllPlansSync()
        val plan1Id = plans.find { it.name == "Новичок" }?.id ?: return@withContext
        val plan2Id = plans.find { it.name == "Набор массы" }?.id ?: return@withContext
        val plan3Id = plans.find { it.name == "Сжигание жира" }?.id ?: return@withContext

        val plan1Exercises = listOf(
            "Приседания" to 3, "Жим лёжа" to 3, "Подтягивания" to 3,
            "Отжимания" to 3, "Скручивания" to 3
        )
        val plan2Exercises = listOf(
            "Приседания" to 4, "Жим лёжа" to 4, "Становая тяга" to 4,
            "Тяга в наклоне" to 4, "Сгибания на бицепс" to 3, "Разгибания на трицепс" to 3
        )
        val plan3Exercises = listOf(
            "Приседания с прыжком" to 4, "Бёрпи" to 4, "Планка" to 3,
            "Выпады" to 3, "Бег на месте" to 5
        )

        val plan1Entities = plan1Exercises.mapNotNull { (name, sets) ->
            exerciseMap[name]?.let { ex ->
                TrainingPlanExerciseEntity(planId = plan1Id, exerciseId = ex.id, sets = sets, reps = 12)
            }
        }
        val plan2Entities = plan2Exercises.mapNotNull { (name, sets) ->
            exerciseMap[name]?.let { ex ->
                TrainingPlanExerciseEntity(planId = plan2Id, exerciseId = ex.id, sets = sets, reps = 10)
            }
        }
        val plan3Entities = plan3Exercises.mapNotNull { (name, sets) ->
            exerciseMap[name]?.let { ex ->
                TrainingPlanExerciseEntity(planId = plan3Id, exerciseId = ex.id, sets = sets, reps = 15)
            }
        }

        trainingPlanDao.insertPlanExercises(plan1Entities)
        trainingPlanDao.insertPlanExercises(plan2Entities)
        trainingPlanDao.insertPlanExercises(plan3Entities)
    }
}