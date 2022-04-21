package com.example.fitnessapptabbed.ui.main.plans;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fitnessapptabbed.R;
import com.example.fitnessapptabbed.databinding.FragmentEditPlanBinding;
import com.example.fitnessapptabbed.ui.main.PlansDatabaseHelper;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditPlanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditPlanFragment extends Fragment {
    private FragmentEditPlanBinding binding;
    private ExerciseAdapter adapter;
    private List<Exercise> exercises, tmpExercises;
    private PlansDatabaseHelper databaseHelper;
    private TrainingPlan plan;

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
        exercises = databaseHelper.getExercisesOfPlanFromDb(plan);

        buildRecyclerView();

        Toast.makeText(requireActivity().getBaseContext(),
                R.string.stayOnTabPlease, Toast.LENGTH_LONG).show();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHeader();
        setButtons();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }

    private void setHeader() {
        String title = EditPlanFragmentArgs.fromBundle(getArguments()).getTitle();
        String description = EditPlanFragmentArgs.fromBundle(getArguments()).getDescription();
        binding.textViewTitle.setText(title);
        binding.textViewDescription.setText(description);
    }

    private void buildRecyclerView() {
        adapter = new ExerciseAdapter(exercises);
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
                addExercise(position);
            }
            @Override
            public void onDeleteClick(int position) {
                deleteExercise(position);
            }
        });
    }

    private void addExercise(int position) {
        Exercise ex = new Exercise("Bench", 10, 10);
        int index = exercises.size();
        exercises.add(index, ex);
        adapter.notifyItemInserted(index);
    }

    private void deleteExercise(int position) {
        exercises.remove(position);
        adapter.notifyItemRemoved(position);
    }

    private void setButtons() {
        binding.cancelEditButton.setOnClickListener(view1 -> setCancelButton());
        binding.saveEditButton.setOnClickListener(view1 -> setSaveButton());
    }

    private void setCancelButton() {
        NavHostFragment.findNavController(EditPlanFragment.this)
                .navigate(R.id.action_editPlan_to_plans);
    }

    private void setSaveButton() {
        NavHostFragment.findNavController(EditPlanFragment.this)
                .navigate(R.id.action_editPlan_to_plans);
    }
}