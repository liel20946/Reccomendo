package com.LielAzulay.reccomendo

import android.app.Activity
import com.LielAzulay.reccomendo.ExtraFiles.TinyDB
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.LottieAnimationView
import com.LielAzulay.reccomendo.Activities.AlreadyLiked
import com.LielAzulay.reccomendo.Activities.FinalPage
import com.LielAzulay.reccomendo.Activities.IntroActivity
import com.LielAzulay.reccomendo.ExtraFiles.HotShow
import com.LielAzulay.reccomendo.ExtraFiles.ListHot
import com.LielAzulay.reccomendo.ExtraFiles.Show
import com.LielAzulay.reccomendo.Fragments.explore
import com.LielAzulay.reccomendo.InBetweenActivities.NoConnection
import com.pd.chocobar.ChocoBar
import info.movito.themoviedbapi.TmdbApi
import info.movito.themoviedbapi.TmdbTV
import info.movito.themoviedbapi.model.tv.TvSeries
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONObject
import java.lang.Exception
import java.net.URL



class MainActivity : AppCompatActivity() {

    companion object {
        var tmdbApi: TmdbApi? = null
        var LightTheme:Boolean = false
        var firstInfo = false//tells if the user gave initial details
        var register = false//tells if the user finished registration
        var tiny: TinyDB? = null //companion object so i could use it from any where to save and get data
       // var getUserData = false
        var selectedShowsReco = mutableMapOf<Int,ArrayList<TvSeries>>()//the main array containing id of a selected show as the key and an array of recommendations as the value
        var selectedShowsIds = ArrayList<Int>()//an array of the selected shows id (easier to use)
        var list = ArrayList<Show>()//the array of the selected shows for the final main page (contains show class and not Tmdb Api object
        var RecomandList = ArrayList<Show>()
        var recoshowsToBlock = ArrayList<Int>()
        var ShowsForInfo = ArrayList<TvSeries>()
        var SelectedTvSeriesArr = ArrayList<TvSeries>()
        var HotShows= ArrayList<ListHot>()

        fun hideKeyboardAuto(window: Window)//hiding the keyboard when user touches outside of the keyboard area
        {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        }

        suspend fun notifyHot(pos:Int)
        {
            delay(500)
            if (explore.hotAdapter!=null)
            {
                withContext(Main)
                {
                    explore.hotAdapter!!.notifyItemRangeChanged(pos,1)
                }
            }
            delay(300)

        }

        fun saveData()//saves the lists we have to save
        {
            tiny?.putListInt("blakclistid", selectedShowsIds)
            tiny?.saveShows("RecoShows", selectedShowsReco)
            tiny?.putListObjectShow("selectedidsList", list)
            tiny?.saveTv("TvSelected", SelectedTvSeriesArr)
        }

         fun isInternetAvailable(context: Context): Boolean {//checking for internet connection

            var result = false
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = connectivityManager.activeNetwork ?: return false
                val actNw =
                    connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
                result = when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }
            else {
                @Suppress("DEPRECATION")
                connectivityManager.run {
                    connectivityManager.activeNetworkInfo?.run {
                        result = when (type) {
                            ConnectivityManager.TYPE_WIFI -> true
                            ConnectivityManager.TYPE_MOBILE -> true
                            ConnectivityManager.TYPE_ETHERNET -> true
                            else -> false
                        }
                    }
                }
            }
            return result
        }

        fun GeneralSearch(searchString:String,listtoAdd: ArrayList<Show>,tmdb:TmdbApi)
        {
            val temp: MutableList<TvSeries> = tmdb.search.searchTv(searchString, "", 1)!!.results//getting the searched shows
            /*
            for (x in temp)
            {
               theArr.add(x)
            }

             */

            SortShows(temp as ArrayList<TvSeries>,listtoAdd)//displaying the shows
        }

