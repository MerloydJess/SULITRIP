package com.example.sulitrip.ui.theme

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sulitrip.R

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the button and set up navigation
        val openMapButton = view.findViewById<Button>(R.id.openMapButton)
        openMapButton.setOnClickListener {
            findNavController().navigate(R.id.mapFragment) // Navigate to MapFragment
        }
    }
}
