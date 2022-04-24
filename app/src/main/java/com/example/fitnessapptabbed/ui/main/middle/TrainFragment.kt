package com.example.fitnessapptabbed.ui.main.middle

import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.fitnessapptabbed.R
import com.example.fitnessapptabbed.database.PlansDatabaseHelper
import com.example.fitnessapptabbed.databinding.FragmentTrainBinding
import com.example.fitnessapptabbed.ui.main.left.edit.Exercise
import com.example.fitnessapptabbed.ui.main.left.plans.TrainingPlan
import kotlinx.android.synthetic.main.fragment_train.*
import kotlinx.android.synthetic.main.layout_control_panel.*

/**
 * A simple [Fragment] subclass.
 * Use the [TrainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrainFragment: Fragment() {
    private var _binding: FragmentTrainBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var databaseHelper: PlansDatabaseHelper
    private lateinit var allPlans: MutableList<TrainingPlan>
    private lateinit var chosenPlan: TrainingPlan
    private lateinit var exercisesInPlan: MutableList<Exercise>
    private lateinit var handler: TrainingProgressHandler

    private lateinit var chronometer: Chronometer
    private var running: Boolean = false
    private var trainingRunning: Boolean = false
    private var pauseOffset: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentTrainBinding.inflate(inflater, container, false)
        databaseHelper = PlansDatabaseHelper(requireContext())
        allPlans = databaseHelper.getPlansFromDb()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSpinner()
        setStartEndButton()
        setControlPanel()

//        chronometer = view.findViewById(R.id.chronometer)
//        chronometer.format = "%s"
//
//        view.findViewById<Button>(R.id.startButton).setOnClickListener { startChronometer() }
//        view.findViewById<Button>(R.id.stopButton).setOnClickListener { stopChronometer() }
//        view.findViewById<Button>(R.id.resetButton).setOnClickListener { resetChronometer() }
    }

    private fun setControlPanel() {
        val ibNext: ImageButton = requireActivity().findViewById(R.id.imButtonNext)
        val ibBack: ImageButton = requireActivity().findViewById(R.id.imButtonBack)
        val editTextInput: EditText = requireActivity().findViewById(R.id.editTextInputWeight)
        var kgInput = 0

        editTextInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(input: CharSequence?, start: Int, before: Int, count: Int) {
                if(input.toString().trim { it <= ' ' }.isEmpty()) ibNext.isEnabled = false
                else {
                    kgInput = input.toString().toInt()
                    ibNext.isEnabled = true
                }
            }
        })

        ibBack.setOnClickListener { handler.moveBack() }
        ibNext.setOnClickListener {
            handler.moveNext(kgInput)
            editTextInput.text.clear()
            kgInput = 0
        }
    }

    private fun setStartEndButton() {
        startEndButton.setOnClickListener {
            if(!trainingRunning) {      // start -> end
                trainingRunning = true
                startEndButton.text = getString(R.string.end)
                choosePlanSpinner.isEnabled = false
                imButtonBack.isEnabled = false
                exercisesInPlan = databaseHelper.getPlanConfigFromDb(chosenPlan.Title)
                handler = TrainingProgressHandler(this, exercisesInPlan)
                lin_layout_2.visibility = View.VISIBLE
                lin_layout_3.visibility = View.VISIBLE
            }
            else {      // end -> start
                trainingRunning = false
                startEndButton.text = getString(R.string.start)
                choosePlanSpinner.isEnabled = true
                imButtonBack.isEnabled = false
                lin_layout_2.visibility = View.INVISIBLE
                lin_layout_3.visibility = View.INVISIBLE
            }
        }
    }

    private fun setSpinner() {
        // prepare values for spinner
        val allPlansPlusChoose: MutableList<String> = ArrayList()
        allPlansPlusChoose.add(getString(R.string.choose_plan_prompt))
        for (plan in allPlans) {
            allPlansPlusChoose.add(plan.Title + " - " + plan.Description)
        }

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
                    startEndButton.visibility = View.INVISIBLE
                    startEndButton.isClickable = false
                }
                else {
                    startEndButton.visibility = View.VISIBLE
                    startEndButton.isClickable = true
                    chosenPlan = allPlans[position - 1] // -1 because of 'choosePlan' string
                }
            }
        }
    }

    private fun startChronometer() {
        if(!running) {
            chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
            chronometer.start()
            running = true
        }
    }

    private fun stopChronometer() {
        if(running) {
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
            chronometer.stop()
            running = false
        }
    }

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
    }
}