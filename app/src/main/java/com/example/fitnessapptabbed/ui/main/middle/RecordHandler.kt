package com.example.fitnessapptabbed.ui.main.middle

import android.widget.Toast
import com.example.fitnessapptabbed.database.PlansDatabaseHelper
import com.example.fitnessapptabbed.ui.main.right.Statistic
import com.example.fitnessapptabbed.util.DateTime
import com.example.fitnessapptabbed.util.ExerciseUtil

/**
 * [RecordHandler] class takes a [TrainFragment] as parameter
 * handles updating record in database and showing a [Toast]
 */
class RecordHandler(val fragment: TrainFragment) {
    private val databaseHelper = PlansDatabaseHelper(fragment.requireContext())
    private val listOfRecords: MutableList<Statistic> = databaseHelper.getAllExercisesFromDb()
    private val exShortcuts: Array<String> = ExerciseUtil.getAllExercisesShortcuts()

    /**
     * Method checks if a record of [exName] with was broken by weight [kgs]
     */
    fun checkIfRecordBroken(exName: String, kgs: Int) {
        var item: Statistic
        for (i in listOfRecords.indices) {
            item = listOfRecords[i]

            if (item.exerciseName == exName && item.recordKgs < kgs) {
                listOfRecords[i] = Statistic(exName, kgs, DateTime.getCurrentDateInString())
                updateRecordInDb(listOfRecords[i])
                makeNewRecordToast(exShortcuts[i], item.recordKgs, kgs)
                return
            }
        }
    }

    /**
     * Method makes a Toast [exName], old [recordKgs] and new record [kgs]
     */
    private fun makeNewRecordToast(exName: String, recordKgs: Int, kgs: Int) {
        val trimmedName = if(exName.length <=10) exName else exName.substring(0, 11)
        val message = "New personal best!!! \n$trimmedName: $recordKgs kg -> $kgs kg"
        Toast.makeText(fragment.context, message.uppercase(), Toast.LENGTH_LONG).show()
    }

    /**
     * Method updates the [statistic] in the database
     */
    private fun updateRecordInDb(statistic: Statistic) {
        databaseHelper.updateRecord(statistic)
    }
}