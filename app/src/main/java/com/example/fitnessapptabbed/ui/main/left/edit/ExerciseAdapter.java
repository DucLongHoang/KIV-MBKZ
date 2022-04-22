package com.example.fitnessapptabbed.ui.main.left.edit;

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
import com.example.fitnessapptabbed.database.PlansDatabaseHelper;
import com.example.fitnessapptabbed.ui.main.left.OnItemClickListener;
import com.example.fitnessapptabbed.ui.main.right.Statistic;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * [ExerciseAdapter] class - fills the recycler-view in EditPlanFragment
 * @author Long
 * @version 1.0
 */
public class ExerciseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String NULL_EXERCISE = "------------------------";
    private final List<Exercise> exercisesInPlan;
    private final List<String> allExercises;
    private OnItemClickListener itemClickListener;

    /**
     * Constructor for ExerciseAdapter
     * @param databaseHelper DatabaseHelper passed from fragment
     * @param list data set
     */
    public ExerciseAdapter(PlansDatabaseHelper databaseHelper, List<Exercise> list) {
        this.exercisesInPlan = list;
        this.allExercises = new ArrayList<>();

        List<Statistic> statistics = databaseHelper.getExercisesFromDb();
        for(Statistic s : statistics) { allExercises.add(s.getExerciseName()); }
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
        return new ExerciseViewHolder(view, this.itemClickListener, this.allExercises);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Not implemented - not needed
    }

    @Override
    public int getItemCount() {
        return exercisesInPlan.size();
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
        public ExerciseViewHolder(@NonNull View itemView, final OnItemClickListener listener, final List<String> allExercises) {
            super(itemView);
            addFab = itemView.findViewById(R.id.addExerciseFab);
            deleteFab = itemView.findViewById(R.id.deleteExerciseFab);
            exercise = itemView.findViewById(R.id.exerciseSpinner);
            sets = itemView.findViewById(R.id.setsSpinner);
            reps = itemView.findViewById(R.id.repsSpinner);
            layout = itemView.findViewById(R.id.exerciseLayout);

            setExerciseSpinner(itemView, allExercises);
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
         * @param allExercises List of all pre-defined exercises
         */
        private void setExerciseSpinner(View itemView, List<String> allExercises) {
            List<String> allPlusNullExercise = new ArrayList<>();
            allPlusNullExercise.add(NULL_EXERCISE);
            allPlusNullExercise.addAll(allExercises);

            // set up adapter for exercises spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(itemView.getContext(),
                    android.R.layout.simple_spinner_dropdown_item, allPlusNullExercise);
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