package com.LielAzulay.reccomendo.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.LielAzulay.reccomendo.ExtraFiles.ListHot
import com.LielAzulay.reccomendo.R
import kotlinx.android.synthetic.main.vertical_hot_list.view.*

class VerticalAdapter(var listToShow: ArrayList<ListHot>): RecyclerView.Adapter<VerticalAdapter.VerticalHolder>() {


    class VerticalHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        var name: TextView? = null
        var pager:RecyclerView?=null

        //3
        init {

            name = view.hot_list_name
            pager = view.hotpager
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.vertical_hot_list, parent, false)


        return VerticalHolder(view)
    }


    override fun getItemCount(): Int {
        return listToShow.size
    }

    override fun onBindViewHolder(holder: VerticalHolder, position: Int) {
        holder.name?.text = listToShow[position].name
        holder.pager?.layoutManager = LinearLayoutManager(
            holder.pager?.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        val adAdapter =  HorizontalAdapter(listToShow[position].hotList)
        adAdapter.setHasStableIds(true)
        holder.pager?.adapter =adAdapter
    }
}