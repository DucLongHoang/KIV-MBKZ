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
import com.example.fitnessapptabbed.util.ExerciseUtils

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
        private const val DATABASE_VERSION: Int = 3
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
        const val COL_EX_SC: String = "shortcut"
        // order column cannot be named 'order', WTF?!
        const val COL_ORDER: String = "order_number"
        const val COL_DATE: String = "date"
    }
    // Exercise names
    private val namesAndShortcuts: List<ExerciseUtils.StringPair> = ExerciseUtils.getAllExerciseNamesAndShortcuts()

    override fun onCreate(db: SQLiteDatabase) {
        createTables(db)
        initExercisesInDb(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXERCISE")
        val createExerciseTableSql: String = "CREATE TABLE $TABLE_EXERCISE (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_EX_NAME TEXT NOT NULL, " +
                "$COL_EX_SC TEXT NOT NULL, " +
                "$COL_EX_KGS INTEGER, " +
                "$COL_DATE TEXT NOT NULL, " +
                "$COL_ORDER INTEGER );"
        db.execSQL(createExerciseTableSql)
        initExercisesInDb(db)
    }

    /**
     * Method creates tables of the database [db]
     */
    private fun createTables(db: SQLiteDatabase) {
        val createPlanTableSql: String = "CREATE TABLE $TABLE_PLAN (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_PLAN_TITLE TEXT NOT NULL, " +
                "$COL_PLAN_DESC TEXT );"

        val createExerciseTableSql: String = "CREATE TABLE $TABLE_EXERCISE (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_EX_NAME TEXT NOT NULL, " +
                "$COL_EX_SC TEXT NOT NULL, " +
                "$COL_EX_KGS INTEGER, " +
                "$COL_DATE TEXT NOT NULL, " +
                "$COL_ORDER INTEGER );"

        val createPlanConfigTableSql: String = "CREATE TABLE $TABLE_PLAN_CONFIG (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_PLAN_TITLE TEXT NOT NULL, " +
                "$COL_EX_NAME TEXT NOT NULL, " +
                "$COL_EX_SETS INTEGER, " +
                "$COL_EX_REPS INTEGER, " +
                "FOREIGN KEY ($COL_PLAN_TITLE) REFERENCES $TABLE_PLAN($COL_PLAN_TITLE), " +
                "FOREIGN KEY ($COL_EX_NAME) REFERENCES $TABLE_EXERCISE($COL_EX_NAME) );"

        db.execSQL(createPlanTableSql)
        db.execSQL(createExerciseTableSql)
        db.execSQL(createPlanConfigTableSql)
    }

    /**
     * Method inserts exercise names into the records table
     * TODO - redo database layout
     */
    private fun initExercisesInDb(db: SQLiteDatabase) {
        val emptyExercise = Statistic()
        val contentValues = ContentValues()

        var order = 1
        for (item in namesAndShortcuts) {
            contentValues.put(COL_EX_NAME, item.name)
            contentValues.put(COL_EX_SC, item.shortcut)
            contentValues.put(COL_EX_KGS, emptyExercise.recordKgs)
            contentValues.put(COL_DATE, emptyExercise.dateOfRecord)
            contentValues.put(COL_ORDER, order)
            db.insert(TABLE_EXERCISE, null, contentValues)
            ++order
        }
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
     * Method changes plan's [oldTitle] to [newTitle] and [newDescription]
     */
    fun updatePlanTitleInDb(oldTitle: String, newTitle: String, newDescription: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_PLAN_TITLE, newTitle)
        contentValues.put(COL_PLAN_DESC, newDescription)

        // change title and description in PLANS Table
        db.update(TABLE_PLAN, contentValues,
            "$COL_PLAN_TITLE=?", arrayOf(oldTitle))

        // change title in PLANCONFIG table
        contentValues.clear()
        contentValues.put(COL_PLAN_TITLE, newTitle)
        db.update(TABLE_PLAN_CONFIG, contentValues,
            "$COL_PLAN_TITLE=?", arrayOf(oldTitle))
    }

    /**
     * Update description of [TrainingPlan] with [title] to [newDescription]
     */
    fun updatePlanDescriptionInDb(title: String, newDescription: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_PLAN_DESC, newDescription)

        db.update(TABLE_PLAN, contentValues,
            "$COL_PLAN_TITLE=?", arrayOf(title))
    }

    /**
     * Method deletes a [plan] from the DB
     */
    fun deletePlanFromDb(plan: String) {
        val database: SQLiteDatabase = this.writableDatabase
        database.delete(TABLE_PLAN, "$COL_PLAN_TITLE=?", arrayOf(plan))
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
     * Method returns record kgs for [exercise]
     * It has to be queried everytime due to multiple record breaks
     */
    @SuppressLint("Range", "Recycle")
    fun getRecordKgsFromDb(exercise: String): Int {
        val db: SQLiteDatabase = this.readableDatabase
        val c: Cursor = db.query(TABLE_EXERCISE, arrayOf(COL_EX_KGS), "${COL_EX_NAME}=?",
            arrayOf(exercise), null, null, null)

        return if(c.moveToFirst())
            c.getInt(c.getColumnIndex(COL_EX_KGS))
        else 0
    }

    /**
     * Method updates [COL_EX_KGS] and [COL_DATE]
     * of table [TABLE_EXERCISE] in the database
     */
    fun updateRecordInDb(statistic: Statistic) {
        val db: SQLiteDatabase = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_EX_KGS, statistic.recordKgs)
        contentValues.put(COL_DATE, statistic.dateOfRecord)

        db.update(TABLE_EXERCISE, contentValues,
            "$COL_EX_NAME=?", arrayOf(statistic.exerciseName))
    }

    /**
     * Method nullifies record of [exerciseName] in Database
     */
    fun nullifyRecordInDb(exerciseName: String) {
        val db: SQLiteDatabase = this.writableDatabase
        val contentValues = ContentValues()
        val emptyExercise = Statistic()

        contentValues.put(COL_EX_KGS, emptyExercise.recordKgs)
        contentValues.put(COL_DATE, emptyExercise.dateOfRecord)

        db.update(TABLE_EXERCISE, contentValues,
            "$COL_EX_NAME=?", arrayOf(exerciseName))
    }

    /**
     * Method inserts the [newExercise] into the Database
     */
    fun insertNewExerciseIntoDb(newExercise: Statistic) {
        val database: SQLiteDatabase = this.writableDatabase
        val contentValues = ContentValues()
        val emptyExercise = Statistic()
        contentValues.put(COL_EX_NAME, newExercise.exerciseName)
        contentValues.put(COL_EX_SC, newExercise.exerciseShortcut)
        contentValues.put(COL_EX_KGS, emptyExercise.recordKgs)
        contentValues.put(COL_DATE, emptyExercise.dateOfRecord)
        contentValues.put(COL_ORDER, newExercise.order)
        database.insert(TABLE_EXERCISE, null, contentValues)
    }


    /**
     * Method deletes [exercise] from the Database
     * Also deletes an exercise from a [TrainingPlan]
     */
    fun deleteExerciseFromDb(exercise: String) {
        val database: SQLiteDatabase = this.writableDatabase
        database.delete(TABLE_EXERCISE, "$COL_EX_NAME=?", arrayOf(exercise))
        database.delete(TABLE_PLAN_CONFIG, "$COL_EX_NAME=?", arrayOf(exercise))
    }

    /**
     * Method returns a [MutableList] of exercises from the DB
     */
    @SuppressLint("Range")
    fun getAllExercisesFromDb(): MutableList<Statistic> {
        val result: MutableList<Statistic> = ArrayList()
        val c: Cursor = getExercisesCursor()
        var exName: String; var exShortcut: String
        var recordKgs: Int; var date: String; var order: Int

        if(c.moveToFirst()) {
            do {
                exName = c.getString(c.getColumnIndex(COL_EX_NAME))
                exShortcut = c.getString(c.getColumnIndex(COL_EX_SC))
                recordKgs = c.getInt(c.getColumnIndex(COL_EX_KGS))
                date = c.getString(c.getColumnIndex(COL_DATE))
                order = c.getInt(c.getColumnIndex(COL_ORDER))
                result.add(Statistic(exName, exShortcut, recordKgs, date, order))
            } while (c.moveToNext())
        }
        return result
    }

    /**
     * Method queries the whole [TABLE_EXERCISE] table and returns a [Cursor] to it
     */
    private fun getExercisesCursor(): Cursor {
        val db: SQLiteDatabase = this.readableDatabase
        val selectQuery = "SELECT $COL_EX_NAME, $COL_EX_SC, $COL_EX_KGS, $COL_DATE, $COL_ORDER " +
                "FROM $TABLE_EXERCISE"
        return db.rawQuery(selectQuery, null)
    }

    /**
     * Method retrieves Exercises of a TrainingPlan from the database
     */
    @SuppressLint("Range")
    fun getPlanConfigFromDb(planTitle: String): MutableList<Exercise> {
        val result: MutableList<Exercise> = ArrayList()
        val c: Cursor = getPlanConfigCursor(planTitle)
        var exName: String; var sets: Int; var reps: Int

        if(c.moveToFirst()) {
            do {
                exName = c.getString(c.getColumnIndex(COL_EX_NAME))
                sets = c.getInt(c.getColumnIndex(COL_EX_SETS))
                reps = c.getInt(c.getColumnIndex(COL_EX_REPS))
                result.add(Exercise(exName, sets, reps))
            } while (c.moveToNext())
        }

        // add empty first Exercise so that the Recycler View shows '+' button
        result.add(Exercise())

        return result
    }

    /**
     * Method queries the whole [TABLE_PLAN_CONFIG] table and returns a [Cursor] to it
     */
    private fun getPlanConfigCursor(planTitle: String): Cursor {
        val db: SQLiteDatabase = this.readableDatabase
        val selectQuery = "SELECT $COL_EX_NAME, $COL_EX_SETS, $COL_EX_REPS " +
                "FROM $TABLE_PLAN_CONFIG WHERE $COL_PLAN_TITLE=?"
        return db.rawQuery(selectQuery, arrayOf(planTitle))
    }

    /**
     * Method updates plan config from Db by deleting old config
     * and inserting a new one, with the same name
     */
    fun updatePlanConfigInDb(title: String, exercises: MutableList<Exercise>) {
        deletePlanConfigFromDb(title)
        insertPlanConfigIntoDb(title, exercises)
    }

    /**
     * Method inserts Training plan [title] with a
     * configuration of [exercises] into the database
     */
    private fun insertPlanConfigIntoDb(title: String, exercises: MutableList<Exercise>) {
        val db: SQLiteDatabase = this.writableDatabase
        var contentValues: ContentValues

        db.beginTransaction()
        for(exercise: Exercise in exercises) {
            contentValues = ContentValues()
            contentValues.put(COL_PLAN_TITLE, title)
            contentValues.put(COL_EX_NAME, exercise.name)
            contentValues.put(COL_EX_SETS, exercise.sets)
            contentValues.put(COL_EX_REPS, exercise.reps)
            db.insert(TABLE_PLAN_CONFIG, null, contentValues)
        }
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    /**
     * Method deletes a plan config [title] from the DB
     */
    fun deletePlanConfigFromDb(title: String) {
        val database: SQLiteDatabase = this.writableDatabase
        database.delete(TABLE_PLAN_CONFIG, "$COL_PLAN_TITLE=?", arrayOf(title))
    }
}