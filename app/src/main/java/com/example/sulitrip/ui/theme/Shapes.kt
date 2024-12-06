// File: ui/theme/Shapes.kt
package com.example.sulitrip.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),   // For buttons, small cards
    medium = RoundedCornerShape(8.dp),  // For dialogs, medium containers
    large = RoundedCornerShape(16.dp)   // For large containers
)
