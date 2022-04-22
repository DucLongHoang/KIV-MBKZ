package com.example.fitnessapptabbed.ui.main.left.plans

import com.example.fitnessapptabbed.ui.main.left.edit.Exercise

/**
 * [TrainingPlan] class - represents a training plan,
 * that consists of a [Title], [Description] and a list of [exercises]
 *
 * @author Long
 * @version 1.0
 */
data class TrainingPlan(
     var Title: String,
     var Description: String
) {
    private var exercises: ArrayList<Exercise> = ArrayList()

    fun addExercise(exercise: Exercise) { this.exercises.add(exercise) }

    fun removeExercise(exercise: Exercise) { this.exercises.remove(exercise) }

    companion object {
        @JvmStatic
        fun createListOfTrainingPlans(num: Int) : ArrayList<TrainingPlan> {
            val trainingPlans = ArrayList<TrainingPlan>()
            for(i in 1..num) {
                trainingPlans.add(TrainingPlan("Training plan $i", "description no. $i"))
            }
            return trainingPlans
        }
    }
}