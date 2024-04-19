package com.LielAzulay.reccomendo.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.LielAzulay.reccomendo.Adapters.TutorialAdapter
import com.LielAzulay.reccomendo.R
import kotlinx.android.synthetic.main.activity_tutorial.*


class Tutorial : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)
        window.statusBarColor = getColor(R.color.tutorial)

        tutorialRecycle.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(tutorialRecycle)
        val tAdapter = TutorialAdapter(this)
        tutorialRecycle.adapter = tAdapter

        tutorialdot.attachToRecyclerView(tutorialRecycle)
    }
}