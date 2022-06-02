package com.example.fitnessapptabbed.ui.main.middle

import android.widget.Toast
import com.example.fitnessapptabbed.R
import com.example.fitnessapptabbed.database.PlansDatabaseHelper
import com.example.fitnessapptabbed.ui.main.right.Statistic
import com.example.fitnessapptabbed.util.DateTimeUtils

/**
 * [RecordHandler] class takes a [TrainFragment] as parameter
 * handles updating record in database and showing a [Toast]
 */
class RecordHandler(val fragment: TrainFragment) {
    private val databaseHelper = PlansDatabaseHelper(fragment.requireContext())
    private val listOfRecords: MutableList<Statistic> = databaseHelper.getAllExercisesFromDb()

    /**
     * Method checks if a record of [exName] with was broken by weight [kgs]
     */
    fun checkIfRecordBroken(exName: String, kgs: Int) {
        var item: Statistic
        for (i in listOfRecords.indices) {
            item = listOfRecords[i]

            if (item.exerciseName == exName && item.recordKgs < kgs) {
                listOfRecords[i] = Statistic(item.exerciseName, item.exerciseShortcut,
                    kgs, DateTimeUtils.getCurrentDateInString(), item.order)

                updateRecordInDb(listOfRecords[i])
                makeNewRecordToast(item.exerciseShortcut, item.recordKgs, kgs)
                return
            }
        }
    }

    /**
     * Method makes a Toast [exName], old [recordKgs] and new record [kgs]
     */
    private fun makeNewRecordToast(exName: String, recordKgs: Int, kgs: Int) {
        var shortcut: String = exName
        if (exName.length > 11) shortcut = exName.substring(0, 11)

        val message = fragment.context?.getString(R.string.new_record_msg) +
                "\n$shortcut: $recordKgs kg -> $kgs kg"
        Toast.makeText(fragment.context, message.uppercase(), Toast.LENGTH_LONG).show()
    }

    /**
     * Method updates the [statistic] in the database
     */
    private fun updateRecordInDb(statistic: Statistic) {
        databaseHelper.updateRecordInDb(statistic)
    }
}