package com.example.fitnessapptabbed.ui.main.plans

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fitnessapptabbed.databinding.FragmentPlansHolderBinding

/**
 * A simple [Fragment] subclass.
 * Use the [PlansHolderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlansHolderFragment : Fragment() {
    private var _binding: FragmentPlansHolderBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentPlansHolderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlansHolderFragment.
         */
        @JvmStatic
        fun newInstance() =
            PlansHolderFragment().apply { arguments = Bundle() }
    }
}