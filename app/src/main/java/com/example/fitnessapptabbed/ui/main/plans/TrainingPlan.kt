package com.example.fitnessapptabbed.ui.main.plans

data class TrainingPlan(
     var Title: String,
     var Description: String
) {
    private var exercises: ArrayList<Exercise> = ArrayList()

    init {
        addExercise(Exercise("bicep curl", 3, 12, 12))
        addExercise(Exercise("triceps curl", 3, 15, 16))
        addExercise(Exercise("squat", 6, 5, 90))
        addExercise(Exercise("dead lift", 6, 5, 120))
    }

    fun addExercise(exercise: Exercise) {
         this.exercises.add(exercise)
    }

    fun removeExercise(exercise: Exercise) {
        this.exercises.remove(exercise)
    }

    companion object {
        fun createListOfTrainingPlans(num: Int) : ArrayList<TrainingPlan> {
            val trainingPlans = ArrayList<TrainingPlan>()
            for(i in 1..num) {
                trainingPlans.add(TrainingPlan("Training plan $i", "description no. $i"))
            }
            return trainingPlans
        }
    }
}