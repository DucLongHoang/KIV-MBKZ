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
    public static final String NULL_EXERCISE = "------------------------";
    private static final int SETS_RANGE = 6;
    private static final int REPS_RANGE = 30;
    private static final int ALPHA_TRANSPARENT = 0;
    private static final int ALPHA_OPAQUE = 255;

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

        List<Statistic> statistics = databaseHelper.getAllExercisesFromDb();
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
        Exercise currentEx = this.exercisesInPlan.get(position);
        if(currentEx.isNullExercise()) return;

        // setting up view if exercise taken from database
        ExerciseViewHolder evh = (ExerciseViewHolder) holder;
        // index numbers need to change, allExercises missing NULL exercise
            // changed so that at always one exercise selected
        // sets and reps starting from 1 but indexing from zero
        int nameIndex = allExercises.indexOf(currentEx.getName());
        int setsIndex = exercisesInPlan.get(position).getSets();
        int repsIndex = exercisesInPlan.get(position).getReps();

        evh.exerciseSpinner.setSelection(nameIndex);
        evh.setsSpinner.setSelection(setsIndex - 1);
        evh.repsSpinner.setSelection(repsIndex - 1);
        evh.changeExerciseAdded();
    }

    @Override
    public int getItemCount() {
        return exercisesInPlan.size();
    }

    /**
     * ExerciseViewHolder class - encapsulates Exercise data in a View
     */
    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public FloatingActionButton addFab, deleteFab;
        public Spinner exerciseSpinner, setsSpinner, repsSpinner;
        public LinearLayout layout;

        /**
         * Constructor for ExerciseViewHolder
         * @param itemView view of item, used to get references
         * @param listener listener for item clicking events
         */
        public ExerciseViewHolder(@NonNull View itemView, final OnItemClickListener listener,
                                  final List<String> allExercises) {
            super(itemView);
            this.itemView = itemView;

            this.itemView.getBackground().setAlpha(ALPHA_TRANSPARENT);
            addFab = itemView.findViewById(R.id.addExerciseFab);
            deleteFab = itemView.findViewById(R.id.deleteExerciseFab);
            exerciseSpinner = itemView.findViewById(R.id.exerciseSpinner);
            setsSpinner = itemView.findViewById(R.id.setsSpinner);
            repsSpinner = itemView.findViewById(R.id.repsSpinner);
            layout = itemView.findViewById(R.id.exerciseLayout);

            setExerciseSpinner(itemView, allExercises);
            setSpinner(itemView, setsSpinner, SETS_RANGE);
            setSpinner(itemView, repsSpinner, REPS_RANGE);

            addFab.setOnClickListener(view -> {
                if(listener != null && getAbsoluteAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onAddClick(getAbsoluteAdapterPosition());
                    changeExerciseAdded();
                }
            });

            deleteFab.setOnClickListener(view -> {
                if(listener != null && getAbsoluteAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(getAbsoluteAdapterPosition());
                    changeExerciseDeleted();
                }
            });
        }

        /**
         * Method changes the appearance of the add Exercise item
         * upon addFab click
         */
        public void changeExerciseAdded() {
            itemView.getBackground().setAlpha(ALPHA_OPAQUE);
            addFab.setClickable(false);
            addFab.setVisibility(View.INVISIBLE);
            layout.setClickable(true);
            layout.setVisibility(View.VISIBLE);
        }

        /**
         * Method changes the appearance of the add Exercise item
         * upon deleteFab click
         *
         * Important method, because the Recycler View
         * is reusing ViewHolders and keeps their states
         */
        public void changeExerciseDeleted() {
            itemView.getBackground().setAlpha(ALPHA_TRANSPARENT);
            addFab.setClickable(true);
            addFab.setVisibility(View.VISIBLE);
            layout.setClickable(false);
            layout.setVisibility(View.INVISIBLE);
            exerciseSpinner.setSelection(0);
            setsSpinner.setSelection(0);
            repsSpinner.setSelection(0);
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
//            List<String> allPlusNullExercise = new ArrayList<>();
//            allPlusNullExercise.add(NULL_EXERCISE);
//            allPlusNullExercise.addAll(allExercises);

            // set up adapter for exercises spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(itemView.getContext(),
                    android.R.layout.simple_spinner_dropdown_item, allExercises);
            exerciseSpinner.setAdapter(adapter);

            // set listener for spinner
            exerciseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    exerciseSpinner.setSelection(i);
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) { }
            });
        }
    }
}