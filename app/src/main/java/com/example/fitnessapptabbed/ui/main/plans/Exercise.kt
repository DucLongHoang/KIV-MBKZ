package com.example.fitnessapptabbed.ui.main.plans

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
    var kgs: Int
)   {
    // empty secondary constructor with default values
    constructor() : this("", 0, 0, 0)
}