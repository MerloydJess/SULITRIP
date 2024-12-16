package com.example.sulitrip

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Locale
import kotlin.concurrent.thread

@Suppress("DEPRECATION")
class MapActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var mapController: IMapController
    private lateinit var searchBar: EditText
    private lateinit var searchButton: Button
    private lateinit var saveLocationButton: Button
    private lateinit var savedLocationsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mapview)

        // Initialize OSMDroid
        Configuration.getInstance().load(
            applicationContext,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        Configuration.getInstance().userAgentValue = packageName

        // Initialize Views
        map = findViewById(R.id.map)
        searchBar = findViewById(R.id.searchBar)
        searchButton = findViewById(R.id.searchButton)
        saveLocationButton = findViewById(R.id.saveLocationButton)
        savedLocationsButton = findViewById(R.id.savedLocationsButton)

        // Configure Map
        map.setMultiTouchControls(true)
        mapController = map.controller
        mapController.setZoom(15.0)
        mapController.setCenter(GeoPoint(16.4023, 120.5960)) // Default center: Baguio City

        // Add User Location Overlay
        val locationOverlay = MyLocationNewOverlay(map)
        locationOverlay.enableMyLocation()
        map.overlays.add(locationOverlay)

        // Button Listeners
        searchButton.setOnClickListener { searchLocation() }
        saveLocationButton.setOnClickListener { saveLocation() }
        savedLocationsButton.setOnClickListener { navigateToSavedDestinations() }

        // Request permissions
        checkPermissions()
    }

    private fun searchLocation() {
        val locationName = searchBar.text.toString().trim()
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
                    val geoPoint = GeoPoint(address.latitude, address.longitude)

                    runOnUiThread {
                        mapController.setCenter(geoPoint)
                        addMarker(geoPoint, locationName)
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

    private fun saveLocation() {
        val geoPoint = map.mapCenter
        val locationName = searchBar.text.toString().trim()

        if (locationName.isBlank() || geoPoint == null) {
            Toast.makeText(this, "No location to save", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            Toast.makeText(this, "Please log in to save locations", Toast.LENGTH_SHORT).show()
            return
        }

        val locationData = mapOf(
            "name" to locationName,
            "latitude" to geoPoint.latitude,
            "longitude" to geoPoint.longitude,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(user.uid)
            .collection("savedDestinations")
            .add(locationData)
            .addOnSuccessListener {
                Toast.makeText(this, "Location saved!", Toast.LENGTH_SHORT).show()
                saveLocationButton.visibility = Button.GONE
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save location: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                100
            )
        }
    }

    private fun addMarker(geoPoint: GeoPoint, title: String) {
        val marker = Marker(map)
        marker.position = geoPoint
        marker.title = title
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(marker)
    }

    private fun navigateToSavedDestinations() {
        val intent = Intent(this, SavedLocationActivity::class.java)
        startActivity(intent)
    }
}


