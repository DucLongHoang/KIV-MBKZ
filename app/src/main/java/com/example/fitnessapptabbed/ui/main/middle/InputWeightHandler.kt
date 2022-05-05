package com.example.fitnessapptabbed.ui.main.middle

import android.widget.EditText
import com.example.fitnessapptabbed.ui.main.left.edit.Exercise

/**
 * [InputWeightHandler] class - handles correct
 * display of input weight edit text, showing nothing
 * or already inputted values, also changing values
 */
class InputWeightHandler(private val editText: EditText) {

    /**
     * Method saves [kgs] to [setNum] of [currExercise]
     */
    fun saveWeight(currExercise: Exercise, setNum:Int, kgs: Int) {
        // setNum - 1 because sets counted from 1 but indexing from 0
        currExercise.kgs[setNum - 1] = kgs
    }

    /**
     * Method correctly displays weight of the previous set/exercise
     */
    fun displayPreviousWeight(currExercise: Exercise, setNum: Int) {
        // setNum - 1 because sets counted from 1 but indexing from 0
        editText.setText(currExercise.kgs[setNum - 1].toString())
    }

    /**
     * Method correctly displays weight of the following set/exercise
     */
    fun displayNextWeight(currExercise: Exercise, setNum: Int) {
        // setNum - 1 because sets counted from 1 but indexing from 0
        val currentKgs = currExercise.kgs.getOrNull(setNum - 1)
        // all sets have by default kg of 0, next button disabled in TrainFragment class
        if (currentKgs == 0) editText.text.clear()
        else editText.setText(currExercise.kgs[setNum - 1].toString())
    }
}