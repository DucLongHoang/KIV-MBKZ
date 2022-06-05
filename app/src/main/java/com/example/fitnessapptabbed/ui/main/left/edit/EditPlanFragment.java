package com.example.fitnessapptabbed.ui.main.left.edit;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessapptabbed.MainActivity;
import com.example.fitnessapptabbed.R;
import com.example.fitnessapptabbed.database.PlansDatabaseHelper;
import com.example.fitnessapptabbed.databinding.FragmentEditPlanBinding;
import com.example.fitnessapptabbed.databinding.ItemExerciseBinding;
import com.example.fitnessapptabbed.ui.main.left.OnItemClickListener;
import com.example.fitnessapptabbed.ui.main.right.Statistic;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditPlanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditPlanFragment extends Fragment {
    private FragmentEditPlanBinding binding;
    private ExerciseAdapter adapter;
    private List<Exercise> exercisesInPlan;
    private List<String> allExercises;
    private PlansDatabaseHelper databaseHelper;
    private String planTitle, planDescription;

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

    @Override @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditPlanBinding.inflate(inflater, container, false);
        databaseHelper = new PlansDatabaseHelper(requireContext());

        planTitle = EditPlanFragmentArgs.fromBundle(getArguments()).getTitle();
        planDescription = EditPlanFragmentArgs.fromBundle(getArguments()).getDescription();
        exercisesInPlan = databaseHelper.getPlanConfigFromDb(planTitle);
        allExercises = databaseHelper.getAllExercisesFromDb()
                .stream()
                .sorted()
                .map(Statistic::getExerciseName)
                .collect(Collectors.toList());

        buildRecyclerView();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHeader();
        setCancelButton();
        setSaveButton();

//        Snackbar sb = Snackbar.make(binding.getRoot(),
//                R.string.stay_on_tab_msg, Snackbar.LENGTH_SHORT);
//        sb.setAction(R.string.dismiss, view1 -> sb.dismiss());
//        sb.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(databaseHelper != null) databaseHelper.close();
    }

    /**
     * Method sets the header of the EditPlanFragment.
     * Displays the title and description of the selected TrainingPlan
     */
    private void setHeader() {
        binding.textViewTitle.setText(planTitle);
        binding.textViewDescription.setText(planDescription);
    }

    /**
     * Method builds the RecyclerView of Exercises
     * and all the onItemClick actions
     */
    private void buildRecyclerView() {
        adapter = new ExerciseAdapter(exercisesInPlan, allExercises);
        binding.exercisesRecyclerView.setHasFixedSize(true);
        binding.exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.exercisesRecyclerView.setAdapter(adapter);
//        binding.exercisesRecyclerView.setItemViewCacheSize(10);
        adapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) { }
            @Override
            public void onEditClick(int position) { }
            @Override
            public void onAddClick(int position) { addExercise(); }
            @Override
            public void onDeleteClick(int position) { deleteExercise(position); }
        });
    }

    /**
     * Method adds an Exercise to the RecyclerView
     */
    private void addExercise() {
        Exercise ex = new Exercise();
        int index = exercisesInPlan.size();
        exercisesInPlan.add(index, ex);
        adapter.notifyItemInserted(index);
    }

    /**
     * Method deletes an Exercise from the Recycler View
     * @param position at which an Exercise is deleted from
     */
    private void deleteExercise(int position) {
        exercisesInPlan.remove(position);
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
            if ( !( (MainActivity)requireActivity() ).canEditPlan() )
                Toast.makeText(requireContext(), R.string.cannot_edit_msg,
                        Toast.LENGTH_SHORT).show();
            else createDialog(true);
        });
    }

    /**
     * Method saves exercises from the Recycler View
     * into a List to save them into the database
     */
    private void saveExercises() {
        RecyclerView rv = binding.exercisesRecyclerView;
        ExerciseAdapter.ExerciseViewHolder evh;
        ItemExerciseBinding ib;
        Exercise exercise; View v;

        // List length - 1 because don't want to save invisible default exercise
        for(int i = 0; i < exercisesInPlan.size() - 1; i++) {
            v = rv.getChildAt(i);
            evh = (ExerciseAdapter.ExerciseViewHolder) rv.getChildViewHolder(v);
            ib = evh.itemBinding;
            exercise = exercisesInPlan.get(i);

            // setting exercise from ViewHolder
            exercise.setName(ib.exerciseSpinner.getSelectedItem().toString());
            exercise.setSets( (int) ib.setsSpinner.getSelectedItem());
            exercise.setReps( (int) ib.repsSpinner.getSelectedItem());
        }
    }

    /**
     * Method creates a dialog to either cancel or save editing
     * One method to use for both CANCEL and SAVE buttons
     * @param save true to save plan config, false to cancel editing
     */
    private void createDialog(boolean save) {
        String confirmation = save ?
                getString(R.string.save_plan_prompt) : getString(R.string.cancel_editing_prompt);

        // create dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle(confirmation);

        // setting options
        dialogBuilder.setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.cancel());
        dialogBuilder.setPositiveButton(R.string.yes, (dialogInterface, i) -> {
            if(save) {
                saveExercises();
                savePlanConfig(exercisesInPlan);
            }
            NavHostFragment.findNavController(EditPlanFragment.this)
                    .navigate(R.id.action_editPlan_to_plans); });
            ( (MainActivity)requireActivity() ).setCanTrain(true);
            ( (MainActivity)requireActivity() ).setCanEditExercises(true);

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
        List<Exercise> toBeSaved = exercises.subList(0, exercises.size() - 1);
        databaseHelper.updatePlanConfigInDb(title, toBeSaved);
    }
}