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

    /**
     * Constructor for ExerciseAdapter
     * @param list data set
     */
    public ExerciseAdapter(List<Exercise> list) {
        this.exercises = list;
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
        return new ExerciseViewHolder(view, this.itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Exercise currentExercise = this.exercises.get(position);
        ExerciseViewHolder evh = (ExerciseViewHolder) holder;

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
        public ExerciseViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            addFab = itemView.findViewById(R.id.addExerciseFab);
            deleteFab = itemView.findViewById(R.id.deleteExerciseFab);
            exercise = itemView.findViewById(R.id.exerciseSpinner);
            sets = itemView.findViewById(R.id.setsSpinner);
            reps = itemView.findViewById(R.id.repsSpinner);
            layout = itemView.findViewById(R.id.exerciseLayout);

            setSpinner(itemView, sets, 5);
            setSpinner(itemView, reps, 20);

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

        private void setSpinner(@NonNull View itemView, Spinner spinner, int range) {
            String[] values = new String[range];
            for(int i = 0; i < range; i++) {
                values[i] = String.valueOf(i);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    itemView.getContext(), android.R.layout.simple_spinner_item, values);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }
}
