package com.LielAzulay.reccomendo.InBetweenActivities


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.LielAzulay.reccomendo.Activities.AlreadyLiked
import com.LielAzulay.reccomendo.Activities.FinalPage
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.R
import kotlinx.android.synthetic.main.activity_no_connection.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class NoConnection : AppCompatActivity() {//activity that opens when there is no internet connection
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_connection)
        MainActivity.ChangeBackground(window.decorView.rootView,this)
        FinalPage.FinalTheme(window, this)

        reconnectbtn.setOnClickListener {

            if (MainActivity.isInternetAvailable(this)
            )
            {
                finish()
            }
            else
            {
                trytoreco.visibility = View.VISIBLE
                AlreadyLiked.ConFaild = true
                CoroutineScope(IO).launch { trytoReconect() }
            }
        }
    }

    override fun onBackPressed() {}

    private suspend fun trytoReconect()//function that tries to "reconnect"
    {
        withContext(Main)
        {
            while (!MainActivity.isInternetAvailable(this@NoConnection))
            {
                delay(1000L)
            }
            delay(1000L)
            CoroutineScope(Dispatchers.IO).launch { MainActivity.MyViewModel().urlRead()}
            finish()
        }

    }


}