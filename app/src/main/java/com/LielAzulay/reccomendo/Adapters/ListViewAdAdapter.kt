package com.LielAzulay.reccomendo.Adapters


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.LielAzulay.reccomendo.Activities.FinalPage
import com.LielAzulay.reccomendo.Activities.InfoPage
import com.LielAzulay.reccomendo.ExtraFiles.Show
import com.LielAzulay.reccomendo.Fragments.home
import com.LielAzulay.reccomendo.InBetweenActivities.NoConnection
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.R
import com.squareup.picasso.Picasso
import info.movito.themoviedbapi.TmdbTV
import info.movito.themoviedbapi.model.tv.TvSeries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ListViewAdAdapter(var context: Context, var showsList: ArrayList<Show>) : BaseAdapter() {

    private val inflator: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private class ViewHolder {
        lateinit var nameTextView: TextView
        lateinit var profileImage: ImageView
        lateinit var likeor: ImageView
        lateinit var dots: ImageView
        lateinit var statusTextV: TextView
        lateinit var netAndTextV: TextView
        lateinit var lastDate: TextView
    }

    override fun getCount(): Int {
        return showsList.size
    }

    override fun getItem(position: Int): Any {
        return showsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val show = showsList[position]

        val view: View
        val holder: ViewHolder
        if (convertView == null) {
            // 2
            view = inflator.inflate(R.layout.main_list_item, parent, false)

            // 3
            holder = ViewHolder()
            holder.nameTextView = view.findViewById(R.id.mainLName) as TextView
            holder.profileImage = view.findViewById(R.id.mainlImage) as ImageView
            holder.likeor = view.findViewById(R.id.likeor) as ImageView
            holder.dots = view.findViewById(R.id.tdots) as ImageView
            holder.statusTextV  =  view.findViewById(R.id.status) as TextView
            holder.netAndTextV  =  view.findViewById(R.id.netandT) as TextView
            holder.lastDate  =  view.findViewById(R.id.lastAirDate) as TextView


            // 4
            view.tag = holder

        } else
        {
            // 5
            view = convertView
            holder = convertView.tag as ViewHolder
        }


        view.setOnClickListener {
            InfoPage.currentshowclass = show
            InfoPage.currentpos = position
            FinalPage.regularShow = true
            val intent = Intent(context, InfoPage::class.java)
            context.startActivity(intent)
        }

        val namev = holder.nameTextView
        val imagev = holder.profileImage
        val sta = holder.statusTextV
        val netan = holder.netAndTextV
        val likeoImage = holder.likeor
        val lastADate = holder.lastDate
        val tdots = holder.dots

        namev.text = show.name
        sta.text = show.status
        netan.text = show.network
        lastADate.text = show.lastDate
        Picasso.get().load(show.profilePic).fit().into(imagev)

        tdots.setOnClickListener {
            mainListMenu(context, tdots,show)
        }

        when(show.likeStat)
        {
            1 -> {
                tdots.visibility = View.INVISIBLE
                likeoImage.visibility = View.VISIBLE
                Picasso.get().load(R.drawable.like).into(likeoImage)
            }

            2 -> {
                tdots.visibility = View.INVISIBLE
                likeoImage.visibility = View.VISIBLE
                Picasso.get().load(R.drawable.dislike).into(likeoImage)
            }
            else->{
                tdots.visibility = View.VISIBLE
                likeoImage.visibility=View.INVISIBLE
            }
        }

        return view
    }


    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("InflateParams")
    fun mainListMenu(context: Context, v: View,show: Show)
    {
        val popup = PopupMenu(context, v)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.main_list_menu, popup.menu)
        popup.show()
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.like_menu -> {
                    if (MainActivity.isInternetAvailable(context)) {

                        show.likeStat=1
                        MainActivity.selectedShowsIds.add(show.id)
                        CoroutineScope(Dispatchers.IO).launch { RateFullTvShow(show.id) }
                        putOnButtom(show)
                        home.RecoAdapter?.notifyDataSetChanged()
                        MainActivity.tiny?.putListObjectShow("recoShowsListview", MainActivity.RecomandList)
                        MainActivity.ShowChoco("You Liked "+show.name, context as Activity)

                    }
                    else
                    {
                        val noint = Intent(context, NoConnection::class.java)
                        context.startActivity(noint)
                    }
                }
                R.id.dis_menu ->
                {
                    if (MainActivity.isInternetAvailable(context)) {
                        CoroutineScope(Dispatchers.IO).launch { DisLike(show) }
                        MainActivity.ShowChoco("You Disliked "+show.name, context as Activity)
                    }
                    else
                    {
                        val nointer = Intent(context, NoConnection::class.java)
                        context.startActivity(nointer)
                    }

                }
            }

            true
        }
    }

    private fun putOnButtom(show: Show)
    {
        MainActivity.RecomandList.remove(show)
        MainActivity.RecomandList.add(MainActivity.RecomandList.size,show)

        var temps : TvSeries? = null

        for (i in MainActivity.ShowsForInfo)
        {
            if (i.id == show.id)
            {
                temps = i
            }
        }

        if (temps!=null)
        {
            MainActivity.ShowsForInfo.remove(temps)
            MainActivity.ShowsForInfo.add(MainActivity.ShowsForInfo.size,temps)
        }

    }

    fun RateFullTvShow(id:Int)//a function that add the selected show and gets all the recommendations for it
    {
        try {
            val CurrentSelectedTv = MainActivity.tmdbApi!!.tvSeries.getSeries(id, "en", TmdbTV.TvMethod.recommendations)
            MainActivity.GetFullTvShow(CurrentSelectedTv,MainActivity.tmdbApi!!)
            MainActivity.saveData()//need to fix bug that cause crash on tinydb saveshows!!
        }catch (except:Exception){}

    }

    fun removeshowfromList(id:Int,list:ArrayList<TvSeries>):ArrayList<TvSeries>
    {
        val returnarr = ArrayList<TvSeries>()
        for (x in list)
        {
            if (x.id!=id)
            {
                returnarr.add(x)
            }
        }

        return returnarr
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private suspend fun DisLike(show:Show)
    {
        try {
            val showapi = MainActivity.tmdbApi!!.tvSeries.getSeries(show.id, "en", TmdbTV.TvMethod.recommendations)
            val recodisids = ArrayList<Int>()

            for (f in showapi.recommendations.results)
            {
                recodisids.add(f.id)
            }

            for (i in MainActivity.selectedShowsReco)
            {
                for (x in i.value)
                {
                    val othersideid = x.id
                    if (recodisids.contains(othersideid))
                    {
                        val temparr =  removeshowfromList(x.id,i.value)
                        val tempk = i.key
                        MainActivity.selectedShowsReco[tempk] = temparr
                    }
                }
            }

            show.likeStat=2
            putOnButtom(show)
            MainActivity.tiny?.putListObjectShow("recoShowsListview", MainActivity.RecomandList)

            withContext(Dispatchers.Main)
            {
                home.RecoAdapter?.notifyDataSetChanged()
            }

        }catch (except:Exception){}

    }



}