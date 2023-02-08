package com.advanced.chapter1

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(private val mainActivity: MainActivity) :
    FragmentStateAdapter(mainActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                return WebViewFragment(
                    position,
                    "https://comic.naver.com/webtoon/detail?titleId=648419&no=371&weekday=mon"
                ).apply {
                    listener = mainActivity
                }
            }
            1 -> {
                return WebViewFragment(
                    position,
                    "https://comic.naver.com/webtoon/detail?titleId=783053&no=67&weekday=tue"
                ).apply {
                    listener = mainActivity
                }
            }
            else -> {
                return WebViewFragment(
                    position,
                    "https://comic.naver.com/webtoon/detail?titleId=783769&no=66&weekday=wed"
                ).apply {
                    listener = mainActivity
                }
            }
        }
    }
}