package com.LielAzulay.reccomendo.InBetweenActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.airbnb.lottie.LottieAnimationView
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.Activities.Tutorial
import com.LielAzulay.reccomendo.R
import kotlinx.android.synthetic.main.activity_all_done_now.*
import kotlinx.coroutines.*

class AllDoneNow : AppCompatActivity() {//this activity making sure that the user is done with his registration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_done_now)


        buttonFin.setOnClickListener {

            //checking if all the internet requests are finished
            if (MainActivity.selectedShowsReco.isEmpty()|| MainActivity.selectedShowsReco.size!= MainActivity.selectedShowsIds.size)
            {
                CoroutineScope(Dispatchers.IO).launch { Load(this@AllDoneNow.loadanim) }
            }
            else
            {
                MainActivity.tiny?.putBoolean("registerdone",true)
                MainActivity.saveData()
                val intent = Intent(this@AllDoneNow, Tutorial::class.java)
                startActivity(intent)
                finishAffinity()

            }

        }
        back.setOnClickListener {
            finish()
        }
    }

    private suspend fun Load(load:LottieAnimationView)
    {
        val opss = MainActivity.Load(load)
        withContext(Dispatchers.Main)
        {

            if (!opss)
            {
                MainActivity.tiny?.putBoolean("registerdone",true)
                MainActivity.saveData()
                val intent = Intent(this@AllDoneNow, Tutorial::class.java)
                startActivity(intent)
                finishAffinity()
            }
            else
            {
                val intent = Intent(this@AllDoneNow, Ops::class.java)
                startActivity(intent)
                finish()
            }

        }
    }
}