package com.LielAzulay.reccomendo.Adapters

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
import com.airbnb.lottie.LottieAnimationView
import com.LielAzulay.reccomendo.Activities.AlreadyLiked
import com.LielAzulay.reccomendo.ExtraFiles.Show
import com.LielAzulay.reccomendo.Fragments.shows
import com.LielAzulay.reccomendo.InBetweenActivities.NoConnection
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.R
import com.squareup.picasso.Picasso
import info.movito.themoviedbapi.model.tv.TvSeries
import kotlinx.android.synthetic.main.delete_pop.view.*
import kotlinx.android.synthetic.main.quick_info.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WatchedShowsAdapter(var context: Context, var showsList: ArrayList<Show>) : BaseAdapter() {  //costume adapter for the grid view (shows Fragment)

    private val inflator: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    private class ViewHolderF {
        lateinit var nameTextView: TextView
        lateinit var profileImage: ImageView
        lateinit var CheckB: ImageView
        lateinit var DelB: ImageView
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
            holderF.DelB = view.findViewById(R.id.DelBox) as ImageView
            holderF.loadCircle = view.findViewById(R.id.circle_load) as LottieAnimationView

            // 4
            view.tag = holderF

        } else {
            // 5
            view = convertView
            holderF = convertView.tag as ViewHolderF
        }

        val namev = holderF.nameTextView
        val imagev = holderF.profileImage
        val checkBo = holderF.CheckB
        val circle = holderF.loadCircle
        val deletebox = holderF.DelB

        namev.text = show.name
        Picasso.get().load(show.profilePic).fit().into(imagev)

        val animin = AnimationUtils.loadAnimation(this@WatchedShowsAdapter.context, R.anim.fragment_fade_in)
        val animout= AnimationUtils.loadAnimation(this@WatchedShowsAdapter.context, R.anim.fade_out_quick)

        if (shows.deleteMode) {
            if (deletebox.visibility!=View.VISIBLE)
            {
                checkBo.visibility = View.INVISIBLE
                deletebox.visibility = View.VISIBLE
                checkBo.startAnimation(animout)
                deletebox.startAnimation(animin)
            }

        }
        else {
            if (deletebox.visibility!=View.GONE)
            {
                checkBo.visibility = View.VISIBLE
                deletebox.visibility = View.GONE
                checkBo.startAnimation(animin)
                deletebox.startAnimation(animout)
            }
        }

        circle.visibility = View.INVISIBLE


        view.setOnClickListener {
            if (shows.deleteMode) {
                if (this.context.let { MainActivity.isInternetAvailable(it) }) {

                    CoroutineScope(Dispatchers.IO).launch { startpopup(show, view) }

                }
                else
                {
                    val intent = Intent(context, NoConnection::class.java)
                    context.startActivity(intent)
                }
            }
            else
            {
                CoroutineScope(Dispatchers.IO).launch { getquickinfoTv(show.id) }
            }

        }

        return view
    }



    @SuppressLint("InflateParams")
    suspend fun startpopup(show: Show, adView: View) {
        withContext(Main)
        {
            val inflater: LayoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.delete_pop, null)
            val popupWindow = PopupWindow(
                view, // Custom view to show in popup window
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                LinearLayout.LayoutParams.WRAP_CONTENT // Window height
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
            val str = "Are Sure You Want To Remove \n" + show.name + "\n From Your List?"
            view.deleteT.text = str
            popupWindow.elevation = 10.0F
            popupWindow.isFocusable = true
            popupWindow.showAtLocation(adView, Gravity.CENTER, 0, 0)
            AlreadyLiked.dimBehind(popupWindow)

            val buttony = view.findViewById<Button>(R.id.button_yes)
            val buttonn = view.findViewById<Button>(R.id.button_no)

            val anim = AnimationUtils.loadAnimation(
                this@WatchedShowsAdapter.context,
                R.anim.fade_out_quick
            )

            buttony.setOnClickListener {
                // Dismiss the popup window when the button is pressed

                //removing the show
                var index: TvSeries? = null
                //adView.ShowBox.visibility = View.INVISIBLE

                for (i in MainActivity.selectedShowsIds) {
                    if (i == show.id) {
                        MainActivity.list.removeAt(MainActivity.selectedShowsIds.indexOf(i))
                    }
                }

                for (i in MainActivity.SelectedTvSeriesArr)
                    if (i.id == show.id) {
                        index = i
                    }
                MainActivity.SelectedTvSeriesArr.remove(index)

                MainActivity.selectedShowsIds.remove(show.id)
                MainActivity.selectedShowsReco.remove(show.id)
                MainActivity.saveData()//need to fix bug that cause crash on tinydb saveshows!!

                adView.startAnimation(anim)
                anim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {
                    }

                    override fun onAnimationRepeat(p0: Animation?) {
                    }

                    override fun onAnimationEnd(p0: Animation?) {
                        shows.adapter?.notifyDataSetChanged()
                    }
                })

                popupWindow.dismiss()

            }

            buttonn.setOnClickListener {
                // Dismiss the popup window when the button is pressed
                popupWindow.dismiss()

            }

        }

    }


    @SuppressLint("InflateParams")
    suspend fun quickInfoPop(show: TvSeries) {
        withContext(Main)
        {
            val inflater: LayoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.quick_info, null)
            val viewfrag = inflater.inflate(R.layout.fragment_shows, null)
            val popupWindow = PopupWindow(
                view, // Custom view to show in popup window
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                LinearLayout.LayoutParams.WRAP_CONTENT // Window height
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Create a new slide animation for popup window enter transition
                val slideIn = Slide()
                slideIn.slideEdge = Gravity.BOTTOM
                popupWindow.enterTransition = slideIn
                // Slide animation for popup window exit transition
                val slideOut = Slide()
                slideOut.slideEdge = Gravity.TOP
                popupWindow.exitTransition = slideOut
            }
            val ratingString = show.voteAverage.toString() + "/10"
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
                if (view.quickdesc.isExpanded) {
                    drawicon = ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_baseline_keyboard_arrow_down_24
                    )!!
                    view.quickexpand.text = collapseS
                    view.quickexpand.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        drawicon,
                        null
                    )
                } else {
                    drawicon = ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_baseline_keyboard_arrow_up_24
                    )!!
                    view.quickexpand.text = expandS
                    view.quickexpand.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        drawicon,
                        null
                    )
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

    suspend fun getquickinfoTv(id: Int) {
        if (MainActivity.tmdbApi != null) {
            quickInfoPop(MainActivity.tmdbApi!!.tvSeries.getSeries(id, "en"))
        }

    }
}