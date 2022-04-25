package com.example.fitnessapptabbed.ui.main.middle

import android.widget.Toast
import com.example.fitnessapptabbed.database.PlansDatabaseHelper
import com.example.fitnessapptabbed.ui.main.right.Statistic
import java.text.SimpleDateFormat
import java.util.*

/**
 * [RecordHandler] class takes a [TrainFragment] as parameter
 * handles updating record in database and showing a [Toast]
 */
class RecordHandler(val fragment: TrainFragment) {
    private val databaseHelper = PlansDatabaseHelper(fragment.requireContext())
    private val listOfRecords: MutableList<Statistic> = databaseHelper.getAllExercisesFromDb()
    private val exShortcuts: Array<String> = arrayOf(
        "bench press", "deadlift", "dl - sumo", "squat", "bicep curl", "hamm. curl",
        "tricep ext", "tricep kb", "tricep pd", "front raise", "side raise",
        "shldr pulls", "shldr press", "db chest p", "chest fly", "bb row",
        "db row", "hamst. curl", "bulg. s. s", "lunge", "calf raise", "push up",
        "pull up", "chin up", "dip", "pis. squat", "abs ex"
    )

    /**
     * Method checks if a record of [exName] with was broken by weight [kgs]
     */
    fun checkIfRecordBroken(exName: String, kgs: Int) {
        var item: Statistic
        for (i in listOfRecords.indices) {
            item = listOfRecords[i]

            if (item.exerciseName == exName && item.recordKgs < kgs) {
                val date = getCurrentDateTime()
                val dateInString = date.toString("dd.MM.yyyy")

                listOfRecords[i] = Statistic(exName, kgs, dateInString)
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

    /**
     * Method extension, formats [Date] to [format]
     */
    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    /**
     * Method returns [Date] with current time
     */
    private fun getCurrentDateTime(): Date = Calendar.getInstance().time

}