        fun SortShows(showsArr:ArrayList<TvSeries>,listtoAdd:ArrayList<Show>)
        {
            var name: String
            var overview: String
            var profilepic: String
            ArrayList<String>()

            for (i in showsArr) {
                profilepic = "https://image.tmdb.org/t/p/w500" + i.posterPath
                name = i.name
                overview = i.overview


                if (!name.equals("") && !overview.equals("") && !profilepic.equals("https://image.tmdb.org/t/p/w500null"))
                {
                    val tempS = Show(name, profilepic, false, i.id,"","",0,"")
                    listtoAdd.add(tempS)
                }
            }
        }

        suspend fun Load(load:LottieAnimationView):Boolean
        {
            var ops = false
            withContext(Main)
            {
                var count = 0
                load.visibility = View.VISIBLE
                load.playAnimation()
                while (selectedShowsReco.size != selectedShowsIds.size) {
                    delay(500L)
                    count++
                    if (count == 15) {
                        ops = true
                        break
                    }
                }
            }
            return ops
        }

        fun GetFullTvShow(CurrentSelectedTv:TvSeries,tmdb: TmdbApi)//a function that add the selected show and gets all the recommendations for it
        {
            val tempArray = ArrayList<TvSeries>()
            SelectedTvSeriesArr.add(CurrentSelectedTv)

            for (i in CurrentSelectedTv.recommendations.results)
            {
                tempArray.add(tmdb.tvSeries.getSeries(i.id, "en"))
            }
            selectedShowsReco[CurrentSelectedTv.id] = tempArray

        }

        fun FinalRecoScore(List: ArrayList<TvSeries>): ArrayList<TvSeries>//this function sort the shows array so in top is the shows with the best score of rating and popularity
        {
            var TempShow: TvSeries
            var TempScoreCurrent: Float
            var TempScoreNext: Float

            for (i in 0 until List.size - 1)
            {
                for (x in 0 until List.size - i - 1)
                {
                    TempScoreCurrent = List[x].popularity + (List[x].voteAverage * 10)
                    TempScoreNext = List[x + 1].popularity + (List[x + 1].voteAverage * 10)

                    if (TempScoreCurrent < TempScoreNext)
                    {
                        TempShow = List[x + 1]
                        List[x + 1] = List[x]
                        List[x] = TempShow
                    }
                }
            }

            return List
        }

        fun ChangeBackground(view: View,context: Context)
        {
            if (LightTheme)
            {
                view.background = ContextCompat.getDrawable(context,R.color.lightBack)
            }
            else
            {
                view.background = ContextCompat.getDrawable(context,R.color.backColor)
            }

        }

