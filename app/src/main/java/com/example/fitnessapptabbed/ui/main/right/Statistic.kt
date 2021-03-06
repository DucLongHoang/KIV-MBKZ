package com.example.fitnessapptabbed.ui.main.right

/**
 * [Statistic] data class - defines a record statistic,
 * that consists of [exerciseName], [recordKgs] and [dateOfRecord]
 *
 * @author Long
 * @version 1.0
 */
data class Statistic(
    var exerciseName: String,
    var exerciseShortcut: String,
    var recordKgs: Int,
    var dateOfRecord: String,
    var order: Int
) : Comparable<Statistic> {
    // empty secondary constructor with default values
    constructor() :
            this("", "", 0, "dd.MM.yyyy", 0)
    // constructor to nullify record in database
    constructor(exerciseName: String, exShortcut: String, order: Int) :
            this(exerciseName, exShortcut,0, "dd.MM.yyyy", order)

    override fun compareTo(other: Statistic): Int {
        return this.order.compareTo(other.order)
    }

}