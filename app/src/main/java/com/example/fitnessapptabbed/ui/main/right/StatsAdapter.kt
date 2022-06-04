package com.example.fitnessapptabbed.ui.main.right

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapptabbed.R
import com.example.fitnessapptabbed.databinding.ItemStatisticBinding

/**
 *
 */
class StatsAdapter(
    private val statistics: MutableList<Statistic>,
    private val context: Context)
    : RecyclerView.Adapter<StatsAdapter.StatisticViewHolder>() {
    private lateinit var optionClickListener: OnOptionClickListener

    /**
     * Sets [optionClickListener] to [onOptionClickListener]
     */
    fun setItemClickListener(onOptionClickListener: OnOptionClickListener) {
        this.optionClickListener = onOptionClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticViewHolder {
        val itemBinding = ItemStatisticBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return StatisticViewHolder(itemBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StatisticViewHolder, position: Int) {
        val statistic = statistics[position]
        holder.bind(statistic)
        holder.itemBinding.optionMenu.setOnClickListener {
            val popup = PopupMenu(context, holder.itemBinding.optionMenu)
            popup.inflate(R.menu.options_menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.option_move_one_up -> { optionClickListener.onMoveUpClick(position) }
                    R.id.option_move_one_down -> { optionClickListener.onMoveDownClick(position) }
                    R.id.option_nullify_record -> { optionClickListener.onNullifyRecordClick(position) }
                    R.id.option_remove_exercise -> { optionClickListener.onRemoveOptionClick(position) }
                }
                true
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = statistics.size

    inner class StatisticViewHolder(val itemBinding: ItemStatisticBinding)
        : RecyclerView.ViewHolder(itemBinding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(statistic: Statistic) {
            itemBinding.apply {
                textViewExName.text = statistic.exerciseName
                textViewKgs.text = "${statistic.recordKgs} kg"
                textViewDate.text = statistic.dateOfRecord
            }
        }
    }
}