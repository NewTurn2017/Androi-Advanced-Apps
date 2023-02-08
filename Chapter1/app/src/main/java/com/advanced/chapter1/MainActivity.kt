package com.advanced.chapter1

import android.R.color.black
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.advanced.chapter1.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(), OnTabLayoutNameChanged {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.viewPager.adapter = ViewPagerAdapter(this)

        val sharedPreference = getSharedPreferences("WEB_HISTORY", MODE_PRIVATE)
        val tab0 = sharedPreference.getString("tab0_name", "")
        val tab1 = sharedPreference.getString("tab1_name", "")
        val tab2 = sharedPreference.getString("tab2_name", "")

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            run {
                tab.text = when (position) {
                    0 -> if (tab0.isNullOrEmpty()) "탭1" else tab0
                    1 -> if (tab1.isNullOrEmpty()) "탭2" else tab1
                    else -> if (tab2.isNullOrEmpty()) "탭3" else tab2
                }
            }
        }.attach()


    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.fragments[binding.viewPager.currentItem]
        if (currentFragment is WebViewFragment) {
            if (currentFragment.canGoBack()) {
                currentFragment.goBack()
            } else {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun nameChanged(position: Int, name: String) {
        val tab = binding.tabLayout.getTabAt(position)
        tab?.text = name

    }

}