package com.example.test_gemini.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.test_gemini.data.AppDatabase
import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ExportImportHelper {
    private const val DB_NAME = "app_database"
    private const val TAG = "ExportImport"

    fun exportDatabase(context: Context) {
        // Запускаем всю операцию в фоновом потоке
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                db.query(androidx.sqlite.db.SimpleSQLiteQuery("PRAGMA wal_checkpoint(TRUNCATE)"))
                AppDatabase.closeDatabase()

                val dbFile = context.getDatabasePath(DB_NAME)
                if (!dbFile.exists()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "База данных не найдена", Toast.LENGTH_SHORT).show()
                    }
                    AppDatabase.getDatabase(context.applicationContext)
                    return@launch
                }

                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                val fileName = "Backup_$timeStamp.db"

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val resolver = context.contentResolver
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }
                    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    uri?.let {
                        resolver.openOutputStream(it)?.use { outputStream ->
                            FileInputStream(dbFile).use { inputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Экспорт выполнен в папку Downloads", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    dir.mkdirs()
                    val backupFile = File(dir, fileName)
                    FileInputStream(dbFile).use { input ->
                        FileOutputStream(backupFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Экспорт выполнен: ${backupFile.absolutePath}", Toast.LENGTH_LONG).show()
                    }
                }

                // Переоткрываем базу, чтобы приложение работало дальше
                AppDatabase.getDatabase(context.applicationContext)

            } catch (e: Exception) {
                Log.e(TAG, "Export error", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Ошибка экспорта: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                try {
                    AppDatabase.getDatabase(context.applicationContext)
                } catch (_: Exception) {}
            }
        }
    }

    fun importDatabase(context: Context, uri: Uri): Boolean {
        return try {
            // Закрываем текущую базу (без БД-запросов, можно в главном)
            AppDatabase.closeDatabase()

            val dbFile = context.getDatabasePath(DB_NAME)
            Log.d(TAG, "Importing to: ${dbFile.absolutePath}")
            val parentDir = dbFile.parentFile
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs()
            }

            dbFile.delete()
            File(dbFile.absolutePath + "-wal").delete()
            File(dbFile.absolutePath + "-shm").delete()

            // Копирование файла – можно в фоне, но и так быстро
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(dbFile).use { output ->
                    input.copyTo(output)
                }
            }

            val fileSize = dbFile.length()
            Log.d(TAG, "Imported file size: $fileSize bytes")
            if (fileSize == 0L) {
                Toast.makeText(context, "Импортированный файл пуст!", Toast.LENGTH_SHORT).show()
                false
            } else {
                Toast.makeText(
                    context,
                    "База импортирована (${fileSize} байт)",
                    Toast.LENGTH_LONG
                ).show()
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Import error", e)
            Toast.makeText(context, "Ошибка импорта: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }
}