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
    private val destinations: MutableList<Map<String, Any>>,
    private val onClick: (Map<String, Any>) -> Unit,
    private val onDelete: (Map<String, Any>, Int) -> Unit // Callback for deletion
) : RecyclerView.Adapter<SavedDestinationsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val destinationName: TextView = itemView.findViewById(R.id.destinationName)
        val destinationDistance: TextView = itemView.findViewById(R.id.destinationDistance)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_destination, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val destination = destinations[position]
        holder.destinationName.text = destination["name"] as? String ?: "Unknown"
        holder.destinationDistance.text = String.format(
            "%.2f km",
            destination["distance"] as? Double ?: 0.0
        )

        holder.itemView.setOnClickListener { onClick(destination) }
        holder.deleteButton.setOnClickListener { onDelete(destination, position) }
    }

    override fun getItemCount(): Int = destinations.size

    fun removeItem(position: Int) {
        destinations.removeAt(position)
        notifyItemRemoved(position)
    }
}

