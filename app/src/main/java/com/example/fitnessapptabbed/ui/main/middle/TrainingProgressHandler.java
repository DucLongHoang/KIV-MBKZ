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
import java.util.ListIterator;

/**
 * TrainingProgressHandler class - handles the logic and view
 * of the currently trained exercise in the train layout
 * @author Long
 * @version 1.0
 */
public class TrainingProgressHandler {
    // Constants
    private static final double HYPER_TROPHY = 0.8;
    private static final int SET_VIB_DURATION = 100;
    private static final int EX_VIB_DURATION = 400;

    // Variables
    private final TrainFragment fragment;
    private final PlansDatabaseHelper databaseHelper;
    private final InputWeightHandler inputWeightHandler;
    private final RecordHandler recordHandler;
    private final Vibrator vibrator;
    private final ListIterator<Exercise> exIterator;
    private final Exercise firstExercise, lastExercise;
    private Exercise currExercise;
    private int setCounter;
    private boolean exMovingBackward, exMovingForward;

    // View objects
    private final ImageButton ibNext, ibBack;
    private final TextView tvCurrEx, tvSets, tvReps, tvKgs, tvRecord;

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
        this.exIterator = exercisesList.listIterator();
        this.currExercise = exIterator.next();
        this.firstExercise = currExercise;
        // length - 2, because last exercise is empty exercise
        this.lastExercise = exercisesList.get(exercisesList.size() - 2);
        this.setCounter = 1;
        this.exMovingBackward = false;
        this.exMovingForward = true;

        ibNext = fragment.requireActivity().findViewById(R.id.imButtonNext);
        ibBack = fragment.requireActivity().findViewById(R.id.imButtonBack);
        tvCurrEx = fragment.requireActivity().findViewById(R.id.textViewCurrEx);
        tvSets = fragment.requireActivity().findViewById(R.id.textViewSetCounter);
        tvReps = fragment.requireActivity().findViewById(R.id.textViewRepCounter);
        tvKgs = fragment.requireActivity().findViewById(R.id.textViewIdealWeight);
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
        inputWeightHandler.displayPreviousWeight();
    }

    /**
     * Method move to the next set / exercise
     * @param inputKgs weight of the just done exercise
     */
    public void moveNext(int inputKgs) {
        boolean atLastExercise = false;
        recordHandler.checkIfRecordBroken(currExercise.getName(), inputKgs);

        int vibDuration = SET_VIB_DURATION;
        ibBack.setEnabled(true);
        setCounter++;

        // last set done, change exercise to next
        if(setCounter > currExercise.getSets()) {
            vibDuration = EX_VIB_DURATION;
            // if exercise is last, disable Next button
            if(!hasNextExercise()) {

                ibNext.setEnabled(false);
            }
        }
        vibrator.vibrate(vibDuration);
        displayInfo();
        inputWeightHandler.displayNextWeight(inputKgs);
    }

    /**
     * Method moves iterator to the previous exercise
     * @return true if not first, otherwise false
     */
    private boolean hasPreviousExercise() {
        if(exIterator.hasPrevious()) {
            // handling iterator changing directions, move one extra back
            if(exMovingForward) {
                exMovingForward = false;
                exMovingBackward = true;
                currExercise = exIterator.previous();
            }
            // handling going back on first exercise
            if(currExercise.equals(firstExercise)) {
                setCounter++;
                return false;
            }
            // normal behaviour
            currExercise = exIterator.previous();
            setCounter = currExercise.getSets();
            return true;
        }
        setCounter++;
        return false;
    }

    /**
     * Method moves iterator to next exercise
     * because the sets of the previous one are done
     * @return true if not last, otherwise false
     */
    private boolean hasNextExercise() {
        if(exIterator.hasNext()) {
            // handling iterator changing directions, move one extra next
            if(exMovingBackward) {
                currExercise = exIterator.next();
                exMovingBackward = false;
                exMovingForward = true;
            }
            // handling going next on last exercise
            if(currExercise.equals(lastExercise)) {
                setCounter--;
                fragment.endTrainingDialog();
                return false;
            }
            // normal behaviour
            currExercise = exIterator.next();
            setCounter = 1;
            return true;
        }
        setCounter--;
        return false;
    }

    /**
     * Method displays current exercise, set and rep count
     * as well as ideal weight for hypertrophy
     */
    @SuppressLint("SetTextI18n")
    private void displayInfo() {
        int currRecord = databaseHelper.getRecordKgs(currExercise.getName());
        tvCurrEx.setText(currExercise.getName().toUpperCase());
        tvSets.setText(setCounter + "/" + currExercise.getSets());
        tvReps.setText("" + currExercise.getReps());
        tvKgs.setText(round(currRecord * HYPER_TROPHY) + " kg");
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