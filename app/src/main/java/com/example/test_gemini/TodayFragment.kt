package com.example.test_gemini

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class TodayFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Загружаем наш дизайн (XML)
        return inflater.inflate(R.layout.fragment_today, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = view.findViewById<ViewPager2>(R.id.view_pager)

        // Подключаем Адаптер (он написан чуть ниже), который будет переключать экраны
        val adapter = TodayPagerAdapter(this)
        viewPager.adapter = adapter

        // Связываем вкладки с перелистываемыми экранами и задаем им названия
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Задачи"
                1 -> tab.text = "Расписание"
                2 -> tab.text = "Тренировки"
            }
        }.attach()
    }
}

// Это специальный класс (Адаптер), который говорит приложению, какой экран показывать внутри ViewPager2
class TodayPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    // У нас 3 под-вкладки
    override fun getItemCount(): Int = 3

    // В зависимости от позиции (счет начинается с 0), выдаем нужный фрагмент
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TasksFragment()
            1 -> ScheduleFragment()
            2 -> WorkoutsFragment()
            else -> TasksFragment() // На всякий случай возвращаем первый по умолчанию
        }
    }
}