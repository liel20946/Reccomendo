package com.LielAzulay.reccomendo.Adapters

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.InBetweenActivities.NoConnection
import com.LielAzulay.reccomendo.R
import com.LielAzulay.reccomendo.ExtraFiles.Show
import com.squareup.picasso.Picasso
import info.movito.themoviedbapi.TmdbTV
import info.movito.themoviedbapi.model.tv.TvSeries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GridAdapter(var context: Context, var showsList: ArrayList<Show>) : BaseAdapter() {  //costume adapter for the grid view (AlreadyLiked)
    private val inflator: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    private class ViewHolder {
        lateinit var nameTextView: TextView
        lateinit var profileImage: ImageView
        lateinit var CheckB: ImageView
        lateinit var loadCircle: LottieAnimationView
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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val show = showsList[position]

        val view: View
        val holder: ViewHolder
        if (convertView == null) {

            // 2
            view = inflator.inflate(R.layout.search_shows, parent, false)

            // 3
            holder = ViewHolder()
            holder.nameTextView = view.findViewById(R.id.nameSingle) as TextView
            holder.profileImage = view.findViewById(R.id.imageSingle) as ImageView
            holder.CheckB = view.findViewById(R.id.ShowBox) as ImageView
            holder.loadCircle = view.findViewById(R.id.circle_load) as LottieAnimationView

            // 4
            view.tag = holder

        } else
        {
            // 5
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val namev = holder.nameTextView
        val imagev = holder.profileImage
        val checkBo = holder.CheckB
        val circle = holder.loadCircle



        namev.text = show.name
        Picasso.get().load(show.profilePic).fit().into(imagev)


        if ( MainActivity.selectedShowsIds.contains(show.id)) {//making the shows selected if needed
            show.Selected = true
        }


        if (show.Selected)
        {

            checkBo.visibility = View.VISIBLE
        }
        else
        {
            checkBo.visibility = View.INVISIBLE
        }

        circle.visibility = View.INVISIBLE
        view.isEnabled =true

        view.setOnClickListener()//handling clicks (add or remove)
        {
            if (this.context.let { MainActivity.isInternetAvailable(it) })
            {
                if (checkBo.visibility==View.INVISIBLE) {
                    view.isEnabled = false
                    circle.visibility = View.VISIBLE
                    circle.setMinAndMaxProgress(0f,0.4f)
                    circle.playAnimation()
                    show.Selected = true
                    MainActivity.selectedShowsIds.add(show.id)
                    MainActivity.list.add(show)
                    CoroutineScope(Dispatchers.IO).launch { GridFullTvShow(show.id,circle,checkBo,view) }

                } else {
                    val anim = AnimationUtils.loadAnimation(this.context, R.anim.fade_out_quick)
                    checkBo.startAnimation(anim)
                    checkBo.visibility = View.INVISIBLE
                    show.Selected = false
                    MainActivity.selectedShowsIds.remove(show.id)
                    MainActivity.list.remove(show)
                    CoroutineScope(Dispatchers.IO).launch { DelFullTvShow(show.id) }

                }
            }

            else
            {
                val con = context
                val intent = Intent(context, NoConnection::class.java)
                con.startActivity(intent)
            }

        }

        return view
    }

    suspend fun GridFullTvShow(id:Int, circleload:LottieAnimationView, checkBo:ImageView, view:View)//a function that add the selected show and gets all the recommendations for it
    {
        try {
            val CurrentSelectedTv = MainActivity.tmdbApi!!.tvSeries.getSeries(id, "en", TmdbTV.TvMethod.recommendations)
            MainActivity.GetFullTvShow(CurrentSelectedTv,MainActivity.tmdbApi!!)

            withContext(Dispatchers.Main)
            {
                circleload.setMinAndMaxProgress(0f,1f)
                circleload.loop(false)
                circleload.speed = 2.5f
                circleload.playAnimation()
                circleload.addAnimatorListener(object : Animator.AnimatorListener{
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        circleload.visibility = View.INVISIBLE
                        checkBo.startAnimation(AnimationUtils.loadAnimation(this@GridAdapter.context, R.anim.fade_quick))
                        checkBo.visibility = View.VISIBLE
                        view.isEnabled = true
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }

                })

            }

        }catch (except:Exception){}

    }


    fun DelFullTvShow(id: Int)//a function that delete the selected show and deletes all the recommendations for it
    {
        var index:TvSeries? = null
        MainActivity.selectedShowsReco.remove(id)
        for (i in MainActivity.SelectedTvSeriesArr)
            if (i.id==id)
            {
                index = i
            }
        MainActivity.SelectedTvSeriesArr.remove(index)

    }


}

