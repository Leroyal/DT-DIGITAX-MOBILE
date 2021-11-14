package com.digitaltaxusa.digitax.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Constructor
 *
 * @param fragment Static library support version of the framework's [android.app.Fragment].
 * @property alFragment List of fragments.
 */
class TabsFragmentPagerAdapter(
    fragment: FragmentActivity,
    private val alFragment: List<Fragment> = mutableListOf()
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return alFragment.size
    }

    override fun createFragment(position: Int): Fragment {
        return alFragment[position]
    }
}