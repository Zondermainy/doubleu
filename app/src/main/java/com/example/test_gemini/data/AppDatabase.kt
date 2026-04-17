package com.example.test_gemini.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TaskEntity::class,
        WorkoutEntity::class,
        DailyHistoryEntity::class,
        ExerciseEntity::class,
        TrainingPlanEntity::class,
        TrainingPlanExerciseEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun dailyHistoryDao(): DailyHistoryDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun trainingPlanDao(): TrainingPlanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDefaultExercises(database.exerciseDao())
                }
            }
        }

        private suspend fun populateDefaultExercises(exerciseDao: ExerciseDao) {
            val defaultExercises = listOf(
                ExerciseEntity(
                    name = "Приседания",
                    description = "Базовое упражнение для ног. Встаньте прямо, ноги на ширине плеч. Медленно опускайтесь, сгибая колени, пока бёдра не будут параллельны полу. Вернитесь в исходное положение.",
                    muscleGroup = "Ноги",
                    isDefault = true
                ),
                ExerciseEntity(
                    name = "Жим лёжа",
                    description = "Лягте на скамью, возьмите гриф широким хватом. Опустите штангу к груди, затем выжмите вверх до полного разгибания рук.",
                    muscleGroup = "Грудь",
                    isDefault = true
                ),
                ExerciseEntity(
                    name = "Становая тяга",
                    description = "Встаньте перед штангой, ноги на ширине плеч. Наклонитесь, возьмите гриф прямым хватом. Выпрямите спину и поднимите штангу, выпрямляя ноги и спину.",
                    muscleGroup = "Спина",
                    isDefault = true
                ),
                ExerciseEntity(
                    name = "Жим штанги стоя",
                    description = "Встаньте прямо, штанга на уровне ключиц. Выжмите штангу вверх до полного разгибания рук, затем верните в исходное положение.",
                    muscleGroup = "Плечи",
                    isDefault = true
                ),
                ExerciseEntity(
                    name = "Подтягивания",
                    description = "Повисните на перекладине прямым хватом. Подтянитесь вверх, пока подбородок не окажется над перекладиной. Медленно опуститесь.",
                    muscleGroup = "Спина",
                    isDefault = true
                ),
                ExerciseEntity(
                    name = "Отжимания",
                    description = "Примите упор лёжа, руки на ширине плеч. Опустите корпус, сгибая руки, пока грудь не коснётся пола. Выпрямите руки.",
                    muscleGroup = "Грудь",
                    isDefault = true
                ),
                ExerciseEntity(
                    name = "Выпады",
                    description = "Встаньте прямо. Сделайте шаг вперёд, сгибая переднее колено до 90 градусов. Заднее колено почти касается пола. Вернитесь и повторите с другой ногой.",
                    muscleGroup = "Ноги",
                    isDefault = true
                ),
                ExerciseEntity(
                    name = "Сгибания на бицепс",
                    description = "Встаньте с гантелями в опущенных руках. Сгибайте руки в локтях, поднимая гантели к плечам. Медленно опустите.",
                    muscleGroup = "Руки",
                    isDefault = true
                ),
                ExerciseEntity(
                    name = "Разгибания на трицепс",
                    description = "Поднимите руки со штангой/гантелью над головой. Опустите вес за голову, сгибая локти. Вернитесь в исходное положение.",
                    muscleGroup = "Руки",
                    isDefault = true
                ),
                ExerciseEntity(
                    name = "Планка",
                    description = "Примите упор лёжа на предплечьях. Держите тело прямой линией от головы до пят. Удерживайте положение.",
                    muscleGroup = "Пресс",
                    isDefault = true
                ),
                ExerciseEntity(
                    name = "Скручивания",
                    description = "Лягте на спину, ноги согнуты. Поднимайте плечи к коленям, сокращая мышцы пресса. Медленно опуститесь.",
                    muscleGroup = "Пресс",
                    isDefault = true
                ),
                ExerciseEntity(
                    name = "Тяга в наклоне",
                    description = "Наклонитесь вперёд с прямой спиной, возьмите гантели. Тяните гантели к бёдрам, сводя лопатки. Медленно опустите.",
                    muscleGroup = "Спина",
                    isDefault = true
                ),
                ExerciseEntity(
                    name = "Бёрпи",
                    description = "Из положения стоя присядьте и положите руки на пол. Прыжком примите упор лёжа. Прыжком вернитесь в присед. Прыжком встаньте.",
                    muscleGroup = "Кардио",
                    isDefault = true
                ),
                ExerciseEntity(
                    name = "Бег на месте",
                    description = "Бегите на месте, поднимая колени высоко. Руки согнуты в локтях и работают в ритме бега.",
                    muscleGroup = "Кардио",
                    isDefault = true
                ),
                ExerciseEntity(
                    name = "Приседания с прыжком",
                    description = "Присядьте, руки за головой. Прыгните вверх как можно выше. Приземлитесь мягко и повторите.",
                    muscleGroup = "Ноги",
                    isDefault = true
                ),
                ExerciseEntity(
                    name = "Отжимания на брусьях",
                    description = "Поднимитесь на брусьях. Опускайтесь, сгибая руки, пока плечи не будут ниже локтей. Выпрямите руки.",
                    muscleGroup = "Грудь",
                    isDefault = true
                )
            )
            exerciseDao.insertAll(defaultExercises)
        }
    }

    override fun clearAllTables() {
    }

    suspend fun clearAllData() {
        taskDao().deleteAll()
        workoutDao().deleteAll()
        dailyHistoryDao().deleteAll()
        exerciseDao().deleteCustomExercises()
    }
}