package com.LielAzulay.reccomendo.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import at.blogc.android.views.ExpandableTextView
import com.LielAzulay.reccomendo.Activities.MainPage
import com.LielAzulay.reccomendo.Fragments.statistics
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.R
import com.squareup.picasso.Picasso
import info.movito.themoviedbapi.model.tv.TvSeries
import kotlinx.android.synthetic.main.activity_main_page.view.*


class MainPageAdapter(var recommendL: ArrayList<TvSeries>,var context: Context): RecyclerView.Adapter<MainPageAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v){

        private var view: View = v

        var name: TextView? = null
        var desc: ExpandableTextView? = null
        var season:TextView? = null
        var episode :TextView? = null
        var rating:TextView? = null
        var genere:TextView? = null
        var aird:TextView? = null
        var network:TextView? = null
        var epiTime:TextView? = null
        var profile: ImageView? = null
        var backprof: ImageView? = null
        var rateS:TextView?=null
        var finalS:TextView?=null
        var fulltext:TextView?=null
        var backfullt:Button?=null
        var fullLay:ConstraintLayout? = null


        //3
        init {
            name = view.RecoName
            profile = view.imageReco
            backprof = view.backroundimg
            desc = view.RecoDesc
            season =  view.RecoSeas
            episode = view.RecoEpi
            rating = view.RecoRating
            genere = view.RecoGen
            aird =  view.releaseGen
            network = view.network
            epiTime = view.EpisodeTime
            rateS = view.rate_score
            finalS = view.final_score
            fulltext = view.fulltextview
            backfullt = view.back_full_text
            fullLay = view.full_text_lay
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.activity_main_page, parent, false)
        return  ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return recommendL.size
    }

    override fun getItemId(position: Int): Long {
        return recommendL[position].id.toLong()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val show = recommendL[position]
        val profilepic = "https://image.tmdb.org/t/p/w500" + show.posterPath
        val backpic = "https://image.tmdb.org/t/p/original" + show.backdropPath
        val aird:String
        val genS = MainPage.getGenString(show.genres)
        val netS = MainPage.GetNetString(show.networks)
        val episodeTime = statistics.getAverege(show.episodeRuntime).toString()+" min"
        val finalScore =  show.popularity + (show.voteAverage * 10)
        val scoreString = Math.round(finalScore).toString()
        val rateString = Math.round(show.popularity).toString()

        holder.fullLay?.visibility = View.GONE
        holder.fulltext?.text = show.overview
        holder.desc?.setOnClickListener {
            val fadeOut = AnimationUtils.loadAnimation(context, R.anim.fragment_fade_out)
            val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fragment_fade_in)
            holder.fullLay?.startAnimation(fadeIn)
            holder.fullLay?.visibility = View.VISIBLE
            holder.desc?.isEnabled = false
            holder.backfullt?.setOnClickListener {
                holder.fullLay?.startAnimation(fadeOut)
                holder.fullLay?.visibility =View.GONE
                holder.desc?.isEnabled = true
            }
        }

        if (show.firstAirDate!=null)
        {
            aird = show.firstAirDate.split("-").toTypedArray()[0]
        }
        else
        {
            aird ="Unknown"
        }

        if (show.posterPath!=null)
        {
            Picasso.get().load(profilepic).fit().into(holder.profile)
        }
        /*
        else
        {
            Picasso.get().load(R.drawable.place_holder_no_image).into(view.imageReco)
        }

         */

        if (show.backdropPath!=null)
        {
            Picasso.get().load(backpic).fit().into(holder.backprof)
        }
        /*
        else
        {
            Picasso.get().load(R.drawable.place_holder_no_image).into(view.backroundimg)
        }

         */

        holder.name?.text = show.name
        holder.desc?.text = show.overview
        holder.season?.text = show.numberOfSeasons.toString() + " Seasons"
        holder.episode?.text = show.numberOfEpisodes.toString()+" Episodes"
        holder.rating?.text = show.voteAverage.toString()+"/10"
        holder.genere?.text = genS
        holder.aird?.text = aird
        holder.network?.text = netS
        holder.epiTime?.text = episodeTime
        holder.rateS?.text = rateString
        holder.finalS?.text = scoreString


    }


    fun MainPageTheme(view: View)
    {
        if (MainActivity.LightTheme)
        {
            view.imageViewclear.setImageResource(R.drawable.trans_back)
        }
        else
        {
            view.imageViewclear.setImageResource(R.drawable.tansback_d)
        }

    }


}