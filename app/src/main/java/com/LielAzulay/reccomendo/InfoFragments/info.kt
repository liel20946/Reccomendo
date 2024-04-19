package com.LielAzulay.reccomendo.InfoFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.LielAzulay.reccomendo.Activities.InfoPage
import com.LielAzulay.reccomendo.Activities.MainPage
import com.LielAzulay.reccomendo.Fragments.statistics
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.squareup.picasso.Picasso
import info.movito.themoviedbapi.model.tv.TvSeries
import kotlinx.android.synthetic.main.fragment_info.view.*
import kotlinx.android.synthetic.main.fragment_info.view.popcorn2
import kotlinx.android.synthetic.main.fragment_info.view.star



class info : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_info, container, false)
        //InfoPageTheme(view)
        val theShow = InfoPage.currentshowinfo
        if (theShow!=null)
        {

            val aird = theShow.firstAirDate
            val genS = MainPage.getGenString(theShow.genres)
            val netS = MainPage.GetNetString(theShow.networks)
            val profilepic = "https://image.tmdb.org/t/p/w500" + theShow.posterPath
            val backpic = "https://image.tmdb.org/t/p/original" + theShow.backdropPath
            val googleS = "https://www.google.com/search?q="
            val tmdbS = "https://www.themoviedb.org/tv/"
            val collapseS = "Show More"
            val expandS = "Show Less"
            var draw:Drawable
            val epiTime:String
            val time = statistics.getAverege(theShow.episodeRuntime)
            val fadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fragment_fade_out)
            val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fragment_fade_in)
            if (time==0)
            {
                epiTime = "Unknown"
            }
            else
            {
                epiTime = "$time min"
            }

            //setting the text views
            view.ShowDesc.text = theShow.overview
            view.ShowSeas.text = theShow.numberOfSeasons.toString() + " Seasons"
            view.ShowEpi.text = theShow.numberOfEpisodes.toString()+" Episodes"
            view.ShowRating.text = theShow.voteAverage.toString()+"/10"
            view.ShowGen.text = genS
            view.ShowAirD.text = aird
            view.ShowNetwork.text = netS
            view.epiTimeInfo.text = epiTime


            view.homepage.setOnClickListener {
                if (theShow.homepage != "")
                {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(theShow.homepage))
                    startActivity(browserIntent)
                }
                else
                {
                    MainActivity.ShowChoco("This Show Has No Home",requireActivity())
                }

            }

            view.websearchbutton.setOnClickListener {
                val googleNameS = getNameForS(theShow.name)
                val endS = googleS+googleNameS
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(endS))
                startActivity(browserIntent)
            }

            view.web.setOnClickListener {
                view.homepage.performClick()
            }

            view.TheTVDB.setOnClickListener {
                val showstr = tmdbS+theShow.id
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(showstr))
                startActivity(browserIntent)
            }

            view.Stream.setOnClickListener {
                val showN = getNameForS(theShow.name)
                val streamS = "https://www.justwatch.com/us/search?q=$showN"
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(streamS))
                startActivity(browserIntent)
            }

            Picasso.get().load(backpic).fit().into(view.backinfoImage)
            Picasso.get().load(profilepic).fit().into(view.profieInfo)

            var theplaya:YouTubePlayer?=null
            val thekey = getTrailer(theShow)
            if (!thekey.equals(""))
            {
                val playerview = view.videoView
                lifecycle.addObserver(playerview)

                playerview.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(thekey, 0f)
                        youTubePlayer.pause()
                        theplaya=youTubePlayer
                    }
                })
            }

            val vto: ViewTreeObserver =  view.ShowDesc.viewTreeObserver
            vto.addOnGlobalLayoutListener {
                val l: Layout =  view.ShowDesc.layout
                val lines: Int = l.lineCount
                if (lines > 0) if (l.getEllipsisCount(lines - 1) > 0)
                    view.expandButton.visibility = View.VISIBLE
            }

            view.ShowDesc.setInterpolator(OvershootInterpolator())
            view.expandButton.setOnClickListener {
                if (view.ShowDesc.isExpanded)
                {
                    draw = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_down_24)!!
                    view.expandButton.text = collapseS
                    view.expandButton.setCompoundDrawablesWithIntrinsicBounds(null,null,draw,null)
                }
                else
                {
                    draw = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_up_24)!!
                    view.expandButton.text = expandS
                    view.expandButton.setCompoundDrawablesWithIntrinsicBounds(null,null,draw,null)
                }

                view.ShowDesc.toggle()
            }


            view.playTrailer.setOnClickListener {

                view.backinfoImage.startAnimation(fadeOut)
                view.clearv.startAnimation(fadeOut)

                view.popcorn2.visibility=View.INVISIBLE
                view.timer.visibility = View.INVISIBLE
                view.star.visibility=View.INVISIBLE
                view.epiTimeInfo.visibility=View.INVISIBLE
                view.profieInfo.visibility=View.INVISIBLE
                view.backinfoImage.visibility=View.INVISIBLE
                view.clearv.visibility = View.INVISIBLE
                view.ShowRating.visibility=View.INVISIBLE
                view.playTrailer.visibility=View.INVISIBLE
                view.thecard.visibility = View.INVISIBLE
                view.homepage.visibility=View.INVISIBLE
                view.web.visibility=View.INVISIBLE

                view.videoView.startAnimation(fadeIn)
                view.videoView.visibility=View.VISIBLE
                view.backImagebtn.visibility = View.VISIBLE
                view.backImagebtn.playAnimation()
                theplaya?.play()

            }

            view.backImagebtn.setOnClickListener {

                view.videoView.startAnimation(fadeOut)
                view.clearv.startAnimation(fadeIn)
                view.backinfoImage.startAnimation(fadeIn)

                view.videoView.visibility=View.INVISIBLE
                view.popcorn2.visibility=View.VISIBLE
                view.star.visibility=View.VISIBLE
                view.timer.visibility = View.VISIBLE
                view.epiTimeInfo.visibility=View.VISIBLE
                view.profieInfo.visibility=View.VISIBLE
                view.backinfoImage.visibility=View.VISIBLE
                view.clearv.visibility = View.VISIBLE
                view.ShowRating.visibility=View.VISIBLE
                view.playTrailer.visibility=View.VISIBLE
                view.thecard.visibility = View.VISIBLE
                view.homepage.visibility=View.VISIBLE
                view.web.visibility=View.VISIBLE
                view.backImagebtn.visibility = View.INVISIBLE
                theplaya?.pause()

            }


            view.popcorn2.setOnClickListener {
                view.popcorn2.playAnimation()
            }
            view.star.setOnClickListener {
                view.star.playAnimation()
            }

        }
        return view
    }

    fun getTrailer(show:TvSeries):String
    {
        var trailerkey = ""
        if (show.videos.size>1)
        {
            for (i in show.videos)
            {
                if (i.type.equals("Trailer"))
                {
                    trailerkey = i.key
                }
            }
        }
        else if (show.videos.isNotEmpty())
        {
            trailerkey = show.videos[0].key
        }


        return trailerkey
    }

    fun getNameForS(name:String):String
    {
        return name.replace(" ","+")
    }

    fun InfoPageTheme(view: View)
    {
        if (MainActivity.LightTheme)
        {
            view.clearv.setImageResource(R.drawable.transinfo)
        }
        else
        {
            view.clearv.setImageResource(R.drawable.tansinfo_d)
        }

    }
    
}