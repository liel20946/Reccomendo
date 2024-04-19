package com.LielAzulay.reccomendo.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.LielAzulay.reccomendo.Activities.MainPage
import com.LielAzulay.reccomendo.Adapters.ListViewAdAdapter
import com.LielAzulay.reccomendo.ExtraFiles.Show
import com.LielAzulay.reccomendo.MainActivity
import com.LielAzulay.reccomendo.R
import com.rm.rmswitch.RMTristateSwitch
import kotlinx.android.synthetic.main.fragment_home.view.*


class home : Fragment() {//the Main Home Page Fragment

    companion object
    {
        @JvmStatic
        fun newInstance() = home().apply { arguments = Bundle().apply {} }
        var RecoAdapter: ListViewAdAdapter? = null
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        HomeTheme(view)
        RecoAdapter = ListViewAdAdapter(requireContext(), MainActivity.RecomandList)
        view.RecoList.adapter = RecoAdapter
        view.RecoList.emptyView = view.emptytext
//        val switchToggle = view.togglebtn
//        val fadeoutA = AnimationUtils.loadAnimation(requireContext(), R.anim.fragment_fade_out)
//        val fadeinA = AnimationUtils.loadAnimation(requireContext(), R.anim.fragment_fade_in)


        val fab = view.getARecoBtn
        fab.setOnClickListener {//test button
            val intent = Intent(activity?.baseContext, MainPage::class.java)
            startActivity(intent)
        }

        fab.attachToListView(view.RecoList)

//        switchToggle.addSwitchObserver { switchView, state ->
//            view.RecoList.startAnimation(fadeoutA)
//            val tempAdapter:ListViewAdAdapter = when (state) {
//                RMTristateSwitch.STATE_LEFT -> {
//                    RecoAdapter!!
//                }
//                RMTristateSwitch.STATE_MIDDLE ->
//                {
//                    ListViewAdAdapter(requireContext(),getFilterArr(true))
//                }
//                else -> {
//                    ListViewAdAdapter(requireContext(),getFilterArr(false))
//                }
//
//            }
//            view.RecoList.adapter = tempAdapter
//            view.RecoList.startAnimation(fadeinA)
//        }
        
        return view
    }

    fun HomeTheme(view: View)
    {
        if (MainActivity.LightTheme)
        {
            view.getARecoBtn.setImageResource(R.drawable.ic_baseline_add_to_queue_24)
        }
        else
        {
            view.getARecoBtn.setImageResource(R.drawable.ic_baseline_add_to_queue_24_d)
        }

    }

//    private fun getFilterArr(liked:Boolean):ArrayList<Show>
//    {
//        val checkInt: Int
//        val returnArr = ArrayList<Show>()
//
//        if (liked)
//        {
//            checkInt = 1
//        }
//        else
//        {
//            checkInt = 2
//        }
//
//        for(i in MainActivity.RecomandList)
//        {
//            if (i.likeStat==checkInt)
//            {
//                returnArr.add(i)
//            }
//        }
//
//        return returnArr
//
//    }

}