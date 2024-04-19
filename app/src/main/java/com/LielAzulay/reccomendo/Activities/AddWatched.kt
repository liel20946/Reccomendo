package com.LielAzulay.reccomendo.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.LielAzulay.reccomendo.Adapters.FinalGridAdapter
import com.LielAzulay.reccomendo.ExtraFiles.Show
import com.LielAzulay.reccomendo.InBetweenActivities.NoConnection
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.R
import com.paulrybitskyi.persistentsearchview.PersistentSearchView
import kotlinx.android.synthetic.main.activity_add_watched.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddWatched : AppCompatActivity() {
    var searchShowArr = ArrayList<Show>()
    var adapterwatched:FinalGridAdapter ?=null

    companion object
    {
        var isSActivOn = false
        fun ShowsTheme(searchV: PersistentSearchView, context: Context)
        {
            if (MainActivity.LightTheme)
            {
                searchV.setCardBackgroundColor(ContextCompat.getColor(context,R.color.actionbar))
                searchV.setQueryInputBarIconColor(ContextCompat.getColor(context,R.color.colorPrimary))
                searchV.setQueryInputTextColor(ContextCompat.getColor(context,R.color.Text))
                searchV.setQueryInputHintColor(ContextCompat.getColor(context,R.color.Text))
            }
            else
            {
                searchV.setCardBackgroundColor(ContextCompat.getColor(context,R.color.lightBack))
                searchV.setQueryInputBarIconColor(ContextCompat.getColor(context,R.color.colorPrimary))
                searchV.setQueryInputTextColor(ContextCompat.getColor(context,R.color.actionbar))
                searchV.setQueryInputHintColor(ContextCompat.getColor(context,R.color.actionbar))
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_watched)
        MainActivity.ChangeBackground(window.decorView.rootView, this)
        FinalPage.FinalTheme(window, this)
        ShowsTheme(add_watchedSearch,this)

        isSActivOn = true

        adapterwatched = FinalGridAdapter(this, searchShowArr)// setting up the adapter
        search_grid.adapter = adapterwatched

        with(add_watchedSearch) {
            MainActivity.hideKeyboardAuto(window)
            setSuggestionsDisabled(true)
            isVoiceInputButtonEnabled = false

            setOnLeftBtnClickListener {
               finish()
            }


            setOnSearchConfirmedListener { _, query ->
                // Handle a search confirmation. This is the place where you'd
                // want to perform a search against your data provider.
                if (query != "")
                {
                    CoroutineScope(Dispatchers.IO).launch { Search(query) } //searching
                    AlreadyLiked.resetscroll(search_grid)
                    clearFocus()
                    collapse()
                }

            }

        }
    }

    override fun onBackPressed() {
        if (add_watchedSearch.inputQuery!="")
        {
            add_watchedSearch.inputQuery = ""
        }
        else
        {
            super.onBackPressed()
        }

    }

    override fun onDestroy() {
        isSActivOn = false
        super.onDestroy()
    }

    suspend fun Search(searchString: String)// function that searches shows by name
    {
        searchShowArr.clear()

        if (MainActivity.isInternetAvailable(this)) {
            try {
                MainActivity.GeneralSearch(searchString,searchShowArr,MainActivity.tmdbApi!!)
                withContext(Dispatchers.Main)
                {
                    adapterwatched?.notifyDataSetChanged()
                }

            }catch (except:Exception){}
        }
        else
        {
            val intent = Intent(this, NoConnection::class.java)
            startActivity(intent)
        }
    }

}