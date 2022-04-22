package com.example.fitnessapptabbed.ui.main.stats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapptabbed.R

class StatsAdapter(private val statistics: MutableList<Statistic>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plan, parent, false)
        return StatisticViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val statistic = statistics[position]
    }

    override fun getItemCount(): Int { return statistics.size }

    class StatisticViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.findViewById(R.id.textViewExName)
        val recordKgs: TextView = itemView.findViewById(R.id.textViewKgs)
        val date: TextView = itemView.findViewById(R.id.textViewDate)
    }
}