package com.LielAzulay.reccomendo.Activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.LielAzulay.reccomendo.Fragments.shows
import com.LielAzulay.reccomendo.InBetweenActivities.About
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.R


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)


            val packgeName = "com.LielAzulay.reccomendo"

            val selectedgen = MainActivity.tiny?.getString("genre")
            val epilen = MainActivity.tiny?.getString("EpisodeLen")

            val genL = findPreference<ListPreference>("genere")
            val cachClear = findPreference<Preference>("clearC")
            val bugr = findPreference<Preference>("bug")
            val rateus = findPreference<Preference>("rate")
            val EpiL = findPreference<ListPreference>("epilen")
            val theme = findPreference<Preference>("theme")
            val about = findPreference<Preference>("about")


            if (genL != null) {
                genL.value = selectedgen
                genL.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        MainActivity.tiny?.putString("genre", newValue.toString())
                        true
                    }
            }


            if (EpiL != null) {
                EpiL.value = epilen
                EpiL.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        MainActivity.tiny?.putString("EpisodeLen", newValue.toString())
                        true
                    }
            }


            if (cachClear!=null)
            {
                cachClear.onPreferenceClickListener = Preference.OnPreferenceClickListener {

                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:$packgeName")
                    startActivity(intent)
                    true
                }
            }

            if (bugr!=null)
            {
                bugr.onPreferenceClickListener = Preference.OnPreferenceClickListener {

                    val gmailIntent = Intent(Intent.ACTION_SEND)
                    gmailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("recomendo2234@gmail.com"))//need to change to company mail
                    gmailIntent.putExtra(Intent.EXTRA_SUBJECT,"Bug Report")
                    gmailIntent.type = "message/rfc822"
                    startActivity(gmailIntent)
                    true
                }
            }

            if (rateus!=null)
            {
                rateus.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    val uri: Uri = Uri.parse("market://details?id=$packgeName")
                    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                    // To count with Play market backstack, After pressing back button,
                    // to taken back to our application, we need to add following flags to intent.
                    goToMarket.addFlags(
                        Intent.FLAG_ACTIVITY_NO_HISTORY or
                                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                    )
                    try {
                        startActivity(goToMarket)
                    } catch (e: ActivityNotFoundException) {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=$packgeName")
                            )
                        )
                    }
                    true
                }
            }

            if (theme!=null)
            {
                theme.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        if (newValue.toString().equals("Light"))
                        {
                            MainActivity.tiny?.putBoolean("LightTheme", true)
                            MainActivity.LightTheme = true

                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        }
                        else
                        {
                            MainActivity.tiny?.putBoolean("LightTheme", false)
                            MainActivity.LightTheme = false
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        }

                        shows.newSearch?.let { AddWatched.ShowsTheme(it, requireContext()) }

                        true
                    }

            }

            if (about != null) {
               about.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                   val intent = Intent(requireContext(), About::class.java)
                   startActivity(intent)
                   true
               }
            }

        }
    }


}