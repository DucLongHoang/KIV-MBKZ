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
    private var atLastEx: Boolean

    init {
        kgList = ArrayList()
        kgListIndex = 0
        atLastEx = false
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
    fun displayNextWeight(inputKgs: Int) {
        kgListIndex++

        // if value was already filled, use it
        var currentKgs = kgList.getOrNull(kgListIndex)
        if(currentKgs != null) {
            editText.setText(currentKgs.toString())
        }
        else {
            if(kgListIndex > kgList.size) {
                kgList.add(inputKgs)
//                println("$inputKgs was put in, index is $kgListIndex")
            }
            editText.text.clear()
            return
        }
        // saving new values while going left and right
        // doesn't work for the next to last element
        kgList[kgListIndex - 1] = inputKgs
//        println("index: $kgListIndex, listSize: ${kgList.size}, list: $kgList")
    }

    /**
     * Method just decrements the [kgListIndex]
     */
    fun decrementKgIndex(){
        kgListIndex--
    }
}