package com.LielAzulay.reccomendo.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.R
import kotlinx.android.synthetic.main.fragment_statistics.view.*


class statistics : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = statistics().apply { arguments = Bundle().apply {} }

        fun getAverege(list:MutableList<Int>):Int
        {
            var retValue = 0
            var sum = 0
            if (list.isNotEmpty())
            {
                for (i in list)
                {
                    sum +=i
                }
                retValue =  (sum/list.size)
            }

            return retValue

        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       val view = inflater.inflate(R.layout.fragment_statistics, container, false)

        val likenum = getLikeNum(1)
        val disnum = getLikeNum(2)
        val notrnum = getLikeNum(0)

        val returnnum = getStatusNum(0)
        val cancelednum = getStatusNum(1)
        val endednum = getStatusNum(2)

        val epinum = getEpisodeNum()
        val totalwatchtimeString = getTotalWatchTime()

        val watchednum = MainActivity.list.size


        val likedString = "$likenum Liked Shows"
        val disString = "$disnum Disliked Shows"
        val notrString = "$notrnum Shows That Are Not Rated Yet"

        val retunString = "$returnnum Returning Shows"
        val cancelString = "$cancelednum Canceled Shows"
        val endedString = "$endednum Ended Shows"

        val recoSize = MainActivity.RecomandList.size

        view.RecoNumver.text = recoSize.toString()
        view.WatchedNum.text = watchednum.toString()
        view.epiNumber.text = epinum.toString()
        view.TotalTimeText.text = totalwatchtimeString
        view.LikedReco.text =  likedString
        view.DisReco.text = disString
        view.NotRated.text = notrString

        view.returningS.text = retunString
        view.canceledS.text = cancelString
        view.EndedS.text = endedString

        view.progressBarLiked.max = recoSize
        view.progressBarLiked.animateProgress(500,0,likenum)

        view.progressBarDis.max = recoSize
        view.progressBarDis.animateProgress(500,0,disnum)

        view.progressBarNot.max = recoSize
        view.progressBarNot.animateProgress(500,0,notrnum)

        view.progressBarReturn.max = recoSize
        view.progressBarReturn.animateProgress(500,0,returnnum)

        view.progressBarCan.max = recoSize
        view.progressBarCan.animateProgress(500,0,cancelednum)

        view.progressBarEnded.max = recoSize
        view.progressBarEnded.animateProgress(500,0,endednum)


        return view
    }

    fun getLikeNum(stat:Int):Int
    {
        var count=0

        for (i in MainActivity.RecomandList)
        {
            if (i.likeStat==stat)
            {
                count++
            }
        }
        return count
    }

    //0==returning
    //1==cancled
    //2==ended
    fun getStatusNum(type:Int):Int
    {
        var count =0
        var StrType =""
        when(type)
        {
            0-> StrType="Returning Series"
            1-> StrType="Canceled"
            2-> StrType="Ended"
        }


        for (i in MainActivity.RecomandList)
        {
            if (i.status == StrType)
            {
                count++
            }
        }

        return count
    }



    fun getEpisodeNum():Int
    {
        var epinum = 0
        for (i in MainActivity.SelectedTvSeriesArr)
        {
            epinum+=i.numberOfEpisodes
        }

        return epinum
    }

    fun getTotalWatchTime():String
    {
        var totalmin = 0
        val theString: String
        for (i in MainActivity.SelectedTvSeriesArr)
        {
            val avr = getAverege(i.episodeRuntime)
           totalmin += (avr*i.numberOfEpisodes)
        }

        val year = totalmin/ 525600
        val month = (totalmin%525600)/43829
        val days = ((totalmin%525600)%43829)/1440
        val hours = (((totalmin%525600)%43829)%1440)/60
        val min = (((totalmin%525600)%43829)%1440)%60

        if (year<1)
        {
            if (month<1)
            {
                if (days<1)
                {
                    if (hours<1)
                    {
                        theString = "$min Minutes"
                    }
                    else
                    {
                        theString = "$hours Hours $min Minutes"
                    }
                }
                else
                {
                    theString = "$days Days $hours Hours $min Minutes"
                }
            }
            else
            {
                theString = "$month Months $days Days $hours Hours $min Minutes"
            }
        }
        else
        {
            theString = "$year Years $month Months $days Days $hours Hours $min Minutes"
        }

        return theString
    }

}