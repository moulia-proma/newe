package com.example.classwave.presentation.page.teacher

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

public const val TABS = 2

class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val classId: String,
    private val stdId: String,
    private val stdName: String,
    private  val stdProfile: String
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return TABS
    }

    override fun createFragment(position: Int): Fragment {

        when (position) {
            0 -> return positiveFragment(classId,stdId,stdName,stdProfile)
            1 -> return NeedsWorkFragment(classId,stdId,stdName,stdProfile)
        }
        return positiveFragment(classId, stdId, stdName, stdProfile)
    }
}