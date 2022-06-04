package com.example.fitnessapptabbed.ui.main.left.plans;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fitnessapptabbed.MainActivity;
import com.example.fitnessapptabbed.R;
import com.example.fitnessapptabbed.database.PlansDatabaseHelper;
import com.example.fitnessapptabbed.databinding.FragmentPlansBinding;
import com.example.fitnessapptabbed.ui.main.left.OnItemClickListener;

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.addPlanFab.setOnClickListener(view1 -> showCreatePlanDialog());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(databaseHelper != null) databaseHelper.close();
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
            public void onItemClick(int position) { navigateToEditPlan(position); }
            @Override
            public void onDeleteClick(int position) { showDeletePlanDialog(position); }
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEditClick(int position) {
                showEditTitleDialog(trainingPlans.get(position), position);
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
        ( (MainActivity)requireActivity() ).setCanTrain(false);
        ( (MainActivity)requireActivity() ).setCanAddNewExercise(false);
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
        databaseHelper.deletePlanFromDb(toDelete.getTitle());
        databaseHelper.deletePlanConfigFromDb(toDelete.getTitle());
        trainingPlans.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void editPlan(String[] newPlanInfo, TrainingPlan planToBeRenamed, int position) {
        String newTitle = newPlanInfo[0];
        String newDesc = newPlanInfo[1];

        // branch 1 - check if Title remains the same
        if (newTitle.equals(planToBeRenamed.getTitle())) {
            // check if Description is new
            if (!newDesc.equals(planToBeRenamed.getDescription())) {
                databaseHelper.updatePlanDescriptionInDb(newTitle, newDesc);
                planToBeRenamed.setDescription(newDesc);
            }
            adapter.notifyItemChanged(position);    // just for the animation
            return;
        }
        // branch 2 - check if new Title already used
        if (!isDuplicatePlanName(newTitle)) {
            databaseHelper.updatePlanTitleInDb(planToBeRenamed.getTitle(), newTitle, newDesc);
            planToBeRenamed.setTitle(newTitle);
            planToBeRenamed.setDescription(newDesc);
            adapter.notifyItemChanged(position);
        }
    }

    /**
     * Method shows AlertDialog with the option to delete a new TrainingPlan
     * @param position of TrainingPlan to be deleted
     */
    private void showDeletePlanDialog(int position) {
        // create Dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        String title = trainingPlans.get(position).getTitle();
        String msg = getString(R.string.delete_plan_prompt) + title + " ?";
        dialogBuilder.setTitle(msg);

        // setting options
        dialogBuilder.setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.cancel());
        dialogBuilder.setPositiveButton(R.string.yes, (dialogInterface, i) -> deletePlan(position));

        // show dialog
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    /**
     * Method shows AlertDialog with the option to create a new TrainingPlan
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showCreatePlanDialog() {
        // editable fields
        final EditText inputTitle = new EditText(getContext());
        final EditText inputDescription = new EditText(getContext());
        inputTitle.setHint(R.string.title_edit_text);
        inputDescription.setHint(R.string.description_edit_text);

        // add layout for fields
        LinearLayout layout = new LinearLayout(getContext());
        layout.setPadding(16, 0, 16, 0);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputTitle);
        layout.addView(inputDescription);

        // create Dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle(getString(R.string.create_new_plan_msg));
        dialogBuilder.setView(layout);

        // setting options
        dialogBuilder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel());
        dialogBuilder.setPositiveButton(R.string.create, (dialogInterface, i) -> {
            String title = inputTitle.getText().toString();
            String desc = inputDescription.getText().toString();
            if (!isDuplicatePlanName(title)) addPlan(new TrainingPlan(title, desc));
        });

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

    /**
     * Method shows AlertDialog with the option to edit Title
     * and Description of a TrainingPlan
     * @param planToBeRenamed plan whose title and description will be changed
     * @param position at which the TrainingPlan is
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showEditTitleDialog(TrainingPlan planToBeRenamed, int position) {
        // editable fields
        final EditText inputTitle = new EditText(getContext());
        final EditText inputDescription = new EditText(getContext());
        inputTitle.setHint(R.string.title_edit_text);
        inputDescription.setHint(R.string.description_edit_text);
        // use old values
        inputTitle.setText(planToBeRenamed.getTitle());
        inputDescription.setText(planToBeRenamed.getDescription());

        // add layout for fields
        LinearLayout layout = new LinearLayout(getContext());
        layout.setPadding(16, 0, 16, 0);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputTitle);
        layout.addView(inputDescription);

        // create Dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle(getString(R.string.edit_plan_title_msg));
        dialogBuilder.setView(layout);

        // setting options
        dialogBuilder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel());
        dialogBuilder.setPositiveButton(R.string.edit, (dialogInterface, i) -> {
            String newTitle = inputTitle.getText().toString();
            String newDesc = inputDescription.getText().toString();
            editPlan(new String[]{newTitle, newDesc}, planToBeRenamed, position);
        });

        // empty Title edit field
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();  // has to be in this order, 1.show dialog, 2.disable button

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

    /**
     * Method checks if the inputted Plan name is already used or not
     * @param name new name of TrainingPlan
     * @return true if name already used, otherwise false
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean isDuplicatePlanName(String name) {
        boolean duplicate = trainingPlans.stream().anyMatch(tp -> tp.getTitle().equalsIgnoreCase(name));
        if (duplicate) Toast.makeText(getContext(), R.string.duplicate_plan_name_msg,
                Toast.LENGTH_SHORT).show();
        return duplicate;
    }
}