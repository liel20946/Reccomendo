package com.LielAzulay.reccomendo.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.LielAzulay.reccomendo.Activities.AddWatched
import com.LielAzulay.reccomendo.Adapters.FinalGridAdapter
import com.LielAzulay.reccomendo.Adapters.WatchedShowsAdapter
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.R
import com.paulrybitskyi.persistentsearchview.PersistentSearchView
import kotlinx.android.synthetic.main.fragment_shows.view.*



class shows : Fragment() {//my shows fragment

    companion object {
        @JvmStatic
        fun newInstance() = shows().apply { arguments = Bundle().apply {} }
        var deleteMode = false
        var adapter: WatchedShowsAdapter? = null
        var gridV:GridView? = null
        var newSearch:PersistentSearchView? = null

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_shows, container, false)//setting the view for the fragment
        val canStr = "Cancel"
        val rmStr = "Remove"
        gridV = view.showsgrid

        view.addnew_watched.setOnClickListener{
            val intent = Intent(requireContext(), AddWatched::class.java)
            startActivity(intent)
        }

        view.remove_show.setOnClickListener {
            deleteMode = !deleteMode
            if (deleteMode)
            {
                view.remove_show.text = canStr
                view.remove_show.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_replay_24),null,null,null)
            }
            else
            {
                view.remove_show.text = rmStr
                view.remove_show.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_delete_forever_24_d),null,null,null)
            }
            adapter?.notifyDataSetChanged()

        }

        adapter = WatchedShowsAdapter(requireContext(),  MainActivity.list)// setting up the adapter
        view.showsgrid.adapter = adapter

        return view
    }


}