package com.LielAzulay.reccomendo.Activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.LielAzulay.reccomendo.Adapters.InfoPagerAdapter
import com.LielAzulay.reccomendo.ExtraFiles.Show
import com.LielAzulay.reccomendo.Fragments.home
import com.LielAzulay.reccomendo.InfoFragments.info
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.R
import info.movito.themoviedbapi.model.tv.TvSeries
import kotlinx.android.synthetic.main.activity_info_page.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main


class InfoPage : AppCompatActivity() {

    companion object
    {
        var loaded = false
        var currentpos:Int? = null
        var currentshowinfo : TvSeries? = null
        var currentshowclass : Show?=null
        var show_already_added = false

        fun doesHotInReco(id:Int):Boolean
        {
            var result = false

            for (i in MainActivity.RecomandList)
            {
                if (i.id==id)
                {
                    result = true
                }
            }

            for (x in MainActivity.list)
            {
                if (x.id==id)
                {
                    result = true
                }
            }

            return result
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_page)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        title = currentshowclass?.name

        MainActivity.ChangeBackground(window.decorView.rootView, this)

        loaded= false
        CoroutineScope(Dispatchers.IO).launch { currentpos?.let { CheckIfAllLoaded(it) } }
        show_already_added = currentshowclass?.id?.let { doesHotInReco(it) }!!

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        viewPager.clearOnPageChangeListeners()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        if (!FinalPage.regularShow)
        {
            inflater.inflate(R.menu.explore_menu, menu)
            if  (show_already_added)
            {
                if (menu != null) {
                    menu.getItem(0).icon = ContextCompat.getDrawable(this,R.drawable.ic_baseline_done_24)
                }
            }

        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemid = item.itemId
        when(itemid)
        {
            R.id.addToMainReco ->
            {
                if (loaded)
                {
                    if (!show_already_added)
                    {
                        if (MainActivity.isInternetAvailable(this))
                        {
                            val currentHot = currentshowinfo
                            if (currentHot!=null)
                            {
                                MainActivity.recoshowsToBlock.add(currentHot.id)
                                val picString = "https://image.tmdb.org/t/p/w500" + currentHot.posterPath
                                val net = MainPage.GetNetString(currentHot.networks)
                                MainActivity.RecomandList.add(0, Show(currentHot.name, picString, false, currentHot.id, currentHot.status, net, 0,currentHot.lastAirDate))
                                CoroutineScope(Dispatchers.IO).launch { MainPage.addShowAsReco(currentHot.id) }
                                MainActivity.tiny?.putListInt("showtoBlock", MainActivity.recoshowsToBlock)
                                MainActivity.tiny?.putListObjectShow("recoShowsListview", MainActivity.RecomandList)
                                item.icon = ContextCompat.getDrawable(this,R.drawable.ic_baseline_done_24)
                                home.RecoAdapter?.notifyDataSetChanged()
                                MainActivity.ShowChoco("This Show Was Added To Recommendations List",this)
                                show_already_added = true
                            }

                        }
                    }
                    else
                    {
                        MainActivity.ShowChoco("This Show Is Already In One Of You're List",this)
                    }
                }

            }
            android.R.id.home -> onBackPressed()
        }

        return true
    }


    suspend fun CheckIfAllLoaded(pos:Int)
    {
        if (FinalPage.regularShow)
        {
            if (MainActivity.ShowsForInfo.size-1<pos)
            {
                CoroutineScope(Dispatchers.IO).launch {preloadAnime()}

                while (MainActivity.ShowsForInfo.size-1<pos)
                {
                    delay(500)
                }

                withContext(Main)
                {
                    cloudload.visibility = View.INVISIBLE
                    cloudload.cancelAnimation()
                }

            }
            currentshowinfo = MainActivity.ShowsForInfo[pos]
        }
        else
        {
            if (currentshowinfo?.id!= currentshowclass?.id)
            {
                CoroutineScope(Dispatchers.IO).launch {preloadAnime()}

                while (currentshowinfo?.id!= currentshowclass?.id)
                {
                    delay(500)
                }

                withContext(Main)
                {
                    cloudload.visibility = View.INVISIBLE
                    cloudload.cancelAnimation()
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {Initialize()}

    }

    suspend fun Initialize()
    {
        withContext(Main)
        {
            //val pager = viewPager
            val adapter = InfoPagerAdapter(supportFragmentManager)
            adapter.addFragment(info())

            tabs.setupWithViewPager(viewPager)
            viewPager.adapter = adapter
            tabs.getTabAt(0)?.setIcon(R.drawable.ic_baseline_live_tv_24)

            tabs.visibility = View.GONE

            /*//will be used in future tabs
            pager.addOnPageChangeListener(object :
                ViewPager.OnPageChangeListener {//handling changes in the fragments

                override fun onPageScrollStateChanged(state: Int) {}

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    if (position == 1) {
                        rate.statlotti?.playAnimation()
                    }

                }

            })

             */

            loaded = true
        }

    }

    suspend fun preloadAnime()
    {
        withContext(Main)
        {
            cloudload.visibility = View.VISIBLE
            cloudload.playAnimation()
        }
    }



}