package com.example.sulitrip.ui.theme

import android.annotation.SuppressLint
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
import org.osmdroid.util.GeoPoint

class SavedDestinationsFragment : Fragment(R.layout.fragment_saved_destinations) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SavedDestinationsAdapter
    private val destinations = mutableListOf<Map<String, Any>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_saved_destinations, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.savedDestinationsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = SavedDestinationsAdapter(
            destinations,
            onClick = { destination -> navigateToDestination(destination) },
            onDelete = { destination, position -> deleteDestinationFromFirestore(destination, position) }
        )
        recyclerView.adapter = adapter

        fetchSavedDestinations()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchSavedDestinations() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "Please log in to view saved destinations", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(user.uid)
            .collection("savedDestinations")
            .get()
            .addOnSuccessListener { result ->
                destinations.clear()
                for (document in result) {
                    val data = document.data
                    val name = data["name"] as? String
                    val lat = data["latitude"] as? Double
                    val lon = data["longitude"] as? Double

                    if (name != null && lat != null && lon != null) {
                        destinations.add(
                            mapOf(
                                "id" to document.id,
                                "name" to name,
                                "latitude" to lat,
                                "longitude" to lon
                            )
                        )
                    } else {
                        Toast.makeText(requireContext(), "Invalid location data skipped", Toast.LENGTH_SHORT).show()
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to load destinations: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteDestinationFromFirestore(destination: Map<String, Any>, position: Int) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val destinationId = destination["id"] as? String
        if (destinationId.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Invalid destination ID", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(user.uid)
            .collection("savedDestinations")
            .document(destinationId)
            .delete()
            .addOnSuccessListener {
                adapter.removeItem(position)
                Toast.makeText(requireContext(), "Destination deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to delete destination: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToDestination(destination: Map<String, Any>) {
        val latitude = destination["latitude"] as? Double
        val longitude = destination["longitude"] as? Double
        val destinationName = destination["name"] as? String ?: "Unknown Destination"

        if (latitude == null || longitude == null) {
            Toast.makeText(requireContext(), "Invalid destination coordinates", Toast.LENGTH_SHORT).show()
            return
        }

        GeoPoint(latitude, longitude)
        Toast.makeText(requireContext(), "Navigating to $destinationName", Toast.LENGTH_SHORT).show()
    }
}
