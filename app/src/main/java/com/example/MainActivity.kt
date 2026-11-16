package com.example

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.view.WindowCompat
import com.example.data.SettingsRepository
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.FullscreenMarqueeScreen
import com.example.ui.theme.MyApplicationTheme

enum class AppScreen {
  EDITOR,
  FULLSCREEN
}

class MainActivity : ComponentActivity() {
  private lateinit var settingsRepository: SettingsRepository

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Enable full display cutout coverage across status bar and notch area
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      window.attributes.layoutInDisplayCutoutMode =
        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      window.attributes.layoutInDisplayCutoutMode =
        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    }
    WindowCompat.setDecorFitsSystemWindows(window, false)

    settingsRepository = SettingsRepository(applicationContext)

    setContent {
      MyApplicationTheme(darkTheme = true) {
        val settings by settingsRepository.settingsFlow.collectAsState()
        var currentScreen by remember { mutableStateOf(AppScreen.EDITOR) }

        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        // When turned sideways to landscape, automatically launch full screen over status bar
        LaunchedEffect(isLandscape) {
          if (isLandscape) {
            currentScreen = AppScreen.FULLSCREEN
          }
        }

        Surface(
          modifier = Modifier.fillMaxSize(),
          color = Color(0xFF121212)
        ) {
          AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "ScreenTransition"
          ) { screen ->
            when (screen) {
              AppScreen.EDITOR -> {
                EditorScreen(
                  settings = settings,
                  onUpdateSettings = { newSettings ->
                    settingsRepository.saveSettings(newSettings)
                  },
                  onLaunchFullscreen = {
                    currentScreen = AppScreen.FULLSCREEN
                  }
                )
              }
              AppScreen.FULLSCREEN -> {
                FullscreenMarqueeScreen(
                  settings = settings,
                  onUpdateSettings = { newSettings ->
                    settingsRepository.saveSettings(newSettings)
                  },
                  onExitFullscreen = {
                    currentScreen = AppScreen.EDITOR
                  }
                )
              }
            }
          }
        }
      }
    }
  }
}
