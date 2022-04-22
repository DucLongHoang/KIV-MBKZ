package com.example.fitnessapptabbed.ui.main.plans;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessapptabbed.R;
import com.example.fitnessapptabbed.ui.main.PlansDatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * [ExerciseAdapter] class - fills the recycler-view in EditPlanFragment
 * @author Long
 * @version 1.0
 */
public class ExerciseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Exercise> exercises;
    private OnItemClickListener itemClickListener;
    public PlansDatabaseHelper databaseHelper;

    /**
     * Constructor for ExerciseAdapter
     * @param databaseHelper DatabaseHelper passed from fragment
     * @param list data set
     */
    public ExerciseAdapter(PlansDatabaseHelper databaseHelper, List<Exercise> list) {
        this.exercises = list;
        this.databaseHelper = databaseHelper;
    }

    /**
     * Setter for itemClickListener
     * @param itemClickListener OnItemClickListener to be set
     */
    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view, this.itemClickListener, this.databaseHelper);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Exercise exercise = this.exercises.get(position);
        ExerciseViewHolder evh = (ExerciseViewHolder) holder;

//        exercise.setName(evh.exercise.getSelectedItem().toString());
//        exercise.setSets((int)evh.sets.getSelectedItem());
//        exercise.setReps((int)evh.reps.getSelectedItem());
//
//        System.out.println(exercise);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    /**
     * ExerciseViewHolder class - encapsulates Exercise data in a View
     */
    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        public FloatingActionButton addFab, deleteFab;
        public Spinner exercise, sets, reps;
        public LinearLayout layout;

        /**
         * Constructor for ExerciseViewHolder
         * @param itemView view of item, used to get references
         * @param listener listener for item clicking events
         */
        public ExerciseViewHolder(@NonNull View itemView, final OnItemClickListener listener, final PlansDatabaseHelper databaseHelper) {
            super(itemView);
            addFab = itemView.findViewById(R.id.addExerciseFab);
            deleteFab = itemView.findViewById(R.id.deleteExerciseFab);
            exercise = itemView.findViewById(R.id.exerciseSpinner);
            sets = itemView.findViewById(R.id.setsSpinner);
            reps = itemView.findViewById(R.id.repsSpinner);
            layout = itemView.findViewById(R.id.exerciseLayout);

            setExerciseSpinner(itemView, databaseHelper);
            setSpinner(itemView, sets, 5);
            setSpinner(itemView, reps, 15);

            addFab.setOnClickListener(view -> {
                if(listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onAddClick(getAdapterPosition());
                    changeExerciseItemLook();
                }
            });

            deleteFab.setOnClickListener(view -> {
                if(listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                    listener.onDeleteClick(getAdapterPosition());
            });
        }

        /**
         * Method changes the appearance of the add Exercise item
         * upon addFab click
         */
        private void changeExerciseItemLook() {
            addFab.setClickable(false);
            addFab.setVisibility(View.INVISIBLE);
            layout.setClickable(true);
            layout.setVisibility(View.VISIBLE);
        }

        /**
         * Method to set Spinner items with number values
         * @param itemView of Spinner
         * @param spinner to add selectable items to
         * @param range of items
         */
        private void setSpinner(@NonNull View itemView, Spinner spinner, int range) {
            spinner.setDropDownWidth(200);

            // prepare values for spinner
            Integer[] values = new Integer[range];
            for(int i = 0; i < range; i++) { values[i] = i + 1; }

            // set up adapter to pass values to spinner
            ArrayAdapter<Integer> adapter = new ArrayAdapter<>(itemView.getContext(),
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
         * @param itemView of Spinner
         * @param databaseHelper to retrieve exercises from the database
         */
        private void setExerciseSpinner(View itemView, PlansDatabaseHelper databaseHelper) {
            // prepare values for spinner
            List<String> exercises = databaseHelper.getExerciseNamesFromDb();
            String[] values = new String[exercises.size()];
            for(int i = 0; i < exercises.size(); i++) { values[i] = exercises.get(i); }

            // set up adapter for exercises spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(itemView.getContext(),
                    android.R.layout.simple_spinner_dropdown_item, values);
            exercise.setAdapter(adapter);

            // set listener for spinner
            exercise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    exercise.setSelection(i);
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) { }
            });
        }
    }
}