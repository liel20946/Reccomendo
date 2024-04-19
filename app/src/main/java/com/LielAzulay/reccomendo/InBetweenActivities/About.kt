package com.LielAzulay.reccomendo.InBetweenActivities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.LielAzulay.reccomendo.Activities.FinalPage
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.R
import kotlinx.android.synthetic.main.activity_about.*

class About : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        MainActivity.ChangeBackground(window.decorView.rootView,this)
        FinalPage.FinalTheme(window,this)
        finishabout.setOnClickListener {
            finish()
        }
    }
}