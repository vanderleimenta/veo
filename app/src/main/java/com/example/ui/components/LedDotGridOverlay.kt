package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

/**
 * Renders an authentic physical LED dot-matrix grille overlay.
 * Adds tiny simulated LED pixel aperture dots across the screen for realistic stadium/billboard look.
 */
@Composable
fun LedDotGridOverlay(
  modifier: Modifier = Modifier,
  dotRadius: Float = 1.8f,
  dotSpacing: Float = 8f,
  dotColor: Color = Color.Black.copy(alpha = 0.45f)
) {
  Canvas(modifier = modifier.fillMaxSize()) {
    val width = size.width
    val height = size.height

    if (width <= 0 || height <= 0) return@Canvas

    var y = dotSpacing / 2f
    while (y < height) {
      var x = dotSpacing / 2f
      while (x < width) {
        drawCircle(
          color = dotColor,
          radius = dotRadius,
          center = Offset(x, y)
        )
        x += dotSpacing
      }
      y += dotSpacing
    }
  }
}
