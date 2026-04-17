package com.example.test_gemini

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.test_gemini.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<Button>(R.id.btn_back)
        val btnClearAll = view.findViewById<Button>(R.id.btn_clear_all_data)

        btnBack.setOnClickListener {
            // Возвращаемся в профиль
            requireActivity().findViewById<TextView>(R.id.tv_tab_title).text = "Profile"
            requireActivity().findViewById<ImageView>(R.id.iv_settings).visibility = View.VISIBLE
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .commit()
        }

        btnClearAll.setOnClickListener {
            showClearConfirmationDialog()
        }
    }

    private fun showClearConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Сброс всех данных")
            .setMessage("Вы уверены? Будут удалены все задачи, тренировки и история. Это действие нельзя отменить.")
            .setPositiveButton("Удалить") { _, _ ->
                clearAllData()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun clearAllData() {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val database = AppDatabase.getDatabase(requireContext())
                    database.clearAllData()   // <-- изменено
                }
                Toast.makeText(requireContext(), "Все данные удалены", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}