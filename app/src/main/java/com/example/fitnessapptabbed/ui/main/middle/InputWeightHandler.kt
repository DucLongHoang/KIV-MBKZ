package com.example.fitnessapptabbed.ui.main.middle

import android.widget.EditText

/**
 * [InputWeightHandler] class - handles correct
 * display of input weight edit text, showing nothing
 * or already inputted values, also changing values
 */
class InputWeightHandler(private val editText: EditText) {
    private val kgList: MutableList<Int>
    private var kgListIndex: Int

    init {
        kgList = ArrayList()
        kgListIndex = 0
    }

    /**
     * Method correctly displays weight of the previous set/exercise
     */
    fun displayPreviousWeight() {
        kgListIndex--

        val currentKgs = kgList.getOrNull(kgListIndex)
        if(currentKgs != null) {
            editText.setText(currentKgs.toString())
        }
        else kgListIndex++
    }

    /**
     * Method correctly displays weight of the following set/exercise
     */
    @JvmOverloads
    fun displayNextWeight(inputKgs: Int, last: Boolean = false) {
        kgListIndex++

        val currentKgs = kgList.getOrNull(kgListIndex)
        if(currentKgs != null) {
            editText.setText(currentKgs.toString())
        }
        else {
            if(kgListIndex > kgList.size) {
                kgList.add(inputKgs)
            }
            editText.text.clear()
        }
    }

}