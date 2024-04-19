package com.LielAzulay.reccomendo.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.LielAzulay.reccomendo.Activities.SettingsActivity
import com.LielAzulay.reccomendo.Fragments.explore
import com.LielAzulay.reccomendo.Fragments.home
import com.LielAzulay.reccomendo.Fragments.shows
import com.LielAzulay.reccomendo.Fragments.statistics


//an adapter for the fragments pages
class PagerAdapter(fragmentManager: FragmentManager?) : FragmentPagerAdapter(fragmentManager!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
{
    val fragmentsList:ArrayList<Fragment> = ArrayList()

    init {
        this.fragmentsList.add(home.newInstance())
        this.fragmentsList.add(explore.newInstance())
        this.fragmentsList.add(shows.newInstance())
        this.fragmentsList.add(statistics.newInstance())
        this.fragmentsList.add(SettingsActivity.SettingsFragment())
    }


    // Returns total number of pages
    override fun getCount(): Int {
        return NUM_ITEMS
    }

    // Returns the fragment to display for that page
    override fun getItem(position: Int): Fragment {
        return fragmentsList[position]
    }

    // Returns the page title for the top indicator
    override fun getPageTitle(position: Int): CharSequence? {
        return "Page $position"
    }

    companion object {
        private const val NUM_ITEMS = 5
    }
}