package com.LielAzulay.reccomendo.InBetweenActivities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.LielAzulay.reccomendo.Activities.FinalPage
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.R
import kotlinx.android.synthetic.main.activity_no_more.*

class NoMore : AppCompatActivity() {//activity that opens when the user has gone through all the recommendations
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_more)
        MainActivity.ChangeBackground(window.decorView.rootView,this)
        FinalPage.FinalTheme(window, this)

        //gives him two choices getting back to the list or go to home page
        GoMain.setOnClickListener {
            finish()
        }


    }

    override fun onBackPressed() {

    }
}