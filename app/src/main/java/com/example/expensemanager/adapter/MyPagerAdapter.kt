package com.example.expensemanager.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.expensemanager.view.fragment.bottomSheetFragment.DailyShowDataFragment
import com.example.expensemanager.view.fragment.bottomSheetFragment.MonthlyShowFragment
import com.example.expensemanager.view.fragment.bottomSheetFragment.WeeklyShowDataFragment

class MyPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    val fragments: List<Fragment> = listOf(DailyShowDataFragment(), WeeklyShowDataFragment(), MonthlyShowFragment())

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}