package com.example.fitnessapptabbed.ui.main.right

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitnessapptabbed.R
import com.example.fitnessapptabbed.database.PlansDatabaseHelper
import com.example.fitnessapptabbed.databinding.FragmentStatsBinding
import com.example.fitnessapptabbed.ui.main.left.OnItemClickListener
import kotlinx.android.synthetic.main.fragment_stats.*


/**
 * A simple [Fragment] subclass.
 * Use the [StatsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StatsFragment : Fragment() {
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment StatsFragment.
         */
        @JvmStatic
        fun newInstance() = StatsFragment().apply { arguments = Bundle() }
    }

    /**
     * Method builds the RecyclerView
     */
    private fun buildRecyclerView() {
        adapter = StatsAdapter(statistics, requireContext())
        statsRecyclerView.layoutManager = LinearLayoutManager(context)
        statsRecyclerView.setHasFixedSize(true)
        statsRecyclerView.adapter = adapter
        adapter.setItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onAddClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onEditClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onDeleteClick(position: Int) {
                TODO("Not yet implemented")
            }
        })
    }


    /**
     * Method shows AlertDialog with the option to create a new TrainingPlan
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun showAddNewExerciseDialog() {
        // editable fields
        val inputName = EditText(context)
        val inputShortcut = EditText(context)
        inputName.hint = getString(R.string.name_edit_text)
        inputShortcut.hint = getString(R.string.shortcut_edit_text)

        // add layout for fields
        val layout = LinearLayout(context)
        layout.setPadding(4, 0, 4, 0)
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
            var order = statistics.asSequence().map(Statistic::order).maxOrNull()

            if (order != null) {
                addExercise(Statistic(name, shortcut, ++order))
            }
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

//        inputShortcut.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//            override fun afterTextChanged(field: Editable) {
//                if (field.length > 11) {
//                    field.replace(0, field.length, field, 0 ,11)
//                }
//            }
//        })
    }

    private fun addExercise(statistic: Statistic) {
        databaseHelper.insertNewExerciseIntoDb(statistic)
        val index: Int = statistics.size
        statistics.add(index, statistic)
        adapter.notifyItemInserted(index)
    }

}