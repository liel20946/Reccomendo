package com.LielAzulay.reccomendo.Activities

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter
import com.LielAzulay.reccomendo.Adapters.FinalGridAdapter
import com.LielAzulay.reccomendo.Adapters.PagerAdapter
import com.LielAzulay.reccomendo.Fragments.shows
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.R
import kotlinx.android.synthetic.main.activity_final_page.*


class FinalPage : AppCompatActivity() {//this activity is the "main" activity which the user get after completing the registration process

    companion object
    {
        var regularShow = true
        var fragpager:ViewPager? = null

        fun FinalTheme(window: Window, context: Context)
        {
            if (MainActivity.LightTheme)
            {
                window.statusBarColor = context.getColor(R.color.lightBack)
                window.decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)

            }
            else
            {
                window.statusBarColor = context.getColor(R.color.backColor)
            }

        }
    }

    var fadeOut: Animation? = null
    var fadeIn: Animation? = null

    lateinit var adapterViewPager: FragmentPagerAdapter//a fragment adapter that handle the change between fragments

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final_page)
        MainActivity.ChangeBackground(window.decorView.rootView, this)
        FinalTheme(window, this)

        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fragment_fade_out)
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fragment_fade_in)

        //setting up the pager

        val pager = frame_container
        pager.offscreenPageLimit = 3
        adapterViewPager = PagerAdapter(supportFragmentManager)
        pager.adapter = adapterViewPager
        fragpager = pager



        //buttom navigation initialize
        val tabColors = applicationContext.resources.getIntArray(R.array.tab_colors)
        val navigationMenu = AHBottomNavigationAdapter(this, R.menu.bottm_menu)
        navigationMenu.setupWithBottomNavigation(bottomNavigation, tabColors)
        bottomNavigation.isColored = true
       // bottomNavigation.setAccentColor(Color.parseColor("#F63D2B"))
        bottomNavigation.inactiveColor = getColor(R.color.colorPrimary)

        bottomNavigation.setOnTabSelectedListener { position, _ -> // Do something cool here...
            if (position!=bottomNavigation.currentItem)
            {
                frame_container.startAnimation(fadeOut)
                frame_container.setCurrentItem(position, false)
                frame_container.startAnimation(fadeIn)

                if (position==3)
                {

                    val currentFragment: Fragment? =
                        supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.frame_container + ":" + 3)
                    val fragmentTransaction: FragmentTransaction =
                        supportFragmentManager.beginTransaction()
                    if (currentFragment != null) {
                        fragmentTransaction.detach(currentFragment)
                        fragmentTransaction.attach(currentFragment)
                        fragmentTransaction.commit()
                    }

                }
            }

            true
        }

    }

    override fun onBackPressed() {//handling back press in each fragment
        if (bottomNavigation.currentItem==0)//handling home fragment back press
        {
            finishAffinity()
        }
        else
        {
            frame_container.startAnimation(fadeOut)
            frame_container.setCurrentItem(0, false)
            bottomNavigation.currentItem = 0
            frame_container.startAnimation(fadeIn)
        }
        /*
        if (bottomNavigation.currentItem==2)//handling my shows back press and its search view
        {
            if (shows.searchArr.isNotEmpty())
            {
                shows.adapter = FinalGridAdapter(this, MainActivity.list)
                shows.gridV?.adapter = shows.adapter
                shows.searchArr.clear()
                shows.newSearch?.collapse()
                shows.newSearch?.hideLeftButton()
                shows.newSearch?.inputQuery=""
            }
            else
            {

                frame_container.startAnimation(fadeOut)
                frame_container.setCurrentItem(0, false)
                bottomNavigation.currentItem = 0
                frame_container.startAnimation(fadeIn)
            }
        }
        else if (bottomNavigation.currentItem==0)//handling home fragment back press
        {
            finishAffinity()
        }
        else//handling settings fragment back press
        {
            frame_container.startAnimation(fadeOut)
            frame_container.setCurrentItem(0, false)
            bottomNavigation.currentItem = 0
            frame_container.startAnimation(fadeIn)
        }

         */


    }






}