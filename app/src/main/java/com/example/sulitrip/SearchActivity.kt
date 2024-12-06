package com.example.sulitrip

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sulitrip.ui.theme.SavedDestinationsAdapter
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.Locale
import kotlin.concurrent.thread
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Suppress("DEPRECATION")
class SearchActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var mapController: IMapController
    private lateinit var searchInput: EditText
    private lateinit var searchRecyclerView: RecyclerView

    private val searchResults = mutableListOf<Map<String, Any>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.searchbar)

        Configuration.getInstance().load(applicationContext, androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext))

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        mapController = map.controller
        mapController.setZoom(15.0)
        mapController.setCenter(GeoPoint(16.4023, 120.5960))

        searchInput = findViewById(R.id.searchBar)
        searchRecyclerView = findViewById(R.id.searchRecyclerView)
        searchRecyclerView.layoutManager = LinearLayoutManager(this)

        val searchButton = findViewById<Button>(R.id.searchButton)
        searchButton.setOnClickListener { searchLocation() }

        fetchRecentSearches()
    }

    private fun fetchRecentSearches() {
        val recentSearches = listOf(
            mapOf("name" to "Burnham Park", "latitude" to 16.4023, "longitude" to 120.5960, "distance" to 1.2),
            mapOf("name" to "Mines View Park", "latitude" to 16.4151, "longitude" to 120.6202, "distance" to 4.5)
        )

        searchResults.clear()
        searchResults.addAll(recentSearches)
        updateSearchRecyclerView()
    }

    private fun updateSearchRecyclerView() {
        searchRecyclerView.adapter = SavedDestinationsAdapter(
            destinations = searchResults,
            onClick = { destination -> moveToLocation(destination) },
            onDelete = { destination, position -> deleteDestination(destination, position) }
        )
    }

    private fun moveToLocation(destination: Map<String, Any>) {
        val latitude = destination["latitude"] as? Double ?: 0.0
        val longitude = destination["longitude"] as? Double ?: 0.0
        val name = destination["name"] as? String ?: "Unknown Location"

        if (latitude != 0.0 && longitude != 0.0) {
            val geoPoint = GeoPoint(latitude, longitude)
            mapController.setCenter(geoPoint)

            val marker = Marker(map)
            marker.position = geoPoint
            marker.title = name
            map.overlays.add(marker)

            Toast.makeText(this, "Moved to $name", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Invalid location coordinates", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteDestination(destination: Map<String, Any>, position: Int) {
        (searchRecyclerView.adapter as? SavedDestinationsAdapter)?.removeItem(position)
        Toast.makeText(this, "${destination["name"]} removed", Toast.LENGTH_SHORT).show()
    }

    private fun searchLocation() {
        val locationName = searchInput.text.toString()
        if (locationName.isBlank()) {
            Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show()
            return
        }

        thread {
            try {
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses: List<Address> = geocoder.getFromLocationName(/* locationName = */
                    locationName, /* maxResults = */
                    1) ?: emptyList()

                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val lat = address.latitude
                    val lon = address.longitude
                    val geoPoint = GeoPoint(lat, lon)

                    runOnUiThread {
                        mapController.setCenter(geoPoint)

                        val marker = Marker(map)
                        marker.position = geoPoint
                        marker.title = locationName
                        map.overlays.add(marker)

                        val result = mapOf(
                            "name" to locationName,
                            "latitude" to lat,
                            "longitude" to lon,
                            "distance" to calculateDistance(GeoPoint(16.4023, 120.5960), geoPoint)
                        )
                        searchResults.add(result)
                        updateSearchRecyclerView()

                        Toast.makeText(this, "$locationName added to results", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    runOnUiThread { Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show() }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread { Toast.makeText(this, "Error searching location", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun calculateDistance(start: GeoPoint?, end: GeoPoint?): Double {
        if (start == null || end == null) return 0.0
        val radius = 6371.0
        val dLat = Math.toRadians(end.latitude - start.latitude)
        val dLon = Math.toRadians(end.longitude - start.longitude)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(start.latitude)) * cos(Math.toRadians(end.latitude)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return radius * c
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
}
