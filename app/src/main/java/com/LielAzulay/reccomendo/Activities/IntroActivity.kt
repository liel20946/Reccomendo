package com.LielAzulay.reccomendo.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.LielAzulay.reccomendo.R
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {//this is the intro activity only have one button to go to the next one
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val btn = findViewById<Button>(R.id.startbtn)

        btn.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        this.intoranim.setAnimation(R.raw.intro)

    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }

}