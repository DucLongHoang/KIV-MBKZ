package com.example.fitnessapptabbed.ui.main.left.edit;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessapptabbed.R;
import com.example.fitnessapptabbed.database.PlansDatabaseHelper;
import com.example.fitnessapptabbed.databinding.FragmentEditPlanBinding;
import com.example.fitnessapptabbed.ui.main.left.OnItemClickListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditPlanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditPlanFragment extends Fragment {
    private static final String CANCEL_CONFIRM = "Do you really want to cancel editing?";
    private static final String SAVE_CONFIRM = "Save training plan configuration?";

    private FragmentEditPlanBinding binding;
    private ExerciseAdapter adapter;
    private List<Exercise> exercises;
    private PlansDatabaseHelper databaseHelper;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreatePlanFragment.
     */
    public static EditPlanFragment newInstance() {
        EditPlanFragment fragment = new EditPlanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditPlanBinding.inflate(inflater, container, false);
        databaseHelper = new PlansDatabaseHelper(requireContext());
        exercises = databaseHelper.getExercisesOfPlanFromDb(null);
        buildRecyclerView();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHeader();
        setCancelButton();
        setSaveButton();

        Snackbar.make(binding.getRoot(), R.string.stayOnTabPlease, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(databaseHelper != null) databaseHelper.close();
    }

    /**
     * Method sets the header of the EditPlanFragment.
     * Displays the title and description of the selected TrainingPlan
     */
    private void setHeader() {
        String title = EditPlanFragmentArgs.fromBundle(getArguments()).getTitle();
        String description = EditPlanFragmentArgs.fromBundle(getArguments()).getDescription();
        binding.textViewTitle.setText(title);
        binding.textViewDescription.setText(description);
    }

    /**
     * Method builds the RecyclerView of Exercises
     * and all the onItemClick actions
     */
    private void buildRecyclerView() {
        adapter = new ExerciseAdapter(databaseHelper, exercises);
        binding.exercisesRecyclerView.setHasFixedSize(true);
        binding.exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.exercisesRecyclerView.setAdapter(adapter);
        adapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) { }
            @Override
            public void onEditClick(int position) { }
            @Override
            public void onAddClick(int position) {
                addExercise();
            }
            @Override
            public void onDeleteClick(int position) {
                deleteExercise(position);
            }
        });
    }

    /**
     * Method adds an Exercise to the RecyclerView
     */
    private void addExercise() {
        Exercise ex = new Exercise();
        int index = exercises.size();
        exercises.add(index, ex);
        adapter.notifyItemInserted(index);
    }

    /**
     * Method deletes an Exercise from the Recycler View
     * @param position at which an Exercise is deleted from
     */
    private void deleteExercise(int position) {
        exercises.remove(position);
        adapter.notifyItemRemoved(position);
    }

    /**
     * Method sets cancel button functionality
     */
    private void setCancelButton() {
        binding.cancelEditButton.setOnClickListener(view -> createDialog(false));
    }

    /**
     * Method sets save button functionality
     */
    private void setSaveButton() {
        binding.saveEditButton.setOnClickListener(view -> {
            saveExercises();
            printExercises();
            createDialog(true);
            }
        );
    }

    private void printExercises() {
        for(Exercise ex: exercises) System.out.println(ex);
    }

    /**
     * Method saves exercises from the Recycler View
     * into a List to save them into the database
     */
    private void saveExercises() {
        RecyclerView rv = binding.exercisesRecyclerView;
        ExerciseAdapter.ExerciseViewHolder evh;
        Exercise exercise; View v;

        // List length - 1 because don't want to save invisible default exercise
        for(int i = 0; i < exercises.size(); i++) {
            v = rv.getChildAt(i);
            evh = (ExerciseAdapter.ExerciseViewHolder) rv.getChildViewHolder(v);
            exercise = exercises.get(i);
            exercise.setName(evh.exercise.getSelectedItem().toString());
            exercise.setSets((int)evh.sets.getSelectedItem());
            exercise.setReps((int)evh.reps.getSelectedItem());
        }
    }

    /**
     * Method creates a dialog to either cancel or save editing
     * One method to use for both CANCEL and SAVE buttons
     * @param save true to save plan config, false to cancel editing
     */
    private void createDialog(boolean save) {
        String confirmation = save ? SAVE_CONFIRM : CANCEL_CONFIRM;

        // create dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle(confirmation);

        // setting options
        dialogBuilder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());
        dialogBuilder.setPositiveButton("Yes", (dialogInterface, i) -> {
            if(save) { savePlanConfig(exercises); }
            NavHostFragment.findNavController(EditPlanFragment.this)
                    .navigate(R.id.action_editPlan_to_plans); });

        // show dialog
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    /**
     * Method inserts the plan configuration into the database
     * @param exercises to be inserted into the database
     */
    private void savePlanConfig(List<Exercise> exercises) {
        String title = EditPlanFragmentArgs.fromBundle(getArguments()).getTitle();
        databaseHelper.insertPlanConfigIntoDb(title, exercises);
    }
}