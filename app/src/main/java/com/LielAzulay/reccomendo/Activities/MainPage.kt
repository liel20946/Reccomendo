package com.LielAzulay.reccomendo.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.LielAzulay.reccomendo.Adapters.MainPageAdapter
import com.LielAzulay.reccomendo.ExtraFiles.Show
import com.LielAzulay.reccomendo.Fragments.home
import com.LielAzulay.reccomendo.Fragments.shows
import com.LielAzulay.reccomendo.Fragments.statistics
import com.LielAzulay.reccomendo.InBetweenActivities.NoConnection
import com.LielAzulay.reccomendo.InBetweenActivities.NoMore
import com.LielAzulay.reccomendo.InBetweenActivities.Ops
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.R
import com.yuyakaido.android.cardstackview.*
import info.movito.themoviedbapi.TmdbApi
import info.movito.themoviedbapi.TmdbTV
import info.movito.themoviedbapi.model.Genre
import info.movito.themoviedbapi.model.tv.Network
import info.movito.themoviedbapi.model.tv.TvSeries
import kotlinx.android.synthetic.main.activity_main_page_adapter.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception


class MainPage : AppCompatActivity(), CardStackListener {//this activity shows the recommendations


    companion object {
        var mainpageTmdb: TmdbApi? = null
        var pagerAdapter: MainPageAdapter? = null
        var addingNewShow = false
        val doneblockedL = ArrayList<Int>()
        var recommendList = ArrayList<TvSeries>()//an array of the recommendations


        fun addShowAsReco(id: Int) {
            try {
                val temps = mainpageTmdb!!.tvSeries.getSeries(id, "en", TmdbTV.TvMethod.videos)
                MainActivity.ShowsForInfo.add(0, temps)
            } catch (except: Exception) {
            }

        }

        fun updateBlockedList() {
            doneblockedL.clear()
            doneblockedL.addAll(MainActivity.selectedShowsIds)
            if (MainActivity.recoshowsToBlock.isNotEmpty()) {
                for (i in MainActivity.recoshowsToBlock) {
                    if (!doneblockedL.contains(i)) {
                        doneblockedL.add(i)
                    }
                }
            }
        }

        fun getGenString(gen: List<Genre>): String// this function return an array that contain sorted string of the genre and network list
        {
            var GenString = ""


            for (i in gen) {
                if (gen.indexOf(i) != gen.size - 1) {
                    GenString += i.name + ", "
                } else {
                    GenString += i.name
                }
            }

            return GenString
        }

        fun GetNetString(net: List<Network>): String {
            var NetString = ""

            for (x in net) {
                if (net.indexOf(x) != net.size - 1) {
                    NetString += x.name + ", "
                } else {
                    NetString += x.name
                }
            }
            return NetString
        }


    }

