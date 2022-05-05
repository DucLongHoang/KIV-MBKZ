package com.example.fitnessapptabbed.ui.main.middle

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fitnessapptabbed.MainActivity
import com.example.fitnessapptabbed.R
import com.example.fitnessapptabbed.database.PlansDatabaseHelper
import com.example.fitnessapptabbed.databinding.FragmentTrainBinding
import com.example.fitnessapptabbed.ui.main.left.edit.Exercise
import com.example.fitnessapptabbed.ui.main.left.plans.TrainingPlan
import com.example.fitnessapptabbed.util.DateTimeUtils
import com.example.fitnessapptabbed.util.PreferencesUtils
import kotlinx.android.synthetic.main.fragment_train.*
import kotlinx.android.synthetic.main.layout_control_panel.*
import kotlinx.android.synthetic.main.layout_last_training.*

/**
 * A simple [Fragment] subclass.
 * Use the [TrainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrainFragment: Fragment() {
    private var _binding: FragmentTrainBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var vibrator: Vibrator
    private lateinit var databaseHelper: PlansDatabaseHelper
    private lateinit var allPlans: MutableList<TrainingPlan>
    private lateinit var chosenPlan: TrainingPlan
    private lateinit var exercisesInPlan: MutableList<Exercise>
    private lateinit var handler: TrainingProgressHandler
    private var trainingRunning: Boolean = false

    private var running: Boolean = false
    private var pauseOffset: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentTrainBinding.inflate(inflater, container, false)
        vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        databaseHelper = PlansDatabaseHelper(requireContext())
        allPlans = databaseHelper.getPlansFromDb()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSpinner()
        setStartEndButton()
        setControlPanel()
        showLastTraining()
    }

    /** Method returns a [Vibrator] instance */
    fun getVibrator(): Vibrator = this.vibrator

    /**
     * Method show the last done [TrainingPlan] if there is any
     */
    private fun showLastTraining() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val defaultEmptyString = ""

        // get saved values from preferences
        val savedName = sharedPref?.getString(PreferencesUtils.KEY_NAME, defaultEmptyString)
        val savedDate = sharedPref?.getString(PreferencesUtils.KEY_DATE, defaultEmptyString)
        val savedDuration = sharedPref?.getString(PreferencesUtils.KEY_DURATION, defaultEmptyString)

        // if no training saved, show nothing
        if(savedName.equals(defaultEmptyString)) {
            lin_layout_1_5.visibility = View.GONE
            return
        }

        // show training
        textViewLastName.text = savedName
        textViewLastDate.text = savedDate
        textViewLastDuration.text = savedDuration.toString()
        lin_layout_1_5.visibility = View.VISIBLE
    }

    /**
     * Method saves the last done training to the preferences file
     */
    private fun saveTrainingToPreferences() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()

        // duration of training
        val chronometerTime = SystemClock.elapsedRealtime() - chronometer.base
        val formattedDuration = DateTimeUtils.getTimeFromLong(chronometerTime)

        // subtract training duration from current time to get starting time
        val dateInString = DateTimeUtils.getCurrentDateInString(getString(R.string.date_long_format), chronometerTime)

        // save into preferences
        editor?.putString(PreferencesUtils.KEY_NAME, chosenPlan.Title)
        editor?.putString(PreferencesUtils.KEY_DATE, dateInString)
        editor?.putString(PreferencesUtils.KEY_DURATION, formattedDuration)
        editor?.apply()
    }

    /**
     * Method sets up the control panel
     */
    private fun setControlPanel() {
        var userInput: String
        var kgInput = 0

        // set up listener for editText field
        editTextInputWeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(input: CharSequence?, start: Int, before: Int, count: Int) {
                userInput = input.toString()
                if(userInput.isEmpty() || Integer.valueOf(userInput) == 0) {
                    imButtonNext.isEnabled = false
                }
                else {
                    kgInput = userInput.toInt()
                    imButtonNext.isEnabled = true
                }
            }
        })

        imButtonBack.setOnClickListener { handler.moveBack() }
        imButtonNext.setOnClickListener { handler.moveNext(kgInput) }
    }
    private fun printExercises() {
        for (i in 0 until exercisesInPlan.size - 1) println(exercisesInPlan[i])
    }
    /**
     * Method sets up the Start/End button
     */
    private fun setStartEndButton() {
        startEndButton.setOnClickListener {
            if(!trainingRunning) {      // start -> end
                exercisesInPlan = databaseHelper.getPlanConfigFromDb(chosenPlan.Title)
//                printExercises()
                // every TrainingPlan has an empty exercise
                if(exercisesInPlan.size > 1) startTraining()
                else {
                    vibrator.vibrate(VIB_DURATION)
                    Toast.makeText(context,
                        R.string.empty_plan_msg,
                        Toast.LENGTH_SHORT).show()
                }
            }
            else {      // end -> start
                endTrainingDialog()
            }
        }
    }

    /**
     * Method creates a dialog to confirm ending a TrainingPlan
     */
    fun endTrainingDialog() {
        // create dialog
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle(R.string.end_training_prompt)

        // setting options
        dialogBuilder.setNegativeButton(R.string.no) { dialogInterface, _: Int -> dialogInterface.cancel() }
        dialogBuilder.setPositiveButton(R.string.yes) { _, _: Int -> endTraining() }

        // show dialog
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    /**
     * Method starts the training
     */
    private fun startTraining() {
        trainingRunning = true
        startEndButton.text = getString(R.string.end)
        choosePlanSpinner.isEnabled = false
        imButtonBack.isEnabled = false
        imButtonNext.isEnabled = false
        handler = TrainingProgressHandler(this, exercisesInPlan)
        vibrator.vibrate(VIB_DURATION)
        lin_layout_1_5.visibility = View.GONE
        lin_layout_2.visibility = View.VISIBLE
        lin_layout_3.visibility = View.VISIBLE
        (activity as MainActivity).enableSwitch(false)
        (activity as MainActivity).window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        startChronometer()
    }

    /**
     * Method ends the training
     */
    private fun endTraining() {
        trainingRunning = false
        startEndButton.text = getString(R.string.start)
        choosePlanSpinner.isEnabled = true
        choosePlanSpinner.setSelection(0)
        vibrator.vibrate(VIB_DURATION)
        lin_layout_2.visibility = View.INVISIBLE
        lin_layout_3.visibility = View.INVISIBLE
        editTextInputWeight.text.clear()
        (activity as MainActivity).enableSwitch(true)
        (activity as MainActivity).window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        saveTrainingToPreferences()
        showLastTraining()
        resetChronometer()
    }

    /**
     * Method sets up spinner to choose a training plan
     */
    private fun setSpinner() {
        // prepare values for spinner
        val allPlansPlusChoose: MutableList<String> = ArrayList()
        allPlansPlusChoose.add(getString(R.string.choose_plan))
        for (plan in allPlans) { allPlansPlusChoose.add(plan.Title + " - " + plan.Description) }

        // set up adapter for spinner
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireActivity(), android.R.layout.simple_spinner_dropdown_item, allPlansPlusChoose)
        choosePlanSpinner.adapter = adapter

        // set spinner selection listener
        choosePlanSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                choosePlanSpinner.setSelection(position)
                if(position == 0) {
                    startEndButton.visibility = View.GONE
                    chronometer.visibility = View.GONE
                }
                else {
                    startEndButton.visibility = View.VISIBLE
                    chronometer.visibility = View.VISIBLE
                    chosenPlan = allPlans[position - 1] // -1 because of 'choosePlan' string
                }
            }
        }
    }

    /**
     * Method starts the [chronometer] count
     */
    private fun startChronometer() {
        if(!running) {
            chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
            chronometer.start()
            running = true
        }
    }

    /**
     * Method stops the [chronometer] count
     * app does not support pausing, but this is used in [resetChronometer]
     */
    private fun stopChronometer() {
        if(running) {
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
            chronometer.stop()
            running = false
        }
    }

    /**
     * Method resets the [chronometer] to zero
     */
    private fun resetChronometer() {
        chronometer.base = SystemClock.elapsedRealtime()
        pauseOffset = 0
        stopChronometer()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment TrainFragment.
         */
        @JvmStatic
        fun newInstance() = TrainFragment().apply { arguments = Bundle() }
        /** Vibration duration - 0.5 sec */
        private const val VIB_DURATION: Long = 500
    }
}