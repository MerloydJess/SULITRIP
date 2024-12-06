@file:Suppress("DEPRECATION")

package com.example.sulitrip

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sulitrip.R.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Locale
import kotlin.concurrent.thread
import com.google.android.material.appbar.MaterialToolbar
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MapActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var mapController: IMapController
    private lateinit var locationOverlay: MyLocationNewOverlay
    private lateinit var searchBar: EditText
    private lateinit var searchButton: Button
    private lateinit var saveLocationButton: Button
    private var currentMarker: Marker? = null
    private var currentLocation: GeoPoint? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.mapview)

        val toolbar: MaterialToolbar = findViewById(id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize OSMDroid configuration
        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        // Initialize MapView and components
        map = findViewById(id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        // Set map controller
        mapController = map.controller
        mapController.setZoom(15.0)
        mapController.setCenter(GeoPoint(16.4023, 120.5960)) // Default center: Baguio City

        // Initialize UI components
        searchBar = findViewById(id.searchBar)
        searchButton = findViewById(id.searchButton)
        saveLocationButton = findViewById(id.saveLocationButton)
        saveLocationButton.visibility = Button.GONE // Initially hide the save button

        // Add location overlay
        locationOverlay = MyLocationNewOverlay(map)
        locationOverlay.enableMyLocation()
        map.overlays.add(locationOverlay)

        // Set up button listeners
        searchButton.setOnClickListener { searchLocation() }
        saveLocationButton.setOnClickListener { saveCurrentLocationToFirestore() }

        // Fetch predefined markers for tourist spots and jeep terminals
        fetchAndDisplayMarkers()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        map.onDetach()
    }

    private fun fetchAndDisplayMarkers() {
        val touristSpots = listOf(
            GeoPoint(16.4023, 120.5960),  // Baguio City
            GeoPoint(16.4175, 120.5941)   // Example tourist spot
        )
        val jeepneyTerminals = listOf(
            GeoPoint(16.4095, 120.5969),  // Terminal 1
            GeoPoint(16.4019, 120.5927)   // Terminal 2
        )
        addMarkers(touristSpots, "Tourist Spot")
        addMarkers(jeepneyTerminals, "Jeepney Terminal")
    }

    private fun addMarkers(locations: List<GeoPoint>, title: String) {
        for (location in locations) {
            val marker = Marker(map)
            marker.position = location
            marker.title = title
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            map.overlays.add(marker)
        }
    }

    private fun searchLocation() {
        val locationName = searchBar.text.toString()
        if (locationName.isBlank()) {
            Toast.makeText(this, "Enter a location", Toast.LENGTH_SHORT).show()
            return
        }

        thread {
            try {
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses = geocoder.getFromLocationName(locationName, 1)

                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    currentLocation = GeoPoint(address.latitude, address.longitude)

                    runOnUiThread {
                        mapController.setCenter(currentLocation)
                        currentMarker?.let { map.overlays.remove(it) }
                        currentMarker = Marker(map).apply {
                            position = currentLocation
                            title = locationName
                        }
                        map.overlays.add(currentMarker)
                        map.invalidate()
                        saveLocationButton.visibility = Button.VISIBLE
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Error searching location", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveCurrentLocationToFirestore() {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid ?: return

        currentLocation?.let { location ->
            val distance = calculateDistance(locationOverlay.myLocation, location)
            val locationData = mapOf(
                "name" to (currentMarker?.title ?: "Unknown"),
                "latitude" to location.latitude,
                "longitude" to location.longitude,
                "distance" to distance
            )

            db.collection("users")
                .document(userId)
                .collection("savedDestinations")
                .add(locationData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Location saved!", Toast.LENGTH_SHORT).show()
                    saveLocationButton.visibility = Button.GONE
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save location.", Toast.LENGTH_SHORT).show()
                }
        } ?: Toast.makeText(this, "No location to save.", Toast.LENGTH_SHORT).show()
    }

    private fun calculateDistance(start: GeoPoint?, end: GeoPoint?): Double {
        if (start == null || end == null) return 0.0
        val radius = 6371.0 // Earth's radius in km
        val dLat = Math.toRadians(end.latitude - start.latitude)
        val dLon = Math.toRadians(end.longitude - start.longitude)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(start.latitude)) * cos(Math.toRadians(end.latitude)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return radius * c
    }
}
