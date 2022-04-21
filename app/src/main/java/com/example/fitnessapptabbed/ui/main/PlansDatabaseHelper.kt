package com.example.fitnessapptabbed.ui.main

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.fitnessapptabbed.ui.main.plans.Exercise
import com.example.fitnessapptabbed.ui.main.plans.TrainingPlan

/**
 * [PlansDatabaseHelper] class - helps with all database related stuff
 * @author Long
 * @version 1.0
 */
class PlansDatabaseHelper(
    CONTEXT: Context
) : SQLiteOpenHelper(CONTEXT, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME: String = "plans_database.db"
        private const val DATABASE_VERSION: Int = 1
        // Table names
        const val TABLE_PLAN: String = "PLAN"
        const val TABLE_EXERCISE: String = "EXERCISE"
        const val TABLE_RECORDS: String = "RECORDS"
        // Column names
        const val COL_PLAN_TITLE: String = "title"
        const val COL_PLAN_DESC: String = "description"
        const val COL_EX_NAME: String = "name"
        const val COL_EX_SETS: String = "sets"
        const val COL_EX_REPS: String = "reps"
        const val COL_EX_KGS: String = "kgs"
    }
    // Exercise names
    private val exercises: Array<String> = arrayOf(
        "bench", "deadlift", "squat", "bicep curl", "tricep extensions",
        "hamstring curls", "chest flies", "lat pulldown"
    )

    override fun onCreate(db: SQLiteDatabase) {
        createTables(db)
        insertExercisesIntoDb(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO()
    }

    /**
     * Method inserts 5 dummy values into database [db]
     */
    private fun insertDummyValues(db: SQLiteDatabase) {
        val count = 5
        val plans = TrainingPlan.createListOfTrainingPlans(count)
        val contentValues = ContentValues()

        for(plan in plans) {
            contentValues.put(COL_PLAN_TITLE, plan.Title)
            contentValues.put(COL_PLAN_DESC, plan.Description)
            db.insert(TABLE_PLAN, null, contentValues)
        }
    }

    /**
     * Method inserts exercise names into the records table
     * TODO - redo database layout
     */
    private fun insertExercisesIntoDb(db: SQLiteDatabase) {
        val contentValues = ContentValues()
        for (exercise in exercises) {
            contentValues.put(COL_EX_NAME, exercise)
            contentValues.put(COL_EX_KGS, 0)
            db.insert(TABLE_RECORDS, null, contentValues)
        }
    }

    /**
     * Method creates tables of the database [db]
     */
    private fun createTables(db: SQLiteDatabase) {
        val createPlanTableSql: String = ("CREATE TABLE $TABLE_PLAN (" +
                "$COL_PLAN_TITLE TEXT NOT NULL, " +
                "$COL_PLAN_DESC TEXT, " +
                "PRIMARY KEY ($COL_PLAN_TITLE) );")
        val createRecordsTableSql: String = ("CREATE TABLE $TABLE_RECORDS (" +
                "$COL_EX_NAME TEXT NOT NULL, " +
                "$COL_EX_KGS INTEGER, " +
                "PRIMARY KEY ($COL_EX_NAME) );")
        val createExerciseTableSql: String = ("CREATE TABLE $TABLE_EXERCISE (" +
                "$COL_PLAN_TITLE TEXT NOT NULL, " +
                "$COL_EX_NAME TEXT NOT NULL, " +
                "$COL_EX_SETS INTEGER, " +
                "$COL_EX_REPS INTEGER, " +
                "$COL_EX_KGS INTEGER, " +
                "PRIMARY KEY ($COL_PLAN_TITLE, $COL_EX_NAME), " +
                "FOREIGN KEY ($COL_PLAN_TITLE) REFERENCES $TABLE_PLAN($COL_PLAN_TITLE), " +
                "FOREIGN KEY ($COL_EX_NAME) REFERENCES $TABLE_EXERCISE($COL_EX_NAME) );")

        db.execSQL(createPlanTableSql)
        db.execSQL(createExerciseTableSql)
        db.execSQL(createRecordsTableSql)
    }

    /**
     * Method inserts a new [plan] into the DB
     */
    fun insertPlanIntoDb(plan: TrainingPlan) {
        val database: SQLiteDatabase = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_PLAN_TITLE, plan.Title)
        contentValues.put(COL_PLAN_DESC, plan.Description)
        database.insert(TABLE_PLAN, null, contentValues)
    }

    /**
     * Method deletes a [plan] from the DB
     */
    fun deletePlanFromDb(plan: TrainingPlan) {
        val database: SQLiteDatabase = this.writableDatabase
        database.delete(TABLE_PLAN, "$COL_PLAN_TITLE=?", arrayOf(plan.Title))
    }

    /**
     * Method returns a [MutableList] of [TrainingPlan] from the DB
     */
    @SuppressLint("Range")
    fun getPlansFromDb(): MutableList<TrainingPlan> {
        val result: MutableList<TrainingPlan> = ArrayList()
        val c: Cursor = getPlansCursor()
        var title: String; var description: String

        if(c.moveToFirst()) {
            do {
                title = c.getString(c.getColumnIndex(COL_PLAN_TITLE))
                description = c.getString(c.getColumnIndex(COL_PLAN_DESC))
                result.add(TrainingPlan(title, description))
            } while (c.moveToNext())
        }
        return result
    }

    /**
     * Method queries the whole [TABLE_PLAN] table and returns a [Cursor] to it
     */
    private fun getPlansCursor(): Cursor {
        val db: SQLiteDatabase = this.readableDatabase
        val selectQuery = "SELECT $COL_PLAN_TITLE, $COL_PLAN_DESC FROM $TABLE_PLAN "
        return db.rawQuery(selectQuery, null)
    }

    /**
     * Method returns a [MutableList] of [Exercise] from the DB
     */
    @SuppressLint("Range")
    fun getExercisesFromDb(): MutableList<Exercise> {
        val result: MutableList<Exercise> = ArrayList()
        val c: Cursor = getExercisesCursor()
        var exName: String;

        if(c.moveToFirst()) {
            do {
                exName = c.getString(c.getColumnIndex(COL_EX_NAME))
                result.add(Exercise(exName, 0, 0))
            } while (c.moveToNext())
        }
        return result
    }

    fun getExercisesOfPlanFromDb(plan: TrainingPlan?): MutableList<Exercise> {
        val result: MutableList<Exercise> = ArrayList()
        val c: Cursor? = getExercisesCursor()
        result.add(Exercise("deadlift", 10, 10, 100))

        return result
    }

    /**
     * Method queries the whole [TABLE_RECORDS] table and returns a [Cursor] to it
     */
    private fun getExercisesCursor(): Cursor {
        val db: SQLiteDatabase = this.readableDatabase
        val selectQuery = "SELECT $COL_EX_NAME FROM $TABLE_RECORDS "
        return db.rawQuery(selectQuery, null)
    }
}