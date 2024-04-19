package com.LielAzulay.reccomendo.Adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.LielAzulay.reccomendo.Activities.FinalPage
import com.LielAzulay.reccomendo.Activities.MainPage
import com.LielAzulay.reccomendo.R
import kotlinx.android.synthetic.main.tutorial_item.view.*


class TutorialAdapter(var context: Context):RecyclerView.Adapter<TutorialAdapter.TViewHodler>() {

    class TViewHodler(v: View) : RecyclerView.ViewHolder(v) {

        private var view: View = v
        var explain:TextView? = null
        var video:VideoView?=null
        var finish:Button?=null

        //3
        init {
            explain = view.tutorial_explain
            video = view.tutorial_vid
            finish = view.tutorial_finish
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TViewHodler {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.tutorial_item, parent, false)

        return TViewHodler(view)

    }

    override fun onBindViewHolder(holder: TViewHodler, position: Int) {
        val explainStr:String
        val vidInt:Int
        when(position)
        {
            0 -> {
                explainStr = "Swipe Left Or Right (Or Use The Buttons) In Order To Add Or Block The Suggested Show"
                vidInt = R.raw.addblock
            }
            1 ->
            {
                explainStr = "In Case You Already Watched The Suggested Show Press The Play Button"
                vidInt = R.raw.tutorial_watched
            }
            /*
            2 ->
            {
                explainStr = "After Adding A Show To Your List You Can Like Or Dislike It To Get More Accurate Recommendations"
                vidInt = R.raw.likesys
            }

             */
            else ->
            {
                explainStr = "You Can Change The Recommendations Genre And Episode Length At Any Time From The Settings Tab"
                vidInt = R.raw.changeinput
            }
        }

        if (position==2)
        {
            holder.finish?.visibility = View.VISIBLE
            holder.finish?.setOnClickListener {
                val intentfin = Intent(context, FinalPage::class.java)
                val intent = Intent(context, MainPage::class.java)
                context.startActivity(intentfin)
                context.startActivity(intent)
                (context as Activity).finish()
            }
        }
        else
        {
            holder.finish?.visibility = View.INVISIBLE
        }

        holder.explain?.text = explainStr

        val uri: Uri = Uri.parse("android.resource://com.LielAzulay.reccomendo/$vidInt")
        holder.video?.setVideoURI(uri)
        holder.video?.stopPlayback()
        holder.video?.setOnPreparedListener { mp -> //Start Playback
            holder.video?.start()
            //Loop Video
            mp!!.isLooping = true
        }

    }

    override fun getItemCount(): Int {
        return 3
    }



}