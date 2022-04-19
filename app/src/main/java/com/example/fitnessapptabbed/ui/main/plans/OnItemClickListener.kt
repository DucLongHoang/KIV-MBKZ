package com.example.fitnessapptabbed.ui.main.plans

/**
 * OnItemClickListener interface
 * provides items in recycler view with click actions
 * @author Long
 * @version 1.0
 */
interface OnItemClickListener {
    /**
     * Method processes clicking at an item at [position] in RecyclerView
     */
    fun onItemClick(position: Int)

    /**
     * Method processes edit of item at [position] in RecyclerView
     */
    fun onEditClick(position: Int)

    /**
     * Method processes deletion of item at [position] in RecyclerView
     */
    fun onDeleteClick(position: Int)
}