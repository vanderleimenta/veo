package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.MarqueeDirection
import com.example.model.MarqueeFont
import com.example.model.MarqueeSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("letreiro_led_prefs", Context.MODE_PRIVATE)

  private val _settingsFlow = MutableStateFlow(loadSettings())
  val settingsFlow: StateFlow<MarqueeSettings> = _settingsFlow.asStateFlow()

  fun loadSettings(): MarqueeSettings {
    val text = prefs.getString(KEY_TEXT, "BEM-VINDO AO LETREIRO LED!") ?: "BEM-VINDO AO LETREIRO LED!"
    val textColor = prefs.getLong(KEY_TEXT_COLOR, 0xFFFFEB3B)
    val backgroundColor = prefs.getLong(KEY_BG_COLOR, 0xFF000000)
    val textSizeSp = prefs.getFloat(KEY_TEXT_SIZE, 110f)
    val speedFactor = prefs.getFloat(KEY_SPEED, 1.5f)
    val directionStr = prefs.getString(KEY_DIRECTION, MarqueeDirection.RIGHT_TO_LEFT.name)
    val direction = try {
      MarqueeDirection.valueOf(directionStr ?: MarqueeDirection.RIGHT_TO_LEFT.name)
    } catch (_: Exception) {
      MarqueeDirection.RIGHT_TO_LEFT
    }
    val isMirrored = prefs.getBoolean(KEY_MIRRORED, false)
    val fontStr = prefs.getString(KEY_FONT, MarqueeFont.LED_DOTS.name)
    val font = try {
      MarqueeFont.valueOf(fontStr ?: MarqueeFont.LED_DOTS.name)
    } catch (_: Exception) {
      MarqueeFont.LED_DOTS
    }
    val isBlinking = prefs.getBoolean(KEY_BLINKING, false)
    val isGlowEnabled = prefs.getBoolean(KEY_GLOW, true)
    val isLedGridEnabled = prefs.getBoolean(KEY_LED_GRID, true)
    val isNeonBorderEnabled = prefs.getBoolean(KEY_NEON_BORDER, true)

    return MarqueeSettings(
      text = text,
      textColor = textColor,
      backgroundColor = backgroundColor,
      textSizeSp = textSizeSp,
      speedFactor = speedFactor,
      direction = direction,
      isMirrored = isMirrored,
      font = font,
      isBlinking = isBlinking,
      isGlowEnabled = isGlowEnabled,
      isLedGridEnabled = isLedGridEnabled,
      isNeonBorderEnabled = isNeonBorderEnabled
    )
  }

  fun saveSettings(settings: MarqueeSettings) {
    prefs.edit()
      .putString(KEY_TEXT, settings.text)
      .putLong(KEY_TEXT_COLOR, settings.textColor)
      .putLong(KEY_BG_COLOR, settings.backgroundColor)
      .putFloat(KEY_TEXT_SIZE, settings.textSizeSp)
      .putFloat(KEY_SPEED, settings.speedFactor)
      .putString(KEY_DIRECTION, settings.direction.name)
      .putBoolean(KEY_MIRRORED, settings.isMirrored)
      .putString(KEY_FONT, settings.font.name)
      .putBoolean(KEY_BLINKING, settings.isBlinking)
      .putBoolean(KEY_GLOW, settings.isGlowEnabled)
      .putBoolean(KEY_LED_GRID, settings.isLedGridEnabled)
      .putBoolean(KEY_NEON_BORDER, settings.isNeonBorderEnabled)
      .apply()

    _settingsFlow.value = settings
  }

  fun updateText(newText: String) {
    val updated = _settingsFlow.value.copy(text = newText)
    saveSettings(updated)
  }

  fun toggleMirror(): Boolean {
    val current = _settingsFlow.value
    val newMirror = !current.isMirrored
    saveSettings(current.copy(isMirrored = newMirror))
    return newMirror
  }

  fun toggleDirection(): MarqueeDirection {
    val current = _settingsFlow.value
    val newDirection = when (current.direction) {
      MarqueeDirection.RIGHT_TO_LEFT -> MarqueeDirection.LEFT_TO_RIGHT
      MarqueeDirection.LEFT_TO_RIGHT -> MarqueeDirection.RIGHT_TO_LEFT
      MarqueeDirection.STATIC -> MarqueeDirection.RIGHT_TO_LEFT
    }
    saveSettings(current.copy(direction = newDirection))
    return newDirection
  }

  fun adjustSpeed(delta: Float) {
    val current = _settingsFlow.value
    val newSpeed = (current.speedFactor + delta).coerceIn(0.0f, 10.0f)
    saveSettings(current.copy(speedFactor = newSpeed))
  }

  fun adjustTextSize(factor: Float) {
    val current = _settingsFlow.value
    val newSize = (current.textSizeSp * factor).coerceIn(40f, 350f)
    saveSettings(current.copy(textSizeSp = newSize))
  }

  companion object {
    private const val KEY_TEXT = "key_text"
    private const val KEY_TEXT_COLOR = "key_text_color"
    private const val KEY_BG_COLOR = "key_bg_color"
    private const val KEY_TEXT_SIZE = "key_text_size"
    private const val KEY_SPEED = "key_speed"
    private const val KEY_DIRECTION = "key_direction"
    private const val KEY_MIRRORED = "key_mirrored"
    private const val KEY_FONT = "key_font"
    private const val KEY_BLINKING = "key_blinking"
    private const val KEY_GLOW = "key_glow"
    private const val KEY_LED_GRID = "key_led_grid"
    private const val KEY_NEON_BORDER = "key_neon_border"
  }
}
