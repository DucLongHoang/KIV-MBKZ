package com.example.fitnessapptabbed.ui.main.stats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitnessapptabbed.R
import com.example.fitnessapptabbed.databinding.FragmentPlansHolderBinding
import com.example.fitnessapptabbed.databinding.FragmentStatsBinding
import com.example.fitnessapptabbed.ui.main.PlansDatabaseHelper
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentStatsBinding.inflate(inflater, container, false)

        databaseHelper = PlansDatabaseHelper(requireContext())
        statistics = databaseHelper.getExercisesFromDb()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildRecyclerView()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment StatsFragment.
         */
        @JvmStatic
        fun newInstance() =
            StatsFragment().apply { arguments = Bundle() }
    }

    /**
     * Method builds the RecyclerView
     */
    private fun buildRecyclerView() {
        statsRecyclerView.adapter = StatsAdapter(statistics)
        statsRecyclerView.layoutManager = LinearLayoutManager(context)
        statsRecyclerView.setHasFixedSize(true)
    }
}