    var index :Int?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_page_adapter)

        setSupportActionBar(maintoolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24_d)

        MainActivity.ChangeBackground(window.decorView.rootView, this)

        CoroutineScope(Dispatchers.IO).launch { mainConnect() }

        if (MainActivity.selectedShowsReco.isEmpty() || MainActivity.selectedShowsReco.size != MainActivity.selectedShowsIds.size) {
            CoroutineScope(Dispatchers.IO).launch { Load(mainpLoad) }

        } else {
            InitiateMainPage()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    fun InitiateMainPage() {
        updateBlockedList()
        GetPoll(MainActivity.selectedShowsReco, doneblockedL)

        title = recommendList.size.toString() + " Recommendations For You"

        val cardStackView = sliderPagerView

        val manager = CardStackLayoutManager(this,this)
        manager.setCanScrollVertical(false)

        cardStackView.layoutManager = manager
        pagerAdapter = MainPageAdapter(recommendList, this)
        cardStackView.adapter = pagerAdapter

        index = manager.topPosition
        blockBtn.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }

        buttonm.setOnClickListener {//add the show to watched shows
            if (MainActivity.isInternetAvailable(this)) {
                if (!addingNewShow) {
                    fabProgressCircle.show()
                    addingNewShow = true
                    buttonm.isEnabled = false
                    buttonr.isEnabled = false
                    blockBtn.isEnabled = false
                    manager.setCanScrollHorizontal(false)
                    val tempS = recommendList[index!!]
                    CoroutineScope(Dispatchers.IO).launch { getRecos(tempS.id) }
                }

            } else {
                val intent = Intent(this, NoConnection::class.java)
                startActivity(intent)
            }

        }
        buttonr.setOnClickListener {//adding the selected recommendation to the reco list and finishing the activity
            title = (recommendList.size-1).toString() + " Recommendations For You"
           addBtnFun(index!!)

        }


    }

    private suspend fun Load(load: LottieAnimationView) {
        withContext(Main)
        {
            button_container.visibility =View.INVISIBLE
        }
        val ops = MainActivity.Load(load)
        withContext(Main)
        {

            if (!ops) {
                MainActivity.saveData()
                InitiateMainPage()
            } else {
                finish()
                val intent = Intent(this@MainPage, Ops::class.java)
                startActivity(intent)
            }
            button_container.visibility =View.VISIBLE
            load.visibility = View.INVISIBLE
            load.cancelAnimation()
        }
    }


    fun GetPoll(
        selectedList: MutableMap<Int, ArrayList<TvSeries>>,
        blackList: ArrayList<Int>
    )// the main algorithm function that filter through shows to get the best shows for the user
    {

        val finalListIds = ArrayList<Int>()//an array to make sure we don't add the save show twice
        var shortenList = ArrayList<TvSeries>()//filtered array of shows

        val FavGen = MainActivity.tiny?.getString("genre")
        val FavEpiLen =
            MainActivity.tiny?.getString("EpisodeLen")//the selected episode length option
        val EpiLenChoice: Int

        when (FavEpiLen) //switch case for the episode length choice
        {
            "Short (~20–30 min)" -> EpiLenChoice = 1
            "Average (~45 min)" -> EpiLenChoice = 2
            "Long (over 45 min)" -> EpiLenChoice = 3
            else -> {
                EpiLenChoice = 0
            }
        }

        for (i in selectedList) //first we make and array containing all the recommendations while checking we don't have doubles or a selected show in it
        {
            for (x in 0 until i.value.size) {
                if (!blackList.contains(i.value.get(x).id) && !finalListIds.contains(i.value.get(x).id)) {
                    shortenList.add(i.value.get(x))
                    finalListIds.add(i.value.get(x).id)
                }
            }
        }

        if (shortenList.isNotEmpty()) {
            shortenList =
                MainActivity.FinalRecoScore(shortenList)//sorting the shows by popularity and rating

            if (FavGen.equals("I Like It All")) {
                if (EpiLenChoice != 0) //in case the user choose and episode length(anything but "dos'nt matter"
                {
                    shortenList = EpiLenFilter(
                        shortenList,
                        EpiLenChoice
                    )//filtering the shows that at'nt in the right run time range
                }
                if (shortenList.isNotEmpty()) {
                    recommendList =
                        shortenList//put the array of the finished recommendations into an array that the whole functions can use
                } else {
                    val intent = Intent(this, NoMore::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                shortenList = FavGen?.let {
                    GenereFilter(
                        shortenList,
                        it
                    )
                }!!//filtering the shows that ar'nt in the genre

                if (shortenList.isNotEmpty()) {
                    if (EpiLenChoice != 0) //in case the user choose and episode length(anything but "dos'nt matter"
                    {
                        shortenList = EpiLenFilter(
                            shortenList,
                            EpiLenChoice
                        )//filtering the shows that at'nt in the right run time range
                    }
                    if (shortenList.isNotEmpty()) {
                        recommendList =
                            shortenList//put the array of the finished recommendations into an array that the whole functions can use
                    } else {
                        val intent = Intent(this, NoMore::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    val intent = Intent(this, NoMore::class.java)
                    startActivity(intent)
                    finish()
                }

            }

        } else {
            val intent = Intent(this, NoMore::class.java)
            startActivity(intent)
            finish()
        }


    }

    fun addBtnFun(index:Int)
    {
        if (MainActivity.isInternetAvailable(this)) {
            if (!addingNewShow) {
                val myshow = recommendList[index]
                MainActivity.recoshowsToBlock.add(myshow.id)
                val picString = "https://image.tmdb.org/t/p/w500" + myshow.posterPath
                val net = GetNetString(myshow.networks)
                var lastAir = myshow.lastAirDate
                if (lastAir == null) {
                    lastAir = ""
                }
                MainActivity.RecomandList.add(
                    0,
                    Show(
                        myshow.name,
                        picString,
                        false,
                        myshow.id,
                        myshow.status,
                        net,
                        0,
                        lastAir
                    )
                )
                CoroutineScope(Dispatchers.IO).launch { addShowAsReco(myshow.id) }
                MainActivity.tiny?.putListInt("showtoBlock", MainActivity.recoshowsToBlock)
                MainActivity.tiny?.putListObjectShow(
                    "recoShowsListview",
                    MainActivity.RecomandList
                )
                if (MainActivity.register) {
                    finish()
                    home.RecoAdapter?.notifyDataSetChanged()
                } else {
                    val intent = Intent(this, FinalPage::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finishAffinity()
                }
            }

        } else//in case there is no connection
        {
            val intent = Intent(this, NoConnection::class.java)
            startActivity(intent)
        }
    }

    fun blockBtnFun(index: Int)
    {
        if (!addingNewShow) {
            MainActivity.recoshowsToBlock.add(recommendList[index].id)
            MainActivity.tiny?.putListInt("showtoBlock", MainActivity.recoshowsToBlock)
            recommendList.removeAt(index)

            if (recommendList.size == 0) {
                finish()
            }
            else
            {
                sliderPagerView.adapter?.notifyItemRemoved(0)
            }

            title = recommendList.size.toString() + " Recommendations For You"

        }
    }


    fun GenereFilter(LastShowList: ArrayList<TvSeries>,FavGenere:String): ArrayList<TvSeries>//a function that filter by genre
    {
        val Filtered = ArrayList<TvSeries>()
        val indexArr = ArrayList<Int>()


        for (a in LastShowList)// first filter
        {
            for (s in a.genres) {
                if (s.name.equals(FavGenere)) {
                    Filtered.add(a)
                    indexArr.add(a.genres.indexOf(s))
                    break
                }
            }
        }


        if (Filtered.isEmpty())
        {
            return Filtered//in case all the shows got filtered we return the original list
        }

        else
        {//here we make sure that the shows are in the right order - first shows that have the selected genre as the first genre and soo on
            for (i in 0 until Filtered.size - 1) {
                for (x in 0 until Filtered.size - i - 1)
                {
                    val TempScoreCurrent = indexArr[x]
                    val TempScoreNext = indexArr[x + 1]

                    if (TempScoreCurrent > TempScoreNext)
                    {
                        val TempShow = Filtered[x + 1]
                        val TempInd = indexArr[x + 1]

                        Filtered[x + 1] = Filtered[x]
                        Filtered[x] = TempShow
                        indexArr[x + 1]= indexArr[x]
                        indexArr[x]= TempInd
                    }
                }
            }
            return Filtered
        }
    }


    fun EpiLenFilter(List: ArrayList<TvSeries>, choice: Int): ArrayList<TvSeries>//this function filter shows by episode run time range
    {
        val Filtered = ArrayList<TvSeries>()
        var averege: Int
        val range:IntRange

        if (choice==1)
        {
            range= 20..35
        }
        else if (choice==2)
        {
            range = 36..50
        }
        else
        {
            range = 50..1000
        }

        for (i in List)
        {
            averege = statistics.getAverege(i.episodeRuntime)
            if (averege!=0)
            {
                if (averege in range)
                {
                    Filtered.add(i)
                }
            }
            else
            {
                Filtered.add(i)
            }

        }

        return Filtered

    }

    fun mainConnect()
    {
        try {
            mainpageTmdb = TmdbApi("26ab43b20236bd0f7b81b2b16df4d8e3") //connecting to tmdb data base

        }catch (except:Exception){}

        if (mainpageTmdb ==null)
        {
            MainActivity.ShowChoco("Ops Something Went Wrong Try Restarting The Page",this)
        }
    }

    suspend fun getRecos(id: Int)//a function that add a show to the selected and gets all of its recommendations
    {
        val copy = mutableMapOf<Int, ArrayList<TvSeries>>()
        copy.putAll(MainActivity.selectedShowsReco)
        MainActivity.selectedShowsReco.clear()
        val tempArray = ArrayList<TvSeries>()

        try {
            val CurrentSelectedTv = mainpageTmdb!!.tvSeries.getSeries(
                id,
                "en",
                TmdbTV.TvMethod.recommendations
            )
            MainActivity.SelectedTvSeriesArr.add(CurrentSelectedTv)

            val pic = "https://image.tmdb.org/t/p/w500" + CurrentSelectedTv.posterPath
            val recS = CurrentSelectedTv.recommendations.results.size

            if (recS>10)
            {
                val listToRun = MainActivity.FinalRecoScore(CurrentSelectedTv.recommendations.results as ArrayList<TvSeries>)
                for (i in 0 until 10)
                {
                    tempArray.add(mainpageTmdb!!.tvSeries.getSeries(listToRun[i].id, "en"))
                }
            }
            else
            {
                for (i in CurrentSelectedTv.recommendations.results)
                {
                    tempArray.add(mainpageTmdb!!.tvSeries.getSeries(i.id, "en"))
                }
            }


            copy[CurrentSelectedTv.id] = tempArray
            MainActivity.selectedShowsIds.add(0, id)
            MainActivity.list.add(0, Show(CurrentSelectedTv.name, pic, true, id, CurrentSelectedTv.status, "", 0,CurrentSelectedTv.lastAirDate)
            )
            MainActivity.selectedShowsReco = copy


            //updateBlockedList()
            MainActivity.saveData()

            withContext(Main)
            {
                shows.adapter?.notifyDataSetChanged()
            }

            restartAct(this,this)

        }catch (except:Exception){}

    }

    fun restartAct(activity: Activity,context: Context)
    {
        activity.finish()
        activity.overridePendingTransition(0, 0)
        val intent = Intent(context, MainPage::class.java)
        context.startActivity(intent)
        activity.overridePendingTransition(0, 0)
        addingNewShow = false
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
        Log.d("CardStackView", "onCardDrag:")
    }

    override fun onCardSwiped(direction: Direction?) {
        when(direction)
        {
            Direction.Left->
            {
                index?.let { blockBtnFun(it) }
            }
            else ->
            {
                title = (recommendList.size-1).toString() + " Recommendations For You"
                index?.let { addBtnFun(it) }
            }
        }

    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound:")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled:")
    }

    override fun onCardAppeared(view: View?, position: Int) {
        Log.d("CardStackView", "onCardAppeared:")
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        Log.d("CardStackView", "onCardDiss:")
    }



}