        fun ShowChoco(str:String,context: Activity)
        {
            ChocoBar.builder().setActivity(context)
                .setText(str)
                .centerText()
                .setTextColor(getColor(context,R.color.Text))
                .setBackgroundColor(getColor(context,R.color.colorPrimary))
                .setDuration(2000)
                .build()
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try{
            tiny = TinyDB(this)//initializing the tinydb so we can get and save data at all times
            LightTheme = tiny!!.getBoolean("LightTheme")
            if (LightTheme)
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            else
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            register = tiny!!.getBoolean("registerdone")
            firstInfo = tiny!!.getBoolean("FirstInfo")
            list = tiny?.getListObjectShow("selectedidsList")!!
            selectedShowsReco = tiny!!.getShows("RecoShows")
            SelectedTvSeriesArr = tiny!!.getTv("TvSelected")
            selectedShowsIds = tiny!!.getListInt("blakclistid")
            recoshowsToBlock = tiny!!.getListInt("showtoBlock")
            RecomandList = tiny!!.getListObjectShow("recoShowsListview")
            //getUserData = true

        }catch (except:Exception){
            ShowChoco("Ops Something Went Wrong Try Restarting The App",this)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //now we check which activity we should start
        if (register)
        {
            val intent = Intent(this, FinalPage::class.java)
            startActivity(intent)
        }
        else if (firstInfo)
        {
            val intent = Intent(this, AlreadyLiked::class.java)
            startActivity(intent)
        }
        else {
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
        }

        if (isInternetAvailable(this))
        {
            if (ShowsForInfo.isEmpty())
            {
                CoroutineScope(Dispatchers.IO).launch {ConnectApi()}
            }
        }
        else//in case there is no connection
        {
            val intent = Intent(this, NoConnection::class.java)
            startActivity(intent)
        }

    }

    fun ConnectApi()
    {
        try {
            tmdbApi = TmdbApi("26ab43b20236bd0f7b81b2b16df4d8e3") //connecting to tmdb data base
        }catch (except:Exception){}

        if (tmdbApi!=null)
        {
            CoroutineScope(Dispatchers.IO).launch {getShowsForInfo(RecomandList)}
            CoroutineScope(Dispatchers.IO).launch { MyViewModel().urlRead()}
        }

        else
        {
            ShowChoco("Ops Something Went Wrong Try Restarting The App",this)
        }

    }

    suspend fun getShowsForInfo(showslist: ArrayList<Show>)
    {

        if (tmdbApi!=null)
        {
            for (i in 0 until  showslist.size)
            {
                ShowsForInfo.add(tmdbApi!!.tvSeries.getSeries(showslist[i].id, "en", TmdbTV.TvMethod.videos))
            }
        }

    }

    class MyViewModel(): ViewModel() {

        suspend fun urlRead() {
            var theString: String
            viewModelScope.launch(Dispatchers.IO) {
                theString = URL("https://api.themoviedb.org/3/trending/tv/week?api_key=26ab43b20236bd0f7b81b2b16df4d8e3").readText()
                CoroutineScope(Dispatchers.IO).launch {getHotShows(theString)}
            }

        }

        suspend fun getHotShows(Url:String)
        {
            val hotArr = ArrayList<HotShow>()
            val trending = JSONObject(Url)
            val pageRes  = trending.getJSONArray("results")
            for (i in 0 until  pageRes.length())
            {
                val hotS = pageRes.getJSONObject(i)
                val name = hotS["name"].toString()
                val id = hotS["id"] as Int
                val profile = hotS["poster_path"].toString()
                var year = hotS["first_air_date"].toString().split("-").toTypedArray()[0].toIntOrNull()
                if (year == null)
                {
                    hotArr.add(HotShow(name, profile, id, 0))
                }
                else
                {
                    hotArr.add(HotShow(name, profile, id, year))
                }
            }


            HotShows.add(ListHot("Trending",hotArr))
            notifyHot(0)
            getPopularShows()
        }
        suspend fun getPopularShows()
        {

            val temparr = ArrayList<HotShow>()
            if (tmdbApi!=null)
            {
                val pops = tmdbApi!!.tvSeries.getPopular("en", 1).results
                for (i in pops)
                {
                    val name = i.name
                    val id = i.id
                    val profile = i.posterPath
                    val year = i.firstAirDate.split("-").toTypedArray()[0].toInt()
                    temparr.add(HotShow(name, profile, id, year))
                }
                HotShows.add(ListHot("Popular",temparr))
                notifyHot(1)
                getTopRatedShows()
            }
            //getting the most popular shows and putting them in the popularshowslist


        }

        suspend fun getTopRatedShows()
        {  //getting the most popular shows and putting them in the popularshowslist
            val tempR = ArrayList<HotShow>()
            if (tmdbApi!=null)
            {            val Rated = tmdbApi!!.tvSeries.getTopRated("en", 1).results
                for (i in Rated)
                {
                    val name = i.name
                    val id = i.id
                    val profile = i.posterPath
                    val year = i.firstAirDate.split("-").toTypedArray()[0].toInt()
                    tempR.add(HotShow(name, profile, id, year))
                }
                HotShows.add(ListHot("Top Rated",tempR))
                notifyHot(2)
            }
        }
    }





}