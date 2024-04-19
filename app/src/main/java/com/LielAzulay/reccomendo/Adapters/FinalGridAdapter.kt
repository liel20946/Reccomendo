package com.LielAzulay.reccomendo.Adapters

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Layout
import android.transition.Slide
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.ContextCompat
import com.LielAzulay.reccomendo.Activities.AddWatched
import com.airbnb.lottie.LottieAnimationView
import com.LielAzulay.reccomendo.Activities.AlreadyLiked
import com.LielAzulay.reccomendo.ExtraFiles.Show
import com.LielAzulay.reccomendo.Fragments.shows
import com.LielAzulay.reccomendo.InBetweenActivities.NoConnection
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.R
import com.squareup.picasso.Picasso
import info.movito.themoviedbapi.TmdbTV
import info.movito.themoviedbapi.model.tv.TvSeries
import kotlinx.android.synthetic.main.delete_pop.view.*
import kotlinx.android.synthetic.main.popup_layout.view.*
import kotlinx.android.synthetic.main.quick_info.view.*
import kotlinx.android.synthetic.main.search_shows.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FinalGridAdapter(var context: Context, var showsList: ArrayList<Show>) : BaseAdapter() {  //costume adapter for the grid view (shows Fragment)

    private val inflator: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    private class ViewHolderF {
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
        val holderF: ViewHolderF
        if (convertView == null) {

            // 2
            view = inflator.inflate(R.layout.search_shows, parent, false)

            // 3
            holderF = ViewHolderF()
            holderF.nameTextView = view.findViewById(R.id.nameSingle) as TextView
            holderF.profileImage = view.findViewById(R.id.imageSingle) as ImageView
            holderF.CheckB = view.findViewById(R.id.ShowBox) as ImageView
            holderF.loadCircle = view.findViewById(R.id.circle_load) as LottieAnimationView

            // 4
            view.tag = holderF

        } else
        {
            // 5
            view = convertView
            holderF = convertView.tag as ViewHolderF
        }

        val namev = holderF.nameTextView
        val imagev = holderF.profileImage
        val checkBo = holderF.CheckB
        val circle = holderF.loadCircle

        namev.text = show.name
        Picasso.get().load(show.profilePic).fit().into(imagev)

       for (i in MainActivity.list)
       {
           if (i.id==show.id)
           {
               show.Selected = true
           }
       }

        if (show.Selected) {

            checkBo.visibility = View.VISIBLE
        }
        else
        {
            checkBo.visibility = View.INVISIBLE
        }

        circle.visibility = View.INVISIBLE
        view.isEnabled =true

        view.setOnLongClickListener {
            CoroutineScope(Dispatchers.IO).launch { getquickinfoTv(show.id) }
            return@setOnLongClickListener false
        }

        view.setOnClickListener()
        {
            if (this.context.let { MainActivity.isInternetAvailable(it) })
            {
                if (checkBo.visibility==View.INVISIBLE)
                {
                    if (!CheckIfInReco(show.id))
                    {
                        view.isEnabled = false
                        circle.visibility = View.VISIBLE
                        circle.setMinAndMaxProgress(0f, 0.4f)
                        circle.playAnimation()
                        show.Selected = true
                        MainActivity.selectedShowsIds.add(0, show.id)
                        MainActivity.list.add(0, show)
                        CoroutineScope(Dispatchers.IO).launch { AddFullTvShow(
                            show.id,
                            circle,
                            checkBo,
                            view
                        ) }
                        shows.adapter?.notifyDataSetChanged()
                    }
                    else
                    {
                        CoroutineScope(Dispatchers.IO).launch {alreadyinPop(show.name) }
                    }
                }
                else
                {
                    CoroutineScope(Dispatchers.IO).launch {startpopup(show, view) }
                }

            }

            else
            {
                val intent = Intent(context, NoConnection::class.java)
                context.startActivity(intent)
            }

        }

        return view
    }

    suspend fun AddFullTvShow(id: Int, circleload: LottieAnimationView, checkBo: ImageView, view: View)//a function that add the selected show and gets all the recommendations for it
    {
        if (MainActivity.tmdbApi!=null)
        {
            val tempArray = ArrayList<TvSeries>()
            val CurrentSelectedTv = MainActivity.tmdbApi!!.tvSeries.getSeries(id, "en", TmdbTV.TvMethod.recommendations)
            val recSize = CurrentSelectedTv.recommendations.results.size
            MainActivity.SelectedTvSeriesArr.add(CurrentSelectedTv)

            if (recSize>10)
            {
                val runingL = MainActivity.FinalRecoScore(CurrentSelectedTv.recommendations.results as ArrayList<TvSeries>)
                for (i in 0 until 10)
                {
                    tempArray.add(MainActivity.tmdbApi!!.tvSeries.getSeries(runingL[i].id, "en"))
                }
            }
            else
            {
                for (i in CurrentSelectedTv.recommendations.results)
                {
                    tempArray.add(MainActivity.tmdbApi!!.tvSeries.getSeries(i.id, "en"))
                }
            }

            MainActivity.selectedShowsReco[CurrentSelectedTv.id] = tempArray
            MainActivity.saveData()//need to fix bug that cause crash on tinydb saveshows!!

            withContext(Main)
            {
                circleload.setMinAndMaxProgress(0f, 1f)
                circleload.loop(false)
                circleload.speed = 2.5f
                circleload.playAnimation()
                circleload.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        circleload.visibility = View.INVISIBLE
                        checkBo.startAnimation(AnimationUtils.loadAnimation(this@FinalGridAdapter.context, R.anim.fade_quick))
                        checkBo.visibility = View.VISIBLE
                        view.isEnabled = true
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }

                })

            }
        }
    }



    @SuppressLint("InflateParams")
    suspend fun startpopup(show: Show, adView: View)
    {
        withContext(Main)
        {
            val inflater:LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.delete_pop, null)
            val popupWindow = PopupWindow(
                view, // Custom view to show in popup window
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                LinearLayout.LayoutParams.WRAP_CONTENT // Window height
            )

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                // Create a new slide animation for popup window enter transition
                val slideIn = Slide()
                slideIn.slideEdge = Gravity.TOP
                popupWindow.enterTransition = slideIn
                // Slide animation for popup window exit transition
                val slideOut = Slide()
                slideOut.slideEdge = Gravity.BOTTOM
                popupWindow.exitTransition = slideOut
            }

            //ui stuff
            val str =  "Are Sure You Want To Remove \n"+ show.name +"\n From Your List?"
            view.deleteT.text =str
            popupWindow.elevation = 10.0F
            popupWindow.isFocusable = true
            popupWindow.showAtLocation(adView, Gravity.CENTER, 0, 0)
            AlreadyLiked.dimBehind(popupWindow)

            val buttony = view.findViewById<Button>(R.id.button_yes)
            val buttonn = view.findViewById<Button>(R.id.button_no)

            val anim = AnimationUtils.loadAnimation(
                this@FinalGridAdapter.context,
                R.anim.fade_out_quick
            )

            buttony.setOnClickListener{
                // Dismiss the popup window when the button is pressed

                //removing the show
                var index:TvSeries? = null
                adView.ShowBox.startAnimation(anim)
                adView.ShowBox.visibility = View.INVISIBLE
                show.Selected = false

                for (i in MainActivity.selectedShowsIds)
                {
                    if (i==show.id)
                    {
                        MainActivity.list.removeAt(MainActivity.selectedShowsIds.indexOf(i))
                    }
                }

                for (i in MainActivity.SelectedTvSeriesArr)
                    if (i.id==show.id)
                    {
                        index = i
                    }
                MainActivity.SelectedTvSeriesArr.remove(index)

                MainActivity.selectedShowsIds.remove(show.id)
                MainActivity.selectedShowsReco.remove(show.id)
                MainActivity.saveData()//need to fix bug that cause crash on tinydb saveshows!!

                shows.adapter?.notifyDataSetChanged()

                popupWindow.dismiss()

            }
            buttonn.setOnClickListener{
                // Dismiss the popup window when the button is pressed
                popupWindow.dismiss()

            }

        }

    }

    private fun CheckIfInReco(showid: Int):Boolean
    {
        var ans = false
        for (i in MainActivity.RecomandList)
        {
            if (i.id==showid)
            {
                ans = true
            }
        }

        return ans
    }

    @SuppressLint("InflateParams")
    suspend fun alreadyinPop(showname: String)
    {
        withContext(Main)
        {
            val inflater:LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.popup_layout, null)
            val viewto = inflater.inflate(R.layout.fragment_shows, null)
            val popupWindow = PopupWindow(
                view, // Custom view to show in popup window
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                LinearLayout.LayoutParams.WRAP_CONTENT // Window height
            )

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                // Create a new slide animation for popup window enter transition
                val slideIn = Slide()
                slideIn.slideEdge = Gravity.TOP
                popupWindow.enterTransition = slideIn
                // Slide animation for popup window exit transition
                val slideOut = Slide()
                slideOut.slideEdge = Gravity.BOTTOM
                popupWindow.exitTransition = slideOut
            }

            //ui stuff
            val buttonstr = "Ok..."
            val str =  context.getString(R.string.alreadyinreco) + " "+ showname+ " " + context.getString(
                R.string.alreadyinrec
            )
            view.text_viewPop.text = str
            popupWindow.elevation = 10.0F
            popupWindow.isFocusable = true
            popupWindow.showAtLocation(viewto, Gravity.CENTER, 0, 0)
            AlreadyLiked.dimBehind(popupWindow)

            val buttonPopup = view.findViewById<Button>(R.id.button_popup)
            buttonPopup.text = buttonstr
            buttonPopup.setOnClickListener{
                // Dismiss the popup window when the button is pressed
                popupWindow.dismiss()

            }

        }

    }

    @SuppressLint("InflateParams")
    suspend fun quickInfoPop(show: TvSeries)
    {
        withContext(Main)
        {
            val inflater:LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.quick_info, null)
            val viewfrag = inflater.inflate(R.layout.fragment_shows, null)
            val popupWindow = PopupWindow(
                view, // Custom view to show in popup window
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                LinearLayout.LayoutParams.WRAP_CONTENT // Window height
            )

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                // Create a new slide animation for popup window enter transition
                val slideIn = Slide()
                slideIn.slideEdge = Gravity.BOTTOM
                popupWindow.enterTransition = slideIn
                // Slide animation for popup window exit transition
                val slideOut = Slide()
                slideOut.slideEdge = Gravity.TOP
                popupWindow.exitTransition = slideOut
            }
            val ratingString = show.voteAverage.toString()+"/10"
            val showprofile = "https://image.tmdb.org/t/p/w500" + show.posterPath
            var drawicon: Drawable
            val collapseS = "Show More"
            val expandS = "Show Less"
            view.quick_name.text = show.name
            view.quickdesc.text = show.overview
            view.quickrating.text = ratingString
            view.quickyear.text = show.firstAirDate.toString().split("-").toTypedArray()[0]
            Picasso.get().load(showprofile).fit().into(view.quickimage)

            val vto: ViewTreeObserver = view.quickdesc.viewTreeObserver
            vto.addOnGlobalLayoutListener {
                val l: Layout = view.quickdesc.layout
                val lines: Int = l.lineCount
                if (lines > 0) if (l.getEllipsisCount(lines - 1) > 0)
                    view.quickexpand.visibility = View.VISIBLE
            }


            view.quickexpand.setOnClickListener {
                if (view.quickdesc.isExpanded)
                {
                    drawicon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_arrow_down_24)!!
                    view.quickexpand.text = collapseS
                    view.quickexpand.setCompoundDrawablesWithIntrinsicBounds(null, null, drawicon, null)
                }
                else
                {
                    drawicon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_arrow_up_24)!!
                    view.quickexpand.text = expandS
                    view.quickexpand.setCompoundDrawablesWithIntrinsicBounds(null, null, drawicon, null)
                }
                view.quickdesc.toggle()
            }


            view.quickdiss.setOnClickListener {
                popupWindow.dismiss()
            }



            //ui stuff
            popupWindow.elevation = 10.0F
            popupWindow.isFocusable = true
            popupWindow.showAtLocation(viewfrag, Gravity.CENTER, 0, 0)
            AlreadyLiked.dimBehind(popupWindow)


        }

    }

    suspend fun getquickinfoTv(id: Int)
    {
       if (MainActivity.tmdbApi!=null)
       {
           quickInfoPop(MainActivity.tmdbApi!!.tvSeries.getSeries(id, "en"))
       }


    }





}

