package com.example.sulitrip.ui.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sulitrip.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SavedDestinationsFragment : Fragment(R.layout.fragment_saved_destinations) {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_saved_destinations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.savedDestinationsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Fetch saved destinations from Firestore and set them to the adapter
        getSavedDestinations()
    }

    private fun getSavedDestinations() {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("savedDestinations")
            .get()
            .addOnSuccessListener { result ->
                val destinations = mutableListOf<Map<String, Any>>()
                for (document in result) {
                    val destinationData = document.data.toMutableMap()
                    destinationData["id"] = document.id
                    destinations.add(destinationData)
                }

                recyclerView.adapter = SavedDestinationsAdapter(
                    destinations,
                    onClick = { destination ->
                        navigateToDestination(destination)
                    },
                    onDelete = { destination, position ->
                        deleteDestinationFromFirestore(destination, position)
                    }
                )
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error loading destinations", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteDestinationFromFirestore(destination: Map<String, Any>, position: Int) {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid ?: return

        val destinationId = destination["id"] as? String ?: return

        db.collection("users")
            .document(userId)
            .collection("savedDestinations")
            .document(destinationId)
            .delete()
            .addOnSuccessListener {
                (recyclerView.adapter as? SavedDestinationsAdapter)?.removeItem(position)
                Toast.makeText(requireContext(), "Destination deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to delete destination", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToDestination(destination: Map<String, Any>) {
        val latitude = destination["latitude"] as? Double ?: 0.0
        val longitude = destination["longitude"] as? Double ?: 0.0
        val destinationName = destination["name"] as? String ?: "Unknown Destination"

        // Handle navigation to destination (e.g., open a map activity or set a marker)
        Toast.makeText(requireContext(), "Navigating to $destinationName", Toast.LENGTH_SHORT).show()
    }
}
