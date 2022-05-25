package com.example.fitnessapptabbed.ui.main.middle;

import android.annotation.SuppressLint;
import android.os.Vibrator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.fitnessapptabbed.R;
import com.example.fitnessapptabbed.database.PlansDatabaseHelper;
import com.example.fitnessapptabbed.ui.main.left.edit.Exercise;

import java.util.List;

/**
 * TrainingProgressHandler class - handles the logic and view
 * of the currently trained exercise in the train layout
 * @author Long
 * @version 1.0
 */
public class TrainingProgressHandler {
    // Constants
    private static final int SET_VIB_DURATION = 100;
    private static final int EX_VIB_DURATION = 400;

    // Variables
    private final TrainFragment fragment;
    private final PlansDatabaseHelper databaseHelper;
    private final InputWeightHandler inputWeightHandler;
    private final RecordHandler recordHandler;
    private final Vibrator vibrator;
    private final List<Exercise> exercises;
    private Exercise currExercise;
    private int setCounter, exerciseIndex;
    private final int exerciseCount;

    // View objects
    private final ImageButton ibNext, ibBack;
    private final TextView tvCurrEx, tvSets, tvReps, tvRecord;

    /**
     * Constructor for TrainingProgressHandler
     * @param fragment where the handler is used, needed to take bindings
     * @param exercisesList list of Exercises of TrainingPLan to display
     */
    public TrainingProgressHandler(TrainFragment fragment, List<Exercise> exercisesList) {
        this.fragment = fragment;
        this.databaseHelper = new PlansDatabaseHelper(fragment.requireContext());
        this.recordHandler = new RecordHandler(fragment);
        this.vibrator = fragment.getVibrator();
        this.exercises = exercisesList;
        this.currExercise = exercisesList.get(0);
        // length - 1 because of last empty exercise
        this.exerciseCount = exercisesList.size() - 1;
        this.exerciseIndex = 0;
        this.setCounter = 1;

        ibNext = fragment.requireActivity().findViewById(R.id.imButtonNext);
        ibBack = fragment.requireActivity().findViewById(R.id.imButtonBack);
        tvCurrEx = fragment.requireActivity().findViewById(R.id.textViewCurrEx);
        tvSets = fragment.requireActivity().findViewById(R.id.textViewSetCounter);
        tvReps = fragment.requireActivity().findViewById(R.id.textViewRepCounter);
        tvRecord = fragment.requireActivity().findViewById(R.id.textViewRecord);

        EditText editTextKg = fragment.requireActivity().findViewById(R.id.editTextInputWeight);
        this.inputWeightHandler = new InputWeightHandler(editTextKg);
        displayInfo();
    }

    /**
     * Method moves to the previous set / exercise
     */
    public void moveBack() {
        int vibDuration = SET_VIB_DURATION;
        setCounter--;

        // before 1st set, change exercise to previous
        if(setCounter <= 0) {
            vibDuration = EX_VIB_DURATION;
            // if exercise first, disable Back button
            if(!hasPreviousExercise()) ibBack.setEnabled(false);
        }
        vibrator.vibrate(vibDuration);
        displayInfo();
        inputWeightHandler.displayPreviousWeight(currExercise, setCounter);
    }

    /**
     * Method move to the next set / exercise
     * @param inputKgs weight of the just done exercise
     */
    public void moveNext(int inputKgs) {
        recordHandler.checkIfRecordBroken(currExercise.getName(), inputKgs);
        inputWeightHandler.saveWeight(currExercise, setCounter, inputKgs);

        int vibDuration = SET_VIB_DURATION;
        ibBack.setEnabled(true);
        setCounter++;

        // last set done, change exercise to next
        if(setCounter > currExercise.getSets()) {
            vibDuration = EX_VIB_DURATION;
            // if exercise is last, disable Next button
            if(!hasNextExercise()) ibNext.setEnabled(false);
        }
        vibrator.vibrate(vibDuration);
        displayInfo();
        inputWeightHandler.displayNextWeight(currExercise, setCounter);
    }

    /**
     * Method moves iterator to the previous exercise
     * @return true if not first, otherwise false
     */
    private boolean hasPreviousExercise() {
        exerciseIndex--;
        if(exerciseIndex >= 0) {
            currExercise = exercises.get(exerciseIndex);
            setCounter = currExercise.getSets();
            return true;
        }
        else {
            setCounter++;
            exerciseIndex++;    // to prevent negative index
            return false;
        }
    }

    /**
     * Method moves iterator to next exercise
     * because the sets of the previous one are done
     * @return true if not last, otherwise false
     */
    private boolean hasNextExercise() {
        exerciseIndex++;
        if(exerciseIndex <  exerciseCount) {
            currExercise = exercises.get(exerciseIndex);
            setCounter = 1;
            return true;
        }
        else {
            setCounter--;
            exerciseIndex--;    // to not exceed List size
            fragment.endTrainingDialog();
            return false;
        }
    }

    /**
     * Method displays current exercise, set and rep count
     * as well as ideal weight for hypertrophy
     */
    @SuppressLint("SetTextI18n")
    private void displayInfo() {
        int currRecord = databaseHelper.getRecordKgsFromDb(currExercise.getName());
        tvCurrEx.setText(currExercise.getName().toUpperCase());
        tvSets.setText(setCounter + "/" + currExercise.getSets());
        tvReps.setText("" + currExercise.getReps());
        tvRecord.setText(currRecord + " kg");
    }

    /**
     * Method trim number to 1 decimal place
     * @param value to be trimmed
     * @return a new rounded number
     */
    private double round(double value) {
        int scale = (int) Math.pow(10, 1);
        return (double) Math.round(value * scale) / scale;
    }
}