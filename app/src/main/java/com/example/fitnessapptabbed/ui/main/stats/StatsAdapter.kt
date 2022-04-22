package com.example.fitnessapptabbed.ui.main.stats

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapptabbed.R
import kotlinx.android.synthetic.main.item_statistic.view.*

class StatsAdapter(private val statistics: MutableList<Statistic>)
    : RecyclerView.Adapter<StatsAdapter.StatisticViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_statistic, parent, false)
        return StatisticViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StatisticViewHolder, position: Int) {
        val statistic = statistics[position]
        holder.exerciseName.text = statistic.exerciseName
        holder.recordKgs.text = statistic.recordKgs.toString() + " kg"
        holder.date.text = statistic.dateOfRecord
    }

    override fun getItemCount(): Int = statistics.size

    class StatisticViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.textViewExName
        val recordKgs: TextView = itemView.textViewKgs
        val date: TextView = itemView.textViewDate
    }
}