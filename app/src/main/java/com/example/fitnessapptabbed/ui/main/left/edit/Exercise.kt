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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Exercise

        if (name != other.name) return false
        if (sets != other.sets) return false
        if (reps != other.reps) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + sets
        result = 31 * result + reps
        return result
    }

    companion object {
        @JvmStatic
        fun congratulations() = Exercise("Congratulations", 0, 0, intArrayOf(0))
        @JvmStatic
        val nullExercise = Exercise()
    }
}