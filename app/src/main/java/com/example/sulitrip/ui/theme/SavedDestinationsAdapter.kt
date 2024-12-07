package com.example.sulitrip.ui.theme

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sulitrip.R

class SavedDestinationsAdapter(
    private val destinations: MutableList<Map<String, Any>>, // Updated for consistency
    private val onClick: (Map<String, Any>) -> Unit,
    private val onDelete: (Map<String, Any>, Int) -> Unit
) : RecyclerView.Adapter<SavedDestinationsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val destinationName: TextView = itemView.findViewById(R.id.destinationName)
        val destinationDistance: TextView = itemView.findViewById(R.id.destinationDistance)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_destination, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val destination = destinations[position]

        // Safely extract data from the map
        val name = destination["name"] as? String ?: "Unknown"
        val distance = destination["distance"] as? Double ?: 0.0

        // Bind data to UI
        holder.destinationName.text = name
        holder.destinationDistance.text = String.format("%.2f km", distance)

        // Add accessibility descriptions
        holder.destinationName.contentDescription = "Destination name: $name"
        holder.destinationDistance.contentDescription = "Distance: $distance kilometers"
        holder.deleteButton.contentDescription = "Delete $name"

        // Click listeners
        holder.itemView.setOnClickListener { onClick(destination) }
        holder.deleteButton.setOnClickListener { onDelete(destination, position) }
    }

    override fun getItemCount(): Int = destinations.size

    // Remove an item with a targeted animation
    fun removeItem(position: Int) {
        if (position in destinations.indices) {
            destinations.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, destinations.size) // To ensure indices stay consistent
        }
    }

    // Add a method to update the entire list if needed
    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newDestinations: List<Map<String, Any>>) {
        destinations.clear()
        destinations.addAll(newDestinations)
        notifyDataSetChanged()
    }
}
