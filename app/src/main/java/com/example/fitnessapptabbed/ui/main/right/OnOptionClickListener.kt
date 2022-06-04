package com.example.fitnessapptabbed.ui.main.right

/**
 * [OnOptionClickListener] interface provides options for Popup menu
 * @author Long
 */
interface OnOptionClickListener {
    fun onEditExerciseClick(position: Int)
    fun onNullifyRecordClick(position: Int)
    fun onRemoveOptionClick(position: Int)
}