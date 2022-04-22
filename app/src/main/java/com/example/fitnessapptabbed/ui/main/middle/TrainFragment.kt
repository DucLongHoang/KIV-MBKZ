package com.example.fitnessapptabbed.ui.main.middle

import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import com.example.fitnessapptabbed.R
import com.example.fitnessapptabbed.databinding.FragmentTrainBinding


/**
 * A simple [Fragment] subclass.
 * Use the [TrainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrainFragment : Fragment() {
    private lateinit var binding: FragmentTrainBinding
    private lateinit var chronometer: Chronometer
    private var running: Boolean = false
    private var pauseOffset: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTrainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chronometer = view.findViewById(R.id.chronometer)
        chronometer.format = "%s"

        view.findViewById<Button>(R.id.startButton).setOnClickListener { startChronometer() }
        view.findViewById<Button>(R.id.stopButton).setOnClickListener { stopChronometer() }
        view.findViewById<Button>(R.id.resetButton).setOnClickListener { resetChronometer() }
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
        fun newInstance() =
            TrainFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}