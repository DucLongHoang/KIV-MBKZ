package com.example.fitnessapptabbed.ui.main.plans;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fitnessapptabbed.databinding.FragmentPlansBinding;
import com.example.fitnessapptabbed.ui.main.PlansDatabaseHelper;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlansFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlansFragment extends Fragment {
    private FragmentPlansBinding binding;
    private PlanAdapter adapter;
    private List<TrainingPlan> trainingPlans;
    private PlansDatabaseHelper databaseHelper;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PlansFragment.
     */
    public static PlansFragment newInstance() {
        PlansFragment fragment = new PlansFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPlansBinding.inflate(inflater, container, false);
        databaseHelper = new PlansDatabaseHelper(requireContext());
        trainingPlans = databaseHelper.getPlansFromDb();
        buildRecyclerView();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.addPlanFab.setOnClickListener(view1 -> createPlanDialog());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }

    /**
     * Method builds the RecyclerView of TrainingPlans
     * and all the onItemClick actions
     */
    private void buildRecyclerView() {
        adapter = new PlanAdapter(trainingPlans);
        binding.plansRecyclerView.setHasFixedSize(true);
        binding.plansRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.plansRecyclerView.setAdapter(adapter);
        adapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onAddClick(int position) { }
            @Override
            public void onItemClick(int position) {
                navigateToEditPlan(position);
            }
            @Override
            public void onDeleteClick(int position) {
                deletePlan(position);
            }
            @Override
            public void onEditClick(int position) {
                editTitleAndDescription(position, "Changed " + position);
            }
        });
    }

    /**
     * Method navigates to a new Fragment,
     * where editing Exercises is happening
     * @param position of the TrainingPlan to be edited
     */
    private void navigateToEditPlan(int position) {
        TrainingPlan toPass = trainingPlans.get(position);
        PlansFragmentDirections.ActionPlansToEditPlan action = PlansFragmentDirections
                .actionPlansToEditPlan(toPass.getTitle(), toPass.getDescription());
        NavHostFragment.findNavController(PlansFragment.this)
                .navigate(action);
    }

    /**
     * Method adds the TrainingPLan into the database
     * @param tp TrainingPlan to be added
     */
    private void addPlan(TrainingPlan tp) {
        databaseHelper.insertPlanIntoDb(tp);
        int index = trainingPlans.size();
        trainingPlans.add(index, tp);
        adapter.notifyItemInserted(index);
    }

    /**
     * Method to delete a TrainingPlan from RecyclerView
     * @param position of the TrainingPlan to be deleted
     */
    private void deletePlan(int position) {
        TrainingPlan toDelete = trainingPlans.get(position);
        databaseHelper.deletePlanFromDb(toDelete);
        trainingPlans.remove(position);
        adapter.notifyItemRemoved(position);
    }

    /**
     * Method to edit Title of a TrainingPlan
     * @param position of TrainingPlan to be edited
     * @param text to change the Title to
     */
    private void editTitleAndDescription(int position, String text) {
        this.trainingPlans.get(position).setTitle(text);
        adapter.notifyItemChanged(position);
    }

    /**
     * Method shows AlertDialog with the option to create a new TrainingPlan
     */
    private void createPlanDialog() {
        // editable fields
        final EditText inputTitle = new EditText(getContext());
        final EditText inputDescription = new EditText(getContext());
        inputTitle.setHint("Title (mandatory)");
        inputDescription.setHint("Description (optional)");

        // add layout for fields
        LinearLayout layout = new LinearLayout(getContext());
        layout.setPadding(4, 0, 4, 0);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputTitle);
        layout.addView(inputDescription);

        // create Dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle("Create a new training plan");
        dialogBuilder.setView(layout);

        // setting options
        dialogBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        dialogBuilder.setPositiveButton("Create", (dialogInterface, i) ->
                addPlan(new TrainingPlan(inputTitle.getText().toString(),
                inputDescription.getText().toString())));

        // empty Title edit field
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();  // has to be in this order - 1.show dialog, 2.disable button
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);

        // enable create button if Title field not empty
        inputTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                boolean enable = !TextUtils.isEmpty(editable.toString().trim()); // if empty false, else true
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(enable);
            }
        });
    }
}