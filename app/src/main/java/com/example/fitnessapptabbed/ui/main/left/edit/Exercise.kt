package com.example.fitnessapptabbed.ui.main.left.edit

/**
 * [Exercise] data class - defines exercise,
 * that consists of [name], [sets], [reps] and optionally [kgs]
 *
 * @author Long
 * @version 1.0
 */
data class Exercise (
    var name: String,
    var sets: Int,
    var reps: Int,
    var kgs: IntArray
)   {

    // empty secondary constructor with default values
    constructor() : this("", 0, 0, intArrayOf(0))
    constructor(exName: String, numSet: Int, numRep: Int) : this(exName, numSet, numRep, kgs = IntArray(numSet))

    /**
     * Returns [Boolean] if the exercise is valid or not
     */
    fun isNullExercise(): Boolean {
        return this == nullExercise
    }

    companion object {
        @JvmStatic
        fun congratulations() = Exercise("Congratulations", 0, 0, intArrayOf(0))
        @JvmStatic
        val nullExercise = Exercise()
    }
}