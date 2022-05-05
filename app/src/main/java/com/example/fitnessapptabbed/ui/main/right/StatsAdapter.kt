package com.example.fitnessapptabbed.ui.main.right

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapptabbed.R
import com.example.fitnessapptabbed.database.PlansDatabaseHelper
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
        var recordKgs: TextView = itemView.textViewKgs
        var date: TextView = itemView.textViewDate

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
            databaseHelper.updateRecordInDb(Statistic(exerciseName.text.toString()))
            databaseHelper.close()
        }
    }
}