package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.SettingsRepository
import com.example.model.MarqueeDirection
import com.example.model.MarqueeFont
import com.example.model.MarqueeSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("IVEO MSG", appName)
  }

  @Test
  fun `settings repository persists and retrieves marquee state`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = SettingsRepository(context)

    val customSettings = MarqueeSettings(
      text = "TESTE LETREIRO ESPELHADO",
      textColor = 0xFF00F0FF,
      backgroundColor = 0xFF000000,
      textSizeSp = 150f,
      speedFactor = 2.0f,
      direction = MarqueeDirection.LEFT_TO_RIGHT,
      isMirrored = true,
      font = MarqueeFont.LED_DOTS,
      isBlinking = true,
      isGlowEnabled = true,
      isLedGridEnabled = true,
      isNeonBorderEnabled = true
    )

    repository.saveSettings(customSettings)

    val loaded = repository.loadSettings()
    assertEquals("TESTE LETREIRO ESPELHADO", loaded.text)
    assertEquals(0xFF00F0FF, loaded.textColor)
    assertEquals(150f, loaded.textSizeSp, 0.01f)
    assertEquals(2.0f, loaded.speedFactor, 0.01f)
    assertEquals(MarqueeDirection.LEFT_TO_RIGHT, loaded.direction)
    assertTrue(loaded.isMirrored)
    assertTrue(loaded.isBlinking)
    assertEquals(MarqueeFont.LED_DOTS, loaded.font)
  }

  @Test
  fun `toggle mirror mode works correctly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = SettingsRepository(context)
    repository.saveSettings(MarqueeSettings(isMirrored = false))

    val toggledOnce = repository.toggleMirror()
    assertTrue(toggledOnce)
    assertTrue(repository.settingsFlow.value.isMirrored)

    val toggledTwice = repository.toggleMirror()
    assertFalse(toggledTwice)
    assertFalse(repository.settingsFlow.value.isMirrored)
  }

  @Test
  fun `adjust speed supports stopped 0 and ultra speed 10`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = SettingsRepository(context)

    // Test stopping completely
    repository.saveSettings(MarqueeSettings(speedFactor = 1.0f))
    repository.adjustSpeed(-5.0f)
    assertEquals(0.0f, repository.settingsFlow.value.speedFactor, 0.01f)

    // Test speeding up to maximum
    repository.adjustSpeed(15.0f)
    assertEquals(10.0f, repository.settingsFlow.value.speedFactor, 0.01f)
  }

  @Test
  fun `adjust text size supports filling screen up to 350`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = SettingsRepository(context)

    repository.saveSettings(MarqueeSettings(textSizeSp = 100f))
    repository.adjustTextSize(5.0f)
    assertEquals(350f, repository.settingsFlow.value.textSizeSp, 0.01f)
  }
}
