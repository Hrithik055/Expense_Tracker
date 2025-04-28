package com.example.expensemanager.view.activity

import android.icu.util.Calendar
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanager.R
import com.example.expensemanager.adapter.MyPagerAdapter
import com.example.expensemanager.databinding.ActivityMainBinding
import com.example.expensemanager.utils.ExtensionFun
import com.example.expensemanager.view.fragment.bottomSheetFragment.BottomSheetFragment
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var cal: Calendar
    private lateinit var pagerAdapter: MyPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = "Expanse Manager"

        // Initialize ViewPager adapter
        pagerAdapter = MyPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        // Set tab selection listener to switch ViewPager pages
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        ExtensionFun.addCategory() //category call in bottom sheet

        cal = Calendar.getInstance()
        updateDate()

        binding.nextBtnDate.setOnClickListener {
            cal.add(Calendar.DATE, 1)
            updateDate()
        }
        binding.perviousBtnDate.setOnClickListener {
            cal.add(Calendar.DATE, -1)
            updateDate()
        }

        binding.floatingActionButton.setOnClickListener {
            val bottomSheetFragment = BottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }
    }

    private fun updateDate() {
        binding.currentDate.text = ExtensionFun.dateFormat().format(cal.time)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
