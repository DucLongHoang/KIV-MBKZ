package com.example.fitnessapptabbed.ui.main.middle

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
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
    private lateinit var vibrator: Vibrator
    private lateinit var databaseHelper: PlansDatabaseHelper
    private lateinit var allPlans: MutableList<TrainingPlan>
    private lateinit var chosenPlan: TrainingPlan
    private lateinit var exercisesInPlan: MutableList<Exercise>
    private lateinit var handler: TrainingProgressHandler
    private lateinit var kgList: MutableList<Int>
    private lateinit var kgIterator: ListIterator<Int>
    private var kgListIndex: Int = 0
    private var trainingRunning: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentTrainBinding.inflate(inflater, container, false)
        vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        databaseHelper = PlansDatabaseHelper(requireContext())
        allPlans = databaseHelper.getPlansFromDb()
        kgList = ArrayList()
        kgIterator = kgList.listIterator()
        kgListIndex = 0

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSpinner()
        setStartEndButton()
        setControlPanel()
    }

    /** Method returns a [Vibrator] instance */
    fun getVibrator(): Vibrator = this.vibrator

    /**
     * Method sets up the control panel
     */
    private fun setControlPanel() {
        // init views and values
        val ibNext: ImageButton = requireActivity().findViewById(R.id.imButtonNext)
        val ibBack: ImageButton = requireActivity().findViewById(R.id.imButtonBack)
        val editTextInput: EditText = requireActivity().findViewById(R.id.editTextInputWeight)
        var kgInput = 0

        // set up listener for editText field
        editTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(input: CharSequence?, start: Int, before: Int, count: Int) {
                if(input.toString().isEmpty()) { ibNext.isEnabled = false }
                else {
                    kgInput = input.toString().toInt()
                    ibNext.isEnabled = true
                }
            }
        })

        ibBack.setOnClickListener {
            handler.moveBack()
            kgListIndex--
            println("index: $kgListIndex")

            if(kgList.getOrNull(kgListIndex) != null) {
                editTextInput.setText(kgList[kgListIndex].toString())
            }
            else {
                kgListIndex++
            }
        }

        ibNext.setOnClickListener {
            handler.moveNext(kgInput)
            kgListIndex++
            println("index: $kgListIndex")

            if(kgList.getOrNull(kgListIndex) != null) {
                editTextInput.setText(kgList[kgListIndex].toString())
            }
            else {
                if(kgListIndex > kgList.size) {
                    kgList.add(kgInput)
                }
                editTextInput.text.clear()
            }
        }
    }

    /**
     * Method sets up the Start/End button
     */
    private fun setStartEndButton() {
        startEndButton.setOnClickListener {
            if(!trainingRunning) {      // start -> end
                trainingRunning = true
                startEndButton.text = getString(R.string.end)
                choosePlanSpinner.isEnabled = false
                imButtonBack.isEnabled = false
                imButtonNext.isEnabled = false
                exercisesInPlan = databaseHelper.getPlanConfigFromDb(chosenPlan.Title)
                handler = TrainingProgressHandler(this, exercisesInPlan)
                vibrator.vibrate(VIB_DURATION)
                lin_layout_2.visibility = View.VISIBLE
                lin_layout_3.visibility = View.VISIBLE
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
        dialogBuilder.setTitle("Do you want to end the training?")

        // setting options
        dialogBuilder.setNegativeButton("No") { dialogInterface, _: Int -> dialogInterface.cancel() }
        dialogBuilder.setPositiveButton("Yes") { _, _: Int ->
            trainingRunning = false
            startEndButton.text = getString(R.string.start)
            choosePlanSpinner.isEnabled = true
            choosePlanSpinner.setSelection(0)
            imButtonBack.isEnabled = false
            vibrator.vibrate(VIB_DURATION)
            lin_layout_2.visibility = View.INVISIBLE
            lin_layout_3.visibility = View.INVISIBLE
            editTextInputWeight.text.clear()
            kgList.clear()
            kgListIndex = 0
        }

        // show dialog
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    /**
     * Method sets up spinner to choose a training plan
     */
    private fun setSpinner() {
        // prepare values for spinner
        val allPlansPlusChoose: MutableList<String> = ArrayList()
        allPlansPlusChoose.add(getString(R.string.choose_plan_prompt))
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
                if(position == 0) startEndButton.visibility = View.INVISIBLE

                else {
                    startEndButton.visibility = View.VISIBLE
                    chosenPlan = allPlans[position - 1] // -1 because of 'choosePlan' string
                }
            }
        }
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
        private const val VIB_DURATION: Long = 500
    }
}