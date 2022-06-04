package com.example.fitnessapptabbed.ui.main.right

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapptabbed.R
import com.example.fitnessapptabbed.databinding.ItemStatisticBinding
import kotlinx.android.synthetic.main.item_statistic.view.*

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
        holder.menuButton.setOnClickListener {
            val popup = PopupMenu(context, holder.menuButton)
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

    inner class StatisticViewHolder(private val itemBinding: ItemStatisticBinding)
        : RecyclerView.ViewHolder(itemBinding.root) {
        val menuButton: ImageButton = itemView.statisticOptionMenu

        @SuppressLint("SetTextI18n")
        fun bind(statistic: Statistic) {
            itemBinding.textViewExName.text = statistic.exerciseName
            itemBinding.textViewKgs.text = "${statistic.recordKgs} kg"
            itemBinding.textViewDate.text = statistic.dateOfRecord
        }
    }
}