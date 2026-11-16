package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = NeutralDarkPrimary,
  onPrimary = Color(0xFF121212),
  primaryContainer = NeutralDarkPrimaryContainer,
  onPrimaryContainer = Color.White,
  secondary = NeutralDarkSecondary,
  onSecondary = Color(0xFF121212),
  background = NeutralDarkBackground,
  onBackground = Color(0xFFEEEEEE),
  surface = NeutralDarkSurface,
  onSurface = Color(0xFFEEEEEE),
  surfaceVariant = NeutralDarkSurfaceVariant,
  onSurfaceVariant = Color(0xFFAAAAAA),
  outline = NeutralDarkOutline,
  outlineVariant = NeutralDarkOutlineVariant
)

private val LightColorScheme = lightColorScheme(
  primary = NeutralLightPrimary,
  onPrimary = Color.White,
  primaryContainer = NeutralLightPrimaryContainer,
  onPrimaryContainer = Color(0xFF121212),
  secondary = NeutralLightSecondary,
  onSecondary = Color.White,
  background = NeutralLightBackground,
  onBackground = Color(0xFF1A1A1A),
  surface = NeutralLightSurface,
  onSurface = Color(0xFF1A1A1A),
  surfaceVariant = NeutralLightSurfaceVariant,
  onSurfaceVariant = Color(0xFF666666),
  outline = NeutralLightOutline,
  outlineVariant = NeutralLightOutlineVariant
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
