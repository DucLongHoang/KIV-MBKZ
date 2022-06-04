package com.example.fitnessapptabbed.ui.main.left.plans;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessapptabbed.databinding.ItemPlanBinding;
import com.example.fitnessapptabbed.ui.main.left.OnItemClickListener;

import java.util.List;

/**
 * TrainingPlanAdapter class - fills the recycler-view in PlansFragment
 * @author Long
 * @version 1.0
 */
public class PlanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<TrainingPlan> trainingPlans;
    private OnItemClickListener itemClickListener;

    /**
     * Constructor for PlanAdapter
     * @param list data set
     */
    public PlanAdapter(List<TrainingPlan> list) {
        this.trainingPlans = list;
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
        ItemPlanBinding itemBinding = ItemPlanBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PlanViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TrainingPlan plan = trainingPlans.get(position);
        PlanViewHolder tvh = (PlanViewHolder) holder;
        ItemPlanBinding ib = tvh.itemBinding;
        tvh.bind(plan);

        tvh.itemView.setOnClickListener(view -> {
            if(tvh.getAbsoluteAdapterPosition() != RecyclerView.NO_POSITION)
                itemClickListener.onItemClick(tvh.getAbsoluteAdapterPosition());
        });

        ib.editPlanFab.setOnClickListener(view -> {
            if(tvh.getAbsoluteAdapterPosition() != RecyclerView.NO_POSITION)
                itemClickListener.onEditClick(tvh.getAbsoluteAdapterPosition());
        });

        ib.deletePlanFab.setOnClickListener(view -> {
            if(tvh.getAbsoluteAdapterPosition() != RecyclerView.NO_POSITION)
                itemClickListener.onDeleteClick(tvh.getAbsoluteAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return trainingPlans.size();
    }

    /**
     * PlanViewHolder class - encapsulates TrainingPlan data in a View
     */
    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        public ItemPlanBinding itemBinding;

        /**
         * Constructor for PlanViewHolder
         * @param itemView view of item, used to get references
         * @param listener listener for item clicking events
         */
        public PlanViewHolder(ItemPlanBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        /**
         * Method binds a TrainingPlan to TextViews of ViewHolder
         * @param plan TrainingPlan to be bound
         */
        public void bind(TrainingPlan plan) {
            itemBinding.textViewTitle.setText(plan.getTitle());
            itemBinding.textViewDescription.setText(plan.getDescription());
        }
    }
}