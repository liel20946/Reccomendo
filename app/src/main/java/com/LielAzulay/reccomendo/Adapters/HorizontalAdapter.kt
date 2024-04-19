package com.LielAzulay.reccomendo.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.LielAzulay.reccomendo.Activities.FinalPage
import com.LielAzulay.reccomendo.Activities.InfoPage
import com.LielAzulay.reccomendo.ExtraFiles.HotShow
import com.LielAzulay.reccomendo.ExtraFiles.Show
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.R
import com.squareup.picasso.Picasso
import info.movito.themoviedbapi.TmdbTV
import kotlinx.android.synthetic.main.explore_page.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HorizontalAdapter(var hotshowsL: ArrayList<HotShow>):RecyclerView.Adapter<HorizontalAdapter.ViewHodler>() {

    class ViewHodler(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {


        fun getHotFullData(id: Int,name:String, view: View)
        {
            InfoPage.currentshowclass = Show(name,"",false,id,"","",0,"")
            InfoPage.currentpos=0
            FinalPage.regularShow = false
            val intent = Intent(view.context, InfoPage::class.java)
            view.context.startActivity(intent)

            if (InfoPage.currentshowinfo?.id!=id||InfoPage.currentshowinfo==null)
            {
                try {
                    InfoPage.currentshowinfo = (MainActivity.tmdbApi!!.tvSeries.getSeries(id, "en", TmdbTV.TvMethod.videos))
                }catch (except:Exception){}

            }

        }

        private var view: View = v
        var name: TextView? = null
        var year: TextView? = null
        var profile: ImageView? = null
        var hotWatched:ImageView?=null
        var id:Int = 0

        //3
        init {
            v.setOnClickListener(this)
            name = view.explorename
            year = view.exploredate
            profile = view.exploreprofile
            hotWatched = view.hotwatched
        }

        override fun onClick(p0: View?) {
            if (MainActivity.isInternetAvailable(view.context))
            {
                CoroutineScope(Dispatchers.IO).launch {getHotFullData(id,name?.text.toString(), view)}
            }

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHodler {
        val inflater = LayoutInflater.from(parent.context)
        val view:View

        view = if(MainActivity.LightTheme) {
            inflater.inflate(R.layout.light_explore_page, parent, false)
        } else {
            inflater.inflate(R.layout.explore_page, parent, false)
        }


        return ViewHodler(view)

    }

    override fun onBindViewHolder(holder: ViewHodler, position: Int) {
        val profileS = "https://image.tmdb.org/t/p/w500"+hotshowsL[position].profilePic
        holder.id = hotshowsL[position].id
        holder.name?.text = hotshowsL[position].name
        holder.year?.text = hotshowsL[position].year.toString()
        Picasso.get().load(profileS).fit().into(holder.profile)
        if (InfoPage.doesHotInReco(holder.id))
        {
            holder.hotWatched?.visibility = View.VISIBLE
        }
        else
        {
            holder.hotWatched?.visibility = View.INVISIBLE
        }

    }

    override fun getItemCount(): Int {
        return hotshowsL.size
    }

    override fun getItemId(position: Int): Long {
        return hotshowsL[position].id.toLong()
    }


}