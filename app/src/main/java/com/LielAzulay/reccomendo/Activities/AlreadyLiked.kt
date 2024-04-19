package com.LielAzulay.reccomendo.Activities

import com.LielAzulay.reccomendo.Adapters.GridAdapter
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.LielAzulay.reccomendo.ExtraFiles.Show
import com.LielAzulay.reccomendo.InBetweenActivities.AllDoneNow
import com.LielAzulay.reccomendo.InBetweenActivities.NoConnection
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.R
import info.movito.themoviedbapi.TmdbApi
import info.movito.themoviedbapi.model.tv.TvSeries
import kotlinx.android.synthetic.main.activity_already_liked2.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.lang.Exception


class AlreadyLiked : AppCompatActivity() {//this activity is part of the registration in it the user choose shows he already watched

    companion object {
        var oneTime = false//this tells us if its the first time the activity runs so we can show a popup window with instructions
        var showsList = ArrayList<Show>()// array of the shows we display in the activity
        var adapter: GridAdapter? = null//an adapter for the gridview
        var ConFaild = false//tells us if there was'nt a connection and the shows didn't load
        var havesearch = false//checking if a recent search was made so in case of back press we show the popular shows back
        var popularShowsList: MutableList<TvSeries>? = null//a list for getting the shows we need (tmdb api) before converting them to show class


        fun resetscroll(grid : GridView)//resting the position of the gridview (used when searched)
        {
            grid.setSelection(0)
        }

        fun dimBehind(popupWindow: PopupWindow) {// a function that dim the background behind a popup window
            val container: View = popupWindow.contentView.rootView
            val context = popupWindow.contentView.context
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val p = container.getLayoutParams() as WindowManager.LayoutParams
            p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
            p.dimAmount = 0.5f
            wm.updateViewLayout(container, p)
        }

    }

    var alreadyTmdb:TmdbApi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_already_liked2)

        CoroutineScope(Dispatchers.IO).launch { ConnectPop() }//connecting and getting the most popular shows to watch

        with(alreadySearch) {
            MainActivity.hideKeyboardAuto(window)
            setSuggestionsDisabled(true)
            isVoiceInputButtonEnabled = false
            showRightButton()
            hideLeftButton()

            setOnRightBtnClickListener {
                if (MainActivity.selectedShowsIds.size == 0)//checking if at least one show was selected
                {
                    MainActivity.ShowChoco("Please Selected At Least One Show",this@AlreadyLiked)
                }
                else {
                    val intent = Intent(this@AlreadyLiked, AllDoneNow::class.java)
                    startActivity(intent)
                }
            }

            setOnLeftBtnClickListener {
                onBackPressed()
            }

            setOnSearchConfirmedListener { _, query ->
                // Handle a search confirmation. This is the place where you'd
                // want to perform a search against your data provider.
                alreadySearch.clearFocus()
                alreadySearch.collapse()
                showLeftButton()
                if (query != "")
                {

                    CoroutineScope(Dispatchers.IO).launch { Search(query) } //searching
                    havesearch = true
                    resetscroll(gridViewMain)
                }

            }

        }

        adapter = GridAdapter(this, showsList)// setting up the adapter
        gridViewMain.adapter = adapter//setting up the gridview adapter

    }




    override fun onBackPressed() {//handling back press and checking if a search was don recently so instead of closing we will show popular shows
        if (alreadySearch.inputQuery != "") {
            if (havesearch)
            {
                CoroutineScope(Dispatchers.IO).launch { ConnectPop() }//getting back the popular shows
                havesearch = false
                alreadySearch.inputQuery= ""
                alreadySearch.hideLeftButton()
                alreadySearch.clearFocus()
                alreadySearch.collapse()
            }

        }
        else
        {
            finishAffinity()//exit the app
        }
    }


    override fun onResume() {//in case there was no connection when the activity started
        if (ConFaild)
        {
            CoroutineScope(Dispatchers.IO).launch { ConnectPop() }//connecting and getting the most popular shows to watch
        }
       super.onResume()
    }



    private suspend fun ConnectPop()// function that gets the most popular shows
    {
        if (MainActivity.isInternetAvailable(this))//checking there is an internet connection
        {
            try {
                alreadyTmdb = TmdbApi("26ab43b20236bd0f7b81b2b16df4d8e3") //connecting to tmdb data base
                //getting the most popular shows and putting them in the popularshowslist
                val copy = alreadyTmdb!!.tvSeries.getPopular("en", 1).results
                copy.removeAt(copy.size-1)
                copy?.addAll(alreadyTmdb!!.tvSeries.getPopular("en", 2).results)

                popularShowsList = copy
                showsList.clear()

                MainActivity.SortShows(popularShowsList as ArrayList<TvSeries>, showsList)
                withContext(Main)
                {
                    adapter?.notifyDataSetChanged()//updating the grid view with the new shows
                }
                if (!oneTime)
                {
                    PopUpS()//showing a popup window if its the first time the activity launches
                }

            }catch (exception:Exception){}
        }
        else// in case there is no connection
        {
            val intent = Intent(this, NoConnection::class.java)
            startActivity(intent)
        }


    }

     suspend fun Search(searchString: String)// function that searches shows by name
    {
        popularShowsList?.clear()//clearing the current displayed shows

        if (MainActivity.isInternetAvailable(this))//checking for connection
        {
            showsList.clear()
            alreadyTmdb?.let { MainActivity.GeneralSearch(searchString, showsList, it) }
            withContext(Main)
            {
                adapter?.notifyDataSetChanged()//updating the grid view with the new shows
            }
        }
        else//in case there is no connection
        {
            val intent = Intent(this, NoConnection::class.java)
            startActivity(intent)
        }

    }

    private suspend fun PopUpS()//a function that shows a popup windows with some instructions
    {
        withContext(Main)
        {

            val inflater:LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val nullParent = null
            val view = inflater.inflate(R.layout.popup_layout,nullParent)
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
            popupWindow.elevation = 10.0F
            popupWindow.isFocusable = true
            popupWindow.showAtLocation(AlreadyLikedView,Gravity.CENTER, 0, 0)
            dimBehind(popupWindow)

            val buttonPopup = view.findViewById<Button>(R.id.button_popup)
            buttonPopup.setOnClickListener{
                // Dismiss the popup window when the button is pressed
                popupWindow.dismiss()

            }

            oneTime = true//the activity run its first time
        }

    }

    fun dimBehind(popupWindow: PopupWindow) {// a function that dim the background behind a popup window
        val container: View = popupWindow.contentView.rootView
        val context = popupWindow.contentView.context
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.getLayoutParams() as WindowManager.LayoutParams
        p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.5f
        wm.updateViewLayout(container, p)
    }





}