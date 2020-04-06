package com.example.competitiondetails.bottomSheet

import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.competitiondetails.R
import com.example.competitiondetails.databinding.TeamLineNamesBinding
import com.example.presentation.models.Player

class BottomSheetAdapter (
    private var squadList: List<Player>
): RecyclerView.Adapter<BottomSheetAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.team_line_names, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return squadList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding?.players = squadList[position].apply {
//            //This increments the serial number of each player
//            count = position + 1
//        }
    }

    fun updateAdapter(value: List<Player>){
        squadList = value
        return notifyDataSetChanged()
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding: TeamLineNamesBinding? = DataBindingUtil.bind(view)
        init {
            view.tag = binding
        }
    }

}