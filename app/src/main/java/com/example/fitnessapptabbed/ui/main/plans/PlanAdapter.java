package com.example.fitnessapptabbed.ui.main.plans;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessapptabbed.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * TrainingPlanAdapter class - fills the recycler-view in PlansFragment
 * @author Long
 * @version 1.0
 */
public class PlanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<TrainingPlan> TRAINING_PLANS;
    private OnItemClickListener itemClickListener;

    /**
     * Constructor for PlanAdapter
     * @param list data set
     */
    public PlanAdapter(List<TrainingPlan> list) {
        this.TRAINING_PLANS = list;
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
                .inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(view, this.itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TrainingPlan currentPlan = this.TRAINING_PLANS.get(position);
        PlanViewHolder tvh = (PlanViewHolder) holder;

        tvh.title.setText(currentPlan.getTitle());
        tvh.description.setText(currentPlan.getDescription());
        tvh.editFab.setBackgroundColor(Color.BLUE);
        tvh.deleteFab.setBackgroundColor(Color.GREEN);
    }

    @Override
    public int getItemCount() {
        return TRAINING_PLANS.size();
    }

    /**
     * PlanViewHolder class - encapsulates TrainingPlan data in a View
     */
    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        public TextView title, description;
        public FloatingActionButton deleteFab, editFab;

        /**
         * Constructor for PlanViewHolder
         * @param itemView view of item, used to get references
         */
        public PlanViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewTitle);
            description = itemView.findViewById(R.id.textViewDescription);
            deleteFab = itemView.findViewById(R.id.deletePlanFab);
            editFab = itemView.findViewById(R.id.editPlanFab);

            itemView.setOnClickListener(view -> {
                if(listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                        listener.onItemClick(getAdapterPosition());
            });

            editFab.setOnClickListener(view -> {
                if(listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                    listener.onEditClick(getAdapterPosition());
            });

            deleteFab.setOnClickListener(view -> {
                if(listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                    listener.onDeleteClick(getAdapterPosition());
            });
        }
    }
}
