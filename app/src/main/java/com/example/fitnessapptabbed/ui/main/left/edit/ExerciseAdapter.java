package com.example.fitnessapptabbed.ui.main.left.edit;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessapptabbed.databinding.ItemExerciseBinding;
import com.example.fitnessapptabbed.ui.main.left.OnItemClickListener;

import java.util.List;
import java.util.stream.IntStream;

/**
 * ExerciseAdapter class - fills the RecyclerView in EditPlanFragment
 * @author Long
 * @version 2.0
 */
public class ExerciseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SETS_RANGE = 10;
    private static final int REPS_RANGE = 30;
    private static final int ALPHA_TRANSPARENT = 0;
    private static final int ALPHA_OPAQUE = 255;
    private static final int SPINNER_WIDTH_S_R = 200;
    private static final int SPINNER_WIDTH_EX = 600;

    private final List<Exercise> exercisesInPlan;
    private final List<String> allExercises;
    private OnItemClickListener itemClickListener;

    /**
     * Constructor for ExerciseAdapter
     * @param databaseHelper DatabaseHelper passed from fragment
     * @param list data set
     */
    public ExerciseAdapter(List<Exercise> list, List<String> allExercises) {
        this.exercisesInPlan = list;
        this.allExercises = allExercises;
    }

    /**
     * Setter for itemClickListener
     * @param itemClickListener OnItemClickListener to be set
     */
    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull @Override @RequiresApi(api = Build.VERSION_CODES.N)
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExerciseBinding itemBinding = ItemExerciseBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        ExerciseViewHolder evh = new ExerciseViewHolder(itemBinding);

        setExerciseSpinner(evh.itemBinding.exerciseSpinner, allExercises);
        setSpinner(evh.itemBinding.setsSpinner, SETS_RANGE);
        setSpinner(evh.itemBinding.repsSpinner, REPS_RANGE);

        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ExerciseViewHolder evh = (ExerciseViewHolder) holder;
        ItemExerciseBinding ib = evh.itemBinding;

        evh.itemBinding.addExerciseFab.setOnClickListener(view -> {
            itemClickListener.onAddClick(-1);   // index does not matter
            changeExerciseAdded(evh);
        });

        evh.itemBinding.deleteExerciseFab.setOnClickListener(view -> {
            if (evh.getAbsoluteAdapterPosition() != RecyclerView.NO_POSITION) {
                itemClickListener.onDeleteClick(evh.getAbsoluteAdapterPosition());
                changeExerciseDeleted(evh);
            }
        });

        Exercise currentEx = exercisesInPlan.get(position);
        if(currentEx.isNullExercise()) return;

        // index numbers need to change, allExercises missing NULL exercise
            // changed so that at always one exercise selected
        int nameIndex = allExercises.indexOf(currentEx.getName());
        int setsIndex = exercisesInPlan.get(position).getSets();
        int repsIndex = exercisesInPlan.get(position).getReps();

        // sets and reps starting from 1 but indexing from zero
        ib.exerciseSpinner.setSelection(nameIndex);
        ib.setsSpinner.setSelection(setsIndex - 1);
        ib.repsSpinner.setSelection(repsIndex - 1);

        changeExerciseAdded(evh);
    }

    @Override
    public int getItemCount() {
        return exercisesInPlan.size();
    }

    /**
     * Method changes the appearance of the add Exercise item
     * upon addFab click
     */
    public void changeExerciseAdded(ExerciseViewHolder evh) {
        ItemExerciseBinding ib = evh.itemBinding;
        evh.itemView.getBackground().setAlpha(ALPHA_OPAQUE);
        ib.addExerciseFab.setVisibility(View.GONE);
        ib.exerciseLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Method changes the appearance of the add Exercise item
     * upon deleteFab click
     *
     * Important method, because the Recycler View
     * is reusing ViewHolders and keeps their states
     */
    public void changeExerciseDeleted(ExerciseViewHolder evh) {
        ItemExerciseBinding ib = evh.itemBinding;
        evh.itemView.getBackground().setAlpha(ALPHA_TRANSPARENT);
        ib.addExerciseFab.setVisibility(View.VISIBLE);
        ib.exerciseLayout.setVisibility(View.GONE);
        ib.exerciseSpinner.setSelection(0);
        ib.setsSpinner.setSelection(0);
        ib.repsSpinner.setSelection(0);
    }


    /**
     * Method to set Spinner items with number values
     * @param spinner to add selectable items to
     * @param range of items
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setSpinner(Spinner spinner, int range) {
        spinner.setDropDownWidth(SPINNER_WIDTH_S_R);

        // prepare values for spinner
        Integer[] values = IntStream.range(1, range + 1).boxed().toArray(Integer[]::new);

        // set up adapter to pass values to spinner
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(spinner.getContext(),
                android.R.layout.simple_spinner_dropdown_item, values);
        spinner.setAdapter(adapter);

        // set listener for spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinner.setSelection(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

    /**
     * Method to set Spinner items with exercise name values
     * @param exerciseSpinner Spinner containing exercises
     * @param allExercises List of all pre-defined exercises
     */
    private void setExerciseSpinner(Spinner exerciseSpinner, List<String> allExercises) {
        exerciseSpinner.setDropDownWidth(SPINNER_WIDTH_EX);

        // set up adapter for exercises spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(exerciseSpinner.getContext(),
                android.R.layout.simple_spinner_dropdown_item, allExercises);
        exerciseSpinner.setAdapter(adapter);

        // set listener for spinner
        exerciseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                exerciseSpinner.setSelection(i);
                ((TextView) view).setTextSize(15);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

    /**
     * Method moves item in RecyclerView from FROM to TO
     * @param from initial index
     * @param to target index
     */
    public void moveItemInRecyclerViewList(int from, int to) {
        Exercise movedItem = exercisesInPlan.get(from);
        exercisesInPlan.remove(movedItem);
        exercisesInPlan.add(to, movedItem);
    }

    /**
     * ExerciseViewHolder class - encapsulates Exercise data in a View
     */
    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        public ItemExerciseBinding itemBinding;

        /**
         * Constructor for ExerciseViewHolder
         * @param itemBinding binding to get access to child Views
         */
        public ExerciseViewHolder(ItemExerciseBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
            itemView.getBackground().setAlpha(ALPHA_TRANSPARENT);
        }
    }
}