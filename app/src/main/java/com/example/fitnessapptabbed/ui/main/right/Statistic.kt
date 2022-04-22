package com.example.fitnessapptabbed.ui.main.right

/**
 * [Statistic] data class - defines a record statistic,
 * that consists of [exerciseName], [recordKgs] and [dateOfRecord]
 *
 * @author Long
 * @version 1.0
 */
data class Statistic(
    val exerciseName: String,
    val recordKgs: Int,
    val dateOfRecord: String
) {
    // empty secondary constructor with default values
    constructor() : this("", 0, "")
}