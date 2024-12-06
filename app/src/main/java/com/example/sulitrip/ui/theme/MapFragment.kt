package com.example.sulitrip.ui.theme

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.sulitrip.R
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView

class MapFragment : Fragment(R.layout.fragment_map) {

    private var mapView: MapView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize MapView
        mapView = view.findViewById(R.id.mapView)
        mapView?.setTileSource(TileSourceFactory.MAPNIK)
        mapView?.setMultiTouchControls(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cleanup MapView resources
        mapView?.onDetach()
        mapView = null
    }
}
