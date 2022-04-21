package com.example.fitnessapptabbed.ui.main.plans

/**
 * [Exercise] data class - defines exercise,
 * that consists of [name], [sets], [reps] and optionally [kgs]
 *
 * @author Long
 * @version 1.0
 */
data class Exercise(
    var name: String,
    var sets: Int,
    var reps: Int,
) {
    private var kgs: Int = 0

    /**
     * Secondary constructor with known [kgs] value
     */
    constructor(name: String, sets: Int, reps: Int, kgs: Int) : this(name, sets, reps) {
        this.kgs = kgs
    }

    fun setKgs(value: Int) {
        this.kgs = value
    }
}