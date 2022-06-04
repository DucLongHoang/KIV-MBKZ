package com.example.fitnessapptabbed.ui.main.right

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapptabbed.R
import com.example.fitnessapptabbed.database.PlansDatabaseHelper
import com.example.fitnessapptabbed.databinding.FragmentStatsBinding
import kotlinx.android.synthetic.main.fragment_stats.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [StatsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StatsFragment : Fragment() {
    // static variables and methods
    companion object {
        const val MAX_EX_NAME_LENGTH = 25
        const val MAX_EX_SC_LENGTH = 11

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment StatsFragment.
         */
        @JvmStatic
        fun newInstance() = StatsFragment().apply { arguments = Bundle() }
    }

    private var _binding: FragmentStatsBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var statistics: MutableList<Statistic>
    private lateinit var databaseHelper: PlansDatabaseHelper
    private lateinit var adapter: StatsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentStatsBinding.inflate(inflater, container, false)

        databaseHelper = PlansDatabaseHelper(requireContext())
        statistics = databaseHelper.getAllExercisesFromDb()
        databaseHelper.close()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildRecyclerView()
        addNewExerciseFab.setOnClickListener { showAddNewExerciseDialog() }
        registerForContextMenu(statsRecyclerView)
    }

    override fun onDestroy() {
        super.onDestroy()
        databaseHelper.close()
    }

    /**
     * Method builds the [statsRecyclerView]
     */
    private fun buildRecyclerView() {
        adapter = StatsAdapter(statistics, requireContext())
        statsRecyclerView.layoutManager = LinearLayoutManager(context)
        statsRecyclerView.setHasFixedSize(true)
        statsRecyclerView.adapter = adapter
        itemTouchHelper.attachToRecyclerView(statsRecyclerView)
        adapter.setItemClickListener(object : OnOptionClickListener {
            override fun onEditExerciseClick(position: Int) {}
            override fun onNullifyRecordClick(position: Int) { showNullifyRecordDialog(position) }
            override fun onRemoveOptionClick(position: Int) { showRemoveExerciseDialog(position) }
        })
    }

    /**
     * Method shows [AlertDialog] with the option to create a new TrainingPlan
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun showAddNewExerciseDialog() {
        // editable fields
        val inputName = EditText(context)
        val inputShortcut = EditText(context)
        inputName.hint = getString(R.string.name_edit_text)
        inputShortcut.hint = getString(R.string.shortcut_edit_text)
        inputName.filters = arrayOf(InputFilter.LengthFilter(MAX_EX_NAME_LENGTH))
        inputShortcut.filters = arrayOf(InputFilter.LengthFilter(MAX_EX_SC_LENGTH))

        // add layout for fields
        val layout = LinearLayout(context)
        layout.setPadding(16, 0, 16, 0)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(inputName)
        layout.addView(inputShortcut)

        // create Dialog
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle(getString(R.string.add_new_exercise_msg))
        dialogBuilder.setView(layout)

        // setting options
        dialogBuilder.setNegativeButton(R.string.cancel) { dialogInterface, _ -> dialogInterface.cancel() }
        dialogBuilder.setPositiveButton(R.string.create) { _, _ ->
            val name = inputName.text.toString()
            val shortcut = inputShortcut.text.toString()
            var order = statistics.asSequence()
                .map(Statistic::order)
                .maxOrNull() ?: 0

            if (!isDuplicateExerciseName(name))
                addNewExercise(Statistic(name, shortcut, ++order))
        }

        // empty Title edit field
        val dialog = dialogBuilder.create()
        dialog.show() // has to be in this order - 1.show dialog, 2.disable button
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = false

        // enable create button if Title field not empty
        inputName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                val enable = !TextUtils.isEmpty(editable.toString().trim { it <= ' ' }) // if empty false, else true
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = enable
            }
        })
    }

    /**
     * Method adds a [newExercise] into the Database
     */
    private fun addNewExercise(newExercise: Statistic) {
        databaseHelper.insertNewExerciseIntoDb(newExercise)
        val index: Int = statistics.size
        statistics.add(index, newExercise)
        adapter.notifyItemInserted(index)
    }

    /**
     * Method displays an [AlertDialog] to confirm record nullification
     */
    private fun showNullifyRecordDialog(position: Int) {
        val toBeNullified: Statistic = statistics[position]

        // create dialog
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle(R.string.nullify_record_prompt)

        // setting options
        dialogBuilder.setNegativeButton(R.string.no) { dialogInterface, _: Int -> dialogInterface.cancel() }
        dialogBuilder.setPositiveButton(R.string.yes) { _, _: Int ->
            toBeNullified.dateOfRecord = getString(R.string.default_date)
            toBeNullified.recordKgs = getString(R.string.default_kg).toInt()
            nullifyRecord(position)
        }

        // show dialog
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    /**
     * Method nullifies record of exercise at [position] in the DB
     */
    private fun nullifyRecord(position: Int) {
        databaseHelper.nullifyRecordInDb(statistics[position].exerciseName)
        adapter.notifyItemChanged(position)
    }

    /**
     * Method shows [AlertDialog] to confirm removal of exercise at [position]
     */
    private fun showRemoveExerciseDialog(position: Int) {
        // warning text
        val warning = TextView(context)
        warning.setText(R.string.remove_exercise_warning_msg)

        // add layout for fields
        val layout = LinearLayout(context)
        layout.setPadding(24, 0, 24, 0)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(warning)

        // create dialog
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle(R.string.remove_exercise_prompt)
        dialogBuilder.setView(layout)

        // setting options
        dialogBuilder.setNegativeButton(R.string.no) { dialogInterface, _: Int -> dialogInterface.cancel() }
        dialogBuilder.setPositiveButton(R.string.yes) { _, _: Int -> removeExercise(position) }

        // show dialog
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    /**
     * Method removes exercise at [position] from the DB
     */
    private fun removeExercise(position: Int) {
        databaseHelper.deleteExerciseFromDb(statistics[position].exerciseName)
        statistics.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    /**
     * Method returns a boolean whether [name] is already used or not
     * @return true is is duplicate, otherwise false
     */
    private fun isDuplicateExerciseName(name: String): Boolean {
        val duplicate: Boolean = statistics.any { e ->
            e.exerciseName.equals(name, ignoreCase = true ) }
        if (duplicate) Toast.makeText(context, R.string.duplicate_exercise_name_msg,
            Toast.LENGTH_SHORT).show()

        return duplicate
    }

    /**
     * This attribute handles drag and drop of statistic items
     */
    private val itemTouchHelper by lazy {
        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END, 0) {

            // tells when an item is swiped left or right from its position
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            // check if an item has been moved up or down
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {
                val adapter = recyclerView.adapter as StatsAdapter
                val from = viewHolder.absoluteAdapterPosition
                val to = target.absoluteAdapterPosition

                adapter.moveItemInRecyclerViewList(from, to)
                adapter.notifyItemMoved(from, to)

                return true
            }

            // function called on currently selected item
            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG)
                    viewHolder?.itemView?.alpha = 0.5f
            }

            // function called on stopping dragging/swiping/moving
            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.alpha = 1.0f
                println("+".repeat(10))
                println("Stopped moving")
                println("+".repeat(10))
            }
        }
        ItemTouchHelper(itemTouchCallback)
    }
}