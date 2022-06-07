package com.example.fitnessapptabbed.util

/**
 * [ExerciseUtils] class - provides all initial exercises and their shortcuts
 * @author Long
 * @version 1.0
 */
class ExerciseUtils {
    /**
     * [StringPair] data class - just to hold exercise name and exercise shortcut
     * also enables simple sequence/stream manipulation by method calling
     */
    data class StringPair(val name: String, val shortcut: String)

    companion object {
        @JvmStatic
        private var exNamesShortcuts: MutableList<StringPair> = ArrayList()
        init {
            // main lifts
            exNamesShortcuts.add(StringPair("bench press", "bench press"))
            exNamesShortcuts.add(StringPair("deadlift", "deadlift"))
            exNamesShortcuts.add(StringPair("deadlift - sumo", "dl - sumo"))
            exNamesShortcuts.add(StringPair("squat", "squat"))
            // biceps
            exNamesShortcuts.add(StringPair("barbell bicep curl", "bb curl"))
            exNamesShortcuts.add(StringPair("dumbbell bicep curl", "db curl"))
            exNamesShortcuts.add(StringPair("hammer curl", "hamm. curl"))
            // triceps
            exNamesShortcuts.add(StringPair("tricep extension", "tricep ext"))
            exNamesShortcuts.add(StringPair("tricep kickback", "tricep kb"))
            exNamesShortcuts.add(StringPair("tricep pull down", "tricep pd"))
            // front and side deltoids
            exNamesShortcuts.add(StringPair("dumbbell front raise", "db fr raise"))
            exNamesShortcuts.add(StringPair("plate front raise", "pl fr raise"))
            exNamesShortcuts.add(StringPair("lateral raise", "lat. raise"))
            // rear deltoids
            exNamesShortcuts.add(StringPair("barbell high row", "bb high row"))
            exNamesShortcuts.add(StringPair("dumbbell incline row", "db incl row"))
            exNamesShortcuts.add(StringPair("face pulls", "face pulls"))
            exNamesShortcuts.add(StringPair("rear delt fly", "r. d. fly"))
            // shoulders
            exNamesShortcuts.add(StringPair("barbell overhead press", "bb oh press"))
            exNamesShortcuts.add(StringPair("dumbbell shoulder press", "db sh press"))
            // chest
            exNamesShortcuts.add(StringPair("dumbbell chest press", "db chest p"))
            exNamesShortcuts.add(StringPair("chest fly", "chest fly"))
            // lats
            exNamesShortcuts.add(StringPair("barbell row", "bb row"))
            exNamesShortcuts.add(StringPair("dumbbell row", "db row"))
            exNamesShortcuts.add(StringPair("resistance band row", "band row"))
            // legs
            exNamesShortcuts.add(StringPair("hip thrust", "hip thrust"))
            exNamesShortcuts.add(StringPair("hamstring curl", "hamst. curl"))
            exNamesShortcuts.add(StringPair("bulgarian split squat", "bulg. s. s"))
            exNamesShortcuts.add(StringPair("lunge", "lunge"))
            exNamesShortcuts.add(StringPair("calf raise", "calf raise"))
            // calisthenics
            exNamesShortcuts.add(StringPair("push up", "push up"))
            exNamesShortcuts.add(StringPair("pull up", "pull up"))
            exNamesShortcuts.add(StringPair("chin up", "chin up"))
            exNamesShortcuts.add(StringPair("dip", "dip"))
            exNamesShortcuts.add(StringPair("pistol squat", "pis. squat"))
            exNamesShortcuts.add(StringPair("abs exercise", "abs ex"))
        }

        /**
         * Method returns a [List] of exercise names in [String]
         */
        @JvmStatic
        fun getAllExerciseNames(): List<String> {
            return exNamesShortcuts.asSequence()
                .map(StringPair::name)
                .toList()
        }

        /**
         * Method returns a [List] of exercise shortcuts in [String]
         */
        @JvmStatic
        fun getAllExerciseShortcuts(): List<String> {
            return exNamesShortcuts.asSequence()
                .map(StringPair::shortcut)
                .toList()
        }

        @JvmStatic
        fun getAllExerciseNamesAndShortcuts(): List<StringPair> {
            return exNamesShortcuts;
        }
    }
}