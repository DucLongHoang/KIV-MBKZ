package com.example.fitnessapptabbed.util

/**
 * ExerciseUtil class - provides all initial exercises and their shortcuts
 * @author Long
 * @version 1.0
 */
class ExerciseUtils {
    companion object {

        /**
         * Method returns an [Array] of exercise names in [String]
         */
        @JvmStatic
        fun getAllExerciseNames(): Array<String> {
            return arrayOf(
                "bench press", "deadlift", "deadlift (sumo)", "squat", "bicep curl", "hammer curl",
                "tricep extension", "tricep kickback", "tricep pull down", "shoulder front raise",
                "shoulder side raise", "rear delt fly", "barbell overhead press", "dumbbell shoulder press",
                "dumbbell chest press", "chest fly", "barbell row", "dumbbell row", "hamstring curl",
                "bulgarian split squat", "lunge", "calf raise", "push up", "pull up", "chin up", "dip",
                "pistol squat", "abs exercise")
        }

        /**
         * Method returns and [Array] of exercise shortcuts in [String]
         */
        @JvmStatic
        fun getAllExerciseShortcuts(): Array<String> {
            return arrayOf(
                "bench press", "deadlift", "dl - sumo", "squat", "bicep curl", "hamm. curl",
                "tricep ext", "tricep kb", "tricep pd", "front raise",
                "side raise", "r. d. fly", "bb oh press", "db sh press",
                "db chest p", "chest fly", "bb row", "db row", "hamst. curl",
                "bulg. s. s", "lunge", "calf raise", "push up", "pull up", "chin up",
                "dip", "pis. squat", "abs ex"
            )
        }
    }
}