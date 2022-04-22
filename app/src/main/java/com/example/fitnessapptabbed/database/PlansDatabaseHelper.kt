package com.example.fitnessapptabbed.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.example.fitnessapptabbed.ui.main.left.edit.Exercise
import com.example.fitnessapptabbed.ui.main.left.plans.TrainingPlan
import com.example.fitnessapptabbed.ui.main.right.Statistic

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
        const val TABLE_PLAN_CONFIG: String = "PLANCONFIG"
        // Column names
        const val COL_PLAN_TITLE: String = "title"
        const val COL_PLAN_DESC: String = "description"
        const val COL_EX_NAME: String = "name"
        const val COL_EX_SETS: String = "sets"
        const val COL_EX_REPS: String = "reps"
        const val COL_EX_KGS: String = "kgs"
        const val COL_DATE: String = "date"
    }
    // Exercise names
    private val exercises: Array<String> = arrayOf(
        "bench", "deadlift", "deadlift - sumo", "squat", "biceps curls",
        "hammer curls", "triceps extensions", "triceps kickbacks", "shoulder front raises",
        "shoulder side raises", "chest flies", "bent-over rows", "hamstring curls",
        "bulgarian split squats", "lunges", "calf raises", "ab exercise"
    )

    override fun onCreate(db: SQLiteDatabase) {
        createTables(db)
        insertExercisesIntoDb(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) { }

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
            contentValues.put(COL_DATE, "29.02.2020")
            db.insert(TABLE_EXERCISE, null, contentValues)
        }
    }

    /**
     * Method creates tables of the database [db]
     */
    private fun createTables(db: SQLiteDatabase) {
        val createPlanTableSql: String = ("CREATE TABLE $TABLE_PLAN (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_PLAN_TITLE TEXT NOT NULL, " +
                "$COL_PLAN_DESC TEXT );")

        val createExerciseTableSql: String = ("CREATE TABLE $TABLE_EXERCISE (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_EX_NAME TEXT NOT NULL, " +
                "$COL_EX_KGS INTEGER, " +
                "$COL_DATE TEXT NOT NULL );")

        val createPlanConfigTableSql: String = ("CREATE TABLE $TABLE_PLAN_CONFIG (" +
                "$COL_PLAN_TITLE TEXT NOT NULL, " +
                "$COL_EX_NAME TEXT NOT NULL, " +
                "$COL_EX_SETS INTEGER, " +
                "$COL_EX_REPS INTEGER, " +
                "$COL_EX_KGS INTEGER, " +
                "PRIMARY KEY ($COL_PLAN_TITLE), " +
                "FOREIGN KEY ($COL_PLAN_TITLE) REFERENCES $TABLE_PLAN($COL_PLAN_TITLE), " +
                "FOREIGN KEY ($COL_EX_NAME) REFERENCES $TABLE_EXERCISE($COL_EX_NAME) );")

        db.execSQL(createPlanTableSql)
        db.execSQL(createExerciseTableSql)
        db.execSQL(createPlanConfigTableSql)
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
        val selectQuery = "SELECT $COL_PLAN_TITLE, $COL_PLAN_DESC FROM $TABLE_PLAN"
        return db.rawQuery(selectQuery, null)
    }

    /**
     * Method returns a [MutableList] of exercise names from the DB
     */
    @SuppressLint("Range")
    fun getExercisesFromDb(): MutableList<Statistic> {
        val result: MutableList<Statistic> = ArrayList()
        val c: Cursor = getExercisesCursor()
        var exName: String; var recordKgs: Int; var date: String

        if(c.moveToFirst()) {
            do {
                println(c.toString())

                exName = c.getString(c.getColumnIndex(COL_EX_NAME))
                recordKgs = c.getInt(c.getColumnIndex(COL_EX_KGS))
                date = c.getString(c.getColumnIndex(COL_DATE))
                result.add(Statistic(exName, recordKgs, date))
            } while (c.moveToNext())
        }
        return result
    }

    // I need to add the first dummy Exercise so that the Recycler View works
    fun getExercisesOfPlanFromDb(plan: TrainingPlan?): MutableList<Exercise> {
        val result: MutableList<Exercise> = ArrayList()
//        val c: Cursor? = getExercisesCursor()
        result.add(Exercise())

        return result
    }

    /**
     * Method queries the whole [TABLE_EXERCISE] table and returns a [Cursor] to it
     */
    private fun getExercisesCursor(): Cursor {
        val db: SQLiteDatabase = this.readableDatabase
        val selectQuery = "SELECT $COL_EX_NAME, $COL_EX_KGS, $COL_DATE FROM $TABLE_EXERCISE"
        return db.rawQuery(selectQuery, null)
    }
}