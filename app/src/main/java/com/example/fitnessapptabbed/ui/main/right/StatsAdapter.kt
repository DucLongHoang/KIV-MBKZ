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
 * [StatsAdapter] that provides ViewHolders to a RecyclerView
 * @author Long
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

    override fun getItemCount(): Int = statistics.size

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
            popup.inflate(R.menu.options_menu_exercise)
            popup.setOnMenuItemClickListener { item ->
                if (holder.absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    when (item.itemId) {
                        R.id.option_edit_exercise -> optionClickListener
                            .onEditExerciseClick(holder.absoluteAdapterPosition)
                        R.id.option_nullify_record -> optionClickListener
                            .onNullifyRecordClick(holder.absoluteAdapterPosition)
                        R.id.option_remove_exercise -> optionClickListener
                            .onRemoveOptionClick(holder.absoluteAdapterPosition)
                    }
                }; true
            }
            popup.show()
        }
    }

    /**
     * Method reflects drag and drop actions in [RecyclerView]
     */
    fun moveItemInRecyclerViewList(from: Int, to: Int) {
        val movedItem: Statistic = statistics[from]
        statistics.removeAt(from)
        statistics.add(to, movedItem)
    }

    /**
     * [StatisticViewHolder] class - encapsulates a [Statistic]
     */
    inner class StatisticViewHolder(val itemBinding: ItemStatisticBinding)
        : RecyclerView.ViewHolder(itemBinding.root) {

        /**
         * Method binds a [statistic] to a [StatisticViewHolder]
         */
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