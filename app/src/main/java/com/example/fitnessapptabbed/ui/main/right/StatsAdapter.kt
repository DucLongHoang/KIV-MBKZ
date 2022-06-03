package com.example.fitnessapptabbed.ui.main.right

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapptabbed.R
import com.example.fitnessapptabbed.database.PlansDatabaseHelper
import com.example.fitnessapptabbed.ui.main.left.OnItemClickListener
import kotlinx.android.synthetic.main.item_statistic.view.*

class StatsAdapter(
    private val statistics: MutableList<Statistic>,
    private val context: Context)
    : RecyclerView.Adapter<StatsAdapter.StatisticViewHolder>() {
    private lateinit var itemClickListener: OnItemClickListener

    /**
     * Sets [itemClickListener] to [onItemClickListener]
     */
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

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
        holder.menuButton.setOnClickListener {
            val popup = PopupMenu(context, holder.menuButton)
            popup.inflate(R.menu.options_menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.option_move_one_up -> {}
                    R.id.option_move_one_down -> {}
                    R.id.option_nullify_record -> {}
                    R.id.option_remove_exercise -> {}
                }
                true
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = statistics.size

    class StatisticViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.textViewExName
        var recordKgs: TextView = itemView.textViewKgs
        var date: TextView = itemView.textViewDate
        var menuButton: ImageButton = itemView.statisticOptionMenu

        init {
            // onLongClickListener needs to return a boolean, dunno why - StackOverflow
            itemView.setOnLongClickListener { nullifyRecordDialog(); true }
        }

        /**
         * Method displays an [AlertDialog] to confirm record nullification
         */
        private fun nullifyRecordDialog() {
            // create dialog
            val dialogBuilder = AlertDialog.Builder(itemView.context)
            dialogBuilder.setTitle(R.string.nullify_record_prompt)

            // setting options
            dialogBuilder.setNegativeButton(R.string.no) { dialogInterface, _: Int -> dialogInterface.cancel() }
            dialogBuilder.setPositiveButton(R.string.yes) { _, _: Int ->
                nullifyRecord()
                date.setText(R.string.default_date)
                recordKgs.setText(R.string.default_kg)
            }

            // show dialog
            val dialog = dialogBuilder.create()
            dialog.show()
        }

        /**
         * Method nullifies record in the database
         */
        private fun nullifyRecord() {
            val databaseHelper = PlansDatabaseHelper(itemView.context)
            databaseHelper.nullifyRecordInDb(exerciseName.text.toString())
            databaseHelper.close()
        }
    }
}