package com.example.test_gemini

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Загружаем XML разметку фрагмента настроек
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Находим кнопку назад в макете фрагмента
        val btnBack = view.findViewById<Button>(R.id.btn_back)

        btnBack.setOnClickListener {
            // 1. Возвращаем заголовок "Profile" в MainActivity
            requireActivity().findViewById<TextView>(R.id.tv_tab_title).text = "Profile"

            // 2. Снова показываем иконку шестерёнки
            requireActivity().findViewById<ImageView>(R.id.iv_settings).visibility = View.VISIBLE

            // 3. Заменяем текущий фрагмент обратно на ProfileFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .commit()
        }
    }
}