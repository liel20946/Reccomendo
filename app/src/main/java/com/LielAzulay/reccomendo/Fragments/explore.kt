package com.LielAzulay.reccomendo.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.LielAzulay.reccomendo.Adapters.VerticalAdapter
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.R
import kotlinx.android.synthetic.main.fragment_explore.view.*



class explore : Fragment() {
    companion object {
        var hotAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null
        @JvmStatic
        fun newInstance() = explore().apply { arguments = Bundle().apply {} }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)
        view.bigPager.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        view.bigPager.adapter = VerticalAdapter(MainActivity.HotShows)
        hotAdapter = view.bigPager.adapter

        return view
    }

}