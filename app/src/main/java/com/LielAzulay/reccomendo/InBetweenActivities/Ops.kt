package com.LielAzulay.reccomendo.InBetweenActivities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.LielAzulay.reccomendo.Activities.FinalPage
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.R
import info.movito.themoviedbapi.TmdbTV
import info.movito.themoviedbapi.model.tv.TvSeries
import kotlinx.android.synthetic.main.activity_ops.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class Ops : AppCompatActivity() {//an activity that opens when something went wrong for only it opens if the loading page loop goes for too long
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ops)
        MainActivity.ChangeBackground(window.decorView.rootView,this)
        FinalPage.FinalTheme(window, this)
        getMissingShows()

        tryagainbtn.setOnClickListener {
            finish()
        }
    }

    fun getMissingShows()
    {
        if (MainActivity.selectedShowsIds.size>MainActivity.selectedShowsReco.size)
        {
            for (i in MainActivity.selectedShowsIds)
            {
                if (!MainActivity.selectedShowsReco.containsKey(i))
                {
                    if (MainActivity.isInternetAvailable(this))
                    {
                        CoroutineScope(Dispatchers.IO).launch {fixMissing(i)}
                    }
                }
            }
        }
        else
        {
            for (i in MainActivity.selectedShowsReco)
            {
                if (!MainActivity.selectedShowsIds.contains(i.key))
                {
                   MainActivity.selectedShowsIds.add(i.key)
                }
            }
        }

        MainActivity.saveData()


    }

    fun fixMissing(id:Int)
    {
        try {
            val tempArray = ArrayList<TvSeries>()
            val CurrentSelectedTv = MainActivity.tmdbApi!!.tvSeries.getSeries(id, "en", TmdbTV.TvMethod.recommendations)
            MainActivity.SelectedTvSeriesArr.add(CurrentSelectedTv)

            for (i in CurrentSelectedTv.recommendations.results)
            {
                tempArray.add(MainActivity.tmdbApi!!.tvSeries.getSeries(i.id, "en"))
            }
            MainActivity.selectedShowsReco[CurrentSelectedTv.id] = tempArray
            MainActivity.saveData()

        }catch (except:Exception){}

    }


}