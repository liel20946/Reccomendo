package com.LielAzulay.reccomendo.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.R
import kotlinx.android.synthetic.main.activity_register.*


class Register : AppCompatActivity() {//this activity gets the initial information about the user

    lateinit var genres :Array<String>
    lateinit var EpisodeLen :Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        genres = arrayOf(
            getString(R.string.genre),
            "Action & Adventure",
            "Animation",
            "Comedy",
            "Crime",
            "Documentary",
            "Drama",
            "Family",
            "Kids",
            "Mystery",
            "News",
            "Reality",
            "Sci-Fi & Fantasy",
            "Soap",
            "Talk",
            "War & Politics",
            "Western",
            "I Like It All"
        )
        EpisodeLen = arrayOf(
            getString(R.string.epilen),
            "Short (~20–30 min)",
            "Average (~45 min)",
            "Long (over 45 min)",
            "All"
        )


        var choicegere = ""
        var choiceepisode = ""



        back.setOnClickListener {
            onBackPressed()
        }

        buttonEnd2.setOnClickListener {

            if (nameE.text.toString().equals("") ||  choiceepisode.equals("Select Preferred Episode Length") || choicegere.equals("Select One Favorite Genre"))// checking if the user filled all the fields
            {
                MainActivity.ShowChoco("Please Fill All The Fields",this)

            } else // in case the user has filled all the fields saving the data
            {
                MainActivity.tiny?.putString("name", nameE.text.toString())

                MainActivity.tiny?.putString("genre", choicegere)
                MainActivity.tiny?.putString("EpisodeLen", choiceepisode)

                MainActivity.tiny?.putBoolean("FirstInfo", true)

                val intent = Intent(this, AlreadyLiked::class.java)
                startActivity(intent)
            }

        }


        if (spinner != null) { //setting up the spinner for the genres
            val adapter = ArrayAdapter(this,
                R.layout.spinner_item, genres)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    choicegere = genres[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
            spinner.setOnTouchListener { view, _ ->
                hideKeyboard(view)
                spinner.performClick()
            }
        }


        if (spinner2 != null) { //setting up the spinner for the Episode Length
            val adapter = ArrayAdapter(
                this,
                R.layout.spinner_item, EpisodeLen
            )
            spinner2.adapter = adapter
            spinner2.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    choiceepisode = EpisodeLen[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
            spinner2.setOnTouchListener { view, _ ->
                hideKeyboard(view)
                spinner2.performClick()
            }

        }

    }


    fun hideKeyboard(view: View)//hiding the keyboard when user touches outside of the keyboard area
    {
        val inputMethod = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethod.hideSoftInputFromWindow(view.windowToken, 0)

    }
}