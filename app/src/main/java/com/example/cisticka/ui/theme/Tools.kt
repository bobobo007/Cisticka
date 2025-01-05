package com.example.cisticka.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.cisticka.R


@Composable
fun DarkLed(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(22.dp) // Veľkosť s ohraničením
            .background(
                color = colorResource(R.color.LED_dark_edge), // Tmavšie ohraničenie
                shape = CircleShape
            )
            .padding(3.dp) // Vnútorné odsadenie na zobrazenie LED efektu
    ) {
        Box(
            modifier = Modifier
                .size(20.dp) // Skutočná veľkosť LED
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            colorResource(R.color.LED_dark1),
                            colorResource(R.color.LED_dark2),
                            colorResource(R.color.LED_dark3)
                        ),
                        center = Offset(0.5f, 0.5f),
                        radius = 50f
                    ),
                    shape = CircleShape
                )
                .clickable { onClick() } // Akcia pri kliknutí
        )
    }
}


@Composable
fun BrightLed(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(22.dp) // Veľkosť s ohraničením
            .background(
                color = colorResource(R.color.LED_bright_edge), // Tmavšie ohraničenie
                shape = CircleShape
            )
            .padding(3.dp) // Vnútorné odsadenie na zobrazenie LED efektu
    ) {
        Box(
            modifier = Modifier
                .size(20.dp) // Skutočná veľkosť LED
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            colorResource(R.color.LED_bright1),
                            colorResource(R.color.LED_bright2),
                            colorResource(R.color.LED_bright3)
                        ),
                        center = Offset(0.5f, 0.5f),
                        radius = 50f
                    ),
                    shape = CircleShape
                )
                .clickable { onClick() } // Akcia pri kliknutí
        )
    }